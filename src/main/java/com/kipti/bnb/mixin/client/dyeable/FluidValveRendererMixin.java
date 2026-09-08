package com.kipti.bnb.mixin.client.dyeable;

import com.kipti.bnb.content.decoration.dyeable.simple.SimpleDyeableBehaviour;
import com.kipti.bnb.content.decoration.dyeable.simple.SimpleDyeablePartialHelper;
import com.kipti.bnb.registry.client.BnbSpriteShifts;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.content.fluids.pipes.valve.FluidValveRenderer;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.content.fluids.pipes.valve.FluidValveBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Dyes the valve pointer; the buffer is now built during render-state extraction. */
@Environment(EnvType.CLIENT)
@Mixin(FluidValveRenderer.class)
public class FluidValveRendererMixin {

    @WrapOperation(method = "extractRenderState(Lcom/zurrtum/create/content/fluids/pipes/valve/FluidValveBlockEntity;Lcom/zurrtum/create/client/content/fluids/pipes/valve/FluidValveRenderer$FluidValveRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V", at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/client/catnip/render/CachedBuffers;partial(Lcom/zurrtum/create/client/flywheel/lib/model/baked/PartialModel;Lnet/minecraft/world/level/block/state/BlockState;)Lcom/zurrtum/create/client/catnip/render/SuperByteBuffer;"))
    private SuperByteBuffer bnb$applyFluidValvePointerDye(
            PartialModel partial,
            BlockState referenceState,
            Operation<SuperByteBuffer> original,
            FluidValveBlockEntity be
    ) {
        return SimpleDyeablePartialHelper.apply(
                original.call(partial, referenceState),
                SimpleDyeableBehaviour.getDyeColor(be),
                BnbSpriteShifts.DYED_FLUID_VALVE
        );
    }
}
