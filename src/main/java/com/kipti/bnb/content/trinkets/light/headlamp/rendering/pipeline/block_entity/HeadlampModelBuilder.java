package com.kipti.bnb.content.trinkets.light.headlamp.rendering.pipeline.block_entity;

import com.kipti.bnb.content.trinkets.light.headlamp.CCLightAddressing;
import com.kipti.bnb.content.trinkets.light.headlamp.HeadlampBlockEntity;
import com.kipti.bnb.content.trinkets.light.headlamp.rendering.HeadlampConstants;
import com.kipti.bnb.foundation.client.ModelSprites;
import com.kipti.bnb.foundation.client.QuadRotator;
import com.kipti.bnb.registry.client.BnbPartialModels;
import com.zurrtum.create.client.foundation.model.BakedModelHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a headlamp block's geometry from its packed render state.
 * <p>
 * Upstream wrote vertices straight into a {@code BufferBuilder}, reading them out of NeoForge's packed quad arrays.
 * 26.2 quads are records and Create Fly builds its buffers from a model, so the same work is done as a quad list:
 * each lamp's partial model is moved into place and its sprite swapped for the dyed one.
 * <p>
 * The geometry is built facing up with no rotation, so one buffer serves headlamps on any face.
 */
@Environment(EnvType.CLIENT)
public class HeadlampModelBuilder {

    public static List<BakedQuad> buildHeadlampGeometry(final long renderState) {
        final int onOffBits = (int) (renderState & 0xFL);
        final HeadlampBlockEntity.HeadlampPlacement[] allPlacements = HeadlampBlockEntity.HeadlampPlacement.values();
        final List<BakedQuad> out = new ArrayList<>();

        for (int i = 0; i < HeadlampConstants.PLACEMENT_COUNT; i++) {
            final int placementValue = (int) ((renderState >> (HeadlampConstants.RENDER_STATE_ON_OFF_BITS + i * HeadlampConstants.RENDER_STATE_SLOT_BITS)) & HeadlampConstants.SLOT_VALUE_MASK);
            if (placementValue == 0)
                continue;

            final HeadlampBlockEntity.HeadlampPlacement placement = allPlacements[i];
            final boolean shouldDisplayOn = getLightOnOffState(onOffBits, placement);

            final Matrix4f transform = new Matrix4f().translation(
                    (float) placement.horizontalAlignment().getOffset(),
                    0.0f,
                    (float) placement.verticalAlignment().getOffset());

            @Nullable final DyeColor color = placementValue == 1 ? null
                    : DyeColor.values()[Math.clamp(placementValue - HeadlampConstants.DYE_COLOR_OFFSET, 0, DyeColor.values().length - 1)];

            final List<BlockStateModelPart> parts = ModelSprites.collectParts(
                    (shouldDisplayOn ? BnbPartialModels.HEADLAMP_ON : BnbPartialModels.HEADLAMP_OFF).get(), 42L);

            for (final BlockStateModelPart part : parts) {
                final List<BakedQuad> quads = new ArrayList<>(part.getQuads(null));
                for (final net.minecraft.core.Direction cull : net.minecraft.core.Direction.values())
                    quads.addAll(part.getQuads(cull));

                List<BakedQuad> tinted = color == null ? quads
                        : BakedModelHelper.swapSprites(quads, sprite -> tint(sprite, color));
                out.addAll(QuadRotator.transform(tinted, transform));
            }
        }
        return out;
    }

    @Nullable
    private static TextureAtlasSprite tint(final TextureAtlasSprite sprite, final DyeColor color) {
        final TextureAtlasSprite tinted = HeadlampRenderCache.getTintedSprite(sprite, color);
        return tinted == sprite ? null : tinted;
    }

    private static boolean getLightOnOffState(final int onOffBits, final HeadlampBlockEntity.HeadlampPlacement placement) {
        final Vector2i coord = CCLightAddressing.getLocalMaskCoordinateForPlacement(placement);
        return CCLightAddressing.getMaskValue((byte) onOffBits, coord);
    }
}
