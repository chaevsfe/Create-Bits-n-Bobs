package com.kipti.bnb.content.decoration.cogwheel_material;

import com.kipti.bnb.foundation.client.ModelSprites;
import com.kipti.bnb.registry.client.BnbPartialModels;
import com.kipti.bnb.registry.core.BnbTags;
import com.zurrtum.create.catnip.registry.RegisteredObjectsHelper;
import com.zurrtum.create.client.AllPartialModels;
import com.zurrtum.create.client.catnip.render.StitchedSprite;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import com.zurrtum.create.client.foundation.model.BakedModelHelper;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Builds a cogwheel model whose spruce template textures are replaced with the planks and stripped log of another
 * wood.
 * <p>
 * Upstream cloned every quad by hand because NeoForge's {@code BakedModel} exposed them directly. Create Fly ships
 * {@link BakedModelHelper#generateModel(BlockStateModel, UnaryOperator)}, which does exactly the same swap over
 * 26.2's {@code BlockStateModel}, so the quad-cloning half is gone.
 */
@Environment(EnvType.CLIENT)
public class CogwheelMaterialRenderer {

    public static final StitchedSprite SPRUCE_PLANKS_TEMPLATE = new StitchedSprite(Identifier.withDefaultNamespace("block/spruce_planks"));
    public static final StitchedSprite STRIPPED_SPRUCE_LOG_TEMPLATE = new StitchedSprite(Identifier.withDefaultNamespace("block/stripped_spruce_log"));
    public static final StitchedSprite STRIPPED_SPRUCE_LOG_TOP_TEMPLATE = new StitchedSprite(Identifier.withDefaultNamespace("block/stripped_spruce_log_top"));

    public static BlockStateModel generateModel(final CogwheelMaterialRenderer.Variant variant, final BlockState material) {
        return generateModel(variant.model(), material);
    }

    public static BlockStateModel generateModel(final BlockStateModel template, final BlockState planksBlockState) {
        final Block planksBlock = planksBlockState.getBlock();
        final Identifier id = RegisteredObjectsHelper.getKeyOrThrow(planksBlock);
        final String wood = plankStateToWoodName(planksBlockState);

        if (wood == null)
            return BakedModelHelper.generateModel(template, sprite -> null);

        final String namespace = id.getNamespace();
        final BlockState strippedLogBlockState = getStrippedLogBlockState(namespace, wood);

        final Map<TextureAtlasSprite, TextureAtlasSprite> map = new Reference2ReferenceOpenHashMap<>();
        map.put(SPRUCE_PLANKS_TEMPLATE.get(), ModelSprites.getSpriteOnSide(planksBlockState, Direction.UP));
        map.put(STRIPPED_SPRUCE_LOG_TEMPLATE.get(), ModelSprites.getSpriteOnSide(strippedLogBlockState, Direction.SOUTH));
        map.put(STRIPPED_SPRUCE_LOG_TOP_TEMPLATE.get(), ModelSprites.getSpriteOnSide(strippedLogBlockState, Direction.UP));

        return BakedModelHelper.generateModel(template, map::get);
    }

    private static final String[] STRIPPED_LOG_LOCATIONS = new String[]{

            "stripped_x_log", "stripped_x_stem", "stripped_x_block",
            "wood/stripped_log/x"

    };

    private static BlockState getStrippedLogBlockState(final String namespace, final String wood) {
        for (final String location : STRIPPED_LOG_LOCATIONS) {
            final Optional<BlockState> state =
                    BuiltInRegistries.BLOCK.getOptional(Identifier.fromNamespaceAndPath(namespace, location.replace("x", wood)))
                            .map(Block::defaultBlockState);
            if (state.isPresent())
                return state.get();
        }
        return Blocks.OAK_LOG.defaultBlockState();
    }

    @Nullable
    private static String plankStateToWoodName(final BlockState planksBlockState) {
        final Identifier id = RegisteredObjectsHelper.getKeyOrThrow(planksBlockState.getBlock());
        final String path = id.getPath();

        if (path.endsWith("_planks"))
            return (path.startsWith("archwood") ? "blue_" : "") + path.substring(0, path.length() - 7);

        if (path.contains("wood/planks/"))
            return path.substring(12);

        return null;
    }

    @Nullable
    public static Variant getVariant(final BlockState blockState) {
        for (final Variant variant : Variant.values()) {
            if (variant.matches(blockState))
                return variant;
        }
        return null;
    }

    public static void init() {
    }

    public enum Variant {
        COGWHEEL(BnbTags.BnbBlockTags.COGWHEEL_MATERIAL_COGWHEEL_MODEL.tag, () -> AllPartialModels.COGWHEEL),
        SHAFTLESS_COGWHEEL(BnbTags.BnbBlockTags.COGWHEEL_MATERIAL_SHAFTLESS_COGWHEEL_MODEL.tag, () -> AllPartialModels.SHAFTLESS_COGWHEEL),
        SHAFTLESS_LARGE_COGWHEEL(BnbTags.BnbBlockTags.COGWHEEL_MATERIAL_SHAFTLESS_LARGE_COGWHEEL_MODEL.tag, () -> AllPartialModels.SHAFTLESS_LARGE_COGWHEEL),
        FLANGED_COGWHEEL(BnbTags.BnbBlockTags.COGWHEEL_MATERIAL_FLANGED_COGWHEEL_MODEL.tag, () -> BnbPartialModels.SMALL_FLANGED_COGWHEEL_BLOCK),
        LARGE_FLANGED_COGWHEEL(BnbTags.BnbBlockTags.COGWHEEL_MATERIAL_LARGE_FLANGED_COGWHEEL_MODEL.tag, () -> BnbPartialModels.LARGE_FLANGED_COGWHEEL_BLOCK),
        ENCASED_FLANGED_COGWHEEL(BnbTags.BnbBlockTags.COGWHEEL_MATERIAL_ENCASED_FLANGED_COGWHEEL_MODEL.tag, () -> BnbPartialModels.ENCASED_FLANGED_COGWHEEL_BLOCK),
        ENCASED_LARGE_FLANGED_COGWHEEL(BnbTags.BnbBlockTags.COGWHEEL_MATERIAL_ENCASED_LARGE_FLANGED_COGWHEEL_MODEL.tag, () -> BnbPartialModels.ENCASED_LARGE_FLANGED_COGWHEEL_BLOCK);

        private final @Nullable TagKey<Block> tag;
        private final Supplier<PartialModel> partial;

        Variant(@Nullable final TagKey<Block> tag, final Supplier<PartialModel> partial) {
            this.tag = tag;
            this.partial = partial;
        }

        private boolean matches(final BlockState state) {
            return this.tag != null && state.is(this.tag);
        }

        public BlockStateModel model() {
            return this.partial.get()
                    .get();
        }

        public PartialModel partialModel() {
            return this.partial.get();
        }
    }

}
