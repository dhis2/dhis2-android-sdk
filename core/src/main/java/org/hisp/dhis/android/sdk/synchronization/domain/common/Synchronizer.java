package org.hisp.dhis.android.sdk.synchronization.domain.common;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.ApiResponse;
import org.hisp.dhis.android.sdk.persistence.models.Conflict;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.domain.faileditem.IFailedItemRepository;
import org.hisp.dhis.android.sdk.utils.StringConverter;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

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
                    failedItem.setHttpStatusCode(apiException.getResponse().code());
                    try {
                        failedItem.setErrorMessage(
                                new StringConverter().convert(apiException.getResponse().body()).toString());
                    } catch (ConversionException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(apiException.getMessage() != null){
                    failedItem.setErrorMessage(apiException.getMessage());
                }
                failedItem.setItemId(id);
                failedItem.setItemType(type);
                failedItem.setHttpStatusCode(apiException.getResponse().code());
                failedItem.setFailCount(failedItem.getFailCount() + 1);
                mFailedItemRepository.save(failedItem);
            }
        }
    }



    public List<ImportSummary> getImportSummary(Response response) {
        //because the web api almost randomly gives the responses in different forms, this
        //method checks which one it is that is being returned, and parses accordingly.
        if (response.code() == 409){
            try {
                JsonNode node = DhisController.getInstance().getObjectMapper().
                        readTree(new StringConverter().convert(response.body()).toString());
                if (node == null) {
                    return null;
                }
                if (node.has("response")) {
                    return getFailedBatchImportSummary(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ConversionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<ImportSummary> getFailedBatchImportSummary(Response response) {
        ApiResponse apiResponse = null;
        try {
            String body = new StringConverter().convert(response.body()).toString();
            //Log.d(CLASS_TAG, body);
            apiResponse = DhisController.getInstance().getObjectMapper().
                    readValue(body, ApiResponse.class);
            if (apiResponse != null && apiResponse.getImportSummaries() != null
                    && !apiResponse.getImportSummaries().isEmpty()) {
                return (apiResponse.getImportSummaries());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConversionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
