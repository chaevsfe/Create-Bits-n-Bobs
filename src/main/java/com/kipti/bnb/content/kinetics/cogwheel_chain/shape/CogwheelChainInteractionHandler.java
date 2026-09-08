package com.kipti.bnb.content.kinetics.cogwheel_chain.shape;

import com.kipti.bnb.content.kinetics.cogwheel_chain.attachment.CogwheelChainAttachment;
import com.kipti.bnb.content.kinetics.cogwheel_chain.edit.CogwheelChainPartialEditInteractionHandler;
import com.kipti.bnb.content.kinetics.cogwheel_chain.placement.CogwheelChainPlacementInteraction;
import com.kipti.bnb.content.kinetics.cogwheel_chain.riding.CogwheelChainRidingHelper;
import com.kipti.bnb.content.kinetics.cogwheel_chain.world.CogwheelChainWorld;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.AllItemTags;
import com.zurrtum.create.AllItems;
import com.zurrtum.create.catnip.theme.Color;
import com.zurrtum.create.client.catnip.outliner.Outliner;
import com.zurrtum.create.client.foundation.utility.RaycastHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * Tracks which point of which chain the player is pointing at, and draws that selection.
 * <p>
 * Upstream ran this from {@code ClientTickEvent.Post}, {@code RenderLevelStageEvent} and
 * {@code RenderHighlightEvent.Block}. On 26.2 the selection is ticked from
 * {@code ClientTickEvents.END_CLIENT_TICK} and both the custom outline and the suppression of the
 * vanilla block highlight happen in one {@code LevelRenderEvents.BEFORE_BLOCK_OUTLINE} listener,
 * which fires every frame whether or not a block is targeted.
 */
@Environment(EnvType.CLIENT)
public final class CogwheelChainInteractionHandler {

    private static @Nullable BlockPos selectedController;
    private static float selectedChainPosition;
    private static @Nullable Vec3 selectedBakedPosition;
    private static @Nullable CogwheelChainShape selectedShape;

    private CogwheelChainInteractionHandler() {
    }

    public static @Nullable BlockPos getSelectedController() {
        return selectedController;
    }

    public static float getSelectedChainPosition() {
        return selectedChainPosition;
    }

    public static @Nullable Vec3 getSelectedBakedPosition() {
        return selectedBakedPosition;
    }

    public static @Nullable CogwheelChainShape getSelectedShape() {
        return selectedShape;
    }

    private static void clearSelection() {
        selectedController = null;
        selectedChainPosition = 0;
        selectedShape = null;
        selectedBakedPosition = null;
    }

    private static boolean invalidSelection(final Level level) {
        if (selectedController == null || selectedShape == null) {
            return true;
        }

        if (!level.isLoaded(selectedController)) {
            return true;
        }

        return !CogwheelChainWorld.get(level).containsChain(selectedController);
    }

    public static void clientTick() {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            clearSelection();
            return;
        }

        final LocalPlayer player = mc.player;
        if (!isActive(player)) {
            clearSelection();
            return;
        }

        final Vec3 origin = player.getEyePosition();
        final double range = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 1;
        final Vec3 target = RaycastHelper.getTraceTarget(player, range, origin);
        final double vanillaDistSq = mc.hitResult != null
                ? mc.hitResult.getLocation().distanceToSqr(origin)
                : Double.MAX_VALUE;

        final CogwheelChainWorld chainWorld = CogwheelChainWorld.get(mc.level);
        chainWorld.validate(mc.level);

        final ChainDriveShapeHelper.ChainShapeHit hit = ChainDriveShapeHelper.findClosestRenderedRayHit(
                mc.level, origin, target, vanillaDistSq);
        if (hit == null) {
            clearSelection();
            return;
        }

        selectedController = hit.controllerPos();
        selectedShape = hit.shape();
        selectedChainPosition = hit.chainPosition();
        selectedBakedPosition = hit.bakedPosition();

        if (!player.isShiftKeyDown() && !CogwheelChainRidingHelper.isRiding()) {
            Outliner.getInstance()
                    .chaseAABB("CogwheelChainPointSelection", new AABB(selectedBakedPosition, selectedBakedPosition))
                    .colored(Color.WHITE)
                    .lineWidth(1 / 6f)
                    .disableLineNormals();
        }
    }

    private static boolean isActive(final LocalPlayer player) {
        return player.getMainHandItem().is(AllItems.WRENCH)
                || player.getOffhandItem().is(AllItems.WRENCH)
                || player.isHolding(stack -> stack.is(AllItemTags.CHAIN_RIDEABLE))
                || CogwheelChainPlacementInteraction.isCompatibleCogwheelItem(player.getMainHandItem())
                || CogwheelChainPlacementInteraction.isCompatibleCogwheelItem(player.getOffhandItem())
                || CogwheelChainPartialEditInteractionHandler.hasActiveEditContext();
    }

    public static boolean onUse() {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return false;
        if (selectedController == null) return false;
        if (!mc.player.isHolding(stack -> stack.is(AllItemTags.CHAIN_RIDEABLE))) return false;
        if (mc.player.isShiftKeyDown()) return false;

        final CogwheelChainAttachment attachment = new CogwheelChainAttachment(
                selectedController,
                selectedChainPosition
        );
        if (!attachment.isValid(mc.level)) return false;

        CogwheelChainRidingHelper.embark(attachment);
        return true;
    }

    public static boolean beforeBlockOutline(final LevelRenderContext context) {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || invalidSelection(mc.level)) {
            clearSelection();
            return true;
        }

        final Vec3 camera = context.levelState().cameraRenderState.pos;
        if (camera == null) {
            return true;
        }

        final BlockPos controllerPos = selectedController;
        final CogwheelChainShape shape = selectedShape;
        final ChainCoordinateSpace coordinateSpace = ChainCoordinateSpace.forRender(mc.level, controllerPos);
        final PoseStack ms = context.poseStack();

        ms.pushPose();
        ms.translate(-camera.x, -camera.y, -camera.z);
        context.submitNodeCollector().submitCustomGeometry(
                ms,
                RenderTypes.lines(),
                (pose, consumer) -> shape.drawOutline(pose, consumer, coordinateSpace::toWorld)
        );
        ms.popPose();
        return false;
    }
}
