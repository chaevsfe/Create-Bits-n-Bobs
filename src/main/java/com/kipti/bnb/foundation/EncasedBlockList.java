package com.kipti.bnb.foundation;

import com.kipti.bnb.CreateBitsnBobs;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.Create;
import com.kipti.bnb.registrate.builders.BlockBuilder;
import com.kipti.bnb.registrate.entry.BlockEntry;
import com.kipti.bnb.registrate.fn.NonNullUnaryOperator;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Repurposed from {@link com.simibubi.create.foundation.block.DyedBlockList}
 */
public class EncasedBlockList<T extends Block> implements Iterable<BlockEntry<T>> {

    public enum CasingMaterial {
        ANDESITE("andesite", () -> AllBlocks.ANDESITE_CASING, "andesite_casing", "gearbox", true),
        BRASS("brass", () -> AllBlocks.BRASS_CASING, "brass_casing", "brass_gearbox", true),
        INDUSTRIAL_IRON("industrial_iron", () -> AllBlocks.INDUSTRIAL_IRON_BLOCK, "industrial_iron_block", "industrial_iron_gearbox"),
        WEATHERED_IRON("weathered_iron", () -> AllBlocks.WEATHERED_IRON_BLOCK, "weathered_iron_block", "weathered_iron_gearbox"),
        ;

        private final String resourceName;
        private final Supplier<? extends Block> material;

        private final Identifier surfaceTexture;
        private final Identifier gearboxTexture;

        CasingMaterial(final String resourceName, final Supplier<? extends Block> material, final String surfaceTexture, final String gearboxTexture) {
            this(resourceName, material, surfaceTexture, gearboxTexture, false);
        }

        CasingMaterial(final String resourceName, final Supplier<? extends Block> material, final String surfaceTexture, final String gearboxTexture, final boolean isCreateNamespace) {
            this.resourceName = resourceName;
            this.material = material;
            this.surfaceTexture = isCreateNamespace ? Identifier.fromNamespaceAndPath("create", "block/" + surfaceTexture) : CreateBitsnBobs.asResource("block/" + surfaceTexture);
            this.gearboxTexture = isCreateNamespace ? Identifier.fromNamespaceAndPath("create", "block/" + gearboxTexture) : CreateBitsnBobs.asResource("block/" + gearboxTexture);
        }

        public String getResourceName() {
            return this.resourceName;
        }


        public String asId(final String blockId) {
            return this.name().toLowerCase(Locale.ROOT) + "_" + blockId;
        }

        public Supplier<Block> getMaterial() {
            return () -> (Block) this.material.get();
        }

        public Identifier getSurfaceTexture() {
            return this.surfaceTexture;
        }

        public Identifier getGearboxTexture() {
            return this.gearboxTexture;
        }


    }

    private final BlockEntry<?>[] values = new BlockEntry<?>[CasingMaterial.values().length];

    public EncasedBlockList(final Function<CasingMaterial, BlockEntry<? extends T>> filler) {
        for (final CasingMaterial casing : CasingMaterial.values()) {
            this.values[casing.ordinal()] = filler.apply(casing);
        }
    }

    @SuppressWarnings("unchecked")
    public BlockEntry<T> get(final CasingMaterial material) {
        return (BlockEntry<T>) this.values[material.ordinal()];
    }

    public boolean contains(final Block block) {
        for (final BlockEntry<?> entry : this.values) {
            if (entry.is(block)) {
                return true;
            }
        }
        return false;
    }

    public boolean isIn(final BlockState state) {
        for (final BlockEntry<?> entry : this.values) {
            if (entry.is(state.getBlock())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public BlockEntry<T>[] toArray() {
        return (BlockEntry<T>[]) Arrays.copyOf(this.values, this.values.length);
    }

    @Override
    public @NotNull Iterator<BlockEntry<T>> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.index < EncasedBlockList.this.values.length;
            }

            @SuppressWarnings("unchecked")
            @Override
            public BlockEntry<T> next() {
                if (!this.hasNext())
                    throw new NoSuchElementException();
                return (BlockEntry<T>) EncasedBlockList.this.values[this.index++];
            }
        };
    }

}

