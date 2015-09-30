package org.hisp.dhis.android.sdk.models.faileditem;

import org.hisp.dhis.android.sdk.models.common.IModel;
import org.hisp.dhis.android.sdk.models.common.IStore;

import java.util.List;

public interface IFailedItemStore extends IStore<FailedItem>{
    public List<FailedItem> query(FailedItem.Type type);
    public FailedItem query(FailedItem.Type type, long itemId);
}
