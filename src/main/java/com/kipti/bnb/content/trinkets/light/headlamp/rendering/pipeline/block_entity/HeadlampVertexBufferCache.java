package com.kipti.bnb.content.trinkets.light.headlamp.rendering.pipeline.block_entity;

import com.kipti.bnb.foundation.client.QuadListModel;
import com.kipti.bnb.registry.client.BnbPartialModels;
import com.zurrtum.create.client.catnip.render.SuperBufferFactory;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferCache;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * One cached {@link SuperByteBuffer} per packed headlamp render state.
 * <p>
 * Upstream rebuilt a {@code MeshData} on every frame and left its own cache switched off. On 26.2 the buffer is
 * built from a model, so it goes in one of Create Fly's own compartments instead: keyed by the render-state long,
 * shared between headlamps, and dropped whenever the renderer is invalidated.
 */
@Environment(EnvType.CLIENT)
public class HeadlampVertexBufferCache {

    public static final SuperByteBufferCache.Compartment<Long> HEADLAMP = new SuperByteBufferCache.Compartment<>();

    @Nullable
    public static SuperByteBuffer getOrCreate(final long renderState) {
        return SuperByteBufferCache.getInstance().get(HEADLAMP, renderState, () -> build(renderState));
    }

    private static SuperByteBuffer build(final long renderState) {
        final List<BakedQuad> quads = HeadlampModelBuilder.buildHeadlampGeometry(renderState);
        final QuadListModel model = QuadListModel.of(quads, BnbPartialModels.HEADLAMP_OFF.get().particleMaterial());
        return SuperBufferFactory.getInstance().createForBlock(model, Blocks.AIR.defaultBlockState());
    }
}
