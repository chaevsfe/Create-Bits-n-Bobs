package com.kipti.bnb.content.kinetics.encased_blocks.cogwheel;

import com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialVisualSupport;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityVisual;
import com.zurrtum.create.client.content.kinetics.base.RotatingInstance;
import com.zurrtum.create.client.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.zurrtum.create.client.flywheel.api.instance.Instance;
import com.zurrtum.create.client.flywheel.api.model.Model;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.model.Models;
import com.zurrtum.create.client.foundation.render.AllInstanceTypes;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BnbEncasedCogVisual extends KineticBlockEntityVisual<KineticBlockEntity> implements CogwheelMaterialVisualSupport {

    protected RotatingInstance rotatingModel;
    @Nullable
    protected final RotatingInstance rotatingTopShaft;
    @Nullable
    protected final RotatingInstance rotatingBottomShaft;

    protected final CogwheelMaterialVisualSupport.State materialState = new State();

    public static BnbEncasedCogVisual small(final VisualizationContext modelManager, final KineticBlockEntity blockEntity, final float partialTick) {
        return new BnbEncasedCogVisual(modelManager, blockEntity, false, partialTick, Models.chunkPartial(AllPartialModels.SHAFTLESS_COGWHEEL));
    }

    public static BnbEncasedCogVisual large(final VisualizationContext modelManager, final KineticBlockEntity blockEntity, final float partialTick) {
        return new BnbEncasedCogVisual(modelManager, blockEntity, true, partialTick, Models.chunkPartial(AllPartialModels.SHAFTLESS_LARGE_COGWHEEL));
    }

    public BnbEncasedCogVisual(final VisualizationContext modelManager, final KineticBlockEntity blockEntity, final boolean large, final float partialTick, final Model model) {
        super(modelManager, blockEntity, partialTick);

        this.rotatingModel = this.bnb$materialInstancer(this.instancerProvider(),
                        AllInstanceTypes.ROTATING,
                        model,
                        null,
                        null,
                        this.materialState,
                        this.blockEntity,
                        this.blockState)
                .createInstance();

        this.rotatingModel.setup(blockEntity)
                .setPosition(this.getVisualPosition())
                .rotateToFace(this.rotationAxis())
                .setChanged();

        RotatingInstance rotatingTopShaft = null;
        RotatingInstance rotatingBottomShaft = null;

        final Block block = this.blockState.getBlock();
        if (block instanceof final IRotate def) {
            for (final Direction d : Iterate.directionsInAxis(this.rotationAxis())) {
                if (!def.hasShaftTowards(blockEntity.getLevel(), blockEntity.getBlockPos(), this.blockState, d))
                    continue;
                final RotatingInstance instance = this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.chunkPartial(AllPartialModels.SHAFT_HALF))
                        .createInstance();
                instance.setup(blockEntity)
                        .setPosition(this.getVisualPosition())
                        .rotateToFace(Direction.SOUTH, d)
                        .setChanged();

                if (large) {
                    instance.setRotationOffset(BracketedKineticBlockEntityRenderer.getShaftAngleOffset(this.rotationAxis(), this.pos));
                }

                if (d.getAxisDirection() == AxisDirection.POSITIVE) {
                    rotatingTopShaft = instance;
                } else {
                    rotatingBottomShaft = instance;
                }
            }
        }

        this.rotatingTopShaft = rotatingTopShaft;
        this.rotatingBottomShaft = rotatingBottomShaft;
    }

    @Override
    public void setSectionCollector(final SectionCollector sectionCollector) {
        switch (this.blockState.getValue(BlockStateProperties.AXIS)) {
            case X -> this.setSectionCollector(sectionCollector, 0, -1, -1, 0, 1, 1);
            case Y -> this.setSectionCollector(sectionCollector, -1, 0, -1, 1, 0, 1);
            default -> this.setSectionCollector(sectionCollector, -1, -1, 0, 1, 1, 0);
        }
    }

    @Override
    public void update(final float pt) {
        final RotatingInstance updated = this.bnb$updateCog(this.materialState, this.blockEntity, this.blockState, this.instancerProvider(), this.getVisualPosition(), this.rotatingModel);
        if (updated != null) {
            this.rotatingModel = updated;
            this.relight(this.rotatingModel);
        }
        this.rotatingModel.setup(this.blockEntity)
                .setChanged();
        if (this.rotatingTopShaft != null) this.rotatingTopShaft.setup(this.blockEntity)
                .setChanged();
        if (this.rotatingBottomShaft != null) this.rotatingBottomShaft.setup(this.blockEntity)
                .setChanged();
    }

    @Override
    public void updateLight(final float partialTick) {
        this.relight(this.rotatingModel, this.rotatingTopShaft, this.rotatingBottomShaft);
    }

    @Override
    protected void _delete() {
        this.rotatingModel.delete();
        if (this.rotatingTopShaft != null) this.rotatingTopShaft.delete();
        if (this.rotatingBottomShaft != null) this.rotatingBottomShaft.delete();
    }

    @Override
    public void collectCrumblingInstances(final Consumer<@Nullable Instance> consumer) {
        consumer.accept(this.rotatingModel);
        consumer.accept(this.rotatingTopShaft);
        consumer.accept(this.rotatingBottomShaft);
    }
}
