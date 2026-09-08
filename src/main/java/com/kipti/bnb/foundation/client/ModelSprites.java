package com.kipti.bnb.foundation.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads the texture a baked block model uses on one face.
 * <p>
 * On 26.2 a {@link BlockStateModel} no longer answers {@code getQuads}; it collects
 * {@link BlockStateModelPart}s, and each part answers per-cull-face quad lists. The lookup order is the same as it
 * was: the quads culled to the requested face first, then the unculled quads whose own direction matches, and the
 * model's particle sprite as a last resort.
 */
@Environment(EnvType.CLIENT)
public final class ModelSprites {

    private ModelSprites() {
    }

    public static List<BlockStateModelPart> collectParts(BlockStateModel model, long seed) {
        RandomSource random = RandomSource.create();
        random.setSeed(seed);
        List<BlockStateModelPart> parts = new ArrayList<>();
        model.collectParts(random, parts);
        return parts;
    }

    @Nullable
    public static TextureAtlasSprite getSpriteOnSide(BlockState state, Direction side) {
        BlockStateModel model = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state);
        if (model == null)
            return null;
        return getSpriteOnSide(model, side);
    }

    @Nullable
    public static TextureAtlasSprite getSpriteOnSide(BlockStateModel model, Direction side) {
        List<BlockStateModelPart> parts = collectParts(model, 42L);

        for (BlockStateModelPart part : parts) {
            List<BakedQuad> quads = part.getQuads(side);
            if (!quads.isEmpty())
                return quads.getFirst().materialInfo().sprite();
        }

        for (BlockStateModelPart part : parts) {
            for (BakedQuad quad : part.getQuads(null)) {
                if (quad.direction() == side)
                    return quad.materialInfo().sprite();
            }
        }

        return model.particleMaterial().sprite();
    }
}
