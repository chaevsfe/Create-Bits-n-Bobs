package com.kipti.bnb.mixin.datafix;

import com.kipti.bnb.datafix.V4538;
import com.mojang.datafixers.DataFixerBuilder;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.filefix.FileFixerUpper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataFixers.class)
public class DataFixersMixin {
    @Inject(
            method = "addFixers(Lcom/mojang/datafixers/DataFixerBuilder;Lnet/minecraft/util/filefix/FileFixerUpper$Builder;)V",
            at = @At(value = "CONSTANT", args = "intValue=4541")
    )
    private static void bits_n_bobs$addSchema(final DataFixerBuilder builder, final FileFixerUpper.Builder fileFixer, final CallbackInfo ci) {
        V4538.register(builder);
    }
}
