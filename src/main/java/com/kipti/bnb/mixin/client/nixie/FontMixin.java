package com.kipti.bnb.mixin.client.nixie;

import com.kipti.bnb.mixin_accessor.FontAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GlyphSource;
import net.minecraft.network.chat.FontDescription;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Environment(EnvType.CLIENT)
@Mixin(Font.class)
public interface FontMixin extends FontAccess {

    @Invoker("getGlyphSource")
    @Override
    GlyphSource bits_n_bobs$getGlyphSource(FontDescription description);
}
