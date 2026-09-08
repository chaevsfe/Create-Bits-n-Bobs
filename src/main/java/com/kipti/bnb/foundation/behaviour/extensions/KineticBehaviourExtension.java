package com.kipti.bnb.foundation.behaviour.extensions;

import com.zurrtum.create.content.kinetics.base.IRotate;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface KineticBehaviourExtension {

    default float propagateRotationTo(final KineticBlockEntity target,
                                      final BlockState stateFrom,
                                      final BlockState stateTo,
                                      final BlockPos diff,
                                      final boolean connectedViaAxes,
                                      final boolean connectedViaCogs) {
        return 0;
    }

    default List<BlockPos> addExtraPropagationLocations(final IRotate block,
                                                        final BlockState state,
                                                        final List<BlockPos> neighbours) {
        return List.of();
    }
}
