package org.hisp.dhis.android.sdk.synchronization.domain.faileditem;

import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;

public interface IFailedItemRepository {
    void save(FailedItem failedItem);

    void clearFailedItem(String type, long id);

    void handleImportSummaryError(ImportSummary importSummary, String type, int code, long id);

    void handleSerializableItemException(APIException apiException, String type, long id);
}
