package com.kipti.bnb.content.kinetics.gigantic_cogwheel;

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.kipti.bnb.registry.client.BnbPartialModels;
import com.zurrtum.create.client.content.kinetics.base.KineticBlockEntityRenderer;
import com.zurrtum.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.kipti.bnb.foundation.client.ModelSprites;
import com.zurrtum.create.client.foundation.model.BakedModelHelper;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import com.zurrtum.create.catnip.registry.RegisteredObjectsHelper;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.catnip.render.StitchedSprite;
import com.zurrtum.create.client.catnip.render.SuperBufferFactory;
import com.zurrtum.create.client.catnip.render.SuperByteBuffer;
import com.zurrtum.create.client.catnip.render.SuperByteBufferCache;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Renderer for the gigantic cogwheel center block entity.
 * Generates wood-type-specific models by replacing template spruce textures
 * with the material's corresponding plank and log sprites.
 */
@Environment(EnvType.CLIENT)
public class GiganticCogwheelRenderer
		extends KineticBlockEntityRenderer<GiganticCogwheelBlockEntity, KineticBlockEntityRenderer.KineticRenderState> {

	public static final SuperByteBufferCache.Compartment<ModelKey> GIGANTIC_COGWHEEL_CACHE = new SuperByteBufferCache.Compartment<>();

	public static final StitchedSprite SPRUCE_PLANKS_TEMPLATE = new StitchedSprite(Identifier.withDefaultNamespace("block/spruce_planks"));
	public static final StitchedSprite SPRUCE_LOG_TEMPLATE = new StitchedSprite(Identifier.withDefaultNamespace("block/spruce_log"));

	private static final String[] LOG_LOCATIONS = new String[] {
		"x_log", "x_stem", "x_block",
		"wood/log/x"
	};

	public GiganticCogwheelRenderer(Context context) {
		super(context);
	}

	@Override
	protected SuperByteBuffer getRotatedModel(GiganticCogwheelBlockEntity be, KineticRenderState renderState) {
		BlockState state = be.getBlockState();
		ModelKey key = new ModelKey(state, be.getMaterial());
		return SuperByteBufferCache.getInstance().get(GIGANTIC_COGWHEEL_CACHE, key, () -> {
			BlockStateModel model = generateModel(key.material());
			Direction dir = Direction.fromAxisAndDirection(state.getValue(RotatedPillarKineticBlock.AXIS), AxisDirection.POSITIVE);
			PoseStack.Pose transform = CachedBuffers.rotateToFaceVertical(dir);
			return SuperBufferFactory.getInstance().createForBlock(model, Blocks.AIR.defaultBlockState(), transform);
		});
	}

	public static BlockStateModel generateModel(BlockState material) {
		return generateModel(BnbPartialModels.GIGANTIC_COGWHEEL.get(), material);
	}

	public static BlockStateModel generateModel(BlockStateModel template, BlockState planksBlockState) {
		String wood = plankStateToWoodName(planksBlockState);

		if (wood == null)
			return BakedModelHelper.generateModel(template, sprite -> null);

		Identifier id = RegisteredObjectsHelper.getKeyOrThrow(planksBlockState.getBlock());
		BlockState logBlockState = getLogBlockState(id.getNamespace(), wood);

		Map<TextureAtlasSprite, TextureAtlasSprite> map = new Reference2ReferenceOpenHashMap<>();
		map.put(SPRUCE_PLANKS_TEMPLATE.get(), ModelSprites.getSpriteOnSide(planksBlockState, Direction.UP));
		map.put(SPRUCE_LOG_TEMPLATE.get(), ModelSprites.getSpriteOnSide(logBlockState, Direction.SOUTH));

		return BakedModelHelper.generateModel(template, map::get);
	}

	@Nullable
	private static String plankStateToWoodName(BlockState planksBlockState) {
		Identifier id = RegisteredObjectsHelper.getKeyOrThrow(planksBlockState.getBlock());
		String path = id.getPath();

		if (path.endsWith("_planks"))
			return (path.startsWith("archwood") ? "blue_" : "") + path.substring(0, path.length() - 7);

		if (path.contains("wood/planks/"))
			return path.substring(12);

		return null;
	}

	private static BlockState getLogBlockState(String namespace, String wood) {
		for (String location : LOG_LOCATIONS) {
			Optional<BlockState> state =
				BuiltInRegistries.BLOCK.getOptional(Identifier.fromNamespaceAndPath(namespace, location.replace("x", wood)))
					.map(Block::defaultBlockState);
			if (state.isPresent())
				return state.get();
		}
		return Blocks.OAK_LOG.defaultBlockState();
	}

	public record ModelKey(BlockState state, BlockState material) {}
}
