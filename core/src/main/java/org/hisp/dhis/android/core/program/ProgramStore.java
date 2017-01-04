package org.hisp.dhis.android.core.program;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.relationship.RelationshipType;

import java.util.Date;

public interface ProgramStore {
    long insert(
            @NonNull String uid,
            @NonNull String code,
            @NonNull String name,
            @NonNull String displayName,
            @NonNull Date created,
            @NonNull Date lastUpdated,
            @NonNull String shortName,
            @NonNull String displayShortName,
            @NonNull String description,
            @NonNull String displayDescription,
            @NonNull Integer version,
            @NonNull Boolean onlyEnrollOnce,
            @NonNull String enrollmentDateLabel,
            @NonNull Boolean displayIncidentDate,
            @NonNull String incidentDateLabel,
            @NonNull Boolean registration,
            @NonNull Boolean selectEnrollmentDatesInFuture,
            @NonNull Boolean dataEntryMethod,
            @NonNull Boolean ignoreOverdueEvents,
            @NonNull Boolean relationshipFromA,
            @NonNull Boolean selectIncidentDatesInFuture,
            @NonNull Boolean captureCoordinates,
            @NonNull Boolean useFirstStageDuringRegistration,
            @NonNull Boolean displayInFrontPageList,
            @NonNull ProgramType programType,
            @NonNull RelationshipType relationshipType,
            @NonNull String relationshipText,
//            @NonNull List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes,
            Program relatedProgram
            /*          CategoryCombo categoryCombo,
            @NonNull List<ProgramIndicator> programIndicators,
            @NonNull List<ProgramStage> programStages,
            List<ProgramRule> programRules,
            List<ProgramRuleVariable> programRuleVariables */
    );

    void close();
}
