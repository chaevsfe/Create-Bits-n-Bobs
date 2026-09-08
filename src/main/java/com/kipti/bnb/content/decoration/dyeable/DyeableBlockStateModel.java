package com.kipti.bnb.content.decoration.dyeable;

import com.kipti.bnb.foundation.client.QuadShifter;
import com.zurrtum.create.client.catnip.render.SpriteShiftEntry;
import com.zurrtum.create.client.infrastructure.model.WrapperBlockStateModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Re-textures a Create block's model to the dye colour recorded on its block entity.
 * <p>
 * NeoForge carried the colour from the block entity to the model through {@code ModelData}, a channel 26.2 has no
 * counterpart for. Create Fly's {@link WrapperBlockStateModel} makes the channel unnecessary: {@code addPartsWithInfo}
 * is handed the level and the position, so the colour is read where the parts are collected and the shift is applied
 * to them directly. Wrapping composes, so a pipe keeps Create's own attachment model and a tank keeps its connected
 * textures, with the dye shift applied on top of whatever they produced.
 */
@Environment(EnvType.CLIENT)
public class DyeableBlockStateModel extends WrapperBlockStateModel {

    /** Which dye colour a position carries, and which sprite shifts that colour selects. */
    public interface DyeStyle {
        @Nullable
        DyeColor colorAt(BlockAndTintGetter level, BlockPos pos);

        void collectShifts(BlockState state, DyeColor color, List<SpriteShiftEntry> out);
    }

    private final DyeStyle style;

    public DyeableBlockStateModel(BlockState state, BlockStateModel.UnbakedRoot wrapped, DyeStyle style) {
        super(state, wrapped);
        this.style = style;
    }

    public static BiFunction<BlockState, BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> of(DyeStyle style) {
        return (state, wrapped) -> new DyeableBlockStateModel(state, wrapped, style);
    }

    @Override
    public void addPartsWithInfo(
            BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        int from = parts.size();
        super.addPartsWithInfo(level, pos, state, random, parts);

        DyeColor color = style.colorAt(level, pos);
        if (color == null)
            return;

        List<SpriteShiftEntry> shifts = new ArrayList<>();
        style.collectShifts(state, color, shifts);
        if (shifts.isEmpty())
            return;

        QuadShifter.shiftParts(parts, from, quad -> QuadShifter.findShift(quad, shifts));
    }

    public static void add(List<SpriteShiftEntry> out, Map<DyeColor, SpriteShiftEntry> shifts, DyeColor color) {
        SpriteShiftEntry entry = shifts.get(color);
        if (entry != null)
            out.add(entry);
    }
}
