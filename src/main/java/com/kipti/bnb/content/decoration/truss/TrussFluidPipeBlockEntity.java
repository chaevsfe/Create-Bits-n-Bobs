package com.kipti.bnb.content.decoration.truss;

import net.minecraft.world.level.BlockAndLightGetter;
import com.kipti.bnb.content.decoration.dyeable.pipes.DyeablePipeBehaviour;
import com.zurrtum.create.content.fluids.FluidPropagator;
import com.zurrtum.create.content.fluids.FluidTransportBehaviour;
import com.zurrtum.create.content.fluids.pipes.AxisPipeBlock;
import com.zurrtum.create.content.fluids.pipes.EncasedPipeBlock;
import com.zurrtum.create.content.fluids.pipes.FluidPipeBlock;
import com.zurrtum.create.content.fluids.pipes.GlassFluidPipeBlock;
import com.zurrtum.create.content.decoration.bracket.BracketedBlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndLightGetter;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TrussFluidPipeBlockEntity extends SmartBlockEntity {

    public TrussFluidPipeBlockEntity(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(final List<BlockEntityBehaviour<?>> behaviours) {
        behaviours.add(new BracketedBlockEntityBehaviour(this));
        behaviours.add(new DyeablePipeBehaviour(this));
        behaviours.add(new StraightPipeFluidTransportBehaviourWithConnector(this));
    }

    private static class StraightPipeFluidTransportBehaviourWithConnector extends FluidTransportBehaviour {
        public StraightPipeFluidTransportBehaviourWithConnector(final SmartBlockEntity be) {
            super(be);
        }

        @Override
        public boolean canHaveFlowToward(final BlockState state, final Direction direction) {
            return state.hasProperty(AxisPipeBlock.AXIS) && state.getValue(AxisPipeBlock.AXIS) == direction.getAxis();
        }

        @Override
        public AttachmentTypes getRenderedRimAttachment(final BlockAndLightGetter world,
                                                        final BlockPos pos,
                                                        final BlockState state,
                                                        final Direction direction) {
            final AttachmentTypes attachment = super.getRenderedRimAttachment(world, pos, state, direction);

            final BlockPos offsetPos = pos.relative(direction);
            final BlockState otherState = world.getBlockState(offsetPos);

            if (state.getBlock() instanceof EncasedPipeBlock && attachment != AttachmentTypes.DRAIN)
                return AttachmentTypes.NONE;

            if (attachment == AttachmentTypes.RIM) {
                if (!FluidPipeBlock.isPipe(otherState) && !(otherState.getBlock() instanceof EncasedPipeBlock)
                        && !(otherState.getBlock() instanceof GlassFluidPipeBlock)) {
                    final FluidTransportBehaviour pipeBehaviour =
                            BlockEntityBehaviour.get(world, offsetPos, FluidTransportBehaviour.TYPE);
                    if (pipeBehaviour != null && pipeBehaviour.canHaveFlowToward(otherState, direction.getOpposite()))
                        return AttachmentTypes.CONNECTION;
                }

                if (!FluidPipeBlock.shouldDrawRim(world, pos, state, direction))
                    return FluidPropagator.getStraightPipeAxis(state) == direction.getAxis()
                            ? AttachmentTypes.CONNECTION
                            : AttachmentTypes.DETAILED_CONNECTION;
            }

            if (attachment == AttachmentTypes.NONE
                    && direction.getAxis() == state.getValue(TrussFluidPipeBlock.AXIS))
                return AttachmentTypes.DETAILED_CONNECTION;

            return attachment;
        }

//        private AttachmentTypes withConnector(final AttachmentTypes attachment) {
//            if (attachment == AttachmentTypes.PARTIAL_RIM)
//                return AttachmentTypes.RIM;
//            if (attachment == AttachmentTypes.PARTIAL_DRAIN)
//                return AttachmentTypes.DRAIN;
//            return attachment;
//        }
    }
}
