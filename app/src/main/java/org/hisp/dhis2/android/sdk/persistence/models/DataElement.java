package org.hisp.dhis2.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.Map;

/**
 * @author Simen Skogly Russnes on 18.02.15.
 */
@Table
public class DataElement extends BaseNameableObject {

    @JsonProperty("type")
    @Column
    public String type;

    @JsonProperty("zeroIsSignificant")
    @Column
    public boolean zeroIsSignificant;

    @JsonProperty("externalAccess")
    @Column
    public boolean externalAccess;

    @JsonProperty("aggregationOperator")
    @Column
    public String aggregationOperator;

    @JsonProperty("formName")
    @Column
    public String formName;

    @JsonProperty("numberType")
    @Column
    public String numberType;

    @JsonProperty("domainType")
    @Column
    public String domainType;

    @JsonProperty("dimension")
    @Column
    public String dimension;

    @JsonProperty("displayName")
    @Column
    public String displayName;

    @Column
    public String optionSet;

    @JsonProperty("optionSet")
    public void setOptionSet(Map<String, Object> optionSet) {
        this.optionSet = (String) optionSet.get("id");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOptionSet() {
        return optionSet;
    }

    public void setOptionSet(String optionSet) {
        this.optionSet = optionSet;
    }

    /**
     *
     */
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
}
