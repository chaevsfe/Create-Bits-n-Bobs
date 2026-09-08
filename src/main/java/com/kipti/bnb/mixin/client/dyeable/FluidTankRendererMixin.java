package com.kipti.bnb.mixin.client.dyeable;

import com.kipti.bnb.content.decoration.dyeable.simple.SimpleDyeablePartialHelper;
import com.kipti.bnb.content.decoration.dyeable.tanks.DyeableTankBehaviour;
import com.kipti.bnb.registry.client.BnbSpriteShifts;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.fluids.tank.FluidTankRenderer;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.content.fluids.tank.FluidTankBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Dyes the boiler gauge. 26.2 builds the buffer while the render state is extracted, so the hook moved from
 * {@code renderAsBoiler} to {@code updateBoilerState}.
 */
@Environment(EnvType.CLIENT)
@Mixin(FluidTankRenderer.class)
public class FluidTankRendererMixin {

    @WrapOperation(method = "updateBoilerState", at = @At(value = "INVOKE", ordinal = 0, target = "Lcom/zurrtum/create/client/catnip/render/CachedBuffers;partial(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;)Lcom/zurrtum/create/client/catnip/render/SuperByteBuffer;"))
    private SuperByteBuffer bnb$applyBoilerGaugeDye(
            PartialModel partial,
            BlockState referenceState,
            Operation<SuperByteBuffer> original,
            FluidTankBlockEntity be
    ) {
        return SimpleDyeablePartialHelper.apply(
                original.call(partial, referenceState),
                bnb$getDisplayedTankColor(be),
                BnbSpriteShifts.DYED_BOILER_GAUGE
        );
    }

    @Unique
    @Nullable
    private static DyeColor bnb$getDisplayedTankColor(FluidTankBlockEntity be) {
        DyeableTankBehaviour behaviour = BlockEntityBehaviour.get(be, DyeableTankBehaviour.TYPE);
        return behaviour == null ? null : behaviour.getDisplayedColor();
    }
}
