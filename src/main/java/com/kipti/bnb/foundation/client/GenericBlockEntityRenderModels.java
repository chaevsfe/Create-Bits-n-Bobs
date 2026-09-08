package com.kipti.bnb.foundation.client;

import com.kipti.bnb.registrate.entry.BlockEntry;
import com.kipti.bnb.registry.client.BnbPartialModels;
import com.kipti.bnb.registry.content.blocks.BnbKineticBlocks;
import com.kipti.bnb.registry.content.blocks.encased.BnbEncasedBlockLists;
import com.zurrtum.create.api.registry.SimpleRegistry;
import com.zurrtum.create.client.flywheel.lib.model.baked.PartialModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.Block;

/**
 * Which partial model a block's generic kinetic renderer and visual should use.
 * <p>
 * Upstream filled this from {@code onRegister} callbacks on the block builders. Those run during common
 * registration, so on Fabric they would pull {@link PartialModel} — a client class — onto a dedicated server. The
 * table is built from the same block entries at client init instead.
 */
@Environment(EnvType.CLIENT)
public class GenericBlockEntityRenderModels {
    public static final SimpleRegistry<Block, PartialModel> REGISTRY = SimpleRegistry.create();

    public static void register() {
        REGISTRY.register(BnbKineticBlocks.SMALL_FLANGED_COGWHEEL.get(), BnbPartialModels.SMALL_FLANGED_COGWHEEL_BLOCK);
        REGISTRY.register(BnbKineticBlocks.LARGE_FLANGED_COGWHEEL.get(), BnbPartialModels.LARGE_FLANGED_COGWHEEL_BLOCK);

        for (BlockEntry<?> entry : BnbEncasedBlockLists.ENCASED_FLANGED_COGWHEEL)
            REGISTRY.register(entry.get(), BnbPartialModels.ENCASED_FLANGED_COGWHEEL_BLOCK);
        for (BlockEntry<?> entry : BnbEncasedBlockLists.ENCASED_LARGE_FLANGED_COGWHEEL)
            REGISTRY.register(entry.get(), BnbPartialModels.ENCASED_LARGE_FLANGED_COGWHEEL_BLOCK);
    }
}
