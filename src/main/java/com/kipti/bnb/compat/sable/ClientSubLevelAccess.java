package com.kipti.bnb.compat.sable;

import com.kipti.bnb.compat.sable.math.Pose3dc;
import org.jetbrains.annotations.Nullable;

public interface ClientSubLevelAccess extends SubLevelAccess {

    @Nullable
    Pose3dc renderPose();
}
