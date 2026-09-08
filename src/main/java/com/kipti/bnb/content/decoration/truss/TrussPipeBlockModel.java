package com.kipti.bnb.content.decoration.truss;

import com.kipti.bnb.foundation.client.QuadRotator;
import com.kipti.bnb.registry.client.BnbPartialModels;
import com.zurrtum.create.client.infrastructure.model.PipeAttachmentModel;
import com.zurrtum.create.client.infrastructure.model.WrapperBlockStateModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/** A fluid pipe wearing the truss shell, with the same alternation as a plain truss block. */
@Environment(EnvType.CLIENT)
public class TrussPipeBlockModel extends PipeAttachmentModel {

    public TrussPipeBlockModel(BlockState state, BlockStateModel.UnbakedRoot wrapped) {
        super(state, wrapped);
    }

    public static BiFunction<BlockState, BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> of() {
        return TrussPipeBlockModel::new;
    }

    @Override
    public void addPartsWithInfo(
            BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        super.addPartsWithInfo(level, pos, state, random, parts);

        if (!state.hasProperty(TrussBlock.AXIS))
            return;
        Direction.Axis axis = state.getValue(TrussBlock.AXIS);

        List<BlockStateModelPart> truss = new ArrayList<>();
        WrapperBlockStateModel.addPartsWithInfo(BnbPartialModels.INDUSTRIAL_TRUSS.get(), level, pos, state, random, truss);
        if (truss.isEmpty())
            return;

        Matrix4f transform = TrussBlockModel.alternationTransform(pos.get(axis) % 2 == 0 ? axis : null, axis);
        for (BlockStateModelPart part : truss)
            parts.add(QuadRotator.transformed(part, transform));
    }
}
