package com.kipti.bnb.foundation.behaviour;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Attaches behaviours to block entities the mod does not own, keyed either by a predicate over the
 * block entity or by its type. Drained by {@link com.kipti.bnb.mixin.SmartBlockEntityMixin}.
 */
public final class BehaviourApplicators {

    private record PendingTypeApplicator(Supplier<? extends BlockEntityType<?>> type,
                                         Function<SmartBlockEntity, List<BlockEntityBehaviour<?>>> applicator) {
    }

    private static final List<Function<SmartBlockEntity, List<BlockEntityBehaviour<?>>>> GENERAL = new ArrayList<>();
    private static final Map<BlockEntityType<?>, List<Function<SmartBlockEntity, List<BlockEntityBehaviour<?>>>>> BY_TYPE = new IdentityHashMap<>();
    private static final List<PendingTypeApplicator> PENDING = new ArrayList<>();

    private BehaviourApplicators() {
    }

    public static void register(final Function<SmartBlockEntity, List<BlockEntityBehaviour<?>>> applicator) {
        GENERAL.add(applicator);
    }

    public static void registerForType(final Supplier<? extends BlockEntityType<?>> type,
                                       final Function<SmartBlockEntity, List<BlockEntityBehaviour<?>>> applicator) {
        PENDING.add(new PendingTypeApplicator(type, applicator));
    }

    @SafeVarargs
    public static void registerForTypes(final Function<SmartBlockEntity, List<BlockEntityBehaviour<?>>> applicator,
                                        final Supplier<? extends BlockEntityType<?>>... types) {
        for (final Supplier<? extends BlockEntityType<?>> type : types)
            PENDING.add(new PendingTypeApplicator(type, applicator));
    }

    public static void resolveRegisteredTypes() {
        if (PENDING.isEmpty())
            return;
        for (final PendingTypeApplicator pending : PENDING)
            BY_TYPE.computeIfAbsent(pending.type().get(), ignored -> new ArrayList<>()).add(pending.applicator());
        PENDING.clear();
    }

    public static List<BlockEntityBehaviour<?>> getBehavioursFor(final SmartBlockEntity blockEntity) {
        resolveRegisteredTypes();
        final List<BlockEntityBehaviour<?>> behaviours = new ArrayList<>();
        final List<Function<SmartBlockEntity, List<BlockEntityBehaviour<?>>>> typed = BY_TYPE.get(blockEntity.getType());
        if (typed != null)
            for (final Function<SmartBlockEntity, List<BlockEntityBehaviour<?>>> applicator : typed)
                addAll(behaviours, applicator.apply(blockEntity));
        for (final Function<SmartBlockEntity, List<BlockEntityBehaviour<?>>> applicator : GENERAL)
            addAll(behaviours, applicator.apply(blockEntity));
        return behaviours;
    }

    private static void addAll(final List<BlockEntityBehaviour<?>> sink, final List<BlockEntityBehaviour<?>> applied) {
        if (applied != null)
            sink.addAll(applied);
    }
}
