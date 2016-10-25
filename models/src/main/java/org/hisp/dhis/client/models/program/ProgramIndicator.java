package org.hisp.dhis.client.models.program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseNameableObject;

import javax.annotation.Nullable;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramIndicator.Builder.class)
public abstract class ProgramIndicator extends BaseNameableObject {

    private static final String DISPLAY_IN_FORM = "displayInForm";
    private static final String EXPRESSION = "expression";
    private static final String DIMENSION_ITEM = "dimensionItem";
    private static final String FILTER = "filter";
    private static final String DECIMALS = "decimals";

    @Nullable
    @JsonProperty(DISPLAY_IN_FORM)
    public abstract Boolean displayInForm();

    @Nullable
    @JsonProperty(EXPRESSION)
    public abstract String expression();

    @Nullable
    @JsonProperty(DIMENSION_ITEM)
    public abstract String dimensionItem();

    @Nullable
    @JsonProperty(FILTER)
    public abstract String filter();

    @Nullable
    @JsonProperty(DECIMALS)
    public abstract Integer decimals();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {
        @JsonProperty(DISPLAY_IN_FORM)
        public abstract Builder displayInForm(@Nullable Boolean displayInForm);

        @JsonProperty(EXPRESSION)
        public abstract Builder expression(@Nullable String expression);

        @JsonProperty(DIMENSION_ITEM)
        public abstract Builder dimensionItem(@Nullable String dimensionItem);

        @JsonProperty(FILTER)
        public abstract Builder filter(@Nullable String filter);

        @JsonProperty(DECIMALS)
        public abstract Builder decimals(@Nullable Integer decimals);

        abstract ProgramIndicator build();
    }
}