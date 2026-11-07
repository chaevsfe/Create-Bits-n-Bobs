package com.kipti.bnb.mixin.dyeable.create_blocks;

import com.kipti.bnb.content.decoration.dyeable.BaseDyeableBehaviour;
import com.kipti.bnb.content.decoration.dyeable.DyeableBlockItemHelper;
import com.kipti.bnb.content.decoration.dyeable.DyeableCreateBlockItems;
import com.kipti.bnb.content.decoration.dyeable.tanks.DyeableTankBehaviour;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.content.fluids.tank.FluidTankItem;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

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

    @WrapOperation(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;updateCustomBlockEntityTag(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean bits_n_bobs$applyDyeToBlockEntity(final Level level, @Nullable final Player player,
                                                      final BlockPos pos, final ItemStack stack,
                                                      final Operation<Boolean> original) {
        final boolean updated = original.call(level, player, pos, stack);
        if ((Object) this instanceof FluidTankItem) {
            DyeableBlockItemHelper.applyDyeToBlockEntity(level, pos, level.isClientSide(), player, DyeableTankBehaviour.TYPE);
            return updated;
        }
        final BehaviourType<? extends BaseDyeableBehaviour> type =
                DyeableCreateBlockItems.behaviourFor(((BlockItem) (Object) this).getBlock());
        if (type == null || player == null)
            return updated;
        final DyeColor dye = DyeableBlockItemHelper.getOffhandDyeColor(player);
        if (dye != null)
            DyeableBlockItemHelper.setColor(level, pos, type, dye);
        return updated;
    }
}
