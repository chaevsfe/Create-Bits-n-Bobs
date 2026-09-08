package com.kipti.bnb.foundation.behaviour;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.content.contraptions.StructureTransform;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class SuperBlockEntityBehaviour extends BlockEntityBehaviour<SmartBlockEntity> {

    protected SuperBlockEntityBehaviour(final SmartBlockEntity blockEntity) {
        super(blockEntity);
    }

    public BlockState getBlockState() {
        return blockEntity.getBlockState();
    }

    public SmartBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public boolean hasLevel() {
        return getLevel() != null;
    }

    public boolean isClientSide() {
        final Level level = getLevel();
        return level != null && level.isClientSide();
    }

    public boolean isServerLevel() {
        return !isClientSide();
    }

    public void sendData() {
        blockEntity.sendData();
    }

    public void notifyUpdate() {
        blockEntity.notifyUpdate();
    }


    public void detachKinetics() {
        if (blockEntity instanceof final KineticBlockEntity kinetic)
            kinetic.detachKinetics();
    }

    /** Invoked when the owning block entity is removed from its level. */
    public void remove() {
    }

    /** Invoked when the owning block entity leaves a chunk, with isMoving set for contraption assembly. */
    public void removeFromLevel(final boolean isMoving) {
    }

    /** Invoked when a contraption transforms the owning block entity. */
    public void transform(final BlockEntity movedBlockEntity, final StructureTransform structureTransform) {
    }

    /** Invoked before a player breaks the owning block. */
    public void onBlockBroken(final Player player) {
    }

    /** Invoked when a player right-clicks the owning block. */
    public void onItemUse(final BlockUseEvent event) {
    }

    public static <T extends BlockEntityBehaviour<?>> Optional<T> getOptional(final BlockGetter level, final BlockPos pos, final BehaviourType<T> type) {
        return Optional.ofNullable(BlockEntityBehaviour.get(level, pos, type));
    }

    public static <T extends BlockEntityBehaviour<?>> Optional<T> getOptional(final BlockEntity blockEntity, final BehaviourType<T> type) {
        return Optional.ofNullable(BlockEntityBehaviour.get(blockEntity, type));
    }

    public static <T extends BlockEntityBehaviour<?>> T getOrThrow(final BlockGetter level, final BlockPos pos, final BehaviourType<T> type) {
        final T behaviour = BlockEntityBehaviour.get(level, pos, type);
        if (behaviour == null)
            throw new IllegalStateException("Expected a " + type.getName() + " behaviour at " + pos + " but found none");
        return behaviour;
    }

    public static <T extends BlockEntityBehaviour<?>> T getOrThrow(final BlockEntity blockEntity, final BehaviourType<T> type) {
        final T behaviour = BlockEntityBehaviour.get(blockEntity, type);
        if (behaviour == null)
            throw new IllegalStateException("Expected a " + type.getName() + " behaviour on " + blockEntity + " but found none");
        return behaviour;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends SuperBlockEntityBehaviour> T getSameBehaviour(final BlockPos otherPos) {
        final Level level = getLevel();
        if (level == null || !level.isLoaded(otherPos))
            return null;
        if (!(level.getBlockEntity(otherPos) instanceof final SmartBlockEntity other))
            return null;
        return other.getBehaviour((BehaviourType<? extends T>) getType());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T extends SuperBlockEntityBehaviour> T getSameBehaviour(final BlockEntity otherBlockEntity) {
        if (!(otherBlockEntity instanceof final SmartBlockEntity other))
            return null;
        return other.getBehaviour((BehaviourType<? extends T>) getType());
    }

    public <T extends SuperBlockEntityBehaviour> Optional<T> getSameBehaviourOptional(final BlockPos otherPos) {
        return Optional.ofNullable(this.getSameBehaviour(otherPos));
    }

    public <T extends SuperBlockEntityBehaviour> Optional<T> getSameBehaviourOptional(final BlockEntity otherBlockEntity) {
        return Optional.ofNullable(this.getSameBehaviour(otherBlockEntity));
    }

    public <T extends SuperBlockEntityBehaviour> T getSameBehaviourOrThrow(final BlockPos otherPos) {
        return this.<T>getSameBehaviourOptional(otherPos).orElseThrow(() -> new IllegalStateException(
                "Expected a matching " + getType().getName() + " behaviour at " + otherPos + " but found none"));
    }
}
