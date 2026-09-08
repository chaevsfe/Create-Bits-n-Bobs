package com.kipti.bnb.registry.content;

import com.kipti.bnb.CreateBitsnBobs;
import com.zurrtum.create.foundation.advancement.CreateTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class BnbAdvancements {

    public static final Trigger DYE_FLUID_COMPONENT = register("dye_fluid_component");

    public record Trigger(CreateTrigger trigger) {
        public void awardTo(@Nullable final Player player) {
            if (player instanceof final ServerPlayer serverPlayer)
                trigger.trigger(serverPlayer);
        }
    }

    private static Trigger register(final String name) {
        final Identifier id = CreateBitsnBobs.asResource(name);
        final CreateTrigger trigger = new CreateTrigger(id);
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, id.withSuffix("_builtin"), trigger);
        return new Trigger(trigger);
    }

    public static void register() {
        if (!BuiltInRegistries.TRIGGER_TYPES.containsKey(CreateBitsnBobs.asResource("dye_fluid_component_builtin")))
            throw new IllegalStateException("Bits 'n' Bobs advancement triggers did not register");
    }

}
