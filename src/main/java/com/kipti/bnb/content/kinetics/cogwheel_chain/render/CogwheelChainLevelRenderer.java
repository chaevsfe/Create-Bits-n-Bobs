package com.kipti.bnb.content.kinetics.cogwheel_chain.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.world.phys.Vec3;

/**
 * Hosts the cogwheel chain on 26.2. The block entities that carry the chain behaviour belong to Create, and
 * both the renderer and the visualizer registries hold one entry per block entity type, so the chain cannot be
 * drawn by a renderer of its own without replacing Create's. It is drawn from the level's own extraction and
 * submit passes instead, which Flywheel does not take part in, so one path serves both backends.
 */
@Environment(EnvType.CLIENT)
public final class CogwheelChainLevelRenderer {

    private CogwheelChainLevelRenderer() {
    }

    public static void register() {
        LevelExtractionEvents.END_EXTRACTION.register(context -> {
            final Vec3 cameraPos = context.camera().position();
            if (cameraPos == null)
                return;
            CogwheelChainRenderStates.extract(context.level(), cameraPos);
        });

        LevelRenderEvents.COLLECT_SUBMITS.register(context -> {
            final Vec3 cameraPos = context.levelState().cameraRenderState.pos;
            if (cameraPos == null)
                return;
            CogwheelChainRenderStates.submit(context.poseStack(), context.submitNodeCollector(), cameraPos);
        });

        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((client, level) -> CogwheelChainRenderStates.clear());
    }
}
