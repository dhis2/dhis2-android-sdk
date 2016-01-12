package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;

import java.util.Map;

@Table(databaseName = Dhis2Database.NAME)
public class AttributeValue extends BaseModel {

    public AttributeValue(){}

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column(name = "dataElement")
    String dataElement;

    @Column(name = "attributeId")
    String attribute;

    Attribute attributeObj;

    @JsonProperty("value")
    @Column(name = "value")
    String value;

    @JsonProperty("created")
    @Column(name = "created")
    String created;

    @JsonProperty("lastUpdated")
    @Column(name = "lastUpdated")
    String lastUpdated;

    public Attribute getAttribute() {
        return MetaDataController.getAttribute(attribute);
    }

    @JsonProperty("attribute")
    public void setAttribute(Attribute attributeObj){
        attribute = attributeObj.getUid();
        this.attributeObj = attributeObj;
    }

    public void setAttribute(String attributeId) {
        this.attribute = attributeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty("dataElement")
    public void setDataElement(Map<String, Object> dataElement){
        this.dataElement = (String) dataElement.get("id");
    }

    public DataElement getDataElement(){
        return MetaDataController.getDataElement(dataElement);
    }

    public void setDataElement(String dataElement){
        this.dataElement = dataElement;
    }

    public Attribute getAttributeObj(){
        return attributeObj;
    }

    public String getAttributeId(){
        return attribute;
    }
}
