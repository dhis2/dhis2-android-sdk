package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.models.dataelement.DataElement;

import java.util.Comparator;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramStageDataElement.Builder.class)
public abstract class ProgramStageDataElement extends BaseIdentifiableObject {

    private static final String DISPLAY_IN_REPORTS = "displayInReports";
    private static final String DATA_ELEMENT = "dataElement";
    private static final String COMPULSORY = "compulsory";
    private static final String ALLOW_PROVIDED_ELSEWHERE = "allowProvidedElsewhere";
    private static final String SORT_ORDER = "sortOrder";
    private static final String ALLOW_FUTURE_DATE = "allowFutureDate";

    public static final Comparator<ProgramStageDataElement>
            SORT_ORDER_COMPARATOR = new SortOrderComparator();

    @Nullable
    @JsonProperty(DISPLAY_IN_REPORTS)
    public abstract Boolean displayInReports();

    @Nullable
    @JsonProperty(DATA_ELEMENT)
    public abstract DataElement dataElement();

    @Nullable
    @JsonProperty(COMPULSORY)
    public abstract Boolean compulsory();

    @Nullable
    @JsonProperty(ALLOW_PROVIDED_ELSEWHERE)
    public abstract Boolean allowProvidedElsewhere();

    @Nullable
    @JsonProperty(SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @JsonProperty(ALLOW_FUTURE_DATE)
    public abstract Boolean allowFutureDate();

    public static ProgramStageDataElement.Builder builder() {
        return new AutoValue_ProgramStageDataElement.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder> {
        @JsonProperty(DISPLAY_IN_REPORTS)
        public abstract Builder displayInReports(@Nullable Boolean displayInReports);

        @JsonProperty(DATA_ELEMENT)
        public abstract Builder dataElement(@Nullable DataElement dataElement);

        @JsonProperty(COMPULSORY)
        public abstract Builder compulsory(@Nullable Boolean compulsory);

        @JsonProperty(ALLOW_PROVIDED_ELSEWHERE)
        public abstract Builder allowProvidedElsewhere(@Nullable Boolean allowProvidedElsewhere);

        @JsonProperty(SORT_ORDER)
        public abstract Builder sortOrder(@Nullable Integer sortOrder);

        @JsonProperty(ALLOW_FUTURE_DATE)
        public abstract Builder allowFutureDate(@Nullable Boolean allowFutureDate);

        public abstract ProgramStageDataElement build();
    }

    private static final class SortOrderComparator implements Comparator<ProgramStageDataElement> {

        @Override
        public int compare(ProgramStageDataElement one, ProgramStageDataElement two) {
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
