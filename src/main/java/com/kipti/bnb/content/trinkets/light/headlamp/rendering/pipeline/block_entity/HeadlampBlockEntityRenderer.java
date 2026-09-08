package com.kipti.bnb.content.trinkets.light.headlamp.rendering.pipeline.block_entity;

import com.kipti.bnb.content.trinkets.light.foundation.LightBlock;
import com.kipti.bnb.content.trinkets.light.headlamp.HeadlampBlock;
import com.kipti.bnb.content.trinkets.light.headlamp.HeadlampBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferRenderState;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Draws the lamps a headlamp block carries, from a buffer keyed by the block entity's packed render state.
 * <p>
 * The buffer is built facing up, so the block's own facing is applied here and one buffer serves every headlamp
 * in that state. Upstream also had a Flywheel visual for these; this port renders them through the block entity
 * renderer on both backends instead (see the phase report).
 */
@Environment(EnvType.CLIENT)
public class HeadlampBlockEntityRenderer
        extends SmartBlockEntityRenderer<HeadlampBlockEntity, HeadlampBlockEntityRenderer.HeadlampRenderState> {

    public HeadlampBlockEntityRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public HeadlampRenderState createRenderState() {
        return new HeadlampRenderState();
    }

    @Override
    public void extractRenderState(
            HeadlampBlockEntity blockEntity,
            HeadlampRenderState state,
            float partialTicks,
            Vec3 cameraPos,
            @Nullable CrumblingOverlay crumblingOverlay
    ) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPos, crumblingOverlay);
        state.lamps = null;

        Level level = blockEntity.getLevel();
        if (level == null)
            return;

        long renderState = blockEntity.getRenderStateAsLong();
        if (renderState == 0L)
            return;

        SuperByteBuffer cached = HeadlampVertexBufferCache.getOrCreate(renderState);
        if (cached == null)
            return;

        BlockState blockState = blockEntity.getBlockState();
        Direction facing = blockState.getValue(HeadlampBlock.FACING);
        if (facing != Direction.UP)
            cached.center()
                    .rotateTo(Direction.UP, facing)
                    .rotateYDegrees(facing.get2DDataValue() != -1 ? facing.get2DDataValue() * -90.0f : 0)
                    .uncenter();

        state.lamps = cached
                .light(LightBlock.isEmissive(blockState) ? LightCoordsUtil.withBlock(state.lightCoords, 15) : state.lightCoords)
                .disableDiffuse()
                .extractRenderState();
    }

    @Override
    public void submit(
            HeadlampRenderState state,
            PoseStack matrices,
            SubmitNodeCollector queue,
            CameraRenderState cameraState
    ) {
        super.submit(state, matrices, queue, cameraState);
        if (state.lamps != null)
            state.lamps.submit(matrices, queue);
    }

    public static class HeadlampRenderState extends SmartBlockEntityRenderer.SmartRenderState {
        @Nullable
        public SuperByteBufferRenderState lamps;
    }
}
