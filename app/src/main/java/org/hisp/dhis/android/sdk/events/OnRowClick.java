package org.hisp.dhis.android.sdk.events;

import org.hisp.dhis.android.sdk.persistence.models.BaseSerializableModel;

/**
 * Created by Simen S. Russnes on 7/15/15.
 */
public abstract class OnRowClick<T extends BaseSerializableModel>
{
    public static enum ITEM_STATUS {
        OFFLINE,
        SENT,
        ERROR
    }

    private final ITEM_STATUS status;
    private final T item;

    public OnRowClick(ITEM_STATUS status, T item) {
        this.status = status;
        this.item = item;
    }

    public ITEM_STATUS getStatus() {
        return status;
    }

    public T getItem() {
        return item;
    }
}

