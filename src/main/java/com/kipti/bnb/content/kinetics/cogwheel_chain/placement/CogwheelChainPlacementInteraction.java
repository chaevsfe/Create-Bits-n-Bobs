package com.kipti.bnb.content.kinetics.cogwheel_chain.placement;

import com.kipti.bnb.content.kinetics.cogwheel_chain.edit.CogwheelChainPartialEditInteractionHandler;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.CogwheelChainCandidate;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.PlacingCogwheelChain;
import com.kipti.bnb.content.kinetics.cogwheel_chain.shape.CogwheelChainInteractionHandler;
import com.kipti.bnb.content.kinetics.cogwheel_chain.types.CogwheelChainType;
import com.kipti.bnb.network.BnbNetwork;
import com.kipti.bnb.network.packets.from_client.PlaceCogwheelChainPacket;
import com.kipti.bnb.network.packets.from_client.WrenchCogwheelChainPacket;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Client-side interaction entry point for cogwheel-chain placement, removal, and partial edit entry.
 * <p>
 * Upstream drove this from NeoForge's {@code InputEvent.InteractionKeyMappingTriggered}, which fires for
 * every right click. On Fabric the same ground is covered by {@code UseBlockCallback} when a block is
 * targeted and {@code UseItemCallback} when one is not; both are registered from the client entrypoint and
 * both funnel into {@link #onRightClick(InteractionHand)}, which answers whether the click was consumed.
 */
@Environment(EnvType.CLIENT)
public final class CogwheelChainPlacementInteraction {

    private static @Nullable PlacingCogwheelChain currentBuildingChain = null;
    private static @Nullable ResourceKey<Level> currentChainLevel = null;
    private static @Nullable CogwheelChainType currentChainType = null;
    private static @Nullable Item currentChainItemType = null;

    private CogwheelChainPlacementInteraction() {
    }

    public static @Nullable PlacingCogwheelChain getCurrentBuildingChain() {
        return currentBuildingChain;
    }

    public static @Nullable ResourceKey<Level> getCurrentChainLevel() {
        return currentChainLevel;
    }

    public static @Nullable CogwheelChainType getCurrentChainType() {
        return currentChainType;
    }

    public static @Nullable Item getCurrentChainItemType() {
        return currentChainItemType;
    }

    public static void setPlacingChain(final PlacingCogwheelChain buildingChain,
                                       final ResourceKey<Level> chainLevel,
                                       final CogwheelChainType chainType,
                                       final Item chainItemType) {
        currentBuildingChain = buildingChain;
        currentChainLevel = chainLevel;
        currentChainType = chainType;
        currentChainItemType = chainItemType;
    }

    public static boolean onRightClick(final InteractionHand hand) {
        final LocalPlayer player = Minecraft.getInstance().player;
        final ClientLevel level = Minecraft.getInstance().level;
        if (player == null || level == null)
            return false;

        if (tryHandleSelectedChainInteraction(player)) {
            return true;
        }

        final ItemStack chainItemInHand = getChainItemInHand(player);

        if (chainItemInHand == null) {
            return false;
        }

        final CogwheelChainType heldChainType = CogwheelChainType.COGWHEEL_TYPE_BY_ITEM.get(chainItemInHand.getItem());
        if (heldChainType == null) {
            return false;
        }

        if (player.isShiftKeyDown() || (currentChainType != null && currentChainType != heldChainType)) {
            if (currentBuildingChain != null) {
                clearPlacingChain();
            }
            return true;
        }

        final HitResult hitResult = Minecraft.getInstance().hitResult;

        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) {
            return currentBuildingChain != null;
        }

        final BlockHitResult bhr = (BlockHitResult) hitResult;
        final BlockPos hitPos = bhr.getBlockPos();
        final BlockState targetedState = level.getBlockState(hitPos);

        final CogwheelChainCandidate targetedCandidate = CogwheelChainCandidate.getForBlock(targetedState);

        if (targetedCandidate == null) {
            return currentBuildingChain != null;
        }

        if (!heldChainType.getCogwheelPredicate().test(targetedState.getBlock())) {
            showActionBarMessage(
                    new ChainInteractionFailedException("invalid_cogwheel_type." + heldChainType.getTranslationKey()).getComponent()
            );
            return true;
        }

        rightClickForChain(hand, level, hitPos, targetedState, targetedCandidate, heldChainType, chainItemInHand);
        return true;
    }

    private static boolean tryHandleSelectedChainInteraction(final LocalPlayer player) {
        if (tryDestroyChainWithWrench(player)) {
            return true;
        }

        if (CogwheelChainPartialEditInteractionHandler.onRightClick()) {
            return true;
        }

        return CogwheelChainInteractionHandler.onUse();
    }

    private static void rightClickForChain(final InteractionHand hand,
                                           final ClientLevel level,
                                           final BlockPos hitPos,
                                           final BlockState targetedState,
                                           final CogwheelChainCandidate targetedCandidate,
                                           final CogwheelChainType heldChainType,
                                           final ItemStack chainItemInHand) {
        if (currentBuildingChain == null || currentChainLevel == null || !currentChainLevel.equals(level.dimension())) {
            CogwheelChainPartialEditInteractionHandler.clearEditState();
            setPlacingChain(
                    new PlacingCogwheelChain(
                            hitPos,
                            targetedCandidate.axis(),
                            targetedCandidate.isLarge(),
                            targetedCandidate.hasSmallCogwheelOffset()
                    ),
                    level.dimension(),
                    heldChainType,
                    chainItemInHand.getItem()
            );
            showActionBarMessage(Component.translatable("tooltip.bits_n_bobs.chain_drive_placing_hint"));
            return;
        }

        if (currentBuildingChain.getLastNode().pos().equals(hitPos)) {
            currentBuildingChain.getNodes().removeLast();

            if (currentBuildingChain.getNodes().isEmpty()) {
                clearPlacingChain();
            }
            return;
        }

        try {
            if (!currentBuildingChain.tryAddNode(hitPos, targetedState, currentChainType)) {
                return;
            }

            final boolean completed;
            try {
                completed = currentBuildingChain.tryCompleteLoop();
            } catch (final ChainInteractionFailedException exception) {
                showActionBarMessage(exception.getComponent());
                clearPlacingChain();
                return;
            }

            if (!completed) {
                return;
            }

            final LocalPlayer player = Minecraft.getInstance().player;
            final int chainsRequired = currentBuildingChain.getChainsRequiredInLoop(currentChainType);

            final boolean hasEnough = ChainConveyorBlockEntity.getChainsFromInventory(
                    player,
                    currentChainItemType.getDefaultInstance(),
                    chainsRequired,
                    true
            );

            if (!hasEnough && !player.hasInfiniteMaterials()) {
                throw new ChainInteractionFailedException("not_enough_material");
            }

            BnbNetwork.sendToServer(new PlaceCogwheelChainPacket(
                    currentBuildingChain,
                    currentChainType,
                    hand.ordinal(),
                    chainItemInHand.getItem().builtInRegistryHolder()
            ));
            clearPlacingChain();
        } catch (final ChainInteractionFailedException exception) {
            showActionBarMessage(exception.getComponent());
        }
    }

    private static boolean tryDestroyChainWithWrench(final LocalPlayer player) {
        if (!player.isShiftKeyDown())
            return false;

        if (!player.getMainHandItem().is(AllItems.WRENCH) && !player.getOffhandItem().is(AllItems.WRENCH))
            return false;

        final BlockPos controllerPos = CogwheelChainInteractionHandler.getSelectedController();
        if (controllerPos == null)
            return false;

        final float chainPosition = CogwheelChainInteractionHandler.getSelectedChainPosition();

        final ClientLevel level = Minecraft.getInstance().level;
        if (level == null || !level.isLoaded(controllerPos))
            return false;

        BnbNetwork.sendToServer(new WrenchCogwheelChainPacket(controllerPos, chainPosition));
        CogwheelChainPartialEditInteractionHandler.clearEditState();
        return true;
    }

    public static void showActionBarMessage(final Component message) {
        Minecraft.getInstance().gui.hud.setOverlayMessage(message, false);
    }

    public static @Nullable ItemStack getChainItemInHand(final LocalPlayer player) {
        return isChainDriveItem(player.getMainHandItem()) ? player.getMainHandItem() :
                isChainDriveItem(player.getOffhandItem()) ? player.getOffhandItem() : null;
    }

    public static @Nullable ItemStack getCompatibleCogwheelItemInHand(final LocalPlayer player,
                                                                     final CogwheelChainType chainType) {
        return isCompatibleCogwheelItem(player.getMainHandItem(), chainType) ? player.getMainHandItem() :
                isCompatibleCogwheelItem(player.getOffhandItem(), chainType) ? player.getOffhandItem() : null;
    }

    public static void clearPlacingChain() {
        currentBuildingChain = null;
        currentChainLevel = null;
        currentChainType = null;
        currentChainItemType = null;
    }

    public static boolean isChainDriveItem(final ItemStack stack) {
        return CogwheelChainType.COGWHEEL_TYPE_BY_ITEM.get(stack.getItem()) != null;
    }

    public static boolean isCompatibleCogwheelItem(final ItemStack stack) {
        if (!(stack.getItem() instanceof final BlockItem blockItem))
            return false;
        return CogwheelChainCandidate.getForBlock(blockItem.getBlock()) != null;
    }

    public static boolean isCompatibleCogwheelItem(final ItemStack stack, final CogwheelChainType chainType) {
        if (!(stack.getItem() instanceof final BlockItem blockItem))
            return false;
        return chainType.getCogwheelPredicate().test(blockItem.getBlock())
                && CogwheelChainCandidate.getForBlock(blockItem.getBlock()) != null;
    }
}
