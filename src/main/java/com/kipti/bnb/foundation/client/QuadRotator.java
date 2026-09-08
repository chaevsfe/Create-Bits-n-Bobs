package com.kipti.bnb.foundation.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies a rigid transform to baked quads.
 * <p>
 * Azimuth's {@code QuadTransformer.transform} rewrote a quad's packed vertex array in place. A 26.2
 * {@link BakedQuad} is a record of four {@link Vector3fc} positions plus a face direction, so a transformed quad is
 * simply a new record with moved positions and a re-derived face; normals are no longer stored on the quad.
 */
@Environment(EnvType.CLIENT)
public final class QuadRotator {

    private QuadRotator() {
    }

    public static BakedQuad transform(BakedQuad quad, Matrix4fc transform) {
        Vector3fc p0 = move(quad.position0(), transform);
        Vector3fc p1 = move(quad.position1(), transform);
        Vector3fc p2 = move(quad.position2(), transform);
        Vector3fc p3 = move(quad.position3(), transform);
        return new BakedQuad(p0, p1, p2, p3,
                quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                rotate(quad.direction(), transform), quad.materialInfo());
    }

    public static List<BakedQuad> transform(List<BakedQuad> quads, Matrix4fc transform) {
        List<BakedQuad> out = new ArrayList<>(quads.size());
        for (BakedQuad quad : quads)
            out.add(transform(quad, transform));
        return out;
    }

    private static Vector3fc move(Vector3fc position, Matrix4fc transform) {
        Vector3f moved = new Vector3f();
        transform.transformPosition(position.x(), position.y(), position.z(), moved);
        return moved;
    }

    private static Direction rotate(Direction face, Matrix4fc transform) {
        Vector3f normal = new Vector3f(face.getStepX(), face.getStepY(), face.getStepZ());
        transform.transformDirection(normal);
        return Direction.getApproximateNearest(normal.x(), normal.y(), normal.z());
    }

    /**
     * A part whose quads come back transformed. The transformed quads no longer belong to the cull face they were
     * collected under, so every quad is served unculled.
     */
    public static BlockStateModelPart transformed(BlockStateModelPart part, Matrix4fc transform) {
        return new TransformedPart(part, transform);
    }

    private record TransformedPart(BlockStateModelPart delegate, Matrix4fc transform) implements BlockStateModelPart {

        @Override
        public List<BakedQuad> getQuads(@Nullable Direction side) {
            if (side != null)
                return List.of();
            List<BakedQuad> quads = new ArrayList<>(delegate.getQuads(null));
            for (Direction cull : Direction.values())
                quads.addAll(delegate.getQuads(cull));
            return QuadRotator.transform(quads, transform);
        }

        @Override
        public boolean useAmbientOcclusion() {
            return delegate.useAmbientOcclusion();
        }

        @Override
        public Material.Baked particleMaterial() {
            return delegate.particleMaterial();
        }

        @Override
        public int materialFlags() {
            return delegate.materialFlags();
        }
    }
}
