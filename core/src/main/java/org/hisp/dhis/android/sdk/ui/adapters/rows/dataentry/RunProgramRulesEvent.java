package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;

/**
 * Created by thomaslindsjorn on 16/08/16.
 */
public class RunProgramRulesEvent {

    private final String id;

    public RunProgramRulesEvent(BaseValue value) {
        if (value instanceof DataValue) {
            id = ((DataValue) value).getDataElement();
        } else if (value instanceof TrackedEntityAttributeValue) {
            id = ((TrackedEntityAttributeValue) value).getTrackedEntityAttributeId();
        } else {
            id = "";
        }
    }

    public String getId() {
        return id;
    }
}