package org.hisp.dhis.android.sdk.events;

import org.hisp.dhis.android.sdk.persistence.models.BaseSerializableModel;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;

/**
 * Created by erling on 5/11/15.
 */
public class OnTrackerItemClick extends OnRowClick<BaseSerializableModel>
{
    private final boolean onDescriptionClick;

    public OnTrackerItemClick(BaseSerializableModel item, ITEM_STATUS status,
                              boolean description) {
        super(status, item);
        this.onDescriptionClick = description;
    }

    public boolean isOnDescriptionClick() {
        return onDescriptionClick;
    }
}

