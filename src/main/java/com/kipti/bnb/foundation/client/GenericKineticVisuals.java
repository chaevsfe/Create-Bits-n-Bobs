package com.kipti.bnb.foundation.client;

import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.zurrtum.create.client.flywheel.api.visual.BlockEntityVisual;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.model.Models;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * The visual factory for block entities whose model is looked up per block in
 * {@link GenericBlockEntityRenderModels}. Upstream wrote this as a lambda inside the block entity registration; on
 * Fabric that lambda's body would have to be verified while the dedicated server loads that class, so it lives in a
 * client-only class and is referenced only as a method handle.
 */
@Environment(EnvType.CLIENT)
public final class GenericKineticVisuals {

    private GenericKineticVisuals() {
    }

    public static <T extends KineticBlockEntity> BlockEntityVisual<? super T> registeredModel(
            VisualizationContext context, T blockEntity, float partialTick) {
        PartialModel partial = GenericBlockEntityRenderModels.REGISTRY.get(blockEntity.getBlockState().getBlock());
        if (partial == null)
            partial = AllPartialModels.SHAFT;
        return new SingleAxisRotatingVisual<>(context, blockEntity, partialTick, Models.partial(partial));
    }
}
