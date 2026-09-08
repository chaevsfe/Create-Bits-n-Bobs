package com.kipti.bnb.compat.sable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Stand-in for Sable Companion, which has no Fabric build. Every sub-level lookup answers "not in a
 * sub-level", which is the correct answer when Sable is absent.
 */
public final class SableCompanion {

    public static final SableCompanion INSTANCE = new SableCompanion();

    private SableCompanion() {
    }

    @Nullable
    public SubLevelAccess getContaining(final Level level, final BlockPos pos) {
        return null;
    }
}
