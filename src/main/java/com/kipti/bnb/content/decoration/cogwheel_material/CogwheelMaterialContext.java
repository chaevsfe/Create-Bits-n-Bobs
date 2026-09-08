package com.kipti.bnb.content.decoration.cogwheel_material;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperBufferFactory;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferCache;
import net.minecraft.client.Minecraft;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

/**
 * Thread-local material context for the classic (non-Flywheel) {@code SuperByteBuffer} pipeline.
 * <p>
 * {@code BlockEntityRenderDispatcherMixin} and {@code BlockEntityRenderHelperMixin} push the {@link CogwheelMaterialBehaviour} material for the duration of a
 * block entity render, and {@code CachedBuffersMixin} swaps any cogwheel-part buffer for a material-keyed one while
 * the context is set. The material-keyed compartment is required because the base {@code KINETIC_BLOCK} compartment
 * is keyed by {@link BlockState} alone and would collide across materials.
 */
@Environment(EnvType.CLIENT)
public class CogwheelMaterialContext {

    public static final SuperByteBufferCache.Compartment<ModelKey> COGWHEEL_MATERIAL = new SuperByteBufferCache.Compartment<>();

    public static final ThreadLocal<BlockState> CURRENT_RENDER_MATERIAL = new ThreadLocal<>();

    /**
     * Substitutes a partial-model buffer with a material-keyed buffer if the requested partial is a cogwheel variant's
     * own model. The partial-identity guard keeps metal parts ({@code SHAFT_HALF}, {@code COGWHEEL_SHAFT}, mixer
     * pole/head) untouched.
     */
    public static SuperByteBuffer partial(
            final PartialModel partial,
            final BlockState state,
            final Direction facing,
            final TransformKind transform,
            final Supplier<SuperByteBuffer> fallback
    ) {
        final BlockState material = CURRENT_RENDER_MATERIAL.get();
        if (material == null || material.is(Blocks.SPRUCE_PLANKS))
            return fallback.get();

        final CogwheelMaterialRenderer.Variant variant = CogwheelMaterialRenderer.getVariant(state);
        if (variant == null || variant.partialModel() != partial)
            return fallback.get();

        return buffer(variant, material, facing, transform);
    }

    public static SuperByteBuffer block(final BlockState state, final Supplier<SuperByteBuffer> fallback) {
        final BlockState material = CURRENT_RENDER_MATERIAL.get();
        if (material == null || material.is(Blocks.SPRUCE_PLANKS))
            return fallback.get();

        final CogwheelMaterialRenderer.Variant variant = CogwheelMaterialRenderer.getVariant(state);
        if (variant == null)
            return fallback.get();

        return bufferBlock(state, material, Direction.UP, TransformKind.NONE);
    }

    private static SuperByteBuffer buffer(
            final CogwheelMaterialRenderer.Variant variant,
            final BlockState material,
            final Direction facing,
            final TransformKind transform
    ) {
        return SuperByteBufferCache.getInstance().get(
                COGWHEEL_MATERIAL,
                new ModelKey(variant, material, facing, transform),
                () -> {
                    final BlockStateModel model = CogwheelMaterialRenderer.generateModel(variant, material);
                    final PoseStack.Pose pose = switch (transform) {
                        case NONE -> new PoseStack().last();
                        case FACE -> CachedBuffers.rotateToFace(facing);
                        case FACE_VERTICAL -> CachedBuffers.rotateToFaceVertical(facing);
                    };
                    return SuperBufferFactory.getInstance()
                            .createForBlock(model, Blocks.AIR.defaultBlockState(), pose);
                }
        );
    }
    private static SuperByteBuffer bufferBlock(
            final BlockState state,
            final BlockState material,
            final Direction facing,
            final TransformKind transform
    ) {
        return SuperByteBufferCache.getInstance().get(
                COGWHEEL_MATERIAL,
                new ModelKey(state, material, facing, transform),
                () -> {
                    final BlockStateModel model = CogwheelMaterialRenderer.generateModel(
                            Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state), material);
                    final PoseStack.Pose pose = switch (transform) {
                        case NONE -> new PoseStack().last();
                        case FACE -> CachedBuffers.rotateToFace(facing);
                        case FACE_VERTICAL -> CachedBuffers.rotateToFaceVertical(facing);
                    };
                    return SuperBufferFactory.getInstance()
                            .createForBlock(model, state, pose);
                }
        );
    }

    public record ModelKey(
            CogwheelMaterialRenderer.Variant variant,
            BlockState state,
            BlockState material,
            Direction facing,
            TransformKind transform
    ) {
        public ModelKey(final CogwheelMaterialRenderer.Variant variant, final BlockState material, final Direction facing, final TransformKind transform) {
            this(variant, null, material, facing, transform);
        }

        public ModelKey(final BlockState state, final BlockState material, final Direction facing, final TransformKind transform) {
            this(null, state, material, facing, transform);
        }
    }

    public enum TransformKind {
        NONE,
        FACE,
        FACE_VERTICAL
    }
}
