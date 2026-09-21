package com.kipti.bnb.foundation.client.tooltip;

import com.zurrtum.create.client.api.goggles.IHaveGoggleInformation;
import com.zurrtum.create.client.api.goggles.IHaveHoveringInformation;
import com.zurrtum.create.client.content.contraptions.IDisplayAssemblyExceptions;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.TooltipBehaviour;
import com.zurrtum.create.content.contraptions.AssemblyException;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class BnbTooltipBehaviour extends TooltipBehaviour<SmartBlockEntity>
        implements IHaveGoggleInformation, IHaveHoveringInformation, IDisplayAssemblyExceptions {

    public BnbTooltipBehaviour(final SmartBlockEntity blockEntity) {
        super(blockEntity);
    }

    @Override
    public boolean addToGoggleTooltip(final List<Component> tooltip, final boolean isPlayerSneaking) {
        if (!(this.blockEntity instanceof IHaveGoggleInformation goggles))
            return false;
        final int before = tooltip.size();
        return goggles.addToGoggleTooltip(tooltip, isPlayerSneaking) && tooltip.size() > before;
    }

    @Override
    public boolean addToTooltip(final List<Component> tooltip, final boolean isPlayerSneaking) {
        if (!(this.blockEntity instanceof IHaveHoveringInformation hovering))
            return false;
        final int before = tooltip.size();
        return hovering.addToTooltip(tooltip, isPlayerSneaking) && tooltip.size() > before;
    }

    @Override
    public @Nullable AssemblyException getLastAssemblyException() {
        return this.blockEntity instanceof IDisplayAssemblyExceptions display ? display.getLastAssemblyException() : null;
    }

}
