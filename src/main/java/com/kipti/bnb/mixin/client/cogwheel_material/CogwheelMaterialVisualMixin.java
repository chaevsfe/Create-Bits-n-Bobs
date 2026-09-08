package com.kipti.bnb.mixin.client.cogwheel_material;

import com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialVisualSupport;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import com.zurrtum.create.client.content.kinetics.base.RotatingInstance;
import com.zurrtum.create.client.content.kinetics.base.SingleAxisRotatingVisual;
import com.zurrtum.create.client.content.kinetics.simpleRelays.encased.EncasedCogVisual;
import com.zurrtum.create.client.flywheel.api.instance.InstanceType;
import com.zurrtum.create.client.flywheel.api.instance.Instancer;
import com.zurrtum.create.client.flywheel.api.instance.InstancerProvider;
import com.zurrtum.create.client.flywheel.api.model.Model;
import com.zurrtum.create.client.flywheel.api.visualization.VisualizationContext;
import com.zurrtum.create.client.flywheel.lib.visual.AbstractBlockEntityVisual;
import net.minecraft.core.Direction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(value = {SingleAxisRotatingVisual.class, EncasedCogVisual.class})
public abstract class CogwheelMaterialVisualMixin extends AbstractBlockEntityVisual<KineticBlockEntity> implements CogwheelMaterialVisualSupport {

    @Mutable
    @Shadow(remap = false)
    @Final
    protected RotatingInstance rotatingModel;

    @Unique
    protected State bnb$materialState;

    protected CogwheelMaterialVisualMixin(final VisualizationContext context, final KineticBlockEntity blockEntity, final float partialTick) {
        super(context, blockEntity, partialTick);
    }

    @WrapOperation(method = "<init>(Lcom/zurrtum/create/client/flywheel/api/visualization/VisualizationContext;Lcom/zurrtum/create/content/kinetics/base/KineticBlockEntity;ZFLcom/zurrtum/create/client/flywheel/api/model/Model;)V", require = 0, at = @At(value = "INVOKE", ordinal = 0, target = "Lcom/zurrtum/create/client/flywheel/api/instance/InstancerProvider;instancer(Lcom/zurrtum/create/client/flywheel/api/instance/InstanceType;Lcom/zurrtum/create/client/flywheel/api/model/Model;)Lcom/zurrtum/create/client/flywheel/api/instance/Instancer;"))
    public Instancer<RotatingInstance> bnb$materialCog(final InstancerProvider provider, final InstanceType<RotatingInstance> type, final Model model, final Operation<Instancer<RotatingInstance>> original) {
        if (this.bnb$materialState == null)
            this.bnb$materialState = new State();
        return this.bnb$materialInstancer(provider, type, model, null, null, this.bnb$materialState, this.blockEntity, this.blockState);
    }

    @WrapOperation(method = "<init>(Lcom/zurrtum/create/client/flywheel/api/visualization/VisualizationContext;Lcom/zurrtum/create/content/kinetics/base/KineticBlockEntity;FLnet/minecraft/core/Direction;Lcom/zurrtum/create/client/flywheel/api/model/Model;)V", require = 0, at = @At(value = "INVOKE", target = "Lcom/zurrtum/create/client/flywheel/api/instance/InstancerProvider;instancer(Lcom/zurrtum/create/client/flywheel/api/instance/InstanceType;Lcom/zurrtum/create/client/flywheel/api/model/Model;)Lcom/zurrtum/create/client/flywheel/api/instance/Instancer;"))
    public Instancer<RotatingInstance> bnb$materialSingleAxis(final InstancerProvider provider, final InstanceType<RotatingInstance> type, final Model model, final Operation<Instancer<RotatingInstance>> original, @Local(argsOnly = true) final Direction from) {
        if (this.bnb$materialState == null)
            this.bnb$materialState = new State();
        return this.bnb$materialInstancer(provider, type, model, from, null, this.bnb$materialState, this.blockEntity, this.blockState);
    }

    @Inject(method = "update", at = @At("HEAD"))
    public void bnb$update(final float pt, final CallbackInfo ci) {
        final RotatingInstance updated = this.bnb$updateCog(this.bnb$materialState, this.blockEntity, this.blockState, this.instancerProvider(), this.getVisualPosition(), this.rotatingModel);
        if (updated != null) {
            this.rotatingModel = updated;
            this.relight(this.rotatingModel);
        }
    }

}
