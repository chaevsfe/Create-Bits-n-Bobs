package com.kipti.bnb.content.decoration.cogwheel_material;

import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Shared push/pop for the cogwheel material context.
 * <p>
 * Upstream wrapped {@code SafeBlockEntityRenderer.render}, the single funnel every Create renderer went through on
 * 1.21.1. Create Fly has no such class and 26.2 builds its buffers in {@code extractRenderState}, not in a render
 * call, so the funnel moved to the two places that extract block entity render states: the vanilla dispatcher for
 * world block entities and Create's {@code BlockEntityRenderHelper} for contraption block entities.
 */
@Environment(EnvType.CLIENT)
public final class CogwheelMaterialFunnel {

    private CogwheelMaterialFunnel() {
    }

    @Nullable
    public static BlockState materialOf(@Nullable BlockEntity be) {
        if (be == null)
            return null;

        CogwheelMaterialBehaviour behaviour = BlockEntityBehaviour.get(be, CogwheelMaterialBehaviour.TYPE);
        if (behaviour == null)
            return null;

        BlockState material = behaviour.material;
        if (material == null || material.is(Blocks.SPRUCE_PLANKS))
            return null;

        if (CogwheelMaterialRenderer.getVariant(be.getBlockState()) == null)
            return null;

        return material;
    }

    public static void push(BlockState material) {
        CogwheelMaterialContext.CURRENT_RENDER_MATERIAL.set(material);
    }

    public static void pop() {
        CogwheelMaterialContext.CURRENT_RENDER_MATERIAL.remove();
    }
}
