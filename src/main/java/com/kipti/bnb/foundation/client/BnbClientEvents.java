package com.kipti.bnb.foundation.client;

import com.kipti.bnb.content.decoration.weathered_girder.WeatheredGirderWrenchBehaviour;
import com.kipti.bnb.content.kinetics.cogwheel_chain.edit.CogwheelChainPartialEditDisplayHandler;
import com.kipti.bnb.content.kinetics.cogwheel_chain.edit.CogwheelChainPartialEditInteractionHandler;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.CogwheelChainCandidate;
import com.kipti.bnb.content.kinetics.cogwheel_chain.placement.CogwheelChainPlacementEffect;
import com.kipti.bnb.content.kinetics.cogwheel_chain.placement.CogwheelChainPlacementInteraction;
import com.kipti.bnb.content.kinetics.cogwheel_chain.riding.CogwheelChainRidingHelper;
import com.kipti.bnb.content.kinetics.cogwheel_chain.riding.PlayerSkyhookRendererBridge;
import com.kipti.bnb.content.kinetics.cogwheel_chain.shape.CogwheelChainInteractionHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * The client half of the interactions upstream drove from NeoForge's client event bus.
 * <p>
 * {@code ClientTickEvent.Post} becomes {@code ClientTickEvents.END_CLIENT_TICK};
 * {@code InputEvent.InteractionKeyMappingTriggered} becomes {@code UseBlockCallback} plus
 * {@code UseItemCallback}, which between them cover a right click with and without a targeted block;
 * {@code RenderHighlightEvent.Block} and the chain's own selection outline both become one
 * {@code LevelRenderEvents.BEFORE_BLOCK_OUTLINE} listener.
 */
@Environment(EnvType.CLIENT)
public final class BnbClientEvents {

    private BnbClientEvents() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            WeatheredGirderWrenchBehaviour.tick();

            if (client.level == null || client.player == null) {
                return;
            }

            CogwheelChainInteractionHandler.clientTick();
            CogwheelChainRidingHelper.clientTick(client.player);
            CogwheelChainPartialEditDisplayHandler.tick(client.player);
            CogwheelChainPlacementEffect.tick(client.player);
        });

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> handleRightClick(player, level, hand));
        UseItemCallback.EVENT.register(BnbClientEvents::handleRightClick);

        LevelRenderEvents.BEFORE_BLOCK_OUTLINE.register(
                (context, outlineState) -> CogwheelChainInteractionHandler.beforeBlockOutline(context));

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            CogwheelChainPlacementInteraction.clearPlacingChain();
            CogwheelChainPartialEditInteractionHandler.clearEditState();
            CogwheelChainRidingHelper.reset();
            PlayerSkyhookRendererBridge.clear();
            CogwheelChainCandidate.clearCache();
        });
    }

    private static InteractionResult handleRightClick(final Player player, final Level level, final InteractionHand hand) {
        if (!level.isClientSide() || player != Minecraft.getInstance().player) {
            return InteractionResult.PASS;
        }

        return CogwheelChainPlacementInteraction.onRightClick(hand)
                ? InteractionResult.FAIL
                : InteractionResult.PASS;
    }
}
