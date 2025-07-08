/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.program;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.AccessColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DBCaptureCoordinatesFromFeatureTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbProgramTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.AccessLevelColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.FeatureTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.PeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.TrackedEntityTypeWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreAttributeValuesListAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreProgramRuleVariableListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreProgramSectionListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreProgramTrackedEntityAttributeListColumnAdapter;
import org.hisp.dhis.android.core.arch.helpers.AccessHelper;
import org.hisp.dhis.android.core.attribute.AttributeValue;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;

import java.util.List;

@AutoValue
@SuppressWarnings({"PMD.ExcessivePublicCount", "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects", "PMD.GodClass"})
public abstract class Program extends BaseNameableObject
        implements CoreObject, ObjectWithStyle<Program, Program.Builder> {

    @Nullable
    public abstract Integer version();

    @Nullable
    public abstract Boolean onlyEnrollOnce();

    /**
     * @deprecated since v41, replaced by {@link #displayEnrollmentDateLabel()}
     */
    @Deprecated
    @Nullable
    public String enrollmentDateLabel() {
        return displayEnrollmentDateLabel();
    }

    @Nullable
    public abstract String displayEnrollmentDateLabel();

    @Nullable
    public abstract Boolean displayIncidentDate();

    /**
     * @deprecated since v41, replaced by {@link #displayIncidentDateLabel()}
     */
    @Deprecated
    @Nullable
    public String incidentDateLabel() {
        return displayIncidentDateLabel();
    }

    @Nullable
    public abstract String displayIncidentDateLabel();

    @Nullable
    public abstract Boolean registration();

    @Nullable
    public abstract Boolean selectEnrollmentDatesInFuture();

    @Nullable
    public abstract Boolean dataEntryMethod();

    @Nullable
    public abstract Boolean ignoreOverdueEvents();

    @Nullable
    public abstract Boolean selectIncidentDatesInFuture();

    /**
     * @deprecated since 2.29, replaced by {@link #featureType()}
     */
    @Deprecated
    @Nullable
    @ColumnAdapter(DBCaptureCoordinatesFromFeatureTypeColumnAdapter.class)
    abstract Boolean captureCoordinates();

    @Nullable
    public abstract Boolean useFirstStageDuringRegistration();

    @Nullable
    public abstract Boolean displayFrontPageList();

    @Nullable
    @ColumnAdapter(DbProgramTypeColumnAdapter.class)
    public abstract ProgramType programType();

    @Nullable
    @ColumnAdapter(IgnoreProgramTrackedEntityAttributeListColumnAdapter.class)
    abstract List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes();

    @Nullable
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid relatedProgram();

    @Nullable
    @ColumnAdapter(TrackedEntityTypeWithUidColumnAdapter.class)
    public abstract TrackedEntityType trackedEntityType();

    @Nullable
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid categoryCombo();

    public String categoryComboUid() {
        ObjectWithUid combo = categoryCombo();
        return combo == null ? CategoryCombo.DEFAULT_UID : combo.uid();
    }

    @ColumnAdapter(AccessColumnAdapter.class)
    public abstract Access access();

    @Nullable
    @ColumnAdapter(IgnoreProgramRuleVariableListColumnAdapter.class)
    abstract List<ProgramRuleVariable> programRuleVariables();

    @Nullable
    public abstract Integer expiryDays();

    @Nullable
    public abstract Integer completeEventsExpiryDays();

    @Nullable
    @ColumnAdapter(PeriodTypeColumnAdapter.class)
    public abstract PeriodType expiryPeriodType();

    @Nullable
    public abstract Integer minAttributesRequiredToSearch();

    @Nullable
    public abstract Integer maxTeiCountToReturn();

    @Nullable
    @ColumnAdapter(IgnoreProgramSectionListColumnAdapter.class)
    public abstract List<ProgramSection> programSections();

    @Nullable
    @ColumnAdapter(FeatureTypeColumnAdapter.class)
    public abstract FeatureType featureType();

    @Nullable
    @ColumnAdapter(AccessLevelColumnAdapter.class)
    public abstract AccessLevel accessLevel();

    /**
     * @deprecated since v41, replaced by {@link #displayEnrollmentLabel()}
     */
    @Deprecated
    @Nullable
    public String enrollmentLabel() {
        return displayEnrollmentLabel();
    }

    @Nullable
    public abstract String displayEnrollmentLabel();

    /**
     * @deprecated since v41, replaced by {@link #displayFollowUpLabel()}
     */
    @Deprecated
    @Nullable
    public String followUpLabel() {
        return displayFollowUpLabel();
    }

    @Nullable
    public abstract String displayFollowUpLabel();

    /**
     * @deprecated since v41, replaced by {@link #displayOrgUnitLabel()}
     */
    @Deprecated
    @Nullable
    public String orgUnitLabel() {
        return displayOrgUnitLabel();
    }

    @Nullable
    public abstract String displayOrgUnitLabel();

    /**
     * @deprecated since v41, replaced by {@link #displayRelationshipLabel()}
     */
    @Deprecated
    @Nullable
    public String relationshipLabel() {
        return displayRelationshipLabel();
    }

    @Nullable
    public abstract String displayRelationshipLabel();

    /**
     * @deprecated since v41, replaced by {@link #displayNoteLabel()}
     */
    @Deprecated
    @Nullable
    public String noteLabel() {
        return displayNoteLabel();
    }

    @Nullable
    public abstract String displayNoteLabel();

    /**
     * @deprecated since v41, replaced by {@link #displayTrackedEntityAttributeLabel()}
     */
    @Deprecated
    @Nullable
    public String trackedEntityAttributeLabel() {
        return displayTrackedEntityAttributeLabel();
    }

    @Nullable
    public abstract String displayTrackedEntityAttributeLabel();

    /**
     * @deprecated since v41, replaced by {@link #displayProgramStageLabel()}
     */
    @Deprecated
    @Nullable
    public String programStageLabel() {
        return displayProgramStageLabel();
    }

    @Nullable
    public abstract String displayProgramStageLabel();

    /**
     * @deprecated since v41, replaced by {@link #displayEventLabel()}
     */
    @Deprecated
    @Nullable
    public String eventLabel() {
        return displayEventLabel();
    }

    @Nullable
    public abstract String displayEventLabel();

    @Nullable
    @ColumnAdapter(IgnoreAttributeValuesListAdapter.class)
    public abstract List<AttributeValue> attributeValues();

    public static Builder builder() {
        return new $$AutoValue_Program.Builder();
    }

    public static Program create(Cursor cursor) {
        return $AutoValue_Program.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends BaseNameableObject.Builder<Builder>
            implements ObjectWithStyle.Builder<Program, Builder> {

        public abstract Builder id(Long id);

        public abstract Builder version(Integer version);

        public abstract Builder onlyEnrollOnce(Boolean onlyEnrollOnce);

        /**
         * @deprecated replaced by {@link #displayEnrollmentDateLabel(String displayEnrollmentDateLabel))}
         */
        public Builder enrollmentDateLabel(String enrollmentDateLabel) {
            return displayEnrollmentDateLabel(enrollmentDateLabel);
        }

        public abstract Builder displayEnrollmentDateLabel(String displayEnrollmentDateLabel);

        public abstract Builder displayIncidentDate(Boolean displayIncidentDate);

        /**
         * @deprecated replaced by {@link #displayIncidentDateLabel(String displayIncidentDateLabel))}
         */
        public Builder incidentDateLabel(String incidentDateLabel) {
            return displayIncidentDateLabel(incidentDateLabel);
        }

        public abstract Builder displayIncidentDateLabel(String displayIncidentDateLabel);

        public abstract Builder registration(Boolean registration);

        public abstract Builder selectEnrollmentDatesInFuture(Boolean selectEnrollmentDatesInFuture);

        public abstract Builder dataEntryMethod(Boolean dataEntryMethod);

        public abstract Builder ignoreOverdueEvents(Boolean ignoreOverdueEvents);

        public abstract Builder selectIncidentDatesInFuture(Boolean selectIncidentDatesInFuture);

        /**
         * @deprecated since 2.29, replaced by {@link #featureType()}
         */
        @Deprecated
        abstract Builder captureCoordinates(Boolean captureCoordinates);

        public abstract Builder useFirstStageDuringRegistration(Boolean useFirstStageDuringRegistration);

        public abstract Builder displayFrontPageList(Boolean displayFrontPageList);

        public abstract Builder programType(ProgramType programType);

        abstract Builder programTrackedEntityAttributes(List<ProgramTrackedEntityAttribute>
                                                                programTrackedEntityAttributes);

        public abstract Builder relatedProgram(ObjectWithUid relatedProgram);

        public abstract Builder trackedEntityType(TrackedEntityType trackedEntityType);

        public abstract Builder categoryCombo(ObjectWithUid categoryCombo);

        public abstract Builder access(Access access);

        abstract Builder programRuleVariables(List<ProgramRuleVariable> programRuleVariables);

        public abstract Builder expiryDays(Integer expiryDays);

        public abstract Builder completeEventsExpiryDays(Integer completeEventsExpiryDays);

        public abstract Builder expiryPeriodType(PeriodType expiryPeriodType);

        public abstract Builder minAttributesRequiredToSearch(Integer minAttributesRequiredToSearch);

        public abstract Builder maxTeiCountToReturn(Integer maxTeiCountToReturn);

        abstract Builder programSections(List<ProgramSection> programSections);

        public abstract Builder featureType(FeatureType featureType);

        public abstract Builder accessLevel(AccessLevel accessLevel);

        /**
         * @deprecated replaced by {@link #displayEnrollmentLabel(String displayEnrollmentLabel))}
         */
        public Builder enrollmentLabel(String enrollmentLabel) {
            return displayEnrollmentLabel(enrollmentLabel);
        }

        public abstract Builder displayEnrollmentLabel(String displayEnrollmentLabel);

        /**
         * @deprecated replaced by {@link #displayFollowUpLabel(String displayFollowUpLabel))}
         */
        public Builder followUpLabel(String followUpLabel) {
            return displayFollowUpLabel(followUpLabel);
        }

        public abstract Builder displayFollowUpLabel(String displayFollowUpLabel);

        /**
         * @deprecated replaced by {@link #displayOrgUnitLabel(String displayOrgUnitLabel))}
         */
        public Builder orgUnitLabel(String orgUnitLabel) {
            return displayOrgUnitLabel(orgUnitLabel);
        }

        public abstract Builder displayOrgUnitLabel(String displayOrgUnitLabel);

        /**
         * @deprecated replaced by {@link #displayRelationshipLabel(String displayRelationshipLabel))}
         */
        public Builder relationshipLabel(String relationshipLabel) {
            return displayRelationshipLabel(relationshipLabel);
        }

        public abstract Builder displayRelationshipLabel(String displayRelationshipLabel);

        /**
         * @deprecated replaced by {@link #displayNoteLabel(String displayNoteLabel))}
         */
        public Builder noteLabel(String noteLabel) {
            return displayNoteLabel(noteLabel);
        }

        public abstract Builder displayNoteLabel(String displayNoteLabel);

        /**
         * @deprecated replaced by
         * {@link #displayTrackedEntityAttributeLabel(String displayTrackedEntityAttributeLabel))}
         */
        public Builder trackedEntityAttributeLabel(String trackedEntityAttributeLabel) {
            return displayTrackedEntityAttributeLabel(trackedEntityAttributeLabel);
        }

        public abstract Builder displayTrackedEntityAttributeLabel(String displayTrackedEntityAttributeLabel);

        /**
         * @deprecated replaced by {@link #displayProgramStageLabel(String displayProgramStageLabel))}
         */
        public Builder programStageLabel(String programStageLabel) {
            return displayProgramStageLabel(programStageLabel);
        }

        public abstract Builder displayProgramStageLabel(String displayProgramStageLabel);

        /**
         * @deprecated replaced by {@link #displayEventLabel(String displayEventLabel))}
         */
        public Builder eventLabel(String eventLabel) {
            return displayEventLabel(eventLabel);
        }

        public abstract Builder displayEventLabel(String displayEventLabel);

        public abstract Builder attributeValues(List<AttributeValue> attributeValues);

        abstract Program autoBuild();

        // Auxiliary fields
        abstract Boolean captureCoordinates();

        abstract FeatureType featureType();

        abstract Access access();

        abstract AccessLevel accessLevel();

        abstract ObjectStyle style();

        public Program build() {
            if (featureType() == null) {
                if (captureCoordinates() != null) {
                    featureType(captureCoordinates() ? FeatureType.POINT : FeatureType.NONE);
                }
            } else {
                captureCoordinates(featureType() != FeatureType.NONE);
            }

            try {
                access();
            } catch (IllegalStateException e) {
                access(AccessHelper.defaultAccess());
            }

            if (accessLevel() == null) {
                accessLevel(AccessLevel.OPEN);      // Since 2.30
            }

            try {
                style();
            } catch (IllegalStateException e) {
                style(ObjectStyle.builder().build());
            }

            return autoBuild();
        }
    }
}
