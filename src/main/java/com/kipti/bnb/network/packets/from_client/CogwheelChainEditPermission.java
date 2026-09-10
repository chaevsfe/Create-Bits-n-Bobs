package com.kipti.bnb.network.packets.from_client;

import com.kipti.bnb.content.kinetics.cogwheel_chain.behaviour.CogwheelChainBehaviour;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.CogwheelChain;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.PathedCogwheelNode;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.PlacingCogwheelChain;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.PlacingCogwheelNode;
import com.kipti.bnb.foundation.behaviour.SuperBlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public final class CogwheelChainEditPermission {

    private CogwheelChainEditPermission() {
    }

    public static boolean mayEditPosition(final ServerLevel level, final ServerPlayer player, final BlockPos pos) {
        return level.mayInteract(player, pos);
    }

    public static boolean mayEditChain(final ServerLevel level,
                                       final ServerPlayer player,
                                       final BlockPos controllerPos,
                                       final CogwheelChain chain) {
        if (!mayEditPosition(level, player, controllerPos))
            return false;

        for (final PathedCogwheelNode node : chain.getChainPathCogwheelNodes()) {
            if (!mayEditPosition(level, player, controllerPos.offset(node.localPos())))
                return false;
        }
        return true;
    }

    public static boolean mayEditChain(final ServerLevel level,
                                       final ServerPlayer player,
                                       final PlacingCogwheelChain chain) {
        for (final PlacingCogwheelNode node : chain.getNodes()) {
            if (!mayEditPosition(level, player, node.pos()))
                return false;
        }
        return true;
    }

    public static boolean mayEditChainOf(final ServerLevel level,
                                         final ServerPlayer player,
                                         final CogwheelChainBehaviour member) {
        final BlockPos memberPos = member.getPos();
        if (!mayEditPosition(level, player, memberPos))
            return false;

        final CogwheelChainBehaviour controller = resolveController(level, member);
        if (controller == null)
            return true;

        final CogwheelChain chain = controller.getControlledChain();
        if (chain == null)
            return mayEditPosition(level, player, controller.getPos());

        return mayEditChain(level, player, controller.getPos(), chain);
    }

    private static @Nullable CogwheelChainBehaviour resolveController(final ServerLevel level,
                                                                      final CogwheelChainBehaviour member) {
        if (member.isController())
            return member;

        final Vec3i controllerOffset = member.getControllerOffset();
        if (controllerOffset == null)
            return null;

        return SuperBlockEntityBehaviour.get(
                level,
                member.getPos().offset(controllerOffset),
                CogwheelChainBehaviour.TYPE
        );
    }

}
