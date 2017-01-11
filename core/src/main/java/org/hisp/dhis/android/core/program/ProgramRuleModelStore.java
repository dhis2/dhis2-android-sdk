package org.hisp.dhis.android.core.program;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public interface ProgramRuleModelStore {
    long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
                @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated,
                @Nullable Integer priority, @Nullable String condition, @NonNull String program,
                @Nullable String programStage);

    void close();
}
