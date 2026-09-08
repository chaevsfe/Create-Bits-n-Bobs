package com.kipti.bnb.content.kinetics.throttle_lever;

import com.kipti.bnb.registry.client.BnbPartialModels;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.catnip.math.AngleHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ThrottleLeverBlockEntityRenderer extends SmartBlockEntityRenderer<ThrottleLeverBlockEntity, ThrottleLeverBlockEntityRenderer.ThrottleLeverRenderState> {

    public ThrottleLeverBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ThrottleLeverRenderState createRenderState() {
        return new ThrottleLeverRenderState();
    }

    @Override
    public void extractRenderState(
            ThrottleLeverBlockEntity blockEntity,
            ThrottleLeverRenderState state,
            float partialTicks,
            Vec3 cameraPos,
            @Nullable CrumblingOverlay crumblingOverlay
    ) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPos, crumblingOverlay);
        state.handle = null;

        Level level = blockEntity.getLevel();
        if (level == null)
            return;

        float currentPower = blockEntity.getCurrentPower(partialTicks);
        BlockState leverState = blockEntity.getBlockState();
        AttachFace face = leverState.getValue(ThrottleLeverBlock.FACE);
        Direction facing = leverState.getValue(ThrottleLeverBlock.FACING);

        float rX = face == AttachFace.FLOOR ? 0 : face == AttachFace.WALL ? 90 : 180;
        float rY = AngleHelper.horizontalAngle(facing);

        SuperByteBuffer handle = CachedBuffers.partial(BnbPartialModels.THROTTLE_LEVER_HANDLE, leverState);
        handle.center();
        handle.rotateYDegrees(rY);
        handle.rotateXDegrees(rX);
        handle.translate(0, -6 / 16f, 0);
        handle.rotateXDegrees(-45 + (currentPower / 15f) * 90);
        handle.translate(0, 6 / 16f, 0);
        handle.uncenter();

        state.handle = handle.light(state.lightCoords).extractRenderState();
    }

    @Override
    public void submit(
            ThrottleLeverRenderState state,
            PoseStack matrices,
            SubmitNodeCollector queue,
            CameraRenderState cameraState
    ) {
        super.submit(state, matrices, queue, cameraState);
        if (state.handle != null)
            state.handle.submit(matrices, queue);
    }

    public static class ThrottleLeverRenderState extends SmartBlockEntityRenderer.SmartRenderState {
        @Nullable
        public SuperByteBufferRenderState handle;
    }
}
