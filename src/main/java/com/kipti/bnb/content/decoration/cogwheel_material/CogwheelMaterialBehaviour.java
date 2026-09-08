package com.kipti.bnb.content.decoration.cogwheel_material;

import com.kipti.bnb.foundation.behaviour.SuperBlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.kipti.bnb.foundation.behaviour.BlockUseEvent;

public class CogwheelMaterialBehaviour extends SuperBlockEntityBehaviour {

    public static final BehaviourType<CogwheelMaterialBehaviour> TYPE = new BehaviourType<>("cogwheel_material");

    public BlockState material;

    public CogwheelMaterialBehaviour(final SmartBlockEntity be) {
        super(be);
        this.material = Blocks.SPRUCE_PLANKS.defaultBlockState();
    }

    @Override
    public void remove() {
        super.remove();
        this.tryTransferOnRemoval();
    }

    private void tryTransferOnRemoval() {
        final BlockEntity replacingBlockEntity = this.getLevel().getBlockEntity(this.getPos());
        final CogwheelMaterialBehaviour replacingBehaviour = this.getSameBehaviour(replacingBlockEntity);

        if (replacingBehaviour == null)
            return;

        replacingBehaviour.material = this.material;
        replacingBehaviour.sendData();

    }

    @Override
    public void onItemUse(final BlockUseEvent event) {
        final InteractionResult result = this.applyMaterialIfValid(event.getItemStack());

        if (result != InteractionResult.TRY_WITH_EMPTY_HAND) {
            event.cancelWithResult(result);
        }

        super.onItemUse(event);
    }

    public InteractionResult applyMaterialIfValid(final ItemStack stack) {
        if (!(stack.getItem() instanceof final BlockItem blockItem))
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        final BlockState material = blockItem.getBlock()
                .defaultBlockState();
        if (material == this.material)
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (!material.is(BlockTags.PLANKS))
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (this.getLevel().isClientSide() && !this.blockEntity.isVirtual())
            return InteractionResult.SUCCESS;
        this.material = material;
        this.blockEntity.notifyUpdate();
        this.getLevel().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, this.getPos(), Block.getId(material));
        return InteractionResult.SUCCESS;
    }

    private void redraw() {
        if (!this.blockEntity.isVirtual())
        if (this.hasLevel()) {
            this.getLevel().sendBlockUpdated(this.getPos(), this.getBlockState(), this.getBlockState(), 16);
            this.getLevel().getChunkSource()
                    .getLightEngine()
                    .checkBlock(this.getPos());
        }
    }

    @Override
    public void read(final ValueInput compound, final boolean clientPacket) {
        super.read(compound, clientPacket);

        final BlockState prevMaterial = this.material;
        if (compound.read("Material", CompoundTag.CODEC).isEmpty())
            return;

        this.material = NbtUtils.readBlockState(this.blockEntity.blockHolderGetter(), compound.read("Material", CompoundTag.CODEC).orElseGet(CompoundTag::new));
        if (this.material.isAir())
            this.material = Blocks.SPRUCE_PLANKS.defaultBlockState();

        if (clientPacket && prevMaterial != this.material)
            this.redraw();
    }

    @Override
    public void writeSafe(final ValueOutput tag) {
        super.writeSafe(tag);
        tag.store("Material", CompoundTag.CODEC, NbtUtils.writeBlockState(this.material));
    }

    @Override
    public void write(final ValueOutput compound, final boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.store("Material", CompoundTag.CODEC, NbtUtils.writeBlockState(this.material));
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

}
