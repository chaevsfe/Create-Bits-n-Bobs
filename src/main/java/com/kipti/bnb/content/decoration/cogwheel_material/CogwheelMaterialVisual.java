package com.kipti.bnb.content.decoration.cogwheel_material;

import com.zurrtum.create.client.flywheel.api.model.Model;
import com.zurrtum.create.client.flywheel.lib.model.baked.BakedModelBuilder;
import com.zurrtum.create.client.flywheel.lib.util.RendererReloadCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
public class CogwheelMaterialVisual {

    public static final RendererReloadCache<CogwheelMaterialVisual.ModelKey, Model> MODEL_CACHE = new RendererReloadCache<>(CogwheelMaterialVisual::createModel);

    private static Model createModel(final CogwheelMaterialVisual.ModelKey key) {
        final BlockStateModel model = CogwheelMaterialRenderer.generateModel(key.variant(), key.material());
        return new BakedModelBuilder(model)
                .build();
    }

    public record ModelKey(CogwheelMaterialRenderer.Variant variant, BlockState material) {

    }

}
