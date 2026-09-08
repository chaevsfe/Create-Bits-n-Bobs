package com.kipti.bnb.content.decoration.dyeable;

import com.kipti.bnb.content.decoration.dyeable.pipes.DyeablePipeBehaviour;
import com.kipti.bnb.content.decoration.dyeable.simple.SimpleDyeableBehaviour;
import com.kipti.bnb.content.decoration.dyeable.tanks.DyeableTankBehaviour;
import com.kipti.bnb.registry.client.BnbSpriteShifts;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.client.AllModels;
import com.zurrtum.create.client.catnip.render.SpriteShiftEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Installs the dye re-texturing on Create's own fluid blocks.
 * <p>
 * Wrappers are composed onto whatever Create Fly already registered for the block rather than replacing it, so the
 * pipes keep their attachment model and the tank keeps its connected textures.
 */
@Environment(EnvType.CLIENT)
public final class BnbDyeableModels {

    private BnbDyeableModels() {
    }

    public static void register() {
        DyeableBlockStateModel.DyeStyle pipe = new DyeableBlockStateModel.DyeStyle() {
            @Override
            public DyeColor colorAt(BlockAndTintGetter level, BlockPos pos) {
                DyeablePipeBehaviour behaviour = BlockEntityBehaviour.get(level, pos, DyeablePipeBehaviour.TYPE);
                DyeColor color = behaviour == null ? null : behaviour.getColor();
                return color != null ? color : DyeableTransitionHelper.getPendingPlacementColor(level, pos);
            }

            @Override
            public void collectShifts(BlockState state, DyeColor color, List<SpriteShiftEntry> out) {
                DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_PIPES, color);
                DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_PIPES_CONNECTED, color);
                DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_GLASS_FLUID_PIPE, color);
            }
        };

        DyeableBlockStateModel.DyeStyle tank = new DyeableBlockStateModel.DyeStyle() {
            @Override
            public DyeColor colorAt(BlockAndTintGetter level, BlockPos pos) {
                DyeableTankBehaviour behaviour = BlockEntityBehaviour.get(level, pos, DyeableTankBehaviour.TYPE);
                DyeColor color = behaviour == null ? null : behaviour.getDisplayedColor();
                return color != null ? color : DyeableTransitionHelper.getPendingPlacementColor(level, pos);
            }

            @Override
            public void collectShifts(BlockState state, DyeColor color, List<SpriteShiftEntry> out) {
                DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_FLUID_TANK_CONNECTED, color);
                DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_FLUID_TANK_TOP_CONNECTED, color);
                DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_FLUID_TANK_INNER_CONNECTED, color);
                DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_FLUID_TANK_WINDOW, color);
                DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_FLUID_TANK_WINDOW_SINGLE, color);
            }
        };

        DyeableBlockStateModel.DyeStyle simple = new DyeableBlockStateModel.DyeStyle() {
            @Override
            public DyeColor colorAt(BlockAndTintGetter level, BlockPos pos) {
                SimpleDyeableBehaviour behaviour = BlockEntityBehaviour.get(level, pos, SimpleDyeableBehaviour.TYPE);
                DyeColor color = behaviour == null ? null : behaviour.getColor();
                return color != null ? color : DyeableTransitionHelper.getPendingPlacementColor(level, pos);
            }

            @Override
            public void collectShifts(BlockState state, DyeColor color, List<SpriteShiftEntry> out) {
                if (state.is(AllBlocks.MECHANICAL_PUMP)) {
                    DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_PUMP, color);
                    return;
                }
                if (state.is(AllBlocks.SMART_FLUID_PIPE)) {
                    DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_SMART_PIPE_1, color);
                    DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_SMART_PIPE_2, color);
                    DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_PIPES, color);
                    return;
                }
                if (state.is(AllBlocks.FLUID_VALVE)) {
                    DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_VALVE_CLOSED, color);
                    DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_VALVE_OPEN, color);
                    DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_FLUID_VALVE, color);
                    DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_PIPES, color);
                    return;
                }
                DyeableBlockStateModel.add(out, BnbSpriteShifts.DYED_STEAM_ENGINE, color);
            }
        };

        wrap(AllBlocks.FLUID_PIPE, pipe);
        wrap(AllBlocks.ENCASED_FLUID_PIPE, pipe);
        wrap(AllBlocks.GLASS_FLUID_PIPE, pipe);
        wrap(AllBlocks.FLUID_TANK, tank);
        wrap(AllBlocks.MECHANICAL_PUMP, simple);
        wrap(AllBlocks.SMART_FLUID_PIPE, simple);
        wrap(AllBlocks.FLUID_VALVE, simple);
        wrap(AllBlocks.STEAM_ENGINE, simple);
    }

    private static void wrap(Block block, DyeableBlockStateModel.DyeStyle style) {
        BiFunction<BlockState, BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> existing = AllModels.ALL.get(block);
        BiFunction<BlockState, BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> dyed = DyeableBlockStateModel.of(style);
        AllModels.ALL.put(block, existing == null ? dyed : compose(existing, dyed));
    }

    private static BiFunction<BlockState, BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> compose(
            BiFunction<BlockState, BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> inner,
            BiFunction<BlockState, BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> outer) {
        return (state, root) -> outer.apply(state, inner.apply(state, root));
    }

    @Nullable
    public static DyeColor pendingOrBehaviour(BlockAndTintGetter level, BlockPos pos) {
        return DyeableTransitionHelper.getPendingPlacementColor(level, pos);
    }
}
