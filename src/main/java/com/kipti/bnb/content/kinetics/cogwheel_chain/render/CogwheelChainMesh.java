package com.kipti.bnb.content.kinetics.cogwheel_chain.render;

import com.kipti.bnb.content.kinetics.cogwheel_chain.graph.CogwheelChain;
import com.kipti.bnb.content.kinetics.cogwheel_chain.render.CogwheelChainRenderGeometryBuilder.ChainSegment;
import com.kipti.bnb.content.kinetics.cogwheel_chain.types.CogwheelChainType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public final class CogwheelChainMesh {

    private final float[] positions;
    private final float[] uvs;
    private final float[] lightBlend;
    private final int[] vertexSegment;
    private final Vec3[] segmentFrom;
    private final Vec3[] segmentTo;
    private final int[] light;
    private final int vertexCount;
    private final float textureSquish;

    private CogwheelChainMesh(
            final float[] positions,
            final float[] uvs,
            final float[] lightBlend,
            final int[] vertexSegment,
            final Vec3[] segmentFrom,
            final Vec3[] segmentTo,
            final int vertexCount,
            final float textureSquish
    ) {
        this.positions = positions;
        this.uvs = uvs;
        this.lightBlend = lightBlend;
        this.vertexSegment = vertexSegment;
        this.segmentFrom = segmentFrom;
        this.segmentTo = segmentTo;
        this.light = new int[vertexCount];
        this.vertexCount = vertexCount;
        this.textureSquish = textureSquish;
    }

    public static @Nullable CogwheelChainMesh build(final CogwheelChain chain, final boolean flipInsideOutside) {
        final List<ChainSegment> segments = CogwheelChainRenderGeometryBuilder.buildSegments(chain, Vec3.ZERO);
        double totalChainDistance = 0;
        for (final ChainSegment segment : segments)
            totalChainDistance += segment.distance();
        if (totalChainDistance <= 1e-4)
            return null;

        final float textureSquish = (float) (Math.ceil(totalChainDistance) / totalChainDistance);
        final CogwheelChainType.ChainRenderInfo renderInfo = chain.getChainType().getRenderType();

        final Builder builder = new Builder(segments.size());
        final Matrix3f accumulatedOrientation = new Matrix3f();

        for (int segmentIndex = 0; segmentIndex < segments.size(); segmentIndex++) {
            final ChainSegment segment = segments.get(segmentIndex);

            List<Vec3> destinationPoints = CogwheelChainRenderGeometryBuilder.getEndPointsForChainJoint(
                    segment.from(), segment.to(), segment.postTo(), renderInfo, segment.toCogwheelAxis(), accumulatedOrientation);

            if (segment.fromCogwheelAxis().dot(segment.toCogwheelAxis()) < 0.99) {
                final int rotationSign =
                        segment.fromCogwheelAxis().cross(segment.toCogwheelAxis()).dot(segment.to().subtract(segment.from())) > 0 ? 1 : -1;
                accumulatedOrientation.mul(new Matrix3f(0, rotationSign, 0, -rotationSign, 0, 0, 0, 0, 1));
            }

            final List<Vec3> sourcePoints = CogwheelChainRenderGeometryBuilder.getEndPointsForChainJoint(
                    segment.preFrom(), segment.from(), segment.to(), renderInfo, segment.fromCogwheelAxis(), accumulatedOrientation);

            destinationPoints = CogwheelChainRenderGeometryBuilder.getPointsInClosestOrder(destinationPoints, sourcePoints);

            final float length = (float) segment.from().distanceTo(segment.to());
            final float minV = (float) (segment.uvStart() * textureSquish);
            final float maxV = length * textureSquish + minV;

            builder.beginSegment(segmentIndex, segment.from(), segment.to());
            ChainQuadBuilder.buildSegmentFaces(
                    destinationPoints, sourcePoints, renderInfo, minV, maxV, flipInsideOutside, builder, true);
        }

        return builder.finish(textureSquish);
    }

    public void relight(final Function<Vector3f, Integer> lighter, final BlockPos controllerPos) {
        final int segmentCount = this.segmentFrom.length;
        final int[] fromLight = new int[segmentCount];
        final int[] toLight = new int[segmentCount];
        for (int i = 0; i < segmentCount; i++) {
            fromLight[i] = lighter.apply(worldPoint(this.segmentFrom[i], controllerPos));
            toLight[i] = lighter.apply(worldPoint(this.segmentTo[i], controllerPos));
        }
        for (int vertex = 0; vertex < this.vertexCount; vertex++) {
            final int segment = this.vertexSegment[vertex];
            this.light[vertex] = lerpPackedLight(fromLight[segment], toLight[segment], this.lightBlend[vertex]);
        }
    }

    public int vertexCount() {
        return this.vertexCount;
    }

    public float textureSquish() {
        return this.textureSquish;
    }

    public float x(final int vertex) {
        return this.positions[vertex * 3];
    }

    public float y(final int vertex) {
        return this.positions[vertex * 3 + 1];
    }

    public float z(final int vertex) {
        return this.positions[vertex * 3 + 2];
    }

    public float u(final int vertex) {
        return this.uvs[vertex * 2];
    }

    public float v(final int vertex) {
        return this.uvs[vertex * 2 + 1];
    }

    public int light(final int vertex) {
        return this.light[vertex];
    }

    private static Vector3f worldPoint(final Vec3 local, final BlockPos controllerPos) {
        return new Vector3f(
                (float) (local.x + controllerPos.getX()),
                (float) (local.y + controllerPos.getY()),
                (float) (local.z + controllerPos.getZ()));
    }

    private static int lerpPackedLight(final int light1, final int light2, final float t) {
        final int block = (int) Mth.lerp(t, light1 & 0xFFFF, light2 & 0xFFFF);
        final int sky = (int) Mth.lerp(t, (light1 >> 16) & 0xFFFF, (light2 >> 16) & 0xFFFF);
        return block | (sky << 16);
    }

    private static final class Builder implements ChainQuadBuilder.VertexEmitter {

        private final ArrayList<Float> positions = new ArrayList<>();
        private final ArrayList<Float> uvs = new ArrayList<>();
        private final ArrayList<Float> lightBlend = new ArrayList<>();
        private final ArrayList<Integer> vertexSegment = new ArrayList<>();
        private final Vec3[] segmentFrom;
        private final Vec3[] segmentTo;

        private int currentSegment;
        private Vec3 currentFrom = Vec3.ZERO;
        private Vec3 currentDirection = Vec3.ZERO;
        private double currentLengthSquared;

        private Builder(final int segmentCount) {
            this.segmentFrom = new Vec3[segmentCount];
            this.segmentTo = new Vec3[segmentCount];
        }

        private void beginSegment(final int index, final Vec3 from, final Vec3 to) {
            this.currentSegment = index;
            this.currentFrom = from;
            this.currentDirection = to.subtract(from);
            this.currentLengthSquared = this.currentDirection.lengthSqr();
            this.segmentFrom[index] = from;
            this.segmentTo[index] = to;
        }

        @Override
        public void emit(final float x, final float y, final float z,
                         final float u, final float v,
                         final float nx, final float ny, final float nz) {
            this.positions.add(x);
            this.positions.add(y);
            this.positions.add(z);
            this.uvs.add(u);
            this.uvs.add(v);
            final float blend = this.currentLengthSquared > 1e-8
                    ? Mth.clamp((float) (new Vec3(x, y, z).subtract(this.currentFrom).dot(this.currentDirection) / this.currentLengthSquared), 0f, 1f)
                    : 0f;
            this.lightBlend.add(blend);
            this.vertexSegment.add(this.currentSegment);
        }

        private @Nullable CogwheelChainMesh finish(final float textureSquish) {
            final int vertexCount = this.vertexSegment.size();
            if (vertexCount == 0)
                return null;
            final float[] positionArray = new float[vertexCount * 3];
            for (int i = 0; i < positionArray.length; i++)
                positionArray[i] = this.positions.get(i);
            final float[] uvArray = new float[vertexCount * 2];
            for (int i = 0; i < uvArray.length; i++)
                uvArray[i] = this.uvs.get(i);
            final float[] blendArray = new float[vertexCount];
            final int[] segmentArray = new int[vertexCount];
            for (int i = 0; i < vertexCount; i++) {
                blendArray[i] = this.lightBlend.get(i);
                segmentArray[i] = this.vertexSegment.get(i);
            }
            return new CogwheelChainMesh(
                    positionArray, uvArray, blendArray, segmentArray, this.segmentFrom, this.segmentTo, vertexCount, textureSquish);
        }
    }
}
