package com.kipti.bnb.mixin.dyeable.fluid_tank;

import com.zurrtum.create.content.fluids.tank.FluidTankBlockEntity;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidTankBlockEntity.class)
public abstract class FluidTankBlockEntityMixin extends SmartBlockEntity {

    public FluidTankBlockEntityMixin(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "writeSafe", at = @At("HEAD"))
    private void bnb$ensureWriteSafeSuper(final ValueOutput compound, final CallbackInfo ci) {
        super.writeSafe(compound);
    }

}
