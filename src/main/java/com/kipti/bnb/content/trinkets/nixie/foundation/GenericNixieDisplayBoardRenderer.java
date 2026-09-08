package com.kipti.bnb.content.trinkets.nixie.foundation;

import com.kipti.bnb.content.trinkets.nixie.large_nixie_tube.LargeNixieTubeBlockNixie;
import com.kipti.bnb.content.trinkets.nixie.nixie_board.NixieBoardBlockNixie;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zurrtum.create.catnip.data.Couple;
import com.zurrtum.create.client.flywheel.lib.transform.TransformStack;
import com.zurrtum.create.client.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.zurrtum.create.client.foundation.utility.DyeHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import com.kipti.bnb.mixin_accessor.FontAccess;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws a nixie display's text.
 * <p>
 * The 1.21.1 version reached into {@code Font}'s private {@code FontSet} through an accessor mixin and called
 * {@code BakedGlyph.render}. Neither exists on 26.2: {@code Font.getGlyphSource(FontDescription)} is public, and a
 * {@link BakedGlyph} is now a factory that makes a {@link TextRenderable} for one placed glyph. Text is therefore
 * built during render-state extraction and handed to the collector as custom geometry, like Create Fly's own nixie
 * tube renderer does.
 * <p>
 * The back face used to need a mixin on {@code BakedGlyph} because font render types ignore normals. It is drawn
 * here by submitting the same glyph a second time under a pose that is turned around, which needs no mixin.
 */
@Environment(EnvType.CLIENT)
public class GenericNixieDisplayBoardRenderer
        extends SmartBlockEntityRenderer<GenericNixieDisplayBlockEntity, GenericNixieDisplayBoardRenderer.NixieDisplayRenderState> {

    public GenericNixieDisplayBoardRenderer(final BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public NixieDisplayRenderState createRenderState() {
        return new NixieDisplayRenderState();
    }

    @Override
    public void extractRenderState(
            GenericNixieDisplayBlockEntity be,
            NixieDisplayRenderState state,
            float partialTicks,
            Vec3 cameraPos,
            @Nullable CrumblingOverlay crumblingOverlay
    ) {
        super.extractRenderState(be, state, partialTicks, cameraPos, crumblingOverlay);
        state.glyphs.clear();
        state.atlasQuads.clear();

        Font font = Minecraft.getInstance().font;
        GlyphSource glyphSource = ((FontAccess) font).bits_n_bobs$getGlyphSource(Style.EMPTY.getFont());
        if (glyphSource == null)
            return;

        boolean isNixieBoard = GenericNixieDisplayBlock.isNixieBoard(be.getBlockState().getBlock());
        boolean isLargeNixieTube = be.getBlockState().getBlock() instanceof LargeNixieTubeBlockNixie;

        PoseStack ms = new PoseStack();
        Direction facing = be.getBlockState().getValue(NixieBoardBlockNixie.FACING);
        Direction orientation = be.getBlockState().getValue(NixieBoardBlockNixie.ORIENTATION);

        float orientationOffset;
        if (be.currentDisplayOption != GenericNixieDisplayBlockEntity.ConfigurableDisplayOptions.ALWAYS_UP && facing != Direction.UP) {
            TransformStack.of(ms)
                    .center()
                    .mulPose(DoubleOrientedBlockModel.getRotation(facing, orientation))
                    .uncenter();
            orientationOffset = isLargeNixieTube ? -2 / 16f : 0;
        } else {
            TransformStack.of(ms)
                    .center()
                    .translate(Vec3.atLowerCornerOf(facing.getUnitVec3i()).scale(2 / 16f))
                    .mulPose(DoubleOrientedBlockModel.getRotation(Direction.UP, orientation))
                    .uncenter();
            orientationOffset = -4 / 16f;
        }

        ms.translate(0.5, 1f + orientationOffset, 0.5);
        ms.scale(1 / 16f, 1 / 16f, 1 / 16f);
        if (isLargeNixieTube)
            ms.scale(0.85f, 0.85f, 0.85f);
        ms.scale(-1, -1, +1);

        Couple<Integer> baseColor = DyeHelper.getDyeColors(
                be.getBlockState().getBlock() instanceof final DyeProviderBlock dyeProviderBlock
                        ? dyeProviderBlock.getDyeColor() : DyeColor.ORANGE);

        ConfigurableDisplayOptionTransform transform = ConfigurableDisplayOptionTransform.valueOf(be.getCurrentDisplayOption().name());
        TextBlockSubAtlas subAtlas = transform.isMoreThanOneCharacter()
                ? TextBlockSubAtlas.SMALL_NIXIE_TEXT_SUB_ATLAS
                : TextBlockSubAtlas.NIXIE_TEXT_SUB_ATLAS;

        transform.render(ms, be, glyph -> {
            if (glyph == ' ')
                return;
            int charCode = glyph;
            Couple<Integer> color = subAtlas.isInColorExcludedCharacterSet(charCode)
                    ? DyeHelper.getDyeColors(DyeColor.WHITE) : baseColor;
            if (subAtlas.isInCharacterSet(charCode))
                extractAtlasGlyph(state, charCode, new Matrix4f(ms.last().pose()), color);
            else
                extractFontGlyph(state, font, glyphSource, charCode, new Matrix4f(ms.last().pose()), color);
        });
    }

    private static void extractAtlasGlyph(NixieDisplayRenderState state, int glyph, Matrix4f pose, Couple<Integer> color) {
        TextBlockSubAtlas.Uv uv = TextBlockSubAtlas.NIXIE_TEXT_SUB_ATLAS.getUvForCharacter(glyph);
        Matrix4f front = new Matrix4f(pose).translate(-6, -3, 0);
        state.atlasQuads.add(new AtlasQuad(front, uv, ARGB.opaque(color.get(true))));
        state.atlasQuads.add(new AtlasQuad(new Matrix4f(front).translate(0.5f, 0.5f, 0.1f), uv,
                ARGB.opaque(color.get(false))));
    }

    private static void extractFontGlyph(
            NixieDisplayRenderState state, Font font, GlyphSource glyphSource, int glyph, Matrix4f pose, Couple<Integer> color) {
        BakedGlyph baked = glyphSource.getGlyph(glyph);
        if (baked == null)
            return;
        float width = baked.info().getAdvance(false) - 1;

        addGlyph(state, baked, new Matrix4f(pose), -width / 2, 0, ARGB.opaque(color.get(true)));
        addGlyph(state, baked, new Matrix4f(pose).translate(0, 0, 0.1f), -width / 2 + 0.5f, 0.5f,
                ARGB.opaque(color.get(false)));
    }

    private static void addGlyph(NixieDisplayRenderState state, BakedGlyph baked, Matrix4f pose, float x, float y, int color) {
        TextRenderable.Styled renderable = baked.createGlyph(x, y, color, 0, Style.EMPTY, 0, 0);
        RenderType layer = renderable.renderType(Font.DisplayMode.NORMAL);
        state.glyphs.add(new GlyphDraw(layer, pose, renderable));
        // Font render types ignore the normal, so the reverse face is a second draw under a mirrored pose.
        state.glyphs.add(new GlyphDraw(layer, new Matrix4f(pose).scale(-1, 1, 1), renderable));
    }

    @Override
    public void submit(
            NixieDisplayRenderState state,
            PoseStack matrices,
            SubmitNodeCollector queue,
            CameraRenderState cameraState
    ) {
        super.submit(state, matrices, queue, cameraState);

        for (AtlasQuad quad : state.atlasQuads)
            queue.submitCustomGeometry(matrices, RenderTypes.cutoutMovingBlock(), quad);
        for (GlyphDraw glyph : state.glyphs)
            queue.submitCustomGeometry(matrices, glyph.layer(), glyph);
    }

    private record GlyphDraw(RenderType layer, Matrix4fc pose, TextRenderable renderable)
            implements SubmitNodeCollector.CustomGeometryRenderer {

        @Override
        public void render(PoseStack.Pose parent, VertexConsumer consumer) {
            renderable.render(new Matrix4f(parent.pose()).mul(pose), consumer, LightCoordsUtil.FULL_BRIGHT, false);
        }
    }

    private record AtlasQuad(Matrix4fc pose, TextBlockSubAtlas.Uv uv, int color)
            implements SubmitNodeCollector.CustomGeometryRenderer {

        @Override
        public void render(PoseStack.Pose parent, VertexConsumer consumer) {
            Matrix4f matrix = new Matrix4f(parent.pose()).mul(pose);
            float u0 = uv.getU0();
            float u1 = uv.getU1();
            float v0 = uv.getV0();
            float v1 = uv.getV1();

            vertex(consumer, matrix, 0, 12, u0, v1);
            vertex(consumer, matrix, 0, 0, u0, v0);
            vertex(consumer, matrix, 12, 0, u1, v0);
            vertex(consumer, matrix, 12, 12, u1, v1);

            vertex(consumer, matrix, 12, 12, u1, v1);
            vertex(consumer, matrix, 12, 0, u1, v0);
            vertex(consumer, matrix, 0, 0, u0, v0);
            vertex(consumer, matrix, 0, 12, u0, v1);
        }

        private void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float u, float v) {
            consumer.addVertex(matrix, x, y, 0)
                    .setColor(color)
                    .setUv(u, v)
                    .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
                    .setLight(LightCoordsUtil.FULL_BRIGHT)
                    .setNormal(1, 0, 0);
        }
    }

    public static class NixieDisplayRenderState extends SmartBlockEntityRenderer.SmartRenderState {
        public final List<GlyphDraw> glyphs = new ArrayList<>();
        public final List<AtlasQuad> atlasQuads = new ArrayList<>();
    }
}
