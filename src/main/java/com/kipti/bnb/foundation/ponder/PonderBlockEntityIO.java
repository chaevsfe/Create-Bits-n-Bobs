package com.kipti.bnb.foundation.ponder;

import com.kipti.bnb.CreateBitsnBobs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;

@Environment(EnvType.CLIENT)
public final class PonderBlockEntityIO {

    private PonderBlockEntityIO() {
    }

    public static CompoundTag save(final BlockEntity blockEntity, final Level level) {
        return blockEntity.saveWithFullMetadata(level.registryAccess());
    }

    public static void load(final BlockEntity blockEntity, final CompoundTag tag, final Level level) {
        final ProblemReporter.Collector collector = new ProblemReporter.Collector();
        blockEntity.loadWithComponents(TagValueInput.create(collector, level.registryAccess(), tag));
        if (!collector.isEmpty()) {
            CreateBitsnBobs.LOGGER.warn("Ponder block entity data at {} did not read cleanly: {}",
                    blockEntity.getBlockPos(), collector.getReport());
        }
    }
}
