package com.kipti.bnb.content.decoration.grating;

import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import com.zurrtum.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class GratingBlock extends Block implements SimpleWaterloggedBlock, IWrenchable {

    public GratingBlock(final Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    protected @NotNull BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess bits_n_bobs$ticks, final BlockPos pos, final Direction direction, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource bits_n_bobs$random) {
        if (state.getValue(WATERLOGGED))
            bits_n_bobs$ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, level, bits_n_bobs$ticks, pos, direction, neighbourPos, neighbourState, bits_n_bobs$random);
    }

    @Override
    public @NotNull BlockState getStateForPlacement(final @NotNull BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected @NotNull FluidState getFluidState(final @NotNull BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    protected boolean propagatesSkylightDown(final BlockState state) {
        return !state.getValue(WATERLOGGED);
    }

    @Override
    protected boolean skipRendering(final @NotNull BlockState state,
                                    final BlockState adjacentState,
                                    final @NotNull Direction direction) {
        return adjacentState.getBlock() instanceof GratingBlock && !(adjacentState.getBlock() instanceof GratingPanelBlock) || super.skipRendering(
                state,
                adjacentState,
                direction
        );
    }

}

