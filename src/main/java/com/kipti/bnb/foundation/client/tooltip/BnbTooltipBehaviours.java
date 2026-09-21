package com.kipti.bnb.foundation.client.tooltip;

import com.kipti.bnb.registry.content.BnbBlockEntities;
import com.zurrtum.create.client.AllBlockEntityBehaviours;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.KineticTooltipBehaviour;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.tooltip.LinearActuatorTooltipBehaviour;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class BnbTooltipBehaviours {

    private BnbTooltipBehaviours() {
    }

    public static void register() {
        AllBlockEntityBehaviours.add(BnbBlockEntities.ENCASED_SHAFT.get(), KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.ENCASED_FLANGED_COGWHEEL.get(), KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.ENCASED_LARGE_FLANGED_COGWHEEL.get(), KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.SIMPLE_KINETIC.get(), KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.ENCASED_COGWHEEL.get(), KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.ENCASED_LARGE_COGWHEEL.get(), KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.MIGRATING_SIMPLE_KINETIC.get(), KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.GIGANTIC_COGWHEEL.get(), KineticTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.CHAIN_ROPE_PULLEY.get(), LinearActuatorTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.FLYWHEEL_BEARING.get(), BnbFlywheelBearingTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.COGWHEEL_CHAIN_CARRIAGE.get(), BnbTooltipBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.HEADLAMP.get(), BnbTooltipBehaviour::new);
    }

}
