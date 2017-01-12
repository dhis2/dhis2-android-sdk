package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;

import java.util.Date;

public interface EventModelStore {
    long insert(@NonNull String uid, @Nullable String enrollmentUid, @Nullable Date created,
                @Nullable Date lastUpdated, @Nullable String status,
                @Nullable Double latitude, @Nullable Double longitude, @Nullable String program,
                @Nullable String programStage, @Nullable String organisationUnit,
                @Nullable Date eventDate, @Nullable Date completedDate, @Nullable Date dueDate,
                @Nullable State state);

    void close();
}
