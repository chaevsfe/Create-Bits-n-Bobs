package com.kipti.bnb.mixin.client.cogwheel_material;

import com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialContext;
import com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialContext.TransformKind;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferCache;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

/**
 * Substitutes {@link CachedBuffers} output with material-keyed {@link SuperByteBuffer}s while a cogwheel material
 * context is set (see {@code CogwheelMaterialFunnel}). Only buffers for the cogwheel partials themselves are
 * swapped; metal parts ({@code SHAFT_HALF}, {@code COGWHEEL_SHAFT}, mixer pole/head) fall through to the original.
 */
@Environment(EnvType.CLIENT)
@Mixin(CachedBuffers.class)
public abstract class CachedBuffersMixin {

    @WrapMethod(method = "partial(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;)Lcom/zurrtum/create/client/catnip/render/SuperByteBuffer;")
    private static SuperByteBuffer bnb$materialPartial(
            final PartialModel partial,
            final BlockState referenceState,
            final Operation<SuperByteBuffer> original
    ) {
        return CogwheelMaterialContext.partial(
                partial, referenceState, Direction.UP, TransformKind.NONE,
                () -> original.call(partial, referenceState)
        );
    }

    @WrapMethod(method = "partialFacing(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;)Lcom/zurrtum/create/client/catnip/render/SuperByteBuffer;")
    private static SuperByteBuffer bnb$materialPartialFacing(
            final PartialModel partial,
            final BlockState referenceState,
            final Operation<SuperByteBuffer> original
    ) {
        if (!referenceState.hasProperty(FACING))
            return original.call(partial, referenceState);

        return CogwheelMaterialContext.partial(
                partial, referenceState, referenceState.getValue(FACING), TransformKind.FACE,
                () -> original.call(partial, referenceState)
        );
    }

    @WrapMethod(method = "partialFacing(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Lcom/zurrtum/create/client/catnip/render/SuperByteBuffer;")
    private static SuperByteBuffer bnb$materialPartialFacing(
            final PartialModel partial,
            final BlockState referenceState,
            final Direction facing,
            final Operation<SuperByteBuffer> original
    ) {
        return CogwheelMaterialContext.partial(
                partial, referenceState, facing, TransformKind.FACE,
                () -> original.call(partial, referenceState, facing)
        );
    }

    @WrapMethod(method = "partialFacingVertical(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Lcom/zurrtum/create/client/catnip/render/SuperByteBuffer;")
    private static SuperByteBuffer bnb$materialPartialFacingVertical(
            final PartialModel partial,
            final BlockState referenceState,
            final Direction facing,
            final Operation<SuperByteBuffer> original
    ) {
        return CogwheelMaterialContext.partial(
                partial, referenceState, facing, TransformKind.FACE_VERTICAL,
                () -> original.call(partial, referenceState, facing)
        );
    }

    @WrapMethod(method = "block(Lnet/minecraft/world/level/block/state/BlockState;)Lcom/zurrtum/create/client/catnip/render/SuperByteBuffer;")
    private static SuperByteBuffer bnb$materialBlock(
            final BlockState toRender,
            final Operation<SuperByteBuffer> original
    ) {
        return CogwheelMaterialContext.block(toRender, () -> original.call(toRender));
    }

    @WrapMethod(method = "block(Lcom/zurrtum/create/client/catnip/render/SuperByteBufferCache$Compartment;Lnet/minecraft/world/level/block/state/BlockState;)Lcom/zurrtum/create/client/catnip/render/SuperByteBuffer;")
    private static SuperByteBuffer bnb$materialBlock(
            final SuperByteBufferCache.Compartment<BlockState> compartment,
            final BlockState toRender,
            final Operation<SuperByteBuffer> original
    ) {
        return CogwheelMaterialContext.block(toRender, () -> original.call(compartment, toRender));
    }

}
