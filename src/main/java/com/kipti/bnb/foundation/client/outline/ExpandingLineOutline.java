package com.kipti.bnb.foundation.client.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.zurrtum.create.client.catnip.outliner.LineOutline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

@Environment(EnvType.CLIENT)
public class ExpandingLineOutline extends LineOutline {

    private final Vector3d lerpedStart = new Vector3d();
    private final Vector3d lerpedEnd = new Vector3d();

    private int growingTicksElapsed;
    private int growingTicks = 1;

    public ExpandingLineOutline setGrowingTicks(final int growingTicks) {
        this.growingTicks = Math.max(1, growingTicks);
        return this;
    }

    public ExpandingLineOutline setGrowingTicksElapsed(final int growingTicksElapsed) {
        this.growingTicksElapsed = growingTicksElapsed;
        return this;
    }

    public ExpandingLineOutline tickGrowingTicksElapsed() {
        this.growingTicksElapsed++;
        return this;
    }

    @Override
    protected void submitInner(final PoseStack ms, final SubmitNodeCollector collector, final Vec3 camera,
                               final float pt, final Vector3d start, final Vector3d end, final float width,
                               final int color, final int lightmap, final boolean disableNormals) {
        final double progress = easedProgress(pt);
        final double midX = (start.x + end.x) / 2;
        final double midY = (start.y + end.y) / 2;
        final double midZ = (start.z + end.z) / 2;

        this.lerpedStart.set(
                midX + (start.x - midX) * progress,
                midY + (start.y - midY) * progress,
                midZ + (start.z - midZ) * progress
        );
        this.lerpedEnd.set(
                midX + (end.x - midX) * progress,
                midY + (end.y - midY) * progress,
                midZ + (end.z - midZ) * progress
        );

        super.submitInner(ms, collector, camera, pt, this.lerpedStart, this.lerpedEnd, width, color, lightmap, disableNormals);
    }

    private double easedProgress(final float pt) {
        final double progress = Math.min(1d, (this.growingTicksElapsed + pt) / this.growingTicks);
        final double remaining = 1 - progress;
        return 1 - remaining * remaining * remaining;
    }
}
