package com.kipti.bnb.foundation.behaviour.drag;

import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntity;

@Environment(EnvType.CLIENT)
public final class DragInteractionClientHandler {

    private DragInteractionClientHandler() {
    }

    public static void register() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!level.isClientSide())
                return InteractionResult.PASS;

            final BlockEntity blockEntity = level.getBlockEntity(hitResult.getBlockPos());
            if (!(blockEntity instanceof final SmartBlockEntity sbe))
                return InteractionResult.PASS;

            final DragInteractionBehaviour behaviour = sbe.getBehaviour(DragInteractionBehaviour.TYPE);
            if (behaviour == null)
                return InteractionResult.PASS;

            Minecraft.getInstance().setScreenAndShow(new DragInteractionScreen(
                    hitResult.getBlockPos(),
                    behaviour.getValue(),
                    behaviour.getMin(),
                    behaviour.getMax()
            ));
            return InteractionResult.SUCCESS;
        });
    }
}
