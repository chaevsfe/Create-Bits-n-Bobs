package com.kipti.bnb.mixin_accessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.network.chat.FontDescription;

/**
 * Reaches {@code Font.getGlyphSource}, which 26.2 keeps private. It lives outside the mixin package because the
 * nixie renderer, which is ordinary code, casts a {@code Font} to it.
 */
@Environment(EnvType.CLIENT)
public interface FontAccess {
    GlyphSource bits_n_bobs$getGlyphSource(FontDescription description);
}
