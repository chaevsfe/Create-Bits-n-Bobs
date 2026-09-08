package com.kipti.bnb.content.kinetics.flywheel_bearing;

import com.kipti.bnb.registry.client.BnbPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class FlywheelBearingBlockEntityRenderer
        extends KineticBlockEntityRenderer<FlywheelBearingBlockEntity, FlywheelBearingBlockEntityRenderer.FlywheelBearingRenderState> {

    public FlywheelBearingBlockEntityRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public FlywheelBearingRenderState createRenderState() {
        return new FlywheelBearingRenderState();
    }

    @Override
    public void extractRenderState(
            FlywheelBearingBlockEntity be,
            FlywheelBearingRenderState state,
            float partialTicks,
            Vec3 cameraPos,
            @Nullable CrumblingOverlay crumblingOverlay
    ) {
        state.top = null;
        Level level = be.getLevel();
        if (level == null)
            return;

        // The flywheel bearing has no Flywheel visual, so it must render on both backends. The base class
        // skips its whole extraction when visualization is on, so the kinetic state is filled in here instead.
        state.support = false;
        updateBaseRenderState(be, state, level, crumblingOverlay);
        state.angle = getAngleForBe(be, state.blockPos, state.axis);
        state.model = getRotatedModel(be, state)
                .cardinalLighting(state.cardinalLighting)
                .rotateCentered(state.angle, state.direction)
                .light(state.lightCoords)
                .color(state.color)
                .extractRenderState();

        BlockState blockState = be.getBlockState();
        Direction facing = blockState.getValue(FlywheelBearingBlock.FACING);
        SuperByteBuffer top = CachedBuffers.partialFacingVertical(AllPartialModels.BEARING_TOP, blockState, facing);
        top.center()
                .rotate((float) Math.toRadians(be.getInterpolatedAngle(partialTicks)), facing.getAxis())
                .uncenter();
        state.top = top.light(state.lightCoords).extractRenderState();
    }

    @Override
    public void submit(
            FlywheelBearingRenderState state,
            PoseStack matrices,
            SubmitNodeCollector queue,
            CameraRenderState cameraState
    ) {
        super.submit(state, matrices, queue, cameraState);
        if (state.top != null)
            state.top.submit(matrices, queue);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(final FlywheelBearingBlockEntity be, final FlywheelBearingRenderState state) {
        BlockState blockState = be.getBlockState();
        return CachedBuffers.partialFacingVertical(
                BnbPartialModels.LARGE_STONE_COG_SHAFTLESS, blockState, blockState.getValue(FlywheelBearingBlock.FACING));
    }

    public static class FlywheelBearingRenderState extends KineticBlockEntityRenderer.KineticRenderState {
        @Nullable
        public SuperByteBufferRenderState top;
    }
}
