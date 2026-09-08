package com.kipti.bnb.registry.content;

import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.content.kinetics.cogwheel_carriage.contraption.CogwheelChainCarriageContraptionEntity;
import com.kipti.bnb.content.kinetics.flywheel_bearing.contraption.InertControlledContraptionEntity;
import com.kipti.bnb.foundation.data.Lang;
import com.kipti.bnb.registrate.CreateRegistrate;
import com.kipti.bnb.registrate.builders.EntityBuilder;
import com.kipti.bnb.registrate.entry.EntityEntry;
import com.kipti.bnb.registrate.fn.NonNullConsumer;
import com.zurrtum.create.content.contraptions.AbstractContraptionEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class BnbEntityTypes {

    public static final EntityEntry<InertControlledContraptionEntity> INERT_CONTROLLED_CONTRAPTION =
            contraption("inert_stationary_contraption", InertControlledContraptionEntity::new, 20, 40, false)
                    .register();

    public static final EntityEntry<CogwheelChainCarriageContraptionEntity> COGWHEEL_CHAIN_CARRIAGE_CONTRAPTION =
            contraption("cogwheel_chain_carriage_contraption", CogwheelChainCarriageContraptionEntity::new, 20, 40, false)
                    .register();

    private static <T extends Entity> EntityBuilder<T, CreateRegistrate> contraption(final String name,
                                                                                     final EntityType.EntityFactory<T> factory,
                                                                                     final int range,
                                                                                     final int updateFrequency,
                                                                                     final boolean sendVelocity) {
        return register(name, factory, MobCategory.MISC, range, updateFrequency, sendVelocity, true, AbstractContraptionEntity::build);
    }

    private static <T extends Entity> EntityBuilder<T, CreateRegistrate> register(final String name,
                                                                                  final EntityType.EntityFactory<T> factory,
                                                                                  final MobCategory group,
                                                                                  final int range,
                                                                                  final int updateFrequency,
                                                                                  final boolean sendVelocity,
                                                                                  final boolean immuneToFire,
                                                                                  final NonNullConsumer<EntityType.Builder<T>> propertyBuilder) {
        final String id = Lang.asId(name);
        return CreateBitsnBobs.REGISTRATE
                .entity(id, factory, group)
                .properties(b -> b.clientTrackingRange(range)
                        .updateInterval(updateFrequency)
                        )
                .properties(propertyBuilder)
                .properties(b -> {
                    if (immuneToFire)
                        b.fireImmune();
                });
    }

    public static void register() {
    }

}
