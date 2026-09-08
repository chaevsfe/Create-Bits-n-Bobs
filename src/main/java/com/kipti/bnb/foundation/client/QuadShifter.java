package com.kipti.bnb.foundation.client;

import com.zurrtum.create.client.catnip.render.SpriteShiftEntry;
import com.zurrtum.create.client.foundation.model.BakedModelHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Re-points baked quads at a sprite-shifted texture.
 * <p>
 * This is the port's replacement for Azimuth's {@code QuadTransformer}. A 26.2 {@link BakedQuad} is a record with
 * four packed UV longs instead of a raw vertex array, so a shifted quad is rebuilt through Create Fly's
 * {@code BakedModelHelper}: {@code calcSpriteUv} moves one packed UV from the original sprite's frame into the
 * target's, and {@code replaceBakedQuadUV} makes the new quad.
 * <p>
 * Quads are matched to a shift entry the way upstream matched them: by sprite identity first and, failing that, by
 * whether every vertex UV lies inside the original sprite. The second test is what makes this work after a
 * connected-texture pass, which leaves quads pointing at a sheet rather than at the sprite they were baked with.
 */
@Environment(EnvType.CLIENT)
public final class QuadShifter {

    private static final float UV_EPSILON = 1.0E-4f;

    private QuadShifter() {
    }

    public static boolean uvWithinSprite(BakedQuad quad, TextureAtlasSprite sprite) {
        for (int vertex = 0; vertex < BakedQuad.VERTEX_COUNT; vertex++) {
            long packed = quad.packedUV(vertex);
            float u = UVPair.unpackU(packed);
            float v = UVPair.unpackV(packed);
            if (u < sprite.getU0() - UV_EPSILON || u > sprite.getU1() + UV_EPSILON
                    || v < sprite.getV0() - UV_EPSILON || v > sprite.getV1() + UV_EPSILON)
                return false;
        }
        return true;
    }

    public static boolean matches(BakedQuad quad, SpriteShiftEntry shift) {
        TextureAtlasSprite original = shift.getOriginal();
        if (original == null)
            return false;
        return quad.materialInfo().sprite() == original || uvWithinSprite(quad, original);
    }

    @Nullable
    public static SpriteShiftEntry findShift(BakedQuad quad, Iterable<? extends SpriteShiftEntry> shifts) {
        for (SpriteShiftEntry shift : shifts) {
            if (shift != null && matches(quad, shift))
                return shift;
        }
        return null;
    }

    public static BakedQuad shift(BakedQuad quad, SpriteShiftEntry shift) {
        TextureAtlasSprite original = shift.getOriginal();
        TextureAtlasSprite target = shift.getTarget();
        if (original == null || target == null)
            return quad;

        BakedQuad.MaterialInfo info = quad.materialInfo();
        BakedQuad.MaterialInfo shifted = new BakedQuad.MaterialInfo(
                target, info.layer(), info.itemRenderType(), info.tintIndex(), info.shade(), info.lightEmission());

        return BakedModelHelper.replaceBakedQuadUV(
                quad,
                BakedModelHelper.calcSpriteUv(quad.packedUV0(), original, target),
                BakedModelHelper.calcSpriteUv(quad.packedUV1(), original, target),
                BakedModelHelper.calcSpriteUv(quad.packedUV2(), original, target),
                BakedModelHelper.calcSpriteUv(quad.packedUV3(), original, target),
                shifted);
    }

    public static List<BakedQuad> shiftQuads(List<BakedQuad> quads, Function<BakedQuad, SpriteShiftEntry> resolver) {
        List<BakedQuad> out = null;
        for (int i = 0; i < quads.size(); i++) {
            BakedQuad quad = quads.get(i);
            SpriteShiftEntry shift = resolver.apply(quad);
            if (shift == null)
                continue;
            BakedQuad replacement = shift(quad, shift);
            if (replacement == quad)
                continue;
            if (out == null)
                out = new ArrayList<>(quads);
            out.set(i, replacement);
        }
        return out == null ? quads : out;
    }

    /**
     * Wraps every part in {@code parts} (from {@code index} onwards) so its quads come back sprite-shifted.
     */
    public static void shiftParts(List<BlockStateModelPart> parts, int from, Function<BakedQuad, SpriteShiftEntry> resolver) {
        for (int i = from; i < parts.size(); i++)
            parts.set(i, new ShiftedPart(parts.get(i), resolver));
    }

    private record ShiftedPart(BlockStateModelPart delegate, Function<BakedQuad, SpriteShiftEntry> resolver)
            implements BlockStateModelPart {

        @Override
        public List<BakedQuad> getQuads(@Nullable Direction side) {
            return shiftQuads(delegate.getQuads(side), resolver);
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
