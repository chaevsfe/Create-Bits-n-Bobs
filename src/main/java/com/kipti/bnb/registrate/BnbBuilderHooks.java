package com.kipti.bnb.registrate;

import java.util.function.Supplier;
import com.kipti.bnb.registrate.builders.BlockBuilder;
import com.kipti.bnb.registrate.fn.NonNullConsumer;
import com.kipti.bnb.registrate.fn.NonNullUnaryOperator;
import com.zurrtum.create.api.behaviour.display.DisplaySource;
import com.zurrtum.create.api.behaviour.display.DisplayTarget;
import com.zurrtum.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.zurrtum.create.api.behaviour.movement.MovementBehaviour;
import com.zurrtum.create.content.decoration.encasing.EncasableBlock;
import com.zurrtum.create.content.decoration.encasing.EncasedBlock;
import com.zurrtum.create.content.decoration.encasing.EncasingRegistry;
import net.minecraft.world.level.block.Block;

public final class BnbBuilderHooks {

    private BnbBuilderHooks() {
    }

    public static <T extends Block> NonNullConsumer<T> movementBehaviour(final MovementBehaviour behaviour) {
        return block -> MovementBehaviour.REGISTRY.register(block, behaviour);
    }

    public static <T extends Block> NonNullConsumer<T> interactionBehaviour(final MovingInteractionBehaviour behaviour) {
        return block -> MovingInteractionBehaviour.REGISTRY.register(block, behaviour);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <E extends Block, P> NonNullUnaryOperator<BlockBuilder<E, P>> encasedVariantOf(final Supplier<? extends Block> base) {
        return builder -> builder.onRegister(encased -> EncasingRegistry.addVariant((Block & EncasableBlock) base.get(), (Block & EncasedBlock) encased));
    }

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> displaySource(final DisplaySource source) {
        return builder -> builder.onRegister(block -> DisplaySource.BY_BLOCK.register(block, java.util.List.of(source)));
    }

    public static <T extends Block, P> NonNullUnaryOperator<BlockBuilder<T, P>> displayTarget(final DisplayTarget target) {
        return builder -> builder.onRegister(block -> DisplayTarget.BY_BLOCK.register(block, target));
    }
}
