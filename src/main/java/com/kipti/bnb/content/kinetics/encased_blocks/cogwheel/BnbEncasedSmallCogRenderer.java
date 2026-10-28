package com.kipti.bnb.content.kinetics.encased_blocks.cogwheel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getRotateAngleWithoutBeOffset;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getRotationAxisOf;
import static com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer.getTintColor;

@Environment(EnvType.CLIENT)
public class BnbEncasedSmallCogRenderer implements BlockEntityRenderer<SimpleKineticBlockEntity, BnbEncasedSmallCogRenderer.RenderState> {

    public BnbEncasedSmallCogRenderer(final BlockEntityRendererProvider.Context context) {
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(
            final SimpleKineticBlockEntity be,
            final RenderState state,
            final float partialTicks,
            final Vec3 cameraPosition,
            @Nullable final CrumblingOverlay breakProgress
    ) {
        final Level level = SmartBlockEntityRenderer.extractBase(be, state, breakProgress);
        final CardinalLighting cardinalLighting = SmartBlockEntityRenderer.getCardinalLighting(level);
        final BlockState blockState = be.getBlockState();
        final Axis axis = getRotationAxisOf(blockState);
        final Direction direction = axis.getPositive();
        final int color = getTintColor(be);
        state.angle = getRotateAngleWithoutBeOffset(axis, direction, be, state, level);
        state.top = null;
        state.bottom = null;
        final boolean hasTop = blockState.getValue(EncasedCogwheelBlock.TOP_SHAFT);
        final boolean hasBottom = blockState.getValue(EncasedCogwheelBlock.BOTTOM_SHAFT);
        if (hasTop && hasBottom) {
            state.model = CachedBuffers.partialFacingVertical(AllPartialModels.COGWHEEL, blockState, direction)
                    .cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();
            return;
        }
        state.model = CachedBuffers.partialFacingVertical(AllPartialModels.SHAFTLESS_COGWHEEL, blockState, direction)
                .cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();
        if (hasTop) {
            state.top = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, blockState, direction)
                    .cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();
        }
        if (hasBottom) {
            state.bottom = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, blockState, direction.getOpposite())
                    .cardinalLighting(cardinalLighting).light(state.lightCoords).color(color).extractRenderState();
        }
    }

    @Override
    public void submit(final RenderState state, final PoseStack matrices, final SubmitNodeCollector queue, final CameraRenderState camera) {
        if (state.angle != null) {
            matrices.rotateAround(state.angle, 0.5f, 0.5f, 0.5f);
        }
        state.model.submit(matrices, queue);
        if (state.top != null) {
            state.top.submit(matrices, queue);
        }
        if (state.bottom != null) {
            state.bottom.submit(matrices, queue);
        }
    }

    public static class RenderState extends BlockEntityRenderState {
        public SuperByteBufferRenderState model;
        @Nullable
        public Quaternionf angle;
        @Nullable
        public SuperByteBufferRenderState top;
        @Nullable
        public SuperByteBufferRenderState bottom;
    }
}
