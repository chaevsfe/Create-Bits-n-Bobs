package com.kipti.bnb.content.kinetics.cogwheel_chain.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
public final class CogwheelChainRenderState {

    private final CogwheelChainMesh mesh;
    private final RenderType renderType;
    private final BlockPos controllerPos;
    private final float vOffset;

    CogwheelChainRenderState(final CogwheelChainMesh mesh,
                             final RenderType renderType,
                             final BlockPos controllerPos,
                             final float vOffset) {
        this.mesh = mesh;
        this.renderType = renderType;
        this.controllerPos = controllerPos;
        this.vOffset = vOffset;
    }

    /**
     * Reads nothing but this state: the geometry, its lighting and the scroll offset were all resolved during
     * the extraction pass, so no level access happens while the frame is being submitted.
     */
    public void submit(final PoseStack poseStack, final SubmitNodeCollector collector, final Vec3 cameraPos) {
        poseStack.pushPose();
        poseStack.translate(
                this.controllerPos.getX() - cameraPos.x,
                this.controllerPos.getY() - cameraPos.y,
                this.controllerPos.getZ() - cameraPos.z);

        collector.submitCustomGeometry(poseStack, this.renderType, (pose, consumer) -> {
            for (int vertex = 0; vertex < this.mesh.vertexCount(); vertex++) {
                consumer.addVertex(pose.pose(), this.mesh.x(vertex), this.mesh.y(vertex), this.mesh.z(vertex))
                        .setColor(1.0f, 1.0f, 1.0f, 1.0f)
                        .setUv(this.mesh.u(vertex), this.mesh.v(vertex) + this.vOffset)
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(this.mesh.light(vertex))
                        .setNormal(pose, 0.0f, 1.0f, 0.0f);
            }
        });

        poseStack.popPose();
    }
}
