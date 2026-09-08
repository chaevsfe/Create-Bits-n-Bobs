package com.kipti.bnb.content.trinkets.nixie.foundation;

import com.kipti.bnb.foundation.client.QuadRotator;
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

import java.util.List;
import java.util.function.BiFunction;

/**
 * For a block that can sit on any face ({@code FACING}) and additionally spin in place ({@code ORIENTATION}).
 * The blockstate JSON only carries the base model; the pair of directions is turned into a rotation here.
 */
@Environment(EnvType.CLIENT)
public class DoubleOrientedBlockModel extends WrapperBlockStateModel {

    public DoubleOrientedBlockModel(BlockState state, BlockStateModel.UnbakedRoot wrapped) {
        super(state, wrapped);
    }

    public static BiFunction<BlockState, BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> of() {
        return DoubleOrientedBlockModel::new;
    }

    @Override
    public void addPartsWithInfo(
            BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        int from = parts.size();
        super.addPartsWithInfo(level, pos, state, random, parts);

        if (!state.hasProperty(GenericNixieDisplayBlock.FACING) || !state.hasProperty(GenericNixieDisplayBlock.ORIENTATION))
            return;

        Direction up = state.getValue(GenericNixieDisplayBlock.FACING);
        Direction front = state.getValue(GenericNixieDisplayBlock.ORIENTATION);

        Matrix4f transform = new Matrix4f();
        transform.translate(0.5f, 0.5f, 0.5f);
        transform.mul(getRotation(up, front));
        transform.translate(-0.5f, -0.5f, -0.5f);

        for (int i = from; i < parts.size(); i++)
            parts.set(i, QuadRotator.transformed(parts.get(i), transform));
    }

    /** The rotation given the direction at the top of the block and the direction in front. */
    public static Matrix4f getRotation(final Direction upDir, final Direction frontDir) {
        final Direction leftDir = DoubleOrientedDirections.getLeft(upDir, frontDir);
        return new Matrix4f(
                leftDir.getStepX(), leftDir.getStepY(), leftDir.getStepZ(), 0,
                upDir.getStepX(), upDir.getStepY(), upDir.getStepZ(), 0,
                -frontDir.getStepX(), -frontDir.getStepY(), -frontDir.getStepZ(), 0,
                0, 0, 0, 1
        );
    }
}
