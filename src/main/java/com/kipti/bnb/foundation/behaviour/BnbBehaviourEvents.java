package com.kipti.bnb.foundation.behaviour;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Delivers vanilla interaction events to the behaviours this mod attaches to other mods' block entities. */
public final class BnbBehaviourEvents {

    private BnbBehaviourEvents() {
    }

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            final BlockPos pos = hitResult.getBlockPos();
            if (!(level.getBlockEntity(pos) instanceof final SmartBlockEntity blockEntity))
                return InteractionResult.PASS;
            final BlockUseEvent event = new BlockUseEvent(level, pos, player, player.getItemInHand(hand));
            for (final BlockEntityBehaviour<?> behaviour : blockEntity.getAllBehaviours()) {
                if (behaviour instanceof final SuperBlockEntityBehaviour superBehaviour)
                    superBehaviour.onItemUse(event);
                if (event.isCanceled())
                    return event.getCancellationResult();
            }
            return InteractionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, blockEntity) -> {
            notifyBroken(level, pos, player, blockEntity);
            return true;
        });
    }

    private static void notifyBroken(final Level level, final BlockPos pos, final Player player, final BlockEntity blockEntity) {
        if (!(blockEntity instanceof final SmartBlockEntity smartBlockEntity))
            return;
        for (final BlockEntityBehaviour<?> behaviour : smartBlockEntity.getAllBehaviours())
            if (behaviour instanceof final SuperBlockEntityBehaviour superBehaviour)
                superBehaviour.onBlockBroken(player);
    }
}
