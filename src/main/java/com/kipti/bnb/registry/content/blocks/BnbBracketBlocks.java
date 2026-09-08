package com.kipti.bnb.registry.content.blocks;

import com.zurrtum.create.AllItemTags;
import com.zurrtum.create.content.decoration.bracket.BracketBlock;
import com.zurrtum.create.content.decoration.bracket.BracketBlockItem;
import com.kipti.bnb.registrate.entry.BlockEntry;
import net.minecraft.world.level.block.SoundType;

import static com.kipti.bnb.CreateBitsnBobs.REGISTRATE;
import static com.kipti.bnb.foundation.data.TagGen.pickaxeOnly;

public class BnbBracketBlocks {

    public static final BlockEntry<BracketBlock> WEATHERED_METAL_BRACKET = REGISTRATE.block("weathered_metal_bracket", BracketBlock::new)
            
            .properties(p -> p.sound(SoundType.NETHERITE_BLOCK))
            .transform(pickaxeOnly())
            .item(BracketBlockItem::new)
            .tag(AllItemTags.INVALID_FOR_TRACK_PAVING)
            .build()
            
            .register();

    public static void register() {
    }

}
