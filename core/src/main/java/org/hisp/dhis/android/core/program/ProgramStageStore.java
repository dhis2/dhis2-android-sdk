package org.hisp.dhis.android.core.program;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.FormType;

import java.util.Date;

public interface ProgramStageStore {
    long insert(
            @NonNull String uid, @Nullable String code, @NonNull String name, @NonNull String displayName,
            @NonNull Date created, @NonNull Date lastUpdated, @Nullable String executionDateLabel,
            @NonNull Boolean allowGenerateNextVisit, @NonNull Boolean validCompleteOnly,
            @Nullable String reportDateToUse, @NonNull Boolean openAfterEnrollment,
            @NonNull Boolean repeatable, @NonNull Boolean captureCoordinates,
            @NonNull FormType formType, @NonNull Boolean displayGenerateEventBox,
            @NonNull Boolean generatedByEnrollmentDate, @NonNull Boolean autoGenerateEvent,
            @NonNull Integer sortOrder, @NonNull Boolean hideDueDate, @NonNull Boolean blockEntryForm,
            @NonNull Integer minDaysFromStart, @NonNull Integer standardInterval,
            @NonNull String program
    );

    void close();
}
