package org.hisp.dhis.android.sdk.persistence.models;

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

/**
 * @author Ignacio Foche Pérez on 09.11.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class DataElementAttributeValue extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column (name = "dataElementId")
    String dataElementId;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "attributeValueId",
            columnType = Long.class,
            foreignColumnName = "id")},
            saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE)
    AttributeValue attributeValue;

    public String getDataElementId() {
        return dataElementId;
    }

    public void setDataElementId(String dataElementId) {
        this.dataElementId = dataElementId;
    }

    public AttributeValue getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(AttributeValue attributeValue) {
        this.attributeValue = attributeValue;
    }

    public void getAttributeValue(long id){
        MetaDataController.getAttributeValue(id);
    }

    public DataElement getDataElement() {
        return MetaDataController.getDataElement(dataElementId);
    }

    public void setDataElement(DataElement dataElement) {
        this.dataElementId = dataElement.getUid();
    }
}
