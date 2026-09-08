package com.kipti.bnb.content.decoration.dyeable;

import com.kipti.bnb.content.decoration.dyeable.pipes.DyeablePipeBehaviour;
import com.kipti.bnb.content.decoration.dyeable.simple.SimpleDyeableBehaviour;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

public final class DyeableCreateBlockItems {

    private static final Map<Block, BehaviourType<? extends BaseDyeableBehaviour>> BEHAVIOURS = new IdentityHashMap<>();

    static {
        BEHAVIOURS.put(AllBlocks.FLUID_PIPE, DyeablePipeBehaviour.TYPE);
        BEHAVIOURS.put(AllBlocks.MECHANICAL_PUMP, SimpleDyeableBehaviour.TYPE);
        BEHAVIOURS.put(AllBlocks.SMART_FLUID_PIPE, SimpleDyeableBehaviour.TYPE);
        BEHAVIOURS.put(AllBlocks.FLUID_VALVE, SimpleDyeableBehaviour.TYPE);
        BEHAVIOURS.put(AllBlocks.STEAM_ENGINE, SimpleDyeableBehaviour.TYPE);
    }

    private DyeableCreateBlockItems() {
    }

    @Nullable
    public static BehaviourType<? extends BaseDyeableBehaviour> behaviourFor(final Block block) {
        return BEHAVIOURS.get(block);
    }
}
