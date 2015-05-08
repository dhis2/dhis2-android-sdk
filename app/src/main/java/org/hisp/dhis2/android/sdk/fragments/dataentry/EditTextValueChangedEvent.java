package org.hisp.dhis2.android.sdk.fragments.dataentry;

import android.util.Log;

import org.hisp.dhis2.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.TrackedEntityAttributeValue;

public final class EditTextValueChangedEvent {

    public static final String DATAVALUE = "datavalue";
    public static final String TRACKEDENTITYATTRIBUTEVALUE = "trackedentityattributevalue";
    private String type;
    private String id;

    public EditTextValueChangedEvent(BaseValue baseValue) {
        if(baseValue instanceof DataValue) {
            type = DATAVALUE;
            id = ( ( DataValue ) baseValue ).dataElement;
        } else if(baseValue instanceof TrackedEntityAttributeValue) {
            type = TRACKEDENTITYATTRIBUTEVALUE;
            id = ( ( TrackedEntityAttributeValue ) baseValue ).trackedEntityAttributeId;
        }
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public boolean isDataValue() {
        return type.equals(DATAVALUE);
    }
}
