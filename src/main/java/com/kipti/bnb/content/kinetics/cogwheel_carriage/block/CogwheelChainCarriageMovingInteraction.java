package com.kipti.bnb.content.kinetics.cogwheel_carriage.block;

import com.kipti.bnb.content.kinetics.cogwheel_carriage.contraption.CogwheelChainCarriageContraptionEntity;
import com.zurrtum.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.zurrtum.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

public class CogwheelChainCarriageMovingInteraction extends MovingInteractionBehaviour {

    @Override
    public boolean handlePlayerInteraction(final Player player,
                                           final InteractionHand activeHand,
                                           final BlockPos localPos,
                                           final AbstractContraptionEntity contraptionEntity) {
        if (!(contraptionEntity instanceof final CogwheelChainCarriageContraptionEntity cccce)) {
            return false;
        }

        if (player instanceof final ServerPlayer serverPlayer && mayDisassemble(serverPlayer, cccce)) {
            cccce.disassembleNextTick();
        }
        return true;
    }

    private static boolean mayDisassemble(final ServerPlayer player,
                                          final CogwheelChainCarriageContraptionEntity carriage) {
        if (player.isSpectator() || !player.mayBuild()) {
            return false;
        }
        if (player.level() != carriage.level()) {
            return false;
        }
        return carriage.getBoundingBox()
                .inflate(player.entityInteractionRange())
                .contains(player.getEyePosition());
    }

}
