package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseIdentifiableObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramStageSection.Builder.class)
public abstract class ProgramStageSection extends BaseIdentifiableObject {

    public static final Comparator<ProgramStageSection>
            SORT_ORDER_COMPARATOR = new SortOrderComparator();

    private static final String PROGRAM_INDICATORS = "programIndicators";
    private static final String PROGRAM_STAGE_DATA_ELEMENTS = "programStageDataElements";
    private static final String SORT_ORDER = "sortOrder";

    @Nullable
    @JsonProperty(PROGRAM_INDICATORS)
    public abstract List<ProgramIndicator> programIndicators();

    @Nullable
    @JsonProperty(PROGRAM_STAGE_DATA_ELEMENTS)
    public abstract List<ProgramStageDataElement> programStageDataElements();

    @Nullable
    @JsonProperty(SORT_ORDER)
    public abstract Integer sortOrder();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {

        @JsonProperty(PROGRAM_INDICATORS)
        public abstract Builder programIndicators(@Nullable List<ProgramIndicator> programIndicators);

        @JsonProperty(PROGRAM_STAGE_DATA_ELEMENTS)
        public abstract Builder programStageDataElements(@Nullable List<ProgramStageDataElement> programStageDataElements);

        @JsonProperty(SORT_ORDER)
        public abstract Builder sortOrder(@Nullable Integer sortOrder);

        abstract List<ProgramIndicator> programIndicators();

        abstract List<ProgramStageDataElement> programStageDataElements();

        abstract ProgramStageSection autoBuild();

        public ProgramStageSection build() {
            if (programIndicators() != null) {
                programIndicators(Collections.unmodifiableList(programIndicators()));
            }
            if (programStageDataElements() != null) {
                programStageDataElements(Collections.unmodifiableList(programStageDataElements()));
            }
            return autoBuild();
        }
    }

    private static final class SortOrderComparator implements Comparator<ProgramStageSection> {

        @Override
        public int compare(ProgramStageSection one, ProgramStageSection two) {
            if (one == null || two == null) {
                return 0;
            }

            if (one.sortOrder() > two.sortOrder()) {
                return 1;
            } else if (one.sortOrder() < two.sortOrder()) {
                return -1;
            }

            return 0;
        }
    }

}