package org.hisp.dhis.android.core.program;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public interface ProgramStageDataElementStore {
    long insert(
            @NonNull String uid, @Nullable String code, @Nullable String name,
            @Nullable String displayName, @NonNull Date created, @NonNull Date lastUpdated,
            @NonNull Boolean displayInReports, @NonNull Boolean compulsory,
            @NonNull Boolean allowProvidedElsewhere, @Nullable Integer sortOrder,
            @NonNull Boolean allowFutureDate, @NonNull String dataElement, @Nullable String programStage
    );

    void close();
}
