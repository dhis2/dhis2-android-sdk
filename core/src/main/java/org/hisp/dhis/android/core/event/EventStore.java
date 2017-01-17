package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;

import java.util.Date;

public interface EventStore {
    long insert(@NonNull String uid,
                @Nullable String enrollmentUid,
                @Nullable Date created,
                @Nullable Date lastUpdated,
                @Nullable EventStatus status,
                @Nullable String latitude,
                @Nullable String longitude,
                @NonNull String program,
                @NonNull String programStage,
                @NonNull String organisationUnit,
                @Nullable Date eventDate,
                @Nullable Date completedDate,
                @Nullable Date dueDate,
                @Nullable State state
    );

    void close();
}
