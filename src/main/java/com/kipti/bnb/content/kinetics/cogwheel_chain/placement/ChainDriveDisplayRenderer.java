package com.kipti.bnb.content.kinetics.cogwheel_chain.placement;

import com.zurrtum.create.client.catnip.outliner.Outliner;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared display primitives for chain-drive placement and partial-edit previews.
 * <p>
 * Every method accepts an explicit colour so callers decide valid/invalid styling.
 */
@Environment(EnvType.CLIENT)
public final class ChainDriveDisplayRenderer {

    public static final int VALID_COLOUR = 0x95CD41;
    public static final int INVALID_COLOUR = 0xFF5D5D;

    private static final float PARTICLE_DENSITY = 0.1f;
    private static final float OUTLINE_LINE_WIDTH = 1f / 16f;

    private ChainDriveDisplayRenderer() {
    }

    public static void renderParticlesBetween(final ClientLevel level,
                                              final Vec3 from,
                                              final Vec3 to,
                                              final int colour) {
        if (level == null)
            return;

        final Vec3 delta = to.subtract(from);
        final double length = delta.length();
        if (length < 1.0E-3 || length > 256) {
            return;
        }

        final DustParticleOptions particle = new DustParticleOptions(colour, 1);
        final Vec3 dir = delta.normalize();
        final double step = 0.25;

        for (double t = 0; t <= length; t += step) {
            if (level.getRandom().nextFloat() > PARTICLE_DENSITY) {
                continue;
            }
            final Vec3 lerped = from.add(dir.scale(t));
            level.addParticle(particle, true, false, lerped.x, lerped.y, lerped.z, 0, 0, 0);
        }
    }

    public static void renderBlockOutline(final ClientLevel level, final BlockPos pos,
                                          final BlockState placementState, final int colour,
                                          final String keyPrefix) {
        final AtomicInteger counter = new AtomicInteger(0);
        placementState.getShape(level, pos).forAllEdges((fx, fy, fz, tx, ty, tz) ->
                Outliner.getInstance().showLine(
                                keyPrefix + "_" + counter.getAndIncrement(),
                                new Vec3(fx, fy, fz).add(Vec3.atLowerCornerOf(pos)),
                                new Vec3(tx, ty, tz).add(Vec3.atLowerCornerOf(pos))
                        )
                        .colored(colour)
                        .lineWidth(OUTLINE_LINE_WIDTH));
    }

    public static void renderBlockOutline(final ClientLevel level, final BlockPos pos,
                                          final BlockState placementState, final int colour) {
        renderBlockOutline(level, pos, placementState, colour, "chain_outline_" + pos);
    }

    public static void renderBlockOutline(final ClientLevel level, final BlockPos pos, final int colour) {
        renderBlockOutline(level, pos, level.getBlockState(pos), colour, "chain_outline_" + pos);
    }

    public static void renderConnectionSegment(final ChainPlacementPathDisplayHelper.DisplayedSegment segment,
                                               final int colour) {
        renderConnectionLine(segment.from(), segment.to(), colour);
    }

    public static void renderConnectionLine(final Vec3 from, final Vec3 to, final int colour) {
        renderParticlesBetween(Minecraft.getInstance().level, from, to, colour);
    }
}
