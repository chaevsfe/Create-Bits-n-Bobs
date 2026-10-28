package com.kipti.bnb.mixin.client.cogwheel_material;

import com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialVisualSupport;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zurrtum.create.client.content.kinetics.base.RotatingInstance;
import com.zurrtum.create.client.flywheel.api.instance.InstanceType;
import com.zurrtum.create.client.flywheel.api.instance.Instancer;
import com.zurrtum.create.client.flywheel.api.instance.InstancerProvider;
import com.zurrtum.create.client.flywheel.api.model.Model;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.visual.AbstractBlockEntityVisual;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(targets = {"com.zurrtum.create.client.content.kinetics.simpleRelays.encased.EncasedSmallCogVisual", "com.zurrtum.create.client.content.kinetics.simpleRelays.encased.EncasedLargeCogVisual"}, remap = false)
public abstract class EncasedSplitCogVisualMaterialMixin extends AbstractBlockEntityVisual<KineticBlockEntity> implements CogwheelMaterialVisualSupport {

    @Mutable
    @Shadow
    @Final
    protected RotatingInstance rotatingModel;

    @Unique
    protected State bnb$materialState;

    protected EncasedSplitCogVisualMaterialMixin(final VisualizationContext context, final KineticBlockEntity blockEntity, final float partialTick) {
        super(context, blockEntity, partialTick);
    }

    @WrapOperation(method = "<init>(Lcom/zurrtum/create/client/flywheel/api/visualization/VisualizationContext;Lcom/zurrtum/create/content/kinetics/base/KineticBlockEntity;F)V", require = 0, at = @At(value = "INVOKE", ordinal = 0, target = "Lcom/zurrtum/create/client/flywheel/api/instance/InstancerProvider;instancer(Lcom/zurrtum/create/client/flywheel/api/instance/InstanceType;Lcom/zurrtum/create/client/flywheel/api/model/Model;)Lcom/zurrtum/create/client/flywheel/api/instance/Instancer;"))
    public Instancer<RotatingInstance> bnb$materialCog(final InstancerProvider provider, final InstanceType<RotatingInstance> type, final Model model, final Operation<Instancer<RotatingInstance>> original) {
        if (this.bnb$materialState == null)
            this.bnb$materialState = new State();
        return this.bnb$defaultKeepingInstancer(provider, type, model, this.bnb$materialState, this.blockEntity, this.blockState);
    }

    @Inject(method = "update", at = @At("HEAD"))
    public void bnb$update(final float pt, final CallbackInfo ci) {
        final RotatingInstance updated = this.bnb$defaultKeepingUpdateCog(this.bnb$materialState, this.blockEntity, this.blockState, this.instancerProvider(), this.getVisualPosition(), this.rotatingModel);
        if (updated != null) {
            this.rotatingModel = updated;
            this.relight(this.rotatingModel);
        }
    }

}
