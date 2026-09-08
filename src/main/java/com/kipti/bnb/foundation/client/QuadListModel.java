package com.kipti.bnb.foundation.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A one-part {@link BlockStateModel} over a fixed quad list.
 * <p>
 * Create Fly builds a {@code SuperByteBuffer} from a {@code BlockStateModel}, so code that used to write vertices
 * into a {@code BufferBuilder} by hand hands its quads over through this instead.
 */
@Environment(EnvType.CLIENT)
public record QuadListModel(List<BakedQuad> quads, Material.Baked particle, int materialFlags)
        implements BlockStateModel, BlockStateModelPart {

    public static QuadListModel of(List<BakedQuad> quads, Material.Baked particle) {
        int flags = 0;
        for (BakedQuad quad : quads)
            flags |= quad.materialInfo().flags();
        return new QuadListModel(quads, particle, flags);
    }

    @Override
    public void collectParts(RandomSource random, List<BlockStateModelPart> parts) {
        parts.add(this);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable Direction side) {
        return side == null ? quads : List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public Material.Baked particleMaterial() {
        return particle;
    }
}
