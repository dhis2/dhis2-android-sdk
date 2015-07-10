package org.hisp.dhis.android.sdk.fragments.dataentry;

import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;

public final class RowValueChangedEvent {

    public static final String DATAVALUE = "datavalue";
    public static final String TRACKEDENTITYATTRIBUTEVALUE = "trackedentityattributevalue";
    private String type;
    private String id;

    public RowValueChangedEvent(BaseValue baseValue) {
        if(baseValue instanceof DataValue) {
            type = DATAVALUE;
            id = ( ( DataValue ) baseValue ).getDataElement();
        } else if(baseValue instanceof TrackedEntityAttributeValue) {
            type = TRACKEDENTITYATTRIBUTEVALUE;
            id = ( ( TrackedEntityAttributeValue ) baseValue ).getTrackedEntityAttributeId();
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
