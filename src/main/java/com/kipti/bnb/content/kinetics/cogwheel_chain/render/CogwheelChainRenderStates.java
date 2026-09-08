package com.kipti.bnb.content.kinetics.cogwheel_chain.render;

import com.cake.struts.content.IAntiClippedShadowLighter;
import com.kipti.bnb.content.kinetics.cogwheel_chain.behaviour.CogwheelChainBehaviour;
import com.kipti.bnb.foundation.config.BnbServerSettings;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.CogwheelChain;
import com.kipti.bnb.content.kinetics.cogwheel_chain.types.CogwheelChainType;
import com.kipti.bnb.content.kinetics.cogwheel_chain.world.CogwheelChainWorld;
import com.kipti.bnb.foundation.behaviour.SuperBlockEntityBehaviour;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.animation.AnimationTickHolder;
import com.zurrtum.create.client.foundation.render.CreateRenderTypes;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The client half of the cogwheel chain, split across the two phases 26.2 gives a renderer.
 *
 * <p>Chain geometry is rebuilt only when a chain's shape changes, keyed by a signature; the scroll offset and
 * the lighting are the only per-frame work. Nothing here is reachable from a dedicated server: the class is
 * loaded from the client entrypoint by {@link CogwheelChainLevelRenderer}.
 */
@Environment(EnvType.CLIENT)
public final class CogwheelChainRenderStates {

    /**
     * Matches the view distance Create Fly gives its own chain conveyor, whose chains are the closest thing on
     * this platform to these.
     */
    private static final double VIEW_DISTANCE = 256;

    private static final Map<BlockPos, CachedMesh> MESHES = new HashMap<>();
    private static final List<CogwheelChainRenderState> FRAME_STATES = new ArrayList<>();

    private CogwheelChainRenderStates() {
    }

    public static void clear() {
        MESHES.clear();
        FRAME_STATES.clear();
    }

    public static void extract(final ClientLevel level, final Vec3 cameraPos) {
        FRAME_STATES.clear();

        final Set<BlockPos> seen = new HashSet<>();
        for (final Map.Entry<BlockPos, CogwheelChain> entry : CogwheelChainWorld.get(level).entries()) {
            final BlockPos controllerPos = entry.getKey();
            final CogwheelChain chain = entry.getValue();
            seen.add(controllerPos);

            final BlockEntity blockEntity = level.getBlockEntity(controllerPos);
            if (!(blockEntity instanceof final KineticBlockEntity kineticBlockEntity))
                continue;

            final CogwheelChainBehaviour behaviour =
                    SuperBlockEntityBehaviour.getOptional(blockEntity, CogwheelChainBehaviour.TYPE).orElse(null);
            if (behaviour == null || !behaviour.isController())
                continue;

            if (isOutOfRange(chain, controllerPos, cameraPos))
                continue;

            final CogwheelChainType chainType = chain.getChainType();
            final boolean flipInsideOutside =
                    chainType.getRenderType().usesConsistentInsideOutside() && chain.shouldFlipInsideOutside();
            final int signature = Objects.hash(chain.hashCode(), chainType.getKey(), flipInsideOutside);

            final CogwheelChainMesh mesh = meshFor(controllerPos, chain, signature, flipInsideOutside);
            if (mesh == null)
                continue;

            mesh.relight(IAntiClippedShadowLighter.createGlobalLighter(blockEntity), controllerPos);

            final float rotationsPerTick =
                    behaviour.getChainRotationFactor() * kineticBlockEntity.getSpeed() / (60 * 20);
            final float renderTime = AnimationTickHolder.getRenderTime(level);
            final float offset = rotationsPerTick == 0 ? 0 : (float) (Math.PI * 2 * rotationsPerTick * renderTime);

            FRAME_STATES.add(new CogwheelChainRenderState(
                    mesh,
                    CreateRenderTypes.chain(chainType.getRenderTexture()),
                    controllerPos,
                    offset * mesh.textureSquish()));
        }

        MESHES.keySet().retainAll(seen);
    }

    public static void submit(final PoseStack poseStack, final SubmitNodeCollector collector, final Vec3 cameraPos) {
        for (final CogwheelChainRenderState state : FRAME_STATES)
            state.submit(poseStack, collector, cameraPos);
    }

    private static CogwheelChainMesh meshFor(final BlockPos controllerPos,
                                             final CogwheelChain chain,
                                             final int signature,
                                             final boolean flipInsideOutside) {
        final CachedMesh cached = MESHES.get(controllerPos);
        if (cached != null && cached.signature == signature)
            return cached.mesh;
        final CogwheelChainMesh mesh = CogwheelChainMesh.build(chain, flipInsideOutside);
        MESHES.put(controllerPos, new CachedMesh(signature, mesh));
        return mesh;
    }

    private static boolean isOutOfRange(final CogwheelChain chain, final BlockPos controllerPos, final Vec3 cameraPos) {
        if (Vec3.atCenterOf(controllerPos).distanceTo(cameraPos) - BnbServerSettings.cogwheelMaxBounds() > VIEW_DISTANCE)
            return true;
        final AABB localBounds = chain.getRenderBounds();
        if (localBounds == null)
            return true;
        final AABB bounds = localBounds.move(controllerPos);
        final Vec3 center = bounds.getCenter();
        final double radius = Math.sqrt(bounds.getXsize() * bounds.getXsize()
                + bounds.getYsize() * bounds.getYsize()
                + bounds.getZsize() * bounds.getZsize()) / 2;
        return center.distanceTo(cameraPos) - radius > VIEW_DISTANCE;
    }

    private record CachedMesh(int signature, CogwheelChainMesh mesh) {
    }
}
