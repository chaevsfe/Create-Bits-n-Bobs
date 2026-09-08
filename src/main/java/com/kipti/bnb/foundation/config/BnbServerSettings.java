package com.kipti.bnb.foundation.config;

import com.kipti.bnb.registry.core.BnbConfigs;
import org.jetbrains.annotations.Nullable;

/**
 * Single read point for every value of the server config.
 *
 * <p>On a server this always answers from the local config file. On a client connected to a remote
 * server it answers from the snapshot that server sent on join, so that client-side previews and
 * predictions use the same limits the server validates against. The snapshot is held apart from the
 * config objects on purpose: {@code ConfigValue.set} writes through to the JSON tree behind the
 * player's own config file, so applying a server's values to it would overwrite them on disk.
 */
public final class BnbServerSettings {

    public record Values(
            boolean flywheelStorageCapability,
            float flywheelStorageFactor,
            float flywheelTransferCapacityPerAngularMass,
            float flywheelMaxRpmFactor,
            float cogwheelChainDriveCostFactor,
            int headlampCcBlockRange,
            int cogwheelMaxBounds,
            int cogwheelMaxNodeCount
    ) {
    }

    @Nullable
    private static volatile Values synced;

    private BnbServerSettings() {
    }

    public static Values local() {
        final BnbServerConfig config = BnbConfigs.server();
        return new Values(
                config.FLYWHEEL_STORAGE_CAPACITY.get(),
                config.FLYWHEEL_STORAGE_FACTOR.getF(),
                config.FLYWHEEL_TRANSFER_CAPACITY_PER_ANGULAR_MASS.getF(),
                config.FLYWHEEL_MAX_RPM_FACTOR.getF(),
                config.COGWHEEL_CHAIN_DRIVE_COST_FACTOR.getF(),
                config.HEADLAMP_CC_BLOCK_RANGE.get(),
                config.COGWHEEL_MAX_BOUNDS.get(),
                config.COGWHEEL_MAX_NODE_COUNT.get()
        );
    }

    public static void applySynced(final Values values) {
        synced = values;
    }

    public static void clearSynced() {
        synced = null;
    }

    public static boolean isSynced() {
        return synced != null;
    }

    public static boolean flywheelStorageCapability() {
        final Values remote = synced;
        return remote != null ? remote.flywheelStorageCapability() : BnbConfigs.server().FLYWHEEL_STORAGE_CAPACITY.get();
    }

    public static float flywheelStorageFactor() {
        final Values remote = synced;
        return remote != null ? remote.flywheelStorageFactor() : BnbConfigs.server().FLYWHEEL_STORAGE_FACTOR.getF();
    }

    public static float flywheelTransferCapacityPerAngularMass() {
        final Values remote = synced;
        return remote != null ? remote.flywheelTransferCapacityPerAngularMass()
                : BnbConfigs.server().FLYWHEEL_TRANSFER_CAPACITY_PER_ANGULAR_MASS.getF();
    }

    public static float flywheelMaxRpmFactor() {
        final Values remote = synced;
        return remote != null ? remote.flywheelMaxRpmFactor() : BnbConfigs.server().FLYWHEEL_MAX_RPM_FACTOR.getF();
    }

    public static float cogwheelChainDriveCostFactor() {
        final Values remote = synced;
        return remote != null ? remote.cogwheelChainDriveCostFactor() : BnbConfigs.server().COGWHEEL_CHAIN_DRIVE_COST_FACTOR.getF();
    }

    public static int headlampCcBlockRange() {
        final Values remote = synced;
        return remote != null ? remote.headlampCcBlockRange() : BnbConfigs.server().HEADLAMP_CC_BLOCK_RANGE.get();
    }

    public static int cogwheelMaxBounds() {
        final Values remote = synced;
        return remote != null ? remote.cogwheelMaxBounds() : BnbConfigs.server().COGWHEEL_MAX_BOUNDS.get();
    }

    public static int cogwheelMaxNodeCount() {
        final Values remote = synced;
        return remote != null ? remote.cogwheelMaxNodeCount() : BnbConfigs.server().COGWHEEL_MAX_NODE_COUNT.get();
    }

}
