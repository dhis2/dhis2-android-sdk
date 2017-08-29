package org.hisp.dhis.android.sdk.synchronization.domain.faileditem;

import org.hisp.dhis.android.sdk.persistence.models.FailedItem;

public interface IFailedItemRepository {
    void save(FailedItem failedItem);

    void delete(String type, long id);
}
