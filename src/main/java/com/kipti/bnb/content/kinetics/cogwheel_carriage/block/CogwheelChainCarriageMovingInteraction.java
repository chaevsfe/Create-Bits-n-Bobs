package com.kipti.bnb.content.kinetics.cogwheel_carriage.block;

import com.kipti.bnb.network.BnbNetwork;
import com.kipti.bnb.content.kinetics.cogwheel_carriage.contraption.CogwheelChainCarriageContraptionEntity;
import com.kipti.bnb.network.packets.from_client.CogwheelChainCarriageQueueDisassemblyPacket;
import com.zurrtum.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.zurrtum.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.core.BlockPos;
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

        BnbNetwork.sendToServer(new CogwheelChainCarriageQueueDisassemblyPacket(cccce.getId()));
        return true;
    }

}
