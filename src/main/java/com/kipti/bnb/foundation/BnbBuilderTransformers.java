package com.kipti.bnb.foundation;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.zurrtum.create.content.kinetics.simpleRelays.encased.EncasedCogwheelBlock;
import com.zurrtum.create.content.kinetics.simpleRelays.encased.EncasedShaftBlock;
import com.kipti.bnb.foundation.data.SharedProperties;
import com.kipti.bnb.registrate.builders.BlockBuilder;
import com.kipti.bnb.registrate.fn.NonNullUnaryOperator;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;


public class BnbBuilderTransformers {

    public static <B extends EncasedShaftBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedShaftWithoutCT(final String casing) {
        return builder -> encasedBase(builder, () -> AllBlocks.SHAFT)
                
                .item()
                
                .build();
    }


    public static <B extends EncasedCogwheelBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> casingMaterialCogwheelBase(
            final EncasedBlockList.CasingMaterial casing, final Supplier<ItemLike> drop, final boolean large) {
        return b -> {
            encasedBase(b, drop);

            final String encasedSuffix = "_encased_cogwheel_side" + (large ? "_connected" : "");
            final String blockFolder = large ? "encased_large_cogwheel" : "encased_cogwheel";
            final String wood = casing.getResourceName().equals("brass") ? "dark_oak" : "spruce";


            b.item()
                    
                    .build();
            return b;
        };
    }

    public static <B extends EncasedCogwheelBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedCogwheelWithoutCT(final String casing) {
        return b -> encasedCogwheelBaseWithoutCT(b, casing, () -> AllBlocks.COGWHEEL, false);
    }

    public static <B extends EncasedCogwheelBlock, P> NonNullUnaryOperator<BlockBuilder<B, P>> encasedLargeCogwheelWithoutCT(final String casing) {
        return b -> encasedCogwheelBaseWithoutCT(b, casing, () -> AllBlocks.LARGE_COGWHEEL, true);
    }

    private static <B extends EncasedCogwheelBlock, P> BlockBuilder<B, P> encasedCogwheelBaseWithoutCT(final BlockBuilder<B, P> b,
                                                                                                       final String casing, final Supplier<ItemLike> drop, final boolean large) {
        final String encasedSuffix = "_encased_cogwheel_side" + (large ? "_connected" : "");
        final String blockFolder = large ? "encased_large_cogwheel" : "encased_cogwheel";
        return encasedBase(b, drop)
                
                .item()
                
                .build();
    }

    private static <B extends RotatedPillarKineticBlock, P> BlockBuilder<B, P> encasedBase(final BlockBuilder<B, P> b,
                                                                                           final Supplier<ItemLike> drop) {
        return b.initialProperties(SharedProperties::stone)
                .properties(BlockBehaviour.Properties::noOcclusion)
                ;
    }

}
