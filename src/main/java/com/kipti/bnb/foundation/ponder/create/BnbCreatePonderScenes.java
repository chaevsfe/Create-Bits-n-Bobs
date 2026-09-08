package com.kipti.bnb.foundation.ponder.create;

import com.kipti.bnb.registry.content.blocks.BnbKineticBlocks;
import com.zurrtum.create.client.infrastructure.ponder.AllCreatePonderTags;
import com.zurrtum.create.client.infrastructure.ponder.scenes.PulleyScenes;
import com.kipti.bnb.registrate.entry.ItemProviderEntry;
import com.kipti.bnb.registrate.entry.RegistryEntry;
import com.zurrtum.create.client.ponder.api.registration.PonderSceneRegistrationHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class BnbCreatePonderScenes {

    public static void register(final PonderSceneRegistrationHelper<Identifier> helper) {
        final PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);

        HELPER.forComponents(BnbKineticBlocks.CHAIN_PULLEY)
                .addStoryBoard("rope_pulley/anchor", PulleyScenes::movement, AllCreatePonderTags.KINETIC_APPLIANCES, AllCreatePonderTags.MOVEMENT_ANCHOR)
                .addStoryBoard("rope_pulley/modes", PulleyScenes::movementModes)
                .addStoryBoard("rope_pulley/multi_rope", PulleyScenes::multiRope)
                .addStoryBoard("rope_pulley/attachment", PulleyScenes::attachment);
    }

}

