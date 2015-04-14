package org.hisp.dhis2.android.sdk.persistence.models;

import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;

/**
 * @author Simen Skogly Russnes on 03.03.15.
 */
@Table
public class TrackedEntityAttributeValue extends BaseValue {
    private static final String CLASS_TAG = TrackedEntityAttributeValue.class.getSimpleName();

    @JsonProperty("attribute")
    @Column(columnType = Column.PRIMARY_KEY)
    public String trackedEntityAttributeId;

    @JsonIgnore
    @Column(columnType = Column.PRIMARY_KEY)
    public String trackedEntityInstanceId;

    /**
     * workaround for sending code if attribute is option set. Currently best approach because
     * loading from server doesn't even provide the code, only name.
     * @return
     */
    @JsonProperty("value")
    public String getValue() {
        Log.d(CLASS_TAG, "getValue!!!!!");
        TrackedEntityAttribute tea = MetaDataController.
                getTrackedEntityAttribute(trackedEntityAttributeId);
        if(tea.valueType.equals(TrackedEntityAttribute.TYPE_OPTION_SET)) {
            Log.d(CLASS_TAG, "its an option set!" + tea.id);
            OptionSet optionSet = MetaDataController.getOptionSet(tea.getOptionSet());
            for(Option o: optionSet.getOptions()) {
                if(o.name.equals(value)) {
                    Log.d(CLASS_TAG, "returning value: " + value);
                    return null;
                }
            }
        } else return value;
        return null;
    }
}
