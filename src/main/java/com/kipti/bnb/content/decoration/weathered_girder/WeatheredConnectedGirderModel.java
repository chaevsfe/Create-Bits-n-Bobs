package com.kipti.bnb.content.decoration.weathered_girder;

import com.kipti.bnb.registry.client.BnbPartialModels;
import com.zurrtum.create.catnip.data.Iterate;
import com.zurrtum.create.client.infrastructure.model.CTModel;
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

import java.util.List;
import java.util.function.BiFunction;

/**
 * The weathered girder's connected texture plus a bracket model on each connected horizontal face. The connection
 * data used to be gathered into {@code ModelData}; it is read here, at the position the parts are collected for.
 */
@Environment(EnvType.CLIENT)
public class WeatheredConnectedGirderModel extends CTModel {

    public WeatheredConnectedGirderModel(BlockState state, BlockStateModel.UnbakedRoot wrapped) {
        super(state, wrapped, new WeatheredGirderCTBehaviour());
    }

    public static BiFunction<BlockState, BlockStateModel.UnbakedRoot, BlockStateModel.UnbakedRoot> of() {
        return WeatheredConnectedGirderModel::new;
    }

    @Override
    public void addPartsWithInfo(
            BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        super.addPartsWithInfo(level, pos, state, random, parts);

        for (Direction d : Iterate.horizontalDirections) {
            if (!WeatheredGirderBlock.isConnected(level, pos, state, d))
                continue;
            WrapperBlockStateModel.addPartsWithInfo(
                    BnbPartialModels.WEATHERED_METAL_GIRDER_BRACKETS.get(d).get(), level, pos, state, random, parts);
        }
    }
}
