package com.kipti.bnb.mixin.client.cogwheel_material;

import com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialFunnel;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Pushes the cogwheel material for the duration of one world block entity's render-state extraction. This is the
 * only place vanilla extracts a block entity render state, so it covers every renderer.
 */
@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {

    @WrapMethod(method = "tryExtractRenderState")
    private <E extends BlockEntity, S extends BlockEntityRenderState> S bnb$pushMaterialContext(
            E blockEntity,
            float tickProgress,
            ModelFeatureRenderer.CrumblingOverlay crumblingOverlay,
            boolean force,
            Operation<S> original
    ) {
        BlockState material = CogwheelMaterialFunnel.materialOf(blockEntity);
        if (material == null)
            return original.call(blockEntity, tickProgress, crumblingOverlay, force);

        CogwheelMaterialFunnel.push(material);
        try {
            return original.call(blockEntity, tickProgress, crumblingOverlay, force);
        } finally {
            CogwheelMaterialFunnel.pop();
        }
    }
}
