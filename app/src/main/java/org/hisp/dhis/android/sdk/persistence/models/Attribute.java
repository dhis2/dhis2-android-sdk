package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

/**
 * @author Ignacio Foche Pérez on 09.11.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class Attribute extends BaseMetaDataObject {
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

    @JsonProperty("valueType")
    @Column(name = "valueType")
    String valueType;

    @JsonProperty("code")
    @Column(name = "code")
    String code;

    public Attribute(){}

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
