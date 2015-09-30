package org.hisp.dhis.android.sdk.core.controllers.common;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.android.sdk.core.network.APIException;
import org.hisp.dhis.android.sdk.core.providers.ObjectMapperProvider;
import org.hisp.dhis.android.sdk.models.common.ApiResponse;
import org.hisp.dhis.android.sdk.models.conflict.Conflict;
import org.hisp.dhis.android.sdk.models.faileditem.FailedItem;
import org.hisp.dhis.android.sdk.models.faileditem.IFailedItemStore;
import org.hisp.dhis.android.sdk.models.importsummary.ImportSummary;
import org.hisp.dhis.android.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.android.sdk.models.event.Event;
import org.hisp.dhis.android.sdk.models.trackedentityinstance.TrackedEntityInstance;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.client.Response;
import retrofit.converter.ConversionException;

public abstract class PushableDataController {
    public static final String TAG = PushableDataController.class.getSimpleName();

    public static ImportSummary getImportSummary(Response response) {
        //because the web api almost randomly gives the responses in different forms, this
        //method checks which one it is that is being returned, and parses accordingly.
        try {
            JsonNode node = ObjectMapperProvider.getInstance().
                    readTree(new StringConverter().fromBody(response.getBody(), String.class));
            if(node == null) {
                return null;
            }
            if(node.has("response")) {
                return getPutImportSummary(response);
            } else {
                return getPostImportSummary(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConversionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ImportSummary getPostImportSummary(Response response) {
        ImportSummary importSummary = null;
        try {
            String body = new StringConverter().fromBody(response.getBody(), String.class);
            Log.d(TAG, body);
            importSummary = ObjectMapperProvider.getInstance().
                    readValue(body, ImportSummary.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConversionException e) {
            e.printStackTrace();
        }
        return importSummary;
    }

    private static ImportSummary getPutImportSummary(Response response) {
        ApiResponse apiResponse;
        try {
            String body = new StringConverter().fromBody(response.getBody(), String.class);
            Log.d(TAG, body);
            apiResponse = ObjectMapperProvider.getInstance().
                    readValue(body, ApiResponse.class);
            List<ImportSummary> importSummaries = null;
            Map<String, Object> responseMap = apiResponse.getResponse();
                try {
                    String responseType = (String) responseMap.get("responseType");
                    if (responseType.equals( ApiResponse.RESPONSETYPE_IMPORTSUMMARIES )) {
                        TypeReference<List<ImportSummary>> typeRef =
                                new TypeReference<List<ImportSummary>>() {
                                };
                        importSummaries = ObjectMapperProvider.getInstance().
                                convertValue(responseMap.get("importSummaries"), typeRef);
                    } else if (responseType.equals( ApiResponse.RESPONSETYPE_IMPORTSUMMARY )) {
                        ImportSummary importSummary = ObjectMapperProvider.getInstance()
                                .convertValue(response, ImportSummary.class);
                        importSummaries = new ArrayList<>();
                        importSummaries.add(importSummary);
                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                }

            if(importSummaries!=null && !importSummaries.isEmpty()) {
                return(importSummaries.get(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConversionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void handleImportSummary(ImportSummary importSummary, IFailedItemStore failedItemStore, FailedItem.Type type, long id) {
        if ( ImportSummary.Status.ERROR.equals(importSummary.getStatus()) ){
            handleImportSummaryError(importSummary, failedItemStore, type, 200, id);
        }
    }

    public static void handleTrackedEntityInstanceSendException(APIException apiException, IFailedItemStore failedItemStore, TrackedEntityInstance trackedEntityInstance) {
        handleSerializableItemException(apiException, failedItemStore, FailedItem.Type.TRACKEDENTITYINSTANCE, trackedEntityInstance.getId());
    }

    public static void handleEnrollmentSendException(APIException apiException, IFailedItemStore failedItemStore, Enrollment enrollment) {
        handleSerializableItemException(apiException, failedItemStore, FailedItem.Type.ENROLLMENT, enrollment.getId());
    }

    public static void handleEventSendException(APIException apiException, IFailedItemStore failedItemStore, Event event) {
        handleSerializableItemException(apiException, failedItemStore, FailedItem.Type.EVENT, event.getId());
    }

    private static void handleSerializableItemException(APIException apiException, IFailedItemStore failedItemStore, FailedItem.Type type, long id) {
        switch (apiException.getKind()) {
            case NETWORK: {
                FailedItem failedItem = new FailedItem();
                String cause = "Network error\n\n";
                if(apiException != null && apiException.getCause() != null) {
                    StringWriter stringWriter = new StringWriter();
                    apiException.getCause().printStackTrace(new PrintWriter(stringWriter));
                    cause += stringWriter.toString();
                    failedItem.setErrorMessage(cause);
                }
                failedItem.setHttpStatusCode(-1);
                failedItem.setItemId(id);
                failedItem.setItemType(type);
                failedItemStore.save(failedItem);
                break;
            }
            default: {
                FailedItem failedItem = new FailedItem();
                if (apiException.getResponse() != null) {
                    failedItem.setHttpStatusCode(apiException.getResponse().getStatus());
                    try {
                        failedItem.setErrorMessage(new StringConverter().fromBody(apiException.getResponse().getBody(), String.class));
                    } catch (ConversionException e) {
                        e.printStackTrace();
                    }
                }
                failedItem.setItemId(id);
                failedItem.setItemType(type);
                failedItem.setHttpStatusCode(apiException.getResponse().getStatus());
                failedItemStore.save(failedItem);
            }
        }
    }

    public static void handleImportSummaryError(ImportSummary importSummary, IFailedItemStore failedItemStore, FailedItem.Type type, int code, long id) {
        FailedItem failedItem = new FailedItem();
        failedItem.setImportSummary(importSummary);
        failedItem.setItemId(id);
        failedItem.setItemType(type);
        failedItem.setHttpStatusCode(code);
        if( failedItem.getImportSummary() != null && failedItem.getImportSummary().getConflicts() != null ) {
            for(Conflict conflict: failedItem.getImportSummary().getConflicts() ) {
                conflict.setImportSummary(failedItem.getImportSummary());
            }
        }
        failedItemStore.save(failedItem);
    }

    public static void clearFailedItem(FailedItem.Type type, IFailedItemStore failedItemStore, long id) {
        FailedItem item = failedItemStore.query(type, id);
        if (item != null) {
            failedItemStore.delete(item);
        }
    }
}
