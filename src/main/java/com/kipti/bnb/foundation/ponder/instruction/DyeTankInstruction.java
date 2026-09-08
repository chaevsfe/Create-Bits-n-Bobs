package com.kipti.bnb.foundation.ponder.instruction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.kipti.bnb.CreateBitsnBobs;
import com.kipti.bnb.content.decoration.dyeable.tanks.DyeableTankBehaviour;
import com.zurrtum.create.api.behaviour.BlockEntityBehaviour;
import com.zurrtum.create.client.ponder.api.element.WorldSectionElement;
import com.zurrtum.create.client.ponder.foundation.PonderScene;
import com.zurrtum.create.client.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Changes the dye color on a fluid tank block in the ponder world, updating the rendered model
 */
@Environment(EnvType.CLIENT)
public class DyeTankInstruction extends PonderInstruction {

    private final BlockPos tankPos;
    @Nullable
    private final DyeColor targetColor;

    public DyeTankInstruction(final BlockPos tankPos, @Nullable final DyeColor targetColor) {
        this.tankPos = tankPos;
        this.targetColor = targetColor;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public void tick(final PonderScene scene) {
        final Level level = scene.getLevel();
        final DyeableTankBehaviour behaviour = BlockEntityBehaviour.get(level, this.tankPos, DyeableTankBehaviour.TYPE);
        if (behaviour == null) {
            CreateBitsnBobs.LOGGER.warn(
                    "Could not find dyeable fluid tank block entity at {}, skipping instruction",
                    this.tankPos
            );
            return;
        }

        final DyeColor previousColor = behaviour.getColor();
        if (previousColor == this.targetColor) {
            return;
        }

        behaviour.applyColorClientOnly(this.targetColor);
        scene.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
    }
}
