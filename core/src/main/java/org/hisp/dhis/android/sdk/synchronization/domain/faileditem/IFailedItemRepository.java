package org.hisp.dhis.android.sdk.synchronization.domain.faileditem;

import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;

public interface IFailedItemRepository {
    ImportSummary sync(Event event);
}
