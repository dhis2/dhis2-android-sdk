package org.hisp.dhis.android.core.program;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.AccessColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DBCaptureCoordinatesFromFeatureTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbFormTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.FeatureTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.PeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.ValidationStrategyColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreAttributeValuesListAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreProgramStageDataElementListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreProgramStageSectionListColumnAdapter;
import org.hisp.dhis.android.core.arch.helpers.AccessHelper;
import org.hisp.dhis.android.core.attribute.AttributeValue;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValidationStrategy;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.List;

@AutoValue
@SuppressWarnings({"PMD.ExcessivePublicCount", "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects", "PMD.GodClass"})
public abstract class ProgramStage extends BaseIdentifiableObject
        implements ObjectWithStyle<ProgramStage, ProgramStage.Builder>, CoreObject {

    @Nullable
    public abstract String description();

    @Nullable
    public abstract String displayDescription();

    /**
     * @deprecated since v41, replaced by {@link #displayExecutionDateLabel()}
     */
    @Deprecated
    @Nullable
    public String executionDateLabel() {
        return displayExecutionDateLabel();
    }

    @Nullable
    public abstract String displayExecutionDateLabel();

    /**
     * @deprecated since v41, replaced by {@link #displayDueDateLabel()}
     */
    @Deprecated
    @Nullable
    public String dueDateLabel() {
        return displayDueDateLabel();
    }

    @Nullable
    public abstract String displayDueDateLabel();

    @Nullable
    public abstract Boolean allowGenerateNextVisit();

    @Nullable
    public abstract Boolean validCompleteOnly();

    @Nullable
    public abstract String reportDateToUse();

    @Nullable
    public abstract Boolean openAfterEnrollment();

    @Nullable
    public abstract Boolean repeatable();

    /**
     * @deprecated since 2.29, replaced by {@link #featureType()}
     */
    @Deprecated
    @Nullable
    @ColumnAdapter(DBCaptureCoordinatesFromFeatureTypeColumnAdapter.class)
    abstract Boolean captureCoordinates();

    @Nullable
    @ColumnAdapter(FeatureTypeColumnAdapter.class)
    public abstract FeatureType featureType();

    @Nullable
    @ColumnAdapter(DbFormTypeColumnAdapter.class)
    public abstract FormType formType();

    @Nullable
    public abstract Boolean displayGenerateEventBox();

    @Nullable
    public abstract Boolean generatedByEnrollmentDate();

    @Nullable
    public abstract Boolean autoGenerateEvent();

    @Nullable
    public abstract Integer sortOrder();

    @Nullable
    public abstract Boolean hideDueDate();

    @Nullable
    public abstract Boolean blockEntryForm();

    @Nullable
    public abstract Integer minDaysFromStart();

    @Nullable
    public abstract Integer standardInterval();

    @Nullable
    public abstract Boolean enableUserAssignment();

    @Nullable
    @ColumnAdapter(IgnoreProgramStageSectionListColumnAdapter.class)
    abstract List<ProgramStageSection> programStageSections();

    @Nullable
    @ColumnAdapter(IgnoreProgramStageDataElementListColumnAdapter.class)
    abstract List<ProgramStageDataElement> programStageDataElements();

    @Nullable
    @ColumnAdapter(PeriodTypeColumnAdapter.class)
    public abstract PeriodType periodType();

    @Nullable
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid program();

    @ColumnAdapter(AccessColumnAdapter.class)
    public abstract Access access();

    @Nullable
    public abstract Boolean remindCompleted();

    @Nullable
    @ColumnAdapter(ValidationStrategyColumnAdapter.class)
    public abstract ValidationStrategy validationStrategy();

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
        return new $$AutoValue_ProgramStage.Builder();
    }

    public static ProgramStage create(Cursor cursor) {
        return AutoValue_ProgramStage.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends BaseIdentifiableObject.Builder<Builder>
            implements ObjectWithStyle.Builder<ProgramStage, Builder> {

        public abstract Builder id(Long id);

        public abstract Builder description(String description);

        public abstract Builder displayDescription(String displayDescription);

        /**
         * @deprecated since v41, replaced by {@link #displayExecutionDateLabel(String displayExecutionDateLabel)}
         */
        public Builder executionDateLabel(String executionDateLabel) {
            return displayExecutionDateLabel(executionDateLabel);
        }

        public abstract Builder displayExecutionDateLabel(String displayExecutionDateLabel);

        /**
         * @deprecated since v41, replaced by {@link #displayDueDateLabel(String displayDueDateLabel)}
         */
        public Builder dueDateLabel(String dueDateLabel) {
            return displayDueDateLabel(dueDateLabel);
        }

        public abstract Builder displayDueDateLabel(String displayDueDateLabel);

        public abstract Builder allowGenerateNextVisit(Boolean allowGenerateNextVisit);

        public abstract Builder validCompleteOnly(Boolean validCompleteOnly);

        public abstract Builder reportDateToUse(String reportDateToUse);

        public abstract Builder openAfterEnrollment(Boolean openAfterEnrollment);

        public abstract Builder repeatable(Boolean repeatable);

        /**
         * @deprecated since 2.29, replaced by {@link #featureType(FeatureType featureType)}
         */
        abstract Builder captureCoordinates(Boolean captureCoordinates);

        public abstract Builder featureType(FeatureType featureType);

        public abstract Builder formType(FormType formType);

        public abstract Builder displayGenerateEventBox(Boolean displayGenerateEventBox);

        public abstract Builder generatedByEnrollmentDate(Boolean generatedByEnrollmentDate);

        public abstract Builder autoGenerateEvent(Boolean autoGenerateEvent);

        public abstract Builder sortOrder(Integer sortOrder);

        public abstract Builder hideDueDate(Boolean hideDueDate);

        public abstract Builder blockEntryForm(Boolean blockEntryForm);

        public abstract Builder minDaysFromStart(Integer minDaysFromStart);

        public abstract Builder standardInterval(Integer standardInterval);

        public abstract Builder enableUserAssignment(Boolean enableUserAssignment);

        abstract Builder programStageSections(List<ProgramStageSection> programStageSections);

        abstract Builder programStageDataElements(List<ProgramStageDataElement> programStageDataElements);

        public abstract Builder periodType(PeriodType periodType);

        public abstract Builder program(ObjectWithUid program);

        public abstract Builder access(Access access);

        public abstract Builder remindCompleted(Boolean remindCompleted);

        public abstract Builder validationStrategy(ValidationStrategy validationStrategy);

        /**
         * @deprecated since v41, replaced by {@link #displayProgramStageLabel(String displayProgramStageLabel)}
         */
        public Builder programStageLabel(String programStageLabel) {
            return displayProgramStageLabel(programStageLabel);
        }

        public abstract Builder displayProgramStageLabel(String displayProgramStageLabel);

        /**
         * @deprecated since v41, replaced by {@link #displayEventLabel(String displayEventLabel)}
         */
        public Builder eventLabel(String eventLabel) {
            return displayEventLabel(eventLabel);
        }

        public abstract Builder displayEventLabel(String displayEventLabel);

        public abstract Builder attributeValues(List<AttributeValue> attributeValues);

        abstract ProgramStage autoBuild();

        // Auxiliary fields
        abstract Boolean captureCoordinates();

        abstract FeatureType featureType();

        abstract Access access();

        abstract ObjectStyle style();

        abstract Boolean enableUserAssignment();

        public ProgramStage build() {
            if (featureType() == null) {
                if (captureCoordinates() != null) {
                    featureType(captureCoordinates() ? FeatureType.POINT : FeatureType.NONE);
                }
            } else {
                captureCoordinates(featureType() != FeatureType.NONE);
            }

            try {
                if (access() == null) {
                    access(AccessHelper.defaultAccess());
                }
            } catch (IllegalStateException e) {
                access(AccessHelper.defaultAccess());
            }

            try {
                style();
            } catch (IllegalStateException e) {
                style(ObjectStyle.builder().build());
            }

            if (enableUserAssignment() == null) {
                enableUserAssignment(false);
            }

            return autoBuild();
        }
    }
}
