package com.kipti.bnb.foundation.display;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.zurrtum.create.foundation.utility.DynamicComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Holds a display link's raw text and its resolved form. Create Fly's own DynamicComponent keeps no
 * state and exposes only the parser, so the nixie displays carry their own.
 */
public class BnbDynamicComponent {

    @Nullable
    private JsonElement rawCustomText;
    @Nullable
    private Component parsedCustomText;

    public boolean isValid() {
        return parsedCustomText != null;
    }

    public boolean sameAs(final String tagElement) {
        if (rawCustomText == null || tagElement == null)
            return false;
        return rawCustomText.toString().equals(parse(tagElement).toString());
    }

    @Nullable
    public Component get() {
        return parsedCustomText;
    }

    public void displayCustomText(final Level level, final BlockPos pos, final String tagElement) {
        rawCustomText = parse(tagElement);
        parsedCustomText = deserialize(rawCustomText, level.registryAccess());
        if (parsedCustomText != null)
            parsedCustomText = DynamicComponent.parseCustomText(level, pos, parsedCustomText);
    }

    public void setValueToLiteral(final String value, final HolderLookup.Provider registryAccess) {
        parsedCustomText = Component.literal(value);
        rawCustomText = serialize(parsedCustomText, registryAccess);
    }

    public void write(final CompoundTag tag, final HolderLookup.Provider registryAccess) {
        if (rawCustomText != null)
            tag.putString("RawCustomText", rawCustomText.toString());
        if (parsedCustomText != null) {
            final JsonElement parsed = serialize(parsedCustomText, registryAccess);
            if (parsed != null)
                tag.putString("ParsedCustomText", parsed.toString());
        }
    }

    public void read(final BlockPos pos, final CompoundTag tag, final HolderLookup.Provider registryAccess) {
        rawCustomText = tag.getString("RawCustomText").map(BnbDynamicComponent::parse).orElse(null);
        parsedCustomText = tag.getString("ParsedCustomText")
                .map(BnbDynamicComponent::parse)
                .map(json -> deserialize(json, registryAccess))
                .orElse(null);
    }

    public static String toJson(final Component component, final HolderLookup.Provider registryAccess) {
        final JsonElement json = serialize(component, registryAccess);
        return json == null ? "{}" : json.toString();
    }

    private static JsonElement parse(final String value) {
        return JsonParser.parseString(value);
    }

    @Nullable
    private static Component deserialize(@Nullable final JsonElement json, final HolderLookup.Provider registryAccess) {
        if (json == null)
            return null;
        return ComponentSerialization.CODEC
                .parse(RegistryOps.create(JsonOps.INSTANCE, registryAccess), json)
                .result()
                .orElse(null);
    }

    @Nullable
    private static JsonElement serialize(final Component component, final HolderLookup.Provider registryAccess) {
        return ComponentSerialization.CODEC
                .encodeStart(RegistryOps.create(JsonOps.INSTANCE, registryAccess), component)
                .result()
                .orElse(null);
    }
}
