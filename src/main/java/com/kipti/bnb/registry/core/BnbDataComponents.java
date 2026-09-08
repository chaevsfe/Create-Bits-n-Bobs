package com.kipti.bnb.registry.core;

import com.cake.struts.registry.StrutDataComponents;
import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.PlacingCogwheelChain;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.UnaryOperator;

public class BnbDataComponents {

    public static final DataComponentType<BlockPos> GIRDER_STRUT_FROM = StrutDataComponents.GIRDER_STRUT_FROM;

    public static final DataComponentType<Direction> GIRDER_STRUT_FROM_FACE = StrutDataComponents.GIRDER_STRUT_FROM_FACE;

    public static final DataComponentType<PlacingCogwheelChain> PARTIAL_COGWHEEL_CHAIN = register(
            "partial_cogwheel_chain",
            builder -> builder.persistent(PlacingCogwheelChain.CODEC).networkSynchronized(PlacingCogwheelChain.STREAM_CODEC)
    );

    private static <T> DataComponentType<T> register(final String name, final UnaryOperator<DataComponentType.Builder<T>> builder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, CreateBitsnBobs.asResource(name),
                builder.apply(DataComponentType.builder()).build());
    }

    public static void register() {
        if (!BuiltInRegistries.DATA_COMPONENT_TYPE.containsKey(CreateBitsnBobs.asResource("partial_cogwheel_chain")))
            throw new IllegalStateException("Bits 'n' Bobs data components did not register");
    }

}
