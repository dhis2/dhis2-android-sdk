package org.hisp.dhis.client.models.dataelement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.models.common.BaseNameableObject;
import org.hisp.dhis.client.models.common.ValueType;
import org.hisp.dhis.client.models.option.OptionSet;

import javax.annotation.Nullable;

// TODO: Unit tests
@AutoValue
@JsonDeserialize(builder = AutoValue_DataElement.Builder.class)
public abstract class DataElement extends BaseNameableObject {
    private final static String JSON_PROPERTY_VALUE_TYPE = "valueType";
    private final static String JSON_PROPERTY_ZERO_IS_SIGNIFICANT = "zeroIsSignificant";
    private final static String JSON_PROPERTY_AGGREGATION_OPERATOR = "aggregationOperator";
    private final static String JSON_PROPERTY_FORM_NAME = "formName";
    private final static String JSON_PROPERTY_NUMBER_TYPE = "numberType";
    private final static String JSON_PROPERTY_DOMAIN_TYPE = "domainType";
    private final static String JSON_PROPERTY_DIMENSION = "dimension";
    private final static String JSON_PROPERTY_DISPLAY_FORM_NAME = "displayFormName";
    private final static String JSON_PROPERTY_OPTION_SET = "optionSet";

    @Nullable
    @JsonProperty(JSON_PROPERTY_VALUE_TYPE)
    public abstract ValueType valueType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ZERO_IS_SIGNIFICANT)
    public abstract Boolean zeroIsSignificant();

    @Nullable
    @JsonProperty(JSON_PROPERTY_AGGREGATION_OPERATOR)
    public abstract String aggregationOperator();

    @Nullable
    @JsonProperty(JSON_PROPERTY_FORM_NAME)
    public abstract String formName();

    @Nullable
    @JsonProperty(JSON_PROPERTY_NUMBER_TYPE)
    public abstract String numberType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DOMAIN_TYPE)
    public abstract String domainType();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DIMENSION)
    public abstract String dimension();

    @Nullable
    @JsonProperty(JSON_PROPERTY_DISPLAY_FORM_NAME)
    public abstract String displayFormName();

    @Nullable
    @JsonProperty(JSON_PROPERTY_OPTION_SET)
    public abstract OptionSet optionSet();

    public static Builder builder() {
        return new AutoValue_DataElement.Builder();
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder extends BaseNameableObject.Builder<Builder> {

        @JsonProperty(JSON_PROPERTY_VALUE_TYPE)
        public abstract Builder valueType(@Nullable ValueType valueType);

        @JsonProperty(JSON_PROPERTY_ZERO_IS_SIGNIFICANT)
        public abstract Builder zeroIsSignificant(@Nullable Boolean zeroIsSignificant);

        @JsonProperty(JSON_PROPERTY_AGGREGATION_OPERATOR)
        public abstract Builder aggregationOperator(@Nullable String aggregationOperator);

        @JsonProperty(JSON_PROPERTY_FORM_NAME)
        public abstract Builder formName(@Nullable String formName);

        @JsonProperty(JSON_PROPERTY_NUMBER_TYPE)
        public abstract Builder numberType(@Nullable String numberType);

        @JsonProperty(JSON_PROPERTY_DOMAIN_TYPE)
        public abstract Builder domainType(@Nullable String domainType);

        @JsonProperty(JSON_PROPERTY_DIMENSION)
        public abstract Builder dimension(@Nullable String dimension);

        @JsonProperty(JSON_PROPERTY_DISPLAY_FORM_NAME)
        public abstract Builder displayFormName(@Nullable String displayFormName);

        @JsonProperty(JSON_PROPERTY_OPTION_SET)
        public abstract Builder optionSet(@Nullable OptionSet optionSet);

        public abstract DataElement build();
    }
}
