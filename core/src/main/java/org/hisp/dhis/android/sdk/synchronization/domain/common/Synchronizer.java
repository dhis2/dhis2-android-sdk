package org.hisp.dhis.android.sdk.synchronization.domain.common;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.Conflict;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.domain.event.IEventRepository;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;
import org.hisp.dhis.android.sdk.utils.StringConverter;

import retrofit.converter.ConversionException;

public class Synchronizer {
    IFailedItemRepository mFailedItemRepository;

    public Synchronizer(IFailedItemRepository failedItemRepository) {
        mFailedItemRepository = failedItemRepository;
    }


    public void clearFailedItem(String type, long id) {
        mFailedItemRepository.delete(type, id);
    }


    public void handleImportSummaryError(ImportSummary importSummary, String type, int code,
            long id) {
        FailedItem failedItem = new FailedItem();
        failedItem.setImportSummary(importSummary);
        failedItem.setItemId(id);
        failedItem.setItemType(type);
        failedItem.setHttpStatusCode(code);
        failedItem.save();

        if (failedItem.getImportSummary() != null
                && failedItem.getImportSummary().getConflicts() != null) {
            for (Conflict conflict : failedItem.getImportSummary().getConflicts()) {
                conflict.setImportSummary(failedItem.getImportSummary().getId());
                conflict.save();
            }
        }
        System.out.println(
                "saved item: " + failedItem.getItemId() + ":" + failedItem.getItemType());
    }


    public void handleSerializableItemException(APIException apiException, String type, long id) {
        FailedItem failedItem = TrackerController.getFailedItem(type, id);
        switch (apiException.getKind()) {
            case NETWORK: {
                if (failedItem == null) {
                    failedItem = new FailedItem();
                }
                String cause = "Network error\n\n";
                if (apiException != null && apiException.getCause() != null) {
                    cause += ExceptionUtils.getStackTrace(apiException.getCause());
                    failedItem.setErrorMessage(cause);
                }
                failedItem.setHttpStatusCode(-1);
                failedItem.setItemId(id);
                failedItem.setItemType(type);
                failedItem.setFailCount(failedItem.getFailCount() + 1);
                mFailedItemRepository.save(failedItem);
                break;
            }
            default: {
                if (failedItem == null) {
                    failedItem = new FailedItem();
                }

                if (apiException.getResponse() != null) {
                    failedItem.setHttpStatusCode(apiException.getResponse().getStatus());
                    try {
                        failedItem.setErrorMessage(
                                new StringConverter().fromBody(apiException.getResponse().getBody(),
                                        String.class));
                    } catch (ConversionException e) {
                        e.printStackTrace();
                    }
                }
                failedItem.setItemId(id);
                failedItem.setItemType(type);
                failedItem.setHttpStatusCode(apiException.getResponse().getStatus());
                failedItem.setFailCount(failedItem.getFailCount() + 1);
                mFailedItemRepository.save(failedItem);
            }
        }
    }
}
