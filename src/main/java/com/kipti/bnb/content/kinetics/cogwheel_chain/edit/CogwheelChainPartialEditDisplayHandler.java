package com.kipti.bnb.content.kinetics.cogwheel_chain.edit;

import com.kipti.bnb.content.kinetics.cogwheel_chain.edit.CogwheelChainPartialEditInteractionHandler.ProposedPlacement;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.CogwheelChain;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.CogwheelChainCandidate;
import com.kipti.bnb.content.kinetics.cogwheel_chain.placement.ChainDriveDisplayRenderer;
import com.kipti.bnb.content.kinetics.cogwheel_chain.placement.ChainInteractionFailedException;
import com.kipti.bnb.content.kinetics.cogwheel_chain.placement.ChainPlacementPathDisplayHelper;
import com.kipti.bnb.content.kinetics.cogwheel_chain.placement.CogwheelChainPlacementInteraction;
import com.kipti.bnb.content.kinetics.cogwheel_chain.world.CogwheelChainWorld;
import com.zurrtum.create.client.content.equipment.blueprint.BlueprintOverlayRenderer;
import com.zurrtum.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import static com.kipti.bnb.content.kinetics.cogwheel_chain.placement.ChainDriveDisplayRenderer.INVALID_COLOUR;
import static com.kipti.bnb.content.kinetics.cogwheel_chain.placement.ChainDriveDisplayRenderer.VALID_COLOUR;

@Environment(EnvType.CLIENT)
public final class CogwheelChainPartialEditDisplayHandler {

    private CogwheelChainPartialEditDisplayHandler() {
    }

    public static void tick(final @Nullable LocalPlayer player) {
        if (player == null) {
            return;
        }

        final CogwheelChainPartialEdit editContext = CogwheelChainPartialEditInteractionHandler.getCurrentEditContext();
        if (editContext == null) {
            return;
        }

        final ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        if (CogwheelChainPlacementInteraction.getCompatibleCogwheelItemInHand(player, editContext.chainType()) == null) {
            CogwheelChainPartialEditInteractionHandler.clearEditState();
            return;
        }

        final CogwheelChainWorld chainWorld = CogwheelChainWorld.get(level);
        final CogwheelChain existingChain = chainWorld.getChain(editContext.controllerPos());
        if (existingChain == null) {
            CogwheelChainPartialEditInteractionHandler.clearEditState();
            return;
        }

        final ProposedPlacement placement = resolveProposedPlacement(player, level, editContext);
        CogwheelChainPartialEditInteractionHandler.setProposedPlacement(placement);
        if (placement == null) {
            return;
        }

        try {
            final CogwheelChainPartialEditInsertionPlan insertionPlan = CogwheelChainPartialEditInsertionPlanner.planWithCandidate(
                    existingChain, editContext, placement.pos(), placement.candidate()
            );
            if (insertionPlan != null) {
                CogwheelChainPlacementInteraction.showActionBarMessage(Component.empty());
                renderValidPlacement(level, placement, insertionPlan);
                renderCostOverlay(player, editContext, insertionPlan);
                return;
            }
        } catch (final ChainInteractionFailedException e) {
            CogwheelChainPlacementInteraction.showActionBarMessage(e.getComponent());
        }
        renderInvalidPlacement(level, editContext, placement);
    }

    private static void renderValidPlacement(final ClientLevel level,
                                             final ProposedPlacement placement,
                                             final CogwheelChainPartialEditInsertionPlan insertionPlan) {
        ChainDriveDisplayRenderer.renderBlockOutline(
                level,
                placement.pos(),
                placement.placementState(),
                VALID_COLOUR,
                "partial_edit_preview"
        );

        final int[] displaySides = ChainPlacementPathDisplayHelper.getPathDisplaySides(insertionPlan.rebuiltChain());
        ChainDriveDisplayRenderer.renderConnectionSegment(
                ChainPlacementPathDisplayHelper.getDisplayedSegment(
                        insertionPlan.rebuiltChain(),
                        insertionPlan.insertionIndex() - 1,
                        displaySides
                ),
                VALID_COLOUR
        );
        ChainDriveDisplayRenderer.renderConnectionSegment(
                ChainPlacementPathDisplayHelper.getDisplayedSegment(
                        insertionPlan.rebuiltChain(),
                        insertionPlan.insertionIndex(),
                        displaySides
                ),
                VALID_COLOUR
        );
    }

    private static void renderInvalidPlacement(final ClientLevel level,
                                               final CogwheelChainPartialEdit editContext,
                                               final ProposedPlacement placement) {
        ChainDriveDisplayRenderer.renderBlockOutline(
                level,
                placement.pos(),
                placement.placementState(),
                INVALID_COLOUR,
                "partial_edit_preview"
        );

        final Vec3 startCenter = editContext.startNode().center();
        final Vec3 proposedCenter = Vec3.atCenterOf(placement.pos());
        final Vec3 endCenter = editContext.endNode().center();
        ChainDriveDisplayRenderer.renderParticlesBetween(level, startCenter, proposedCenter, INVALID_COLOUR);
        ChainDriveDisplayRenderer.renderParticlesBetween(level, proposedCenter, endCenter, INVALID_COLOUR);
    }

    private static @Nullable ProposedPlacement resolveProposedPlacement(final LocalPlayer player,
                                                                       final ClientLevel level,
                                                                       final CogwheelChainPartialEdit editContext) {
        final HitResult genericHit = Minecraft.getInstance().hitResult;
        if (!(genericHit instanceof final BlockHitResult blockHit) || blockHit.getType() == HitResult.Type.MISS)
            return null;

        final ItemStack heldCogwheel = CogwheelChainPlacementInteraction.getCompatibleCogwheelItemInHand(
                player,
                editContext.chainType()
        );
        if (heldCogwheel == null || !(heldCogwheel.getItem() instanceof final BlockItem blockItem))
            return null;

        final BlockPos hitPos = blockHit.getBlockPos();
        final BlockState hitState = level.getBlockState(hitPos);
        final BlockPos placementPos;
        if (hitState.canBeReplaced()) {
            placementPos = hitPos;
        } else {
            placementPos = hitPos.relative(blockHit.getDirection());
            if (!level.getBlockState(placementPos).canBeReplaced())
                return null;
        }

        final Block cogwheelBlock = blockItem.getBlock();
        if (CogwheelChainCandidate.getForBlock(cogwheelBlock) == null)
            return null;

        final BlockPlaceContext placeContext = new BlockPlaceContext(
                new UseOnContext(
                        level, player, InteractionHand.MAIN_HAND, heldCogwheel,
                        new BlockHitResult(Vec3.atCenterOf(placementPos), blockHit.getDirection(), placementPos, false)
                ));
        final BlockState placementState = cogwheelBlock.getStateForPlacement(placeContext);
        if (placementState == null)
            return null;

        final CogwheelChainCandidate candidate = CogwheelChainCandidate.getForBlock(placementState);
        return new ProposedPlacement(placementPos, candidate, blockHit.getDirection(), placementState);
    }

    private static void renderCostOverlay(final LocalPlayer player,
                                          final CogwheelChainPartialEdit editContext,
                                          final CogwheelChainPartialEditInsertionPlan insertionPlan) {
        final int chainsRequired = insertionPlan.addedCost();
        if (player.hasInfiniteMaterials() || chainsRequired == 0) {
            return;
        }

        final boolean hasEnough = ChainConveyorBlockEntity.getChainsFromInventory(
                player,
                editContext.chainItemType().getDefaultInstance(),
                chainsRequired,
                true
        );
        BlueprintOverlayRenderer.displayChainRequirements(editContext.chainItemType(), chainsRequired, hasEnough);
    }
}
