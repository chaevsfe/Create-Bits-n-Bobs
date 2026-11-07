package com.kipti.bnb.datafix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.fixes.AddNewChoices;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

import java.util.Map;
import java.util.function.Supplier;

public class V4538 extends NamespacedSchema {
    public V4538(final int versionKey, final Schema parent) {
        super(versionKey, parent);
    }

    public static void register(final DataFixerBuilder builder) {
        final Schema schema = builder.addSchema(4538, V4538::new);
        builder.addFixer(new AddNewChoices(schema, "Added Bits n Bobs entities", References.ENTITY));
        builder.addFixer(new AddNewChoices(schema, "Added Bits n Bobs block entities", References.BLOCK_ENTITY));
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
        schema.register(map, "bits_n_bobs:inert_stationary_contraption",
                () -> DSL.optionalFields("Contraption", com.zurrtum.create.datafix.fixes.References.CONTRAPTION.in(schema)));
        schema.register(map, "bits_n_bobs:cogwheel_chain_carriage_contraption",
                () -> DSL.optionalFields("Contraption", com.zurrtum.create.datafix.fixes.References.CONTRAPTION.in(schema)));
        return map;
    }

    @Override
    public Map<String, Supplier<TypeTemplate>> registerBlockEntities(final Schema schema) {
        final Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
        schema.register(map, "bits_n_bobs:industrial_truss_pipe",
                () -> DSL.optionalFields("Bracket", References.BLOCK_STATE.in(schema)));
        return map;
    }
}
