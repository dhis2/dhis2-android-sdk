package org.hisp.dhis.android.core.program;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public interface ProgramRuleVariableModelStore {
    long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
                @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated,
                @Nullable Boolean useCodeForOptionSet, @NonNull String program,
                @Nullable String programStage, @Nullable String dataElement,
                @Nullable String trackedEntityAttribute,
                @Nullable ProgramRuleVariableSourceType programRuleVariableSourceType);

    void close();
}
