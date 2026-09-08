package com.kipti.bnb.mixin.client.cogwheel_material;

import com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialFunnel;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.client.foundation.render.BlockEntityRenderHelper;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;

/**
 * The contraption half of the funnel: Create extracts block entity render states for a moving contraption itself,
 * without going through the vanilla dispatcher.
 */
@Mixin(BlockEntityRenderHelper.class)
public abstract class BlockEntityRenderHelperMixin {

    @WrapOperation(method = "getBlockEntitiesRenderState", at = @org.spongepowered.asm.mixin.injection.At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderer;extractRenderState(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/client/renderer/blockentity/state/BlockEntityRenderState;FLnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
    private static void bnb$pushMaterialContext(
            BlockEntityRenderer<BlockEntity, BlockEntityRenderState> renderer,
            BlockEntity blockEntity,
            BlockEntityRenderState state,
            float tickProgress,
            Vec3 cameraPos,
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay,
            Operation<Void> original
    ) {
        BlockState material = CogwheelMaterialFunnel.materialOf(blockEntity);
        if (material == null) {
            original.call(renderer, blockEntity, state, tickProgress, cameraPos, crumblingOverlay);
            return;
        }

        CogwheelMaterialFunnel.push(material);
        try {
            original.call(renderer, blockEntity, state, tickProgress, cameraPos, crumblingOverlay);
        } finally {
            CogwheelMaterialFunnel.pop();
        }
    }
}
