package com.kipti.bnb.mixin.dyeable.create_blocks;

import com.kipti.bnb.content.decoration.dyeable.BaseDyeableBehaviour;
import com.kipti.bnb.content.decoration.dyeable.DyeableBlockItemHelper;
import com.kipti.bnb.content.decoration.dyeable.DyeableCreateBlockItems;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @WrapMethod(method = "place")
    private InteractionResult bits_n_bobs$placeWithPendingDyeColor(final BlockPlaceContext context,
                                                                  final Operation<InteractionResult> original) {
        final BehaviourType<? extends BaseDyeableBehaviour> type =
                DyeableCreateBlockItems.behaviourFor(((BlockItem) (Object) this).getBlock());
        final DyeColor dye = type == null ? null : DyeableBlockItemHelper.getOffhandDyeColor(context);
        if (dye == null)
            return original.call(context);
        DyeableBlockItemHelper.savePendingPlacementColor(context, context.getClickedPos(), dye);
        try {
            final InteractionResult result = original.call(context);
            if (result.consumesAction())
                DyeableBlockItemHelper.applyColorClientOnly(context.getLevel(), context.getClickedPos(), type, dye);
            return result;
        } finally {
            DyeableBlockItemHelper.consumePendingPlacementColor(context.getLevel(), context.getClickedPos());
        }
    }

    @Inject(method = "updateCustomBlockEntityTag(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("RETURN"))
    private void bits_n_bobs$applyDyeToBlockEntity(final BlockPos pos, final Level level,
                                                   @Nullable final Player player, final ItemStack stack,
                                                   final BlockState state,
                                                   final CallbackInfoReturnable<Boolean> cir) {
        final BehaviourType<? extends BaseDyeableBehaviour> type =
                DyeableCreateBlockItems.behaviourFor(((BlockItem) (Object) this).getBlock());
        if (type == null || player == null)
            return;
        final DyeColor dye = DyeableBlockItemHelper.getOffhandDyeColor(player);
        if (dye != null)
            DyeableBlockItemHelper.setColor(level, pos, type, dye);
    }
}
