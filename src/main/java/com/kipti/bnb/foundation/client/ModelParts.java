package com.kipti.bnb.foundation.client;

import com.zurrtum.create.client.infrastructure.model.WrapperBlockStateModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class ModelParts {

    private ModelParts() {
    }

    public static void addWithInfo(final BlockStateModel model, final BlockAndTintGetter level, final BlockPos pos,
                                   final BlockState state, final RandomSource random,
                                   final List<BlockStateModelPart> parts) {
        if (model instanceof final WrapperBlockStateModel wrapper)
            wrapper.addPartsWithInfo(level, pos, state, random, parts);
        else
            model.collectParts(random, parts);
    }
}
