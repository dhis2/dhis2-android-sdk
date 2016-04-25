package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

@Table(databaseName = Dhis2Database.NAME)
public class Attribute extends BaseMetaDataObject {

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
