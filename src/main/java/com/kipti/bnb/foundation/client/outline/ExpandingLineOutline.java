package com.kipti.bnb.foundation.client.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zurrtum.create.client.catnip.outliner.LineOutline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector4f;

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
    protected void renderInner(final PoseStack ms, final VertexConsumer consumer, final Vec3 camera,
                               final float pt, final float width, final Vector4f color, final int lightmap,
                               final boolean disableNormals) {
        final double progress = easedProgress(pt);
        final double midX = (this.start.x + this.end.x) / 2;
        final double midY = (this.start.y + this.end.y) / 2;
        final double midZ = (this.start.z + this.end.z) / 2;

        this.lerpedStart.set(
                midX + (this.start.x - midX) * progress,
                midY + (this.start.y - midY) * progress,
                midZ + (this.start.z - midZ) * progress
        );
        this.lerpedEnd.set(
                midX + (this.end.x - midX) * progress,
                midY + (this.end.y - midY) * progress,
                midZ + (this.end.z - midZ) * progress
        );

        bufferCuboidLine(ms, consumer, camera, this.lerpedStart, this.lerpedEnd, width, color, lightmap, disableNormals);
    }

    private double easedProgress(final float pt) {
        final double progress = Math.min(1d, (this.growingTicksElapsed + pt) / this.growingTicks);
        final double remaining = 1 - progress;
        return 1 - remaining * remaining * remaining;
    }
}
