package com.kipti.bnb.content.decoration.dyeable.pipes;

import com.kipti.bnb.content.decoration.dyeable.BaseDyeableBehaviour;
import com.kipti.bnb.content.decoration.dyeable.DyeableBlockItemHelper;
import com.zurrtum.create.content.fluids.FluidPropagator;
import com.zurrtum.create.content.fluids.pipes.FluidPipeBlock;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;
import com.zurrtum.create.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class DyeablePipeBehaviour extends BaseDyeableBehaviour {

    public static final BehaviourType<DyeablePipeBehaviour> TYPE = new BehaviourType<>("dyeable_pipe");

    public DyeablePipeBehaviour(final SmartBlockEntity be) {
        super(be);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    protected void onColorChanged(@Nullable final DyeColor color) {
        this.refreshPipeState();
    }


    private void refreshPipeState() {
        if (!this.hasLevel()) {
            return;
        }

        refreshPipeState(this.getLevel(), this.getPos(), this.getBlockState());
    }

    public static void refreshPipeState(final Level level, final BlockPos pos, final BlockState state) {
        refreshPipeState(level, pos, state, true);
    }

    public static void refreshPipeState(final Level level,
                                        final BlockPos pos,
                                        BlockState state,
                                        final boolean propagateToNeighbors) {
        if (state.getBlock() instanceof final FluidPipeBlock pipeBlock) {
            BlockState baseState = pipeBlock.defaultBlockState();
            if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
                baseState = baseState.setValue(
                        BlockStateProperties.WATERLOGGED,
                        state.getValue(BlockStateProperties.WATERLOGGED)
                );
            }
            final BlockState refreshedState = pipeBlock.updateBlockState(
                    baseState,
                    getPreferredDirection(state),
                    null,
                    level,
                    pos
            );
            if (refreshedState != state) {
                level.setBlock(pos, refreshedState, propagateToNeighbors ? 3 : 2);
                state = refreshedState;
            }
        }

        if (propagateToNeighbors) {
            state.updateNeighbourShapes(level, pos, 3);
            if (!level.isClientSide()) {
                FluidPropagator.propagateChangedPipe(level, pos, state);
            }
        }
    }

    private static Direction getPreferredDirection(final BlockState state) {
        for (final Direction direction : Iterate.directions) {
            if (FluidPipeBlock.isOpenAt(state, direction)) {
                return direction;
            }
        }
        return Direction.UP;
    }

}
