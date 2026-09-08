package com.kipti.bnb.content.kinetics.cogwheel_chain.placement;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ChainInteractionFailedException extends Exception {

    public static final String ABORTED_PLACEMENT_PREFIX = "message.bits_n_bobs.cogwheel_chain.chain_addition_aborted.";
    public static final String LANG_KEY_FORMAT = ABORTED_PLACEMENT_PREFIX + "%s";

    public ChainInteractionFailedException(final String message) {
        super(message);
    }

    public MutableComponent getTranslatedMessage() {
        return Component.translatable(ABORTED_PLACEMENT_PREFIX + this.getMessage());
    }

    public Component getComponent() {
        return this.getTranslatedMessage().withColor(0xFF_ff5d6c);
    }

}

