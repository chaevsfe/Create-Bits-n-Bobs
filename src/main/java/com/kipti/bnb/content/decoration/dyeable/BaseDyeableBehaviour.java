package com.kipti.bnb.content.decoration.dyeable;

import com.kipti.bnb.registry.content.BnbAdvancements;
import com.kipti.bnb.foundation.data.DyeColors;
import com.kipti.bnb.foundation.client.BnbClientHooks;
import com.kipti.bnb.foundation.behaviour.SuperBlockEntityBehaviour;
import com.kipti.bnb.registry.core.BnbFeatureFlag;
import com.zurrtum.create.api.connectivity.ConnectivityHandler;
import com.zurrtum.create.foundation.blockEntity.IMultiBlockEntityContainer;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.kipti.bnb.foundation.behaviour.BlockUseEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class BaseDyeableBehaviour extends SuperBlockEntityBehaviour {

    @Nullable
    private DyeColor color;

    protected BaseDyeableBehaviour(final SmartBlockEntity be) {
        super(be);
    }

    @Override
    public void onItemUse(final BlockUseEvent event) {
        if (!this.isDyeingEnabled()) return;

        final ItemStack stack = event.getItemStack();

        if (!(stack.getItem() instanceof final DyeItem dyeItem)) {
            return;
        }

        if (!event.getLevel().isClientSide()) {
            BnbAdvancements.DYE_FLUID_COMPONENT.awardTo(event.getPlayer());
            this.dye(DyeColors.of(stack), event.getPlayer() != null && event.getPlayer().isShiftKeyDown());
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    protected void dye(@Nullable final DyeColor color, final boolean single) {
        this.setColor(color);
    }

    public boolean isDyeingEnabled() {
        return BnbFeatureFlag.DYEABLE_PIPES.isEnabled();
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    @Nullable
    public DyeColor getDisplayedColor() {
        return this.getColor();
    }

    public void applyColorClientOnly(@Nullable final DyeColor color) {
        if (this.color == color) {
            return;
        }
        this.color = color;
        this.refreshRenderedModel();
    }

    public void setColor(@Nullable final DyeColor color) {
        if (!this.isDyeingEnabled() && this.color == null) {
            return;
        }
        if (this.color == color) {
            return;
        }
        this.color = color;
        this.onColorChanged(color);
        this.refreshOrNotifyUpdate();
    }

    protected void onColorChanged(@Nullable final DyeColor color) {
    }

    protected final void refreshConnectedBlocks() {
        if (!this.hasLevel()) {
            return;
        }
        this.getLevel()
                .updateNeighborsAt(this.getPos(), this.getBlockState().getBlock());
    }

    protected final void dyeSinglePart(@Nullable final DyeColor color) {
        if (!(this.blockEntity instanceof final IMultiBlockEntityContainer be)) {
            this.setColor(color);
            return;
        }
        this.dyeSinglePart((BlockEntity & IMultiBlockEntityContainer) be, color);
    }

    @SuppressWarnings("unchecked")
    private <T extends BlockEntity & IMultiBlockEntityContainer> void dyeSinglePart(final T be, @Nullable final DyeColor color) {
        final T controllerBE = be.getControllerBE();
        final boolean wasMulti = controllerBE != null && (controllerBE.getWidth() > 1 || controllerBE.getHeight() > 1);

        if (wasMulti) {
            ConnectivityHandler.splitMulti(controllerBE);
        }

        this.setColor(color);
        ConnectivityHandler.formMulti(be);

        if (wasMulti && controllerBE != be) {
            ConnectivityHandler.formMulti(controllerBE);
        }
    }

    protected final void forEachMultiblockPart(final Consumer<BaseDyeableBehaviour> action) {
        if (!this.hasLevel()) {
            return;
        }

        if (!(this.blockEntity instanceof final IMultiBlockEntityContainer be)) {
            action.accept(this);
            return;
        }

        final IMultiBlockEntityContainer controllerBE = be.getControllerBE();
        if (controllerBE == null) {
            action.accept(this);
            return;
        }

        final Level level = this.getLevel();
        final BlockPos origin = ((BlockEntity) controllerBE).getBlockPos();
        final Direction.Axis axis = controllerBE.getMainConnectionAxis();
        final int width = controllerBE.getWidth();
        final int length = controllerBE.getHeight();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                for (int z = 0; z < width; z++) {
                    final BlockPos pos = switch (axis) {
                        case X -> origin.offset(y, x, z);
                        case Y -> origin.offset(x, y, z);
                        case Z -> origin.offset(x, z, y);
                    };
                    final BaseDyeableBehaviour behaviour = DyeableMultiblockTypes.get(level, pos);
                    if (behaviour != null) {
                        action.accept(behaviour);
                    }
                }
            }
        }
    }

    protected void refreshOrNotifyUpdate() {
        if (this.hasLevel() && this.getLevel().isClientSide()) {
            this.refreshRenderedModel();
        } else {
            this.blockEntity.notifyUpdate();
        }
    }

    @Override
    public void write(final ValueOutput nbt, final boolean clientPacket) {
        super.write(nbt, clientPacket);
        this.writeDyeColor(nbt);
        this.writeAdditionalDyeData(nbt);
    }

    @Override
    public void writeSafe(final ValueOutput nbt) {
        super.writeSafe(nbt);
        this.writeDyeColor(nbt);
        this.writeAdditionalDyeData(nbt);
    }

    @Override
    public boolean isSafeNBT() {
        return true;
    }

    protected void writeDyeColor(final ValueOutput nbt) {
        if (this.color != null) {
            nbt.putInt("DyeColor", this.color.getId());
        }
    }

    protected void writeAdditionalDyeData(final ValueOutput nbt) {
    }

    @Override
    public void read(final ValueInput nbt, final boolean clientPacket) {
        super.read(nbt, clientPacket);
        final boolean dyeColorChanged = this.readDyeColor(nbt);
        final boolean additionalDyeDataChanged = this.readAdditionalDyeData(nbt, clientPacket);
        if (clientPacket && (dyeColorChanged || additionalDyeDataChanged)) {
            this.refreshRenderedModel();
        }
    }

    protected boolean readDyeColor(final ValueInput nbt) {
        final DyeColor previousColor = this.color;
        this.color = getDyeColorFromTag(nbt);
        return previousColor != this.color;
    }

    public static @Nullable DyeColor getDyeColorFromTag(final ValueInput nbt) {
        return nbt.getInt("DyeColor").map(DyeColor::byId).orElse(null);
    }

    protected boolean readAdditionalDyeData(final ValueInput nbt, final boolean clientPacket) {
        return false;
    }

    public void refreshRenderedModel() {
        if (this.hasLevel()) {
            final Level level = this.getLevel();
            final BlockPos pos = this.getPos();
            level.sendBlockUpdated(pos, this.getBlockState(), this.getBlockState(), 16);
            BnbClientHooks.queueVisualUpdate(this.getBlockEntity());
        }
    }

}
