package org.hisp.dhis.android.sdk.models.dataelement;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.android.sdk.models.common.meta.BaseNameableObject;

import java.util.Map;

public final class DataElement extends BaseNameableObject {

    public static final String VALUE_TYPE_INT = "int";
    public static final String VALUE_TYPE_STRING = "string";
    public static final String VALUE_TYPE_USER_NAME = "username";
    public static final String VALUE_TYPE_BOOL = "bool";
    public static final String VALUE_TYPE_TRUE_ONLY = "trueOnly";
    public static final String VALUE_TYPE_DATE = "date";
    public static final String VALUE_TYPE_UNIT_INTERVAL = "unitInterval";
    public static final String VALUE_TYPE_PERCENTAGE = "percentage";
    public static final String VALUE_TYPE_NUMBER = "number";
    public static final String VALUE_TYPE_POSITIVE_INT = "posInt";
    public static final String VALUE_TYPE_NEGATIVE_INT = "negInt";
    public static final String VALUE_TYPE_ZERO_OR_POSITIVE_INT = "zeroPositiveInt";
    public static final String VALUE_TYPE_TEXT = "text";
    public static final String VALUE_TYPE_LONG_TEXT = "longText";

    @JsonProperty("type")
    private String type;

    @JsonProperty("zeroIsSignificant")
    private boolean zeroIsSignificant;

    @JsonProperty("aggregationOperator")
    private String aggregationOperator;

    @JsonProperty("formName")
    private String formName;

    @JsonProperty("numberType")
    private String numberType;

    @JsonProperty("domainType")
    private String domainType;

    @JsonProperty("dimension")
    private String dimension;

    @JsonProperty("displayFormName")
    private String displayFormName;

    private String optionSet;

    @JsonProperty("optionSet")
    public void setOptionSet(Map<String, Object> optionSet) {
        setOptionSet((String) optionSet.get("id"));
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isZeroIsSignificant() {
        return zeroIsSignificant;
    }

    public void setZeroIsSignificant(boolean zeroIsSignificant) {
        this.zeroIsSignificant = zeroIsSignificant;
    }

    public String getAggregationOperator() {
        return aggregationOperator;
    }

    public void setAggregationOperator(String aggregationOperator) {
        this.aggregationOperator = aggregationOperator;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    public String getDomainType() {
        return domainType;
    }

    public void setDomainType(String domainType) {
        this.domainType = domainType;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getDisplayFormName() {
        return displayFormName;
    }

    public void setDisplayFormName(String displayFormName) {
        this.displayFormName = displayFormName;
    }

    public String getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(String optionSet) {
        this.optionSet = optionSet;
    }
}
