package com.kipti.bnb.network.packets.to_client;

import com.kipti.bnb.network.BnbPackets;
import com.zurrtum.create.client.AllSpecialTextures;
import com.kipti.bnb.network.ClientboundPacketPayload;
import com.zurrtum.create.client.catnip.outliner.Outliner;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.shapes.Shapes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public record PeekCogwheelChainControllerHighlightPacket(BlockPos controllerPos) implements ClientboundPacketPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, PeekCogwheelChainControllerHighlightPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    PeekCogwheelChainControllerHighlightPacket::controllerPos,
                    PeekCogwheelChainControllerHighlightPacket::new
            );

    @Override
    @Environment(EnvType.CLIENT)
    public void handle(final LocalPlayer player) {
        if (!player.level().isLoaded(controllerPos)) {
            return;
        }

        Outliner.getInstance()
                .showAABB("bitsnbobsPeekCogwheelChainController", Shapes.block().bounds().move(controllerPos), 200)
                .lineWidth(1 / 16f)
                .colored(0x95CD41)
                .withFaceTexture(AllSpecialTextures.SELECTION);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return BnbPackets.PEEK_COGWHEEL_CHAIN_CONTROLLER_HIGHLIGHT;
    }
}
