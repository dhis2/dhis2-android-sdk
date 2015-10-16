/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.java.sdk.common.controllers;

import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.java.sdk.common.IFailedItemStore;
import org.hisp.dhis.java.sdk.common.StringConverter;
import org.hisp.dhis.java.sdk.core.network.APIException;
import org.hisp.dhis.java.sdk.core.api.utils.ObjectMapperProvider;
import org.hisp.dhis.java.sdk.models.common.faileditem.FailedItemType;
import org.hisp.dhis.java.sdk.common.network.ApiResponse;
import org.hisp.dhis.java.sdk.models.common.importsummary.Conflict;
import org.hisp.dhis.java.sdk.models.common.faileditem.FailedItem;
import org.hisp.dhis.java.sdk.models.common.importsummary.ImportSummary;
import org.hisp.dhis.java.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.java.sdk.models.event.Event;
import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityInstance;

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
                    if (responseType.equals( ApiResponse.RESPONSE_TYPE_IMPORT_SUMMARIES)) {
                        TypeReference<List<ImportSummary>> typeRef =
                                new TypeReference<List<ImportSummary>>() {
                                };
                        importSummaries = ObjectMapperProvider.getInstance().
                                convertValue(responseMap.get("importSummaries"), typeRef);
                    } else if (responseType.equals( ApiResponse.RESPONSE_TYPE_IMPORT_SUMMARY)) {
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

    public static void handleImportSummary(ImportSummary importSummary, IFailedItemStore failedItemStore, FailedItemType type, long id) {
        if ( ImportSummary.Status.ERROR.equals(importSummary.getStatus()) ){
            handleImportSummaryError(importSummary, failedItemStore, type, 200, id);
        }
    }

    public static void handleTrackedEntityInstanceSendException(APIException apiException, IFailedItemStore failedItemStore, TrackedEntityInstance trackedEntityInstance) {
        handleSerializableItemException(apiException, failedItemStore, FailedItemType.TRACKED_ENTITY_INSTANCE, trackedEntityInstance.getId());
    }

    public static void handleEnrollmentSendException(APIException apiException, IFailedItemStore failedItemStore, Enrollment enrollment) {
        handleSerializableItemException(apiException, failedItemStore, FailedItemType.ENROLLMENT, enrollment.getId());
    }

    public static void handleEventSendException(APIException apiException, IFailedItemStore failedItemStore, Event event) {
        handleSerializableItemException(apiException, failedItemStore, FailedItemType.EVENT, event.getId());
    }

    private static void handleSerializableItemException(APIException apiException, IFailedItemStore failedItemStore, FailedItemType type, long id) {
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
                failedItem.setItemFailedItemType(type);
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
                failedItem.setItemFailedItemType(type);
                failedItem.setHttpStatusCode(apiException.getResponse().getStatus());
                failedItemStore.save(failedItem);
            }
        }
    }

    public static void handleImportSummaryError(ImportSummary importSummary, IFailedItemStore failedItemStore, FailedItemType type, int code, long id) {
        FailedItem failedItem = new FailedItem();
        failedItem.setImportSummary(importSummary);
        failedItem.setItemId(id);
        failedItem.setItemFailedItemType(type);
        failedItem.setHttpStatusCode(code);
        if( failedItem.getImportSummary() != null && failedItem.getImportSummary().getConflicts() != null ) {
            for(Conflict conflict: failedItem.getImportSummary().getConflicts() ) {
                conflict.setImportSummary(failedItem.getImportSummary());
            }
        }
        failedItemStore.save(failedItem);
    }

    public static void clearFailedItem(FailedItemType type, IFailedItemStore failedItemStore, long id) {
        FailedItem item = failedItemStore.query(type, id);
        if (item != null) {
            failedItemStore.delete(item);
        }
    }
}
