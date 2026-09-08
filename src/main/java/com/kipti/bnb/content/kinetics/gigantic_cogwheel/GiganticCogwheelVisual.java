package com.kipti.bnb.content.kinetics.gigantic_cogwheel;

import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual;
import com.zurrtum.create.client.content.kinetics.base.RotatingInstance;
import com.zurrtum.create.client.foundation.render.AllInstanceTypes;
import com.zurrtum.create.client.flywheel.api.instance.Instance;
import com.zurrtum.create.client.flywheel.api.model.Model;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.model.baked.BakedModelBuilder;
import com.zurrtum.create.client.flywheel.lib.util.RendererReloadCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

/**
 * Handles Flywheel-based visualization for the gigantic cogwheel, managing
 * rotating instances and model caching based on the cogwheel's material.
 */
@Environment(EnvType.CLIENT)
public class GiganticCogwheelVisual extends KineticBlockEntityVisual<GiganticCogwheelBlockEntity> {

    private static final RendererReloadCache<ModelKey, Model> MODEL_CACHE = new RendererReloadCache<>(
            GiganticCogwheelVisual::createModel);

    protected BlockState lastMaterial;
    protected RotatingInstance rotatingModel;

    public GiganticCogwheelVisual(final VisualizationContext context,
                                  final GiganticCogwheelBlockEntity blockEntity,
                                  final float partialTick) {
        super(context, blockEntity, partialTick);
        this.setupInstance();
    }

    private void setupInstance() {
        this.lastMaterial = this.blockEntity.getMaterial();
        this.rotatingModel = this.instancerProvider().instancer(
                        AllInstanceTypes.ROTATING,
                        MODEL_CACHE.get(new ModelKey(this.blockEntity.getMaterial()))
                )
                .createInstance();
        this.rotatingModel.setup(this.blockEntity)
                .setPosition(this.getVisualPosition())
                .rotateToFace(this.rotationAxis())
                .setChanged();
        this.relight(this.rotatingModel);
    }

    @Override
    public void update(final float pt) {
        if (this.lastMaterial != this.blockEntity.getMaterial()) {
            this.rotatingModel.delete();
            this.setupInstance();
        } else {
            this.rotatingModel.setup(this.blockEntity).setChanged();
        }
    }

    @Override
    public void updateLight(final float partialTick) {
        this.relight(this.rotatingModel);
    }

    @Override
    protected void _delete() {
        this.rotatingModel.delete();
    }

    @Override
    public void collectCrumblingInstances(final Consumer<Instance> consumer) {
        consumer.accept(this.rotatingModel);
    }

    private static Model createModel(final ModelKey key) {
        final BlockStateModel model = GiganticCogwheelRenderer.generateModel(key.material());
        return new BakedModelBuilder(model).build();
    }

    public record ModelKey(BlockState material) {
    }
}
