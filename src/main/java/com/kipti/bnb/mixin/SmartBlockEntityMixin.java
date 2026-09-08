package com.kipti.bnb.mixin;

import com.kipti.bnb.foundation.behaviour.BehaviourApplicators;
import com.kipti.bnb.foundation.behaviour.SuperBlockEntityBehaviour;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import com.zurrtum.create.foundation.blockEntity.behaviour.BehaviourType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;


@Mixin(SmartBlockEntity.class)
public abstract class SmartBlockEntityMixin {

    @Shadow
    private boolean chunkUnloaded;

    @Shadow
    @Final
    private Map<BehaviourType<?>, BlockEntityBehaviour<?>> behaviours;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void bits_n_bobs$applyForeignBehaviours(final CallbackInfo ci) {
        for (final BlockEntityBehaviour<?> behaviour : BehaviourApplicators.getBehavioursFor((SmartBlockEntity) (Object) this))
            this.behaviours.put(behaviour.getType(), behaviour);
    }

    @Inject(method = "setRemoved", at = @At("TAIL"))
    private void bits_n_bobs$removeSuperBehaviours(final CallbackInfo ci) {
        if (this.chunkUnloaded)
            return;
        final SmartBlockEntity self = (SmartBlockEntity) (Object) this;
        for (final BlockEntityBehaviour<?> behaviour : self.getAllBehaviours())
            if (behaviour instanceof final SuperBlockEntityBehaviour superBehaviour)
                superBehaviour.remove();
    }
}
