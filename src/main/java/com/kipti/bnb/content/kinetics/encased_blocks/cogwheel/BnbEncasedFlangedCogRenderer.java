package com.kipti.bnb.content.kinetics.encased_blocks.cogwheel;

import com.kipti.bnb.registry.client.BnbPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.client.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity;
import com.zurrtum.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The cogwheel itself rides the base kinetic pipeline; only the two shaft halves are added here.
 * <p>
 * Upstream took the shaft angle from {@code BracketedKineticBlockEntityRenderer.getAngleForLargeCogShaft}, which
 * Create Fly does not ship. The same value is rebuilt from the two pieces Fly does ship:
 * {@code AnimationTickHolder.getRenderTime} and
 * {@link BracketedKineticBlockEntityRenderer#getShaftAngleOffset(Axis, BlockPos)}, which is the offset a large cog's
 * shaft uses instead of the cog's own position offset.
 */
@Environment(EnvType.CLIENT)
public class BnbEncasedFlangedCogRenderer
        extends KineticBlockEntityRenderer<SimpleKineticBlockEntity, BnbEncasedFlangedCogRenderer.FlangedCogRenderState> {

    private static final float DEG_TO_RAD = (float) (Math.PI / 180);

    private final boolean large;

    public static BnbEncasedFlangedCogRenderer small(final BlockEntityRendererProvider.Context context) {
        return new BnbEncasedFlangedCogRenderer(context, false);
    }

    public static BnbEncasedFlangedCogRenderer large(final BlockEntityRendererProvider.Context context) {
        return new BnbEncasedFlangedCogRenderer(context, true);
    }

    public BnbEncasedFlangedCogRenderer(final BlockEntityRendererProvider.Context context, final boolean large) {
        super(context);
        this.large = large;
    }

    @Override
    public FlangedCogRenderState createRenderState() {
        return new FlangedCogRenderState();
    }

    @Override
    public void extractRenderState(
            final SimpleKineticBlockEntity be,
            final FlangedCogRenderState state,
            final float partialTicks,
            final Vec3 cameraPos,
            @Nullable final CrumblingOverlay crumblingOverlay
    ) {
        super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);
        state.shafts.clear();
        if (state.support)
            return;

        final Level level = be.getLevel();
        if (level == null)
            return;

        final BlockState blockState = be.getBlockState();
        if (!(blockState.getBlock() instanceof final IRotate def))
            return;

        final Axis axis = state.axis;
        final BlockPos pos = state.blockPos;
        final float angle = this.large ? shaftAngle(be, level, axis, pos) : getAngleForBe(be, pos, axis);

        for (final Direction d : Iterate.directionsInAxis(axis)) {
            if (!def.hasShaftTowards(level, pos, blockState, d))
                continue;
            final SuperByteBuffer shaft = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, blockState, d);
            state.shafts.add(shaft.cardinalLighting(state.cardinalLighting)
                    .rotateCentered(angle, state.direction)
                    .light(state.lightCoords)
                    .color(state.color)
                    .extractRenderState());
        }
    }

    private static float shaftAngle(final SimpleKineticBlockEntity be, final Level level, final Axis axis, final BlockPos pos) {
        final float time = AnimationTickHolder.getRenderTime(level);
        final float offset = BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, pos);
        return ((time * be.getSpeed() * 3 / 10f + offset) % 360) * DEG_TO_RAD;
    }

    @Override
    public void submit(
            final FlangedCogRenderState state,
            final PoseStack matrices,
            final SubmitNodeCollector queue,
            final CameraRenderState cameraState
    ) {
        super.submit(state, matrices, queue, cameraState);
        for (final SuperByteBufferRenderState shaft : state.shafts)
            shaft.submit(matrices, queue);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(final SimpleKineticBlockEntity be, final FlangedCogRenderState state) {
        final BlockState blockState = be.getBlockState();
        return CachedBuffers.partialFacingVertical(
                this.large ? BnbPartialModels.ENCASED_LARGE_FLANGED_COGWHEEL_BLOCK : BnbPartialModels.ENCASED_FLANGED_COGWHEEL_BLOCK,
                blockState,
                Direction.fromAxisAndDirection(blockState.getValue(EncasedCogwheelBlock.AXIS), AxisDirection.POSITIVE));
    }

    public static class FlangedCogRenderState extends KineticBlockEntityRenderer.KineticRenderState {
        public final List<SuperByteBufferRenderState> shafts = new ArrayList<>();
    }
}
