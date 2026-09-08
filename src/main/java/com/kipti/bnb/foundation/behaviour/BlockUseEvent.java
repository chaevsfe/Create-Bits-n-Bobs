package com.kipti.bnb.foundation.behaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/** Carries a right-click on a block to the behaviours attached to it, and their answer back. */
public class BlockUseEvent {

    private final Level level;
    private final BlockPos pos;
    private final Player player;
    private final ItemStack itemStack;

    private boolean canceled;
    private InteractionResult cancellationResult = InteractionResult.PASS;

    public BlockUseEvent(final Level level, final BlockPos pos, @Nullable final Player player, final ItemStack itemStack) {
        this.level = level;
        this.pos = pos;
        this.player = player;
        this.itemStack = itemStack;
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Nullable
    public Player getPlayer() {
        return player;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(final boolean canceled) {
        this.canceled = canceled;
    }

    public InteractionResult getCancellationResult() {
        return cancellationResult;
    }

    public void setCancellationResult(final InteractionResult result) {
        this.cancellationResult = result;
    }

    public void cancelWithResult(final InteractionResult result) {
        this.canceled = true;
        this.cancellationResult = result;
    }
}
