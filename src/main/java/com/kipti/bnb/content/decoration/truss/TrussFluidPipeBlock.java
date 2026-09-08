package com.kipti.bnb.content.decoration.truss;

import com.kipti.bnb.registry.content.BnbShapes;
import com.kipti.bnb.content.decoration.dyeable.DyeableTransitionHelper;
import com.kipti.bnb.registry.content.BnbBlockEntities;
import com.kipti.bnb.registry.content.blocks.deco.BnbDecorativeBlocks;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.zurrtum.create.content.fluids.FluidTransportBehaviour;
import com.zurrtum.create.content.fluids.pipes.AxisPipeBlock;
import com.zurrtum.create.content.fluids.pipes.FluidPipeBlock;
import com.zurrtum.create.content.schematics.requirement.ItemRequirement;
import com.zurrtum.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TrussFluidPipeBlock extends AxisPipeBlock implements IBE<TrussFluidPipeBlockEntity>, SimpleWaterloggedBlock, SpecialBlockItemRequirement {

    public TrussFluidPipeBlock(final Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> p_55933_) {
        super.createBlockStateDefinition(p_55933_);
        p_55933_.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public ItemRequirement getRequiredItems(final BlockState state, @Nullable final BlockEntity blockEntity) {
        return new ItemRequirement(
                ItemRequirement.ItemUseType.CONSUME,
                this.asItem().getDefaultInstance()
        ).union(new ItemRequirement(
                ItemRequirement.ItemUseType.CONSUME,
                new ItemStack(AllBlocks.FLUID_PIPE)
        ));
    }

    @Override
    public VoxelShape getShape(final BlockState state,
                               final BlockGetter p_220053_2_,
                               final BlockPos p_220053_3_,
                               final CollisionContext p_220053_4_) {
        return BnbShapes.TRUSS.get(state.getValue(AXIS));
    }

    @Override
    public Class<TrussFluidPipeBlockEntity> getBlockEntityClass() {
        return TrussFluidPipeBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TrussFluidPipeBlockEntity> getBlockEntityType() {
        return BnbBlockEntities.METAL_TRUSS_PIPE.get();
    }

    @Override
    public InteractionResult onSneakWrenched(final BlockState state, final UseOnContext context) {
        final Level world = context.getLevel();
        final BlockPos pos = context.getClickedPos();

        if (world.isClientSide())
            return InteractionResult.SUCCESS;

        context.getLevel()
                .levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, context.getClickedPos(), Block.getId(state));
        final Direction.Axis axis = state.getValue(AXIS);
        final Direction positiveFacing = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE);
        final Direction negativeFacing = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.NEGATIVE);
        final BlockState equivalentPipe = AllBlocks.FLUID_PIPE.defaultBlockState()
                .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(positiveFacing), true)
                .setValue(FluidPipeBlock.PROPERTY_BY_DIRECTION.get(negativeFacing), true)
                .setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED));

        final Direction firstFound = Direction.fromAxisAndDirection(
                state.getValue(AXIS),
                Direction.AxisDirection.POSITIVE
        );

        FluidTransportBehaviour.cacheFlows(world, pos);
        DyeableTransitionHelper.saveCurrentDye(world, pos);
        world.setBlockAndUpdate(
                pos, AllBlocks.FLUID_PIPE
                        .updateBlockState(equivalentPipe, firstFound, null, world, pos)
        );
        FluidTransportBehaviour.loadFlows(world, pos);
        DyeableTransitionHelper.applyPreviousDye(world, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean bits_n_bobs$includeData) {
        return BnbDecorativeBlocks.INDUSTRIAL_TRUSS.asStack();
    }


}
