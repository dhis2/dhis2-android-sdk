package org.hisp.dhis.android.core.program;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public interface ProgramStore {
    long insert(
            @NonNull String uid,
            @Nullable String code,
            @NonNull String name,
            @Nullable String displayName,
            @Nullable Date created,
            @Nullable Date lastUpdated,
            @Nullable String shortName,
            @Nullable String displayShortName,
            @Nullable String description,
            @Nullable String displayDescription,
            @Nullable Integer version,
            @Nullable Boolean onlyEnrollOnce,
            @Nullable String enrollmentDateLabel,
            @Nullable Boolean displayIncidentDate,
            @Nullable String incidentDateLabel,
            @Nullable Boolean registration,
            @Nullable Boolean selectEnrollmentDatesInFuture,
            @Nullable Boolean dataEntryMethod,
            @Nullable Boolean ignoreOverdueEvents,
            @Nullable Boolean relationshipFromA,
            @Nullable Boolean selectIncidentDatesInFuture,
            @Nullable Boolean captureCoordinates,
            @Nullable Boolean useFirstStageDuringRegistration,
            @Nullable Boolean displayInFrontPageList,
            @NonNull ProgramType programType,
            @Nullable String relationshipType,
            @Nullable String relationshipText,
//            @NonNull List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes,
            @Nullable String relatedProgram
            /*
            @NonNull CategoryCombo categoryCombo,
            @Nullable List<ProgramIndicator> programIndicators,
            @Nullable List<ProgramStage> programStages,
            @Nullable List<ProgramRule> programRules,
            @Nullable List<ProgramRuleVariable> programRuleVariables */
    );

    void close();
}
