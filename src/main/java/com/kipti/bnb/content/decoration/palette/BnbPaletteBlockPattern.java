package com.kipti.bnb.content.decoration.palette;

import com.kipti.bnb.CreateBitsnBobs;
import com.zurrtum.create.content.decoration.palettes.ConnectedPillarBlock;
import com.zurrtum.create.client.foundation.block.connected.AllCTTypes;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShiftEntry;
import com.zurrtum.create.client.foundation.block.connected.CTSpriteShifter;
import com.zurrtum.create.client.foundation.block.connected.CTType;
import com.zurrtum.create.client.foundation.block.connected.ConnectedTextureBehaviour;
import com.kipti.bnb.registrate.fn.NonNullBiConsumer;
import com.kipti.bnb.registrate.fn.NonNullFunction;
import com.kipti.bnb.registrate.fn.NonNullSupplier;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * thank you for your mit licence, this code is mine now >:)
 */
public class BnbPaletteBlockPattern {

    public static final BnbPaletteBlockPattern
            TILES = create("tiles", BnbPatternNameType.SUFFIX, BnbPaletteBlockPartial.ALL_PARTIALS_TILE)
            ;

    public enum BnbPatternNameType {
        PREFIX, SUFFIX, WRAP
    }

    public static final BnbPaletteBlockPattern[] ADDITIONS_TO_BASE = {TILES};

    static final String TEXTURE_LOCATION = "block/palettes/stone_types/%s/%s";

    private BnbPatternNameType nameType;
    private String[] textures;
    private String id;
    private boolean isTranslucent;
    private TagKey<Block>[] blockTags;
    private TagKey<Item>[] itemTags;
    private Optional<Function<String, ConnectedTextureBehaviour>> ctFactory;

    private NonNullFunction<BlockBehaviour.Properties, ? extends Block> blockFactory;
    private BnbPaletteBlockPartial<? extends Block>[] partials;

    @Environment(EnvType.CLIENT)
    private RenderType renderType;

    private static BnbPaletteBlockPattern create(final String name, final BnbPatternNameType nameType,
                                                 final BnbPaletteBlockPartial<?>... partials) {
        final BnbPaletteBlockPattern pattern = new BnbPaletteBlockPattern();
        pattern.id = name;
        pattern.ctFactory = Optional.empty();
        pattern.nameType = nameType;
        pattern.partials = partials;
        pattern.isTranslucent = false;
        pattern.blockFactory = Block::new;
        pattern.textures = new String[]{name};
        return pattern;
    }


    public boolean isTranslucent() {
        return isTranslucent;
    }

    public TagKey<Block>[] getBlockTags() {
        return blockTags;
    }

    public TagKey<Item>[] getItemTags() {
        return itemTags;
    }

    public NonNullFunction<BlockBehaviour.Properties, ? extends Block> getBlockFactory() {
        return blockFactory;
    }

    public BnbPaletteBlockPartial<? extends Block>[] getPartials() {
        return partials;
    }

    public String getTexture(final int index) {
        return textures[index];
    }


    public Optional<Supplier<ConnectedTextureBehaviour>> createCTBehaviour(final String variant) {
        return ctFactory.map(d -> () -> d.apply(variant));
    }

    // Builder


    private BnbPaletteBlockPattern textures(final String... textures) {
        this.textures = textures;
        return this;
    }

    private BnbPaletteBlockPattern block(final NonNullFunction<BlockBehaviour.Properties, ? extends Block> blockFactory) {
        this.blockFactory = blockFactory;
        return this;
    }

    private BnbPaletteBlockPattern connectedTextures(final Function<String, ConnectedTextureBehaviour> factory) {
        this.ctFactory = Optional.of(factory);
        return this;
    }

    // Model generators






    // Utility

    protected String createName(final String variant) {
        if (nameType == BnbPatternNameType.WRAP) {
            final String[] split = id.split("_");
            if (split.length == 2) {
                final String formatString = "%s_%s_%s";
                return String.format(formatString, split[0], variant, split[1]);
            }
        }
        final String formatString = "%s_%s";
        return nameType == BnbPatternNameType.SUFFIX ? String.format(formatString, variant, id) : String.format(formatString, id, variant);
    }

    protected static Identifier toLocation(final String variant, final String texture) {
        return CreateBitsnBobs.asResource(
                String.format(TEXTURE_LOCATION, texture, variant + (texture.equals("cut") ? "_" : "_cut_") + texture));
    }

    protected static CTSpriteShiftEntry ct(final String variant, final CTs texture) {
        final Identifier resLoc = texture.srcFactory.apply(variant);
        final Identifier resLocTarget = texture.targetFactory.apply(variant);
        return CTSpriteShifter.getCT(texture.type, resLoc,
                Identifier.fromNamespaceAndPath(resLocTarget.getNamespace(), resLocTarget.getPath() + "_connected"));
    }



    enum PatternNameType {
        PREFIX, SUFFIX, WRAP
    }

    // Textures with connectability, used by Spriteshifter

    public enum CTs {

        PILLAR(AllCTTypes.RECTANGLE, s -> toLocation(s, "pillar")),
        CAP(AllCTTypes.OMNIDIRECTIONAL, s -> toLocation(s, "cap")),
        LAYERED(AllCTTypes.HORIZONTAL_KRYPPERS, s -> toLocation(s, "layered"));

        public CTType type;
        private final Function<String, Identifier> srcFactory;
        private final Function<String, Identifier> targetFactory;

        CTs(final CTType type, final Function<String, Identifier> factory) {
            this(type, factory, factory);
        }

        CTs(final CTType type, final Function<String, Identifier> srcFactory,
            final Function<String, Identifier> targetFactory) {
            this.type = type;
            this.srcFactory = srcFactory;
            this.targetFactory = targetFactory;
        }

    }
}

