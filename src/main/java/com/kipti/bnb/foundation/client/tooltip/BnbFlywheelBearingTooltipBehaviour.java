package com.kipti.bnb.foundation.client.tooltip;

import com.kipti.bnb.content.kinetics.flywheel_bearing.FlywheelBearingBlockEntity;
import com.zurrtum.create.client.content.contraptions.IDisplayAssemblyExceptions;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.GeneratingKineticTooltipBehaviour;
import com.zurrtum.create.content.contraptions.AssemblyException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BnbFlywheelBearingTooltipBehaviour extends GeneratingKineticTooltipBehaviour<FlywheelBearingBlockEntity>
        implements IDisplayAssemblyExceptions {

    public BnbFlywheelBearingTooltipBehaviour(final FlywheelBearingBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public boolean addToGoggleTooltip(final List<Component> tooltip, final boolean isPlayerSneaking) {
        final int before = tooltip.size();
        boolean added = this.blockEntity.addToGoggleTooltip(tooltip, isPlayerSneaking);
        added |= super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        return added && tooltip.size() > before;
    }

    @Override
    public boolean addToTooltip(final List<Component> tooltip, final boolean isPlayerSneaking) {
        final int before = tooltip.size();
        boolean added = super.addToTooltip(tooltip, isPlayerSneaking);
        if (!added)
            added = this.blockEntity.addToTooltip(tooltip, isPlayerSneaking);
        return added && tooltip.size() > before;
    }

    @Override
    public @Nullable AssemblyException getLastAssemblyException() {
        return this.blockEntity.getLastAssemblyException();
    }

}
