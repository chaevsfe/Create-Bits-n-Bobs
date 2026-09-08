package com.kipti.bnb.registry.azimuth;

import com.kipti.bnb.foundation.behaviour.BehaviourApplicators;
import com.kipti.bnb.content.decoration.cogwheel_material.CogwheelMaterialBehaviour;
import com.kipti.bnb.content.decoration.dyeable.pipes.DyeablePipeBehaviour;
import com.kipti.bnb.content.decoration.dyeable.simple.SimpleDyeableBehaviour;
import com.kipti.bnb.content.decoration.dyeable.tanks.DyeableTankBehaviour;
import com.kipti.bnb.content.kinetics.cogwheel_chain.behaviour.CogwheelChainBehaviour;
import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.CogwheelChainCandidate;
import com.kipti.bnb.registry.core.BnbTags;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.AllBlockEntityTypes;
import com.zurrtum.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;
import java.util.function.Supplier;

public class BnbBehaviourApplicators {


    public static void register() {
        BehaviourApplicators.register(be -> {
            if (be instanceof KineticBlockEntity && CogwheelChainCandidate.isValidCandidate(be.getBlockState())) {
                return List.of(new CogwheelChainBehaviour(be));
            }
            return null;
        });
        BehaviourApplicators.register(be -> {
            if (BnbTags.BnbBlockTags.COGWHEEL_MATERIAL_CANDIDATES.matches(be.getBlockState())) {
                return List.of(new CogwheelMaterialBehaviour(be));
            }
            return null;
        });
        registerDyeablePipeBehaviours();
        registerDyeableFluidTankBehaviour();
        registerSimpleDyeableBehaviours();
    }

    private static void registerDyeablePipeBehaviours() {
        registerDyeablePipeBehaviour(() -> AllBlockEntityTypes.FLUID_PIPE);
        registerDyeablePipeBehaviour(() -> AllBlockEntityTypes.ENCASED_FLUID_PIPE);
        registerDyeablePipeBehaviour(() -> AllBlockEntityTypes.GLASS_FLUID_PIPE);
    }

    private static void registerDyeablePipeBehaviour(final Supplier<? extends BlockEntityType<?>> typeSupplier) {
        BehaviourApplicators.registerForType(typeSupplier, be -> List.of(new DyeablePipeBehaviour(be)));
    }

    private static void registerDyeableFluidTankBehaviour() {
        BehaviourApplicators.registerForType(
                () -> AllBlockEntityTypes.FLUID_TANK,
                be -> List.of(new DyeableTankBehaviour(be))
        );
    }

    private static void registerSimpleDyeableBehaviours() {
        registerSimpleDyeableBehaviour(() -> AllBlockEntityTypes.MECHANICAL_PUMP);
        registerSimpleDyeableBehaviour(() -> AllBlockEntityTypes.SMART_FLUID_PIPE);
        registerSimpleDyeableBehaviour(() -> AllBlockEntityTypes.FLUID_VALVE);
        registerSimpleDyeableBehaviour(() -> AllBlockEntityTypes.STEAM_ENGINE);
    }

    private static void registerSimpleDyeableBehaviour(final Supplier<? extends BlockEntityType<?>> typeSupplier) {
        BehaviourApplicators.registerForType(typeSupplier, be -> List.of(new SimpleDyeableBehaviour(be)));
    }


}
