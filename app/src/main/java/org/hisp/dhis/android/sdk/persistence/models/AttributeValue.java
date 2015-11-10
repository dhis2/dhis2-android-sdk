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

/**
 * @author Ignacio Foche Pérez on 09.11.15.
 */
@Table(databaseName = Dhis2Database.NAME)
public class AttributeValue extends BaseMetaDataObject {
    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "attributeId",
            columnType = String.class,
            foreignColumnName = "id")},
            saveForeignKeyModel = false, onDelete = ForeignKeyAction.CASCADE)
    AttributeValue attribute;

    @Column(name = "value")
    String value;

    public AttributeValue getAttribute() {
        return attribute;
    }

    public void setAttribute(AttributeValue attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAttributeName(){
        return MetaDataController.getAttributeName(id);
    }

    public String getAttributeType(){
        return MetaDataController.getAttributeType(id);
    }
}
