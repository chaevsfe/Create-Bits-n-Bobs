package com.kipti.bnb.content.kinetics.cogwheel_carriage.block;

import com.kipti.bnb.content.kinetics.cogwheel_carriage.contraption.CogwheelChainCarriageContraption;
import com.kipti.bnb.content.kinetics.cogwheel_carriage.contraption.CogwheelChainCarriageContraptionEntity;
import com.kipti.bnb.content.kinetics.cogwheel_chain.attachment.CogwheelChainAttachment;
import com.kipti.bnb.content.kinetics.cogwheel_chain.attachment.CogwheelChainAttachmentHelper;
import com.zurrtum.create.AllSoundEvents;
import com.zurrtum.create.content.contraptions.AssemblyException;
import com.zurrtum.create.client.content.contraptions.IDisplayAssemblyExceptions;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CogwheelChainCarriageBlockEntity extends SmartBlockEntity implements IDisplayAssemblyExceptions {

    public static final String ASSEMBLY_EXCEPTION_FORMAT = "create.gui.assembly.exception.%s";

    private @Nullable AssemblyException lastException;
    public boolean assembleNextTick;

    public CogwheelChainCarriageBlockEntity(final BlockEntityType<?> type,
                                            final BlockPos pos,
                                            final BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(final List<BlockEntityBehaviour<?>> behaviours) {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level == null || this.level.isClientSide())
            return;

        if (this.assembleNextTick) {
            this.assembleNextTick = false;
            this.assemble();
            this.notifyUpdate();
        }
    }

    private void assemble() {
        final CogwheelChainAttachment attachment = CogwheelChainAttachmentHelper.findNearestAttachment(
                this.level,
                Vec3.atCenterOf(this.getBlockPos()).add(0, 1, 0),
                1
        );
        if (attachment == null) {
            this.lastException = new AssemblyException("bits_n_bobs.no_chain_to_attach_to");
            return;
        }

        //Change our current block direction to match the attachment
        final Vec3 facing = attachment.getCurrentDirection(this.level);
        final Direction nearestDirection = Direction.getApproximateNearest(facing.x, facing.y, facing.z);

        if (nearestDirection.getAxis() == Direction.Axis.Y) {
            this.lastException = new AssemblyException("bits_n_bobs.no_chain_to_attach_to");
            return;
        }

        this.level.setBlock(
                this.getBlockPos(), this.getBlockState().setValue(CogwheelChainCarriageBlock.FACING, nearestDirection),
                Block.UPDATE_ALL
        );

        final CogwheelChainCarriageContraption contraption =
                new CogwheelChainCarriageContraption(attachment);
        try {
            if (!contraption.assemble(this.level, this.worldPosition)) {
                return;
            }
            this.lastException = null;
        } catch (final AssemblyException e) {
            this.lastException = e;
            this.sendData();
            return;
        }

        contraption.removeBlocksFromWorld(this.level, BlockPos.ZERO);

        final CogwheelChainCarriageContraptionEntity entity =
                CogwheelChainCarriageContraptionEntity.create(this.level, contraption);

        final Vec3 anchor = Vec3.atBottomCenterOf(this.worldPosition);
        entity.setPos(anchor.x, anchor.y, anchor.z);

        this.level.addFreshEntity(entity);
        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(this.level, this.worldPosition);
    }

    @Nullable
    public AssemblyException getLastException() {
        return this.lastException;
    }

    @Override
    protected void write(final ValueOutput tag, final boolean clientPacket) {
        super.write(tag, clientPacket);
        AssemblyException.write(tag, this.lastException);
    }

    @Override
    protected void read(final ValueInput tag, final boolean clientPacket) {
        super.read(tag, clientPacket);
        this.lastException = AssemblyException.read(tag);
    }

    @Override
    public AssemblyException getLastAssemblyException() {
        return this.lastException;
    }
}
