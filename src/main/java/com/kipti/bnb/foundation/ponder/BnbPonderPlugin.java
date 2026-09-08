package com.kipti.bnb.foundation.ponder;

import com.kipti.bnb.CreateBitsnBobs;
import com.zurrtum.create.client.ponder.api.registration.PonderPlugin;
import com.zurrtum.create.client.ponder.api.registration.PonderSceneRegistrationHelper;
import com.zurrtum.create.client.ponder.api.registration.PonderTagRegistrationHelper;
import com.zurrtum.create.client.ponder.api.registration.SharedTextRegistrationHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class BnbPonderPlugin implements PonderPlugin {

    @Override
    public @NotNull String getModId() {
        return CreateBitsnBobs.MOD_ID;
    }

    @Override
    public void registerScenes(final @NotNull PonderSceneRegistrationHelper<Identifier> helper) {
        BnbPonderScenes.register(helper);
    }

    @Override
    public void registerTags(final @NotNull PonderTagRegistrationHelper<Identifier> helper) {
    }

    @Override
    public void registerSharedText(final @NotNull SharedTextRegistrationHelper helper) {
    }

}
