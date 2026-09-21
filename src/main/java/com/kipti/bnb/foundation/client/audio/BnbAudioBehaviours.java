package com.kipti.bnb.foundation.client.audio;

import com.kipti.bnb.registry.content.BnbBlockEntities;
import com.zurrtum.create.client.AllBlockEntityBehaviours;
import com.zurrtum.create.client.foundation.blockEntity.behaviour.audio.KineticAudioBehaviour;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class BnbAudioBehaviours {

    private BnbAudioBehaviours() {
    }

    public static void register() {
        AllBlockEntityBehaviours.add(BnbBlockEntities.ENCASED_FLANGED_COGWHEEL.get(), KineticAudioBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.ENCASED_LARGE_FLANGED_COGWHEEL.get(), KineticAudioBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.ENCASED_COGWHEEL.get(), KineticAudioBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.ENCASED_LARGE_COGWHEEL.get(), KineticAudioBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.SIMPLE_KINETIC.get(), KineticAudioBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.MIGRATING_SIMPLE_KINETIC.get(), KineticAudioBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.GIGANTIC_COGWHEEL.get(), KineticAudioBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.CHAIN_ROPE_PULLEY.get(), KineticAudioBehaviour::new);
        AllBlockEntityBehaviours.add(BnbBlockEntities.FLYWHEEL_BEARING.get(), KineticAudioBehaviour::new);
    }

}
