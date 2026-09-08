package com.kipti.bnb.content.trinkets.nixie.foundation;

import org.joml.Matrix4f;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

public class DoubleOrientedDirections {

    public static Matrix4f getRotation(final Direction upDir, final Direction frontDir) {
        final Direction leftDir = getLeft(upDir, frontDir);
        return new Matrix4f(
                leftDir.getStepX(), leftDir.getStepY(), leftDir.getStepZ(), 0,
                upDir.getStepX(), upDir.getStepY(), upDir.getStepZ(), 0,
                -frontDir.getStepX(), -frontDir.getStepY(), -frontDir.getStepZ(), 0,
                0, 0, 0, 1
        );
    }

    public static Direction getFront(final Direction up, final Direction front) {
        if (up.getAxis() == front.getAxis()) {
            return front;
        }
        return up.getAxis() == Direction.Axis.Y ? Direction.EAST : up.getAxis() != Direction.Axis.Z ? Direction.NORTH : Direction.EAST;
    }

    public static Direction getLeft(final BlockState state) {
        final Direction up = state.getValue(GenericNixieDisplayBlock.FACING);
        final Direction front = state.getValue(GenericNixieDisplayBlock.ORIENTATION);
        return getLeft(up, front);
    }

    static Direction getDirectionByNormal(final Vec3i cross) {
        for (final Direction direction : Direction.values()) {
            if (direction.getUnitVec3i().equals(cross)) {
                return direction;
            }
        }
        return Direction.NORTH;
    }

    public static Direction getLeft(final Direction up, final Direction front) {
        return getDirectionByNormal(up.getUnitVec3i().cross(front.getUnitVec3i())).getOpposite();
    }

}

