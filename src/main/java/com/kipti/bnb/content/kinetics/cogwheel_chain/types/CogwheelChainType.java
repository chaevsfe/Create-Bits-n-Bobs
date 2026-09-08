package com.kipti.bnb.content.kinetics.cogwheel_chain.types;

import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.registry.core.BnbRegistries;
import com.kipti.bnb.registry.core.BnbResourceKeys;
import com.zurrtum.create.api.registry.SimpleRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class CogwheelChainType {

    public static final StreamCodec<RegistryFriendlyByteBuf, CogwheelChainType> STREAM_CODEC = ByteBufCodecs.registry(BnbResourceKeys.COGWHEEL_CHAIN_TYPE);

    public static final SimpleRegistry<Item, CogwheelChainType> COGWHEEL_TYPE_BY_ITEM = SimpleRegistry.create();

    static {
        COGWHEEL_TYPE_BY_ITEM
                .registerProvider((item) -> BnbRegistries.COGWHEEL_CHAIN_TYPES
                        .stream()
                        .filter(type -> type.isRelatedItem(item))
                        .findFirst()
                        .orElse(null));
    }

    public static final Identifier DEFAULT_CHAIN_TEXTURE_LOCATION = CreateBitsnBobs.asResource("textures/block/chain.png");

    public enum VertexShape {
        CROSS,
        SQUARE
    }

    //Todo: custom render types / just make this not an enum
    public enum ChainRenderInfo {
        CHAIN(VertexShape.CROSS, 3, 3, false),
        ROPE(VertexShape.SQUARE, 3, 3, false),
        BELT(VertexShape.SQUARE, 3, 2, true),
        ;

        private final VertexShape vertexShape;
        private final int width;
        private final int height;
        private final boolean consistentInsideOutside;

        ChainRenderInfo(final VertexShape vertexShape, final int width, final int height, final boolean consistentInsideOutside) {
            this.vertexShape = vertexShape;
            this.width = width;
            this.height = height;
            this.consistentInsideOutside = consistentInsideOutside;
        }

        public VertexShape getVertexShape() {
            return vertexShape;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public boolean isDefaultDimensions() {
            return width == 3 && height == 3;
        }

        public boolean usesConsistentInsideOutside() {
            return consistentInsideOutside;
        }
    }

    private final double costFactor;
    private final ChainRenderInfo chainRenderInfo;
    private final Identifier renderTexture;
    private final Predicate<Item> relatedItem;
    private final Predicate<Block> cogwheelPredicate;
    private final boolean permitsAxisChange;
    private final Supplier<Block> breakEffectsBlock;

    public CogwheelChainType(final float costFactor,
                             final ChainRenderInfo chainRenderInfo,
                             final Identifier renderTexture,
                             final Predicate<Item> relatedItem,
                             final Predicate<Block> cogwheelPredicate,
                             final boolean permitsAxisChange,
                             final Supplier<Block> breakEffectsBlock) {
        this.costFactor = costFactor;
        this.chainRenderInfo = chainRenderInfo;
        this.renderTexture = renderTexture;
        this.relatedItem = relatedItem;
        this.cogwheelPredicate = cogwheelPredicate;
        this.permitsAxisChange = permitsAxisChange;
        this.breakEffectsBlock = breakEffectsBlock;
    }

    public boolean alwaysCostsOneItem() {
        return this.costFactor == 0.0f;
    }

    public static class Builder {
        private float costFactor = 1.0f;
        private ChainRenderInfo chainRenderInfo = ChainRenderInfo.CHAIN;
        private Identifier renderTexture = DEFAULT_CHAIN_TEXTURE_LOCATION;
        private Predicate<Item> relatedItem = (item) -> item == Items.IRON_CHAIN;
        private Predicate<Block> cogwheelPredicate = (block) -> true;
        private boolean permitsAxisChange = true;
        private Supplier<Block> breakEffectsBlock = () -> Blocks.IRON_CHAIN;

        public Builder costFactor(final float costFactor) {
            this.costFactor = costFactor;
            return this;
        }

        public Builder renderType(final ChainRenderInfo chainRenderInfo) {
            this.chainRenderInfo = chainRenderInfo;
            return this;
        }

        public Builder renderTexture(final Identifier renderTexture) {
            this.renderTexture = renderTexture;
            return this;
        }

        public Builder relatedItem(final Supplier<Item> relatedItemSupplier) {
            this.relatedItem = (item) -> item == relatedItemSupplier.get();
            return this;
        }

        public Builder relatedTag(final TagKey<Item> itemTag) {
            this.relatedItem = (item) -> item.builtInRegistryHolder().is(itemTag);
            return this;
        }

        public Builder setCogwheelPredicate(final Predicate<Block> predicate) {
            this.cogwheelPredicate = predicate;
            return this;
        }

        public Builder permitsAxisChange(final boolean permitsAxisChange) {
            this.permitsAxisChange = permitsAxisChange;
            return this;
        }

        public Builder breakEffectsBlock(final Supplier<Block> breakEffectsBlock) {
            this.breakEffectsBlock = breakEffectsBlock;
            return this;
        }

        public CogwheelChainType build() {
            return new CogwheelChainType(costFactor, chainRenderInfo, renderTexture, relatedItem, cogwheelPredicate, permitsAxisChange, breakEffectsBlock);
        }
    }

    public Identifier getKey() {
        final Identifier key = BnbRegistries.COGWHEEL_CHAIN_TYPES.getKey(this);
        return key == null ? BnbRegistries.COGWHEEL_CHAIN_TYPES.getKey(BnbCogwheelChainTypes.CHAIN) : key;
    }

    public String getTranslationKey() {
        final Identifier key = getKey();
        return "cogwheel_chain_type." + key.getNamespace() + "." + key.getPath().replace("/", ".");
    }

    public double getCostFactor() {
        return this.costFactor;
    }

    public ChainRenderInfo getRenderType() {
        return chainRenderInfo;
    }

    public Identifier getRenderTexture() {
        return renderTexture;
    }

    private boolean isRelatedItem(final Item item) {
        return relatedItem.test(item);
    }

    public Predicate<Block> getCogwheelPredicate() {
        return cogwheelPredicate;
    }

    public boolean permitsAxisChanges() {
        return permitsAxisChange;
    }

    public Block getBreakEffectsBlock() {
        return breakEffectsBlock.get();
    }

}

