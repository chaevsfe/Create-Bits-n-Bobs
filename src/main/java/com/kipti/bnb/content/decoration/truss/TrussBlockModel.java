package com.kipti.bnb.content.decoration.truss;

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
import org.joml.Quaternionf;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Turns every other truss block a quarter turn about its own axis, so a run of truss alternates.
 * <p>
 * The alternation used to travel through {@code ModelData}; on 26.2 the position is handed straight to
 * {@code addPartsWithInfo}, so the parity is computed where the parts are collected.
 */
@Environment(EnvType.CLIENT)
public class TrussBlockModel extends WrapperBlockStateModel {

    public TrussBlockModel(BlockState state, BlockStateModel.UnbakedRoot wrapped) {
        super(state, wrapped);
    }

    public static BiFunction<BlockState, BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> of() {
        return TrussBlockModel::new;
    }

    @Override
    public void addPartsWithInfo(
            BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        int from = parts.size();
        super.addPartsWithInfo(level, pos, state, random, parts);

        if (!state.hasProperty(TrussBlock.AXIS))
            return;
        Direction.Axis axis = state.getValue(TrussBlock.AXIS);
        if (pos.get(axis) % 2 != 0)
            return;

        Matrix4f transform = alternationTransform(axis, null);
        for (int i = from; i < parts.size(); i++)
            parts.set(i, QuadRotator.transformed(parts.get(i), transform));
    }

    /**
     * The quarter turn about {@code axis}, optionally followed by the rotation that carries a model authored along
     * the Y axis onto {@code axisToRotateTo}.
     */
    public static Matrix4f alternationTransform(Direction.Axis axis, Direction.Axis axisToRotateTo) {
        Matrix4f transform = new Matrix4f();
        transform.translate(0.5f, 0.5f, 0.5f);
        if (axis != null)
            transform.rotate((float) Math.PI / 2,
                    axis == Direction.Axis.X ? 1 : 0,
                    axis == Direction.Axis.Y ? 1 : 0,
                    axis == Direction.Axis.Z ? 1 : 0);
        if (axisToRotateTo != null)
            transform.rotate(new Quaternionf()
                    .rotateX(axisToRotateTo == Direction.Axis.Y ? 0 : (float) (Math.PI / 2))
                    .rotateZ((float) (axisToRotateTo == Direction.Axis.X ? Math.PI / 2
                            : axisToRotateTo == Direction.Axis.Z ? Math.PI : 0)));
        transform.translate(-0.5f, -0.5f, -0.5f);
        return transform;
    }
}
