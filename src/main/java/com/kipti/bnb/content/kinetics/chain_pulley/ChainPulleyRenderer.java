package com.kipti.bnb.content.kinetics.chain_pulley;

import com.kipti.bnb.registry.client.BnbPartialModels;
import com.kipti.bnb.registry.client.BnbSpriteShifts;
import com.kipti.bnb.registry.content.blocks.BnbKineticBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.SpriteShiftEntry;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.content.contraptions.pulley.AbstractPulleyRenderer;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.content.contraptions.AbstractContraptionEntity;
import com.zurrtum.create.content.contraptions.pulley.PulleyBlock;
import com.zurrtum.create.content.contraptions.pulley.PulleyContraption;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * The chain pulley rides Create Fly's own pulley renderer, which already builds the shaft, coil, rope and magnet
 * during render-state extraction. Only the chain that hangs off the magnet is extra, and it is added to a widened
 * render state rather than by re-implementing the whole pulley.
 */
@Environment(EnvType.CLIENT)
public class ChainPulleyRenderer extends AbstractPulleyRenderer<ChainPulleyBlockEntity> {

    public ChainPulleyRenderer(final BlockEntityRendererProvider.Context context) {
        // The magnet is drawn without its chain at every offset and the chain is overlaid separately, so the
        // shared renderer's "half magnet" slot takes the same chainless magnet. Upstream named a
        // chain_rope_half_magnet model here that has never existed in the mod's assets; it went unnoticed because
        // upstream replaced the whole render method and never let the shared renderer reach that slot.
        super(context, BnbPartialModels.CHAIN_ROPE_HALF, BnbPartialModels.CHAIN_PULLEY_MAGNET_NO_CHAIN);
    }

    @Override
    public PulleyRenderState createRenderState() {
        return new ChainPulleyRenderState();
    }

    @Override
    public void extractRenderState(
            ChainPulleyBlockEntity be,
            PulleyRenderState state,
            float partialTicks,
            Vec3 cameraPos,
            @Nullable CrumblingOverlay crumblingOverlay
    ) {
        super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);
        if (!(state instanceof ChainPulleyRenderState chainState))
            return;
        chainState.magnetChain = null;

        Level level = be.getLevel();
        if (level == null)
            return;

        float offset = getOffset(be, partialTicks);
        if (!isRunning(be) && offset != 0)
            return;

        PartialModel model = offset > .25f
                ? BnbPartialModels.CHAIN_PULLEY_MAGNET_CHAIN
                : BnbPartialModels.CHAIN_PULLEY_MAGNET_CHAIN_HALF;
        SuperByteBuffer buffer = CachedBuffers.partial(model, be.getBlockState());
        buffer.shiftUVScrolling(BnbSpriteShifts.CHAIN_ROPE, getCoilVScroll(BnbSpriteShifts.CHAIN_ROPE, offset, 1));
        chainState.magnetChain = buffer
                .cardinalLighting(level)
                .light(SmartBlockEntityRenderer.getLightCoords(level, be.getBlockPos()))
                .extractRenderState();
    }

    @Override
    public void submit(
            PulleyRenderState state,
            PoseStack matrices,
            SubmitNodeCollector queue,
            CameraRenderState cameraState
    ) {
        super.submit(state, matrices, queue, cameraState);
        if (!(state instanceof ChainPulleyRenderState chainState) || chainState.magnetChain == null)
            return;
        matrices.pushPose();
        matrices.translate(0, state.magnetOffset, 0);
        chainState.magnetChain.submit(matrices, queue);
        matrices.popPose();
    }

    @Override
    protected Direction.Axis getShaftAxis(final ChainPulleyBlockEntity be) {
        return be.getBlockState().getValue(PulleyBlock.HORIZONTAL_AXIS);
    }

    @Override
    protected PartialModel getCoil() {
        return BnbPartialModels.CHAIN_ROPE_COIL;
    }

    @Override
    protected SuperByteBuffer renderRope(final ChainPulleyBlockEntity be) {
        return CachedBuffers.block(BnbKineticBlocks.CHAIN_ROPE.getDefaultState());
    }

    @Override
    protected SuperByteBuffer renderMagnet(final ChainPulleyBlockEntity be) {
        return CachedBuffers.partial(BnbPartialModels.CHAIN_PULLEY_MAGNET_NO_CHAIN, be.getBlockState());
    }

    @Override
    protected float getOffset(final ChainPulleyBlockEntity be, final float partialTicks) {
        return getBlockEntityOffset(partialTicks, be);
    }

    @Override
    protected boolean isRunning(final ChainPulleyBlockEntity be) {
        return isPulleyRunning(be);
    }

    public static boolean isPulleyRunning(final ChainPulleyBlockEntity be) {
        return be.running || be.getMirrorParent() != null || be.isVirtual();
    }

    @Override
    protected SpriteShiftEntry getCoilShift() {
        return BnbSpriteShifts.CHAIN_PULLEY_COIL;
    }

    public static float getBlockEntityOffset(final float partialTicks, final ChainPulleyBlockEntity blockEntity) {
        float offset = blockEntity.getInterpolatedOffset(partialTicks);

        final AbstractContraptionEntity attachedContraption = blockEntity.getAttachedContraption();
        if (attachedContraption != null) {
            final PulleyContraption c = (PulleyContraption) attachedContraption.getContraption();
            final double entityPos = Mth.lerp(partialTicks, attachedContraption.yOld, attachedContraption.getY());
            offset = (float) -(entityPos - c.anchor.getY() - c.getInitialOffset());
        }

        return offset;
    }

    public static class ChainPulleyRenderState extends AbstractPulleyRenderer.PulleyRenderState {
        @Nullable
        public SuperByteBufferRenderState magnetChain;
    }
}
