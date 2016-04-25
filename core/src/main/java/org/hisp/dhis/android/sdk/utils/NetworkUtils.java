/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.utils;

import android.util.Log;

import com.raizlabs.android.dbflow.structure.BaseModel;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.models.Conflict;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.FailedItem;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.utils.StringConverter;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.client.Header;
import retrofit.converter.ConversionException;

/**
 * Created by araz on 06.06.2015.
 */
public class NetworkUtils {
    private NetworkUtils() {
        // no instances
    }

    public static <T> List<T> unwrapResponse(Map<String, List<T>> response, String key) {
        if (response != null && response.containsKey(key) && response.get(key) != null) {
            return response.get(key);
        } else {
            return new ArrayList<>();
        }
    }

    public static Header findLocationHeader(List<Header> headers) {
        final String LOCATION = "location";
        if (headers != null && !headers.isEmpty()) {
            for (Header header : headers) {
                if (header.getName().equalsIgnoreCase(LOCATION)) {
                    return header;
                }
            }
        }

        return null;
    }

    public static void handleApiException(APIException apiException) throws APIException {
        handleApiException(apiException, null);
    }

    /**
     * List of errors which this method should handle:
     * <p/>
     * 400 Bad Request
     * 401 Unauthorized (user password has changed)
     * 403 Forbidden (access denied)
     * 404 Not found (object was already removed for example)
     * 405 Method not allowed (wrong HTTP request method)
     * 408 Request Time Out (too slow internet connection, long processing time, etc)
     * 409 Conflict (you are trying to treat some resource as another.
     * For example to create interpretation for map through chart URI)
     * 500 Internal server error (for example NullPointerException)
     * 501 Not implemented (no such method or resource)
     * 502 Bad Gateway (can be retried later)
     * 503 Service unavailable (can be temporary issue)
     * 504 Gateway Timeout (we need to retry request later)
     */
    public static void handleApiException(APIException apiException, BaseModel model) throws APIException {
        switch (apiException.getKind()) {
            case HTTP: {
                switch (apiException.getResponse().getStatus()) {
                    case HttpURLConnection.HTTP_BAD_REQUEST: {
                        // TODO Implement mechanism for handling HTTP errors (allow user to resolve it).
                        break;
                    }
                    case HttpURLConnection.HTTP_UNAUTHORIZED: {
                        // if the user password has changed, none of other network
                        // requests won't pass. So we need to stop synchronization.
                        throw apiException;
                    }
                    case HttpURLConnection.HTTP_FORBIDDEN: {
                        // TODO Implement mechanism for handling HTTP errors (allow user to resolve it).
                        // User does not has access to given resource anymore.
                        // We need to handle this in a special way
                        break;
                    }
                    case HttpURLConnection.HTTP_NOT_FOUND: {
                        // The given resource does not exist on the server anymore.
                        // Remove it locally.
                        if (model != null) {
                            model.delete();
                        }
                        break;
                    }
                    case HttpURLConnection.HTTP_CONFLICT: {
                        // TODO Implement mechanism for handling HTTP errors (allow user to resolve it).
                        // Trying to access wrong resource.
                        break;
                    }
                    case HttpURLConnection.HTTP_INTERNAL_ERROR: {
                        // TODO Implement mechanism for handling HTTP errors (allow user to resolve it).
                        break;
                    }
                    case HttpURLConnection.HTTP_NOT_IMPLEMENTED: {
                        // TODO Implement mechanism for handling HTTP errors (allow user to resolve it).
                        break;
                    }
                }

                break;
            }
            case NETWORK: {
                // Retry later.
                break;
            }
            case CONVERSION:
            case UNEXPECTED: {
                // TODO Implement mechanism for handling HTTP errors (allow user to resolve it).
                // implement possibility to show error status. In most cases, this types of errors
                // won't be resolved automatically.

                // for now, just rethrow exception.
                throw apiException;
            }
        }
    }

    public static void handleTrackedEntityInstanceSendException(APIException apiException, TrackedEntityInstance trackedEntityInstance) {
        handleSerializableItemException(apiException, FailedItem.TRACKEDENTITYINSTANCE, trackedEntityInstance.getLocalId());
    }

    public static void handleEnrollmentSendException(APIException apiException, Enrollment enrollment) {
        handleSerializableItemException(apiException, FailedItem.ENROLLMENT, enrollment.getLocalId());
    }

    public static void handleEventSendException(APIException apiException, Event event) {
        handleSerializableItemException(apiException, FailedItem.EVENT, event.getLocalId());
    }

    private static void handleSerializableItemException(APIException apiException, String type, long id) {
        FailedItem failedItem = TrackerController.getFailedItem(type, id);
        switch (apiException.getKind()) {
            case NETWORK: {
                if(failedItem == null) {
                    failedItem = new FailedItem();
                }
                String cause = "Network error\n\n";
                if(apiException != null && apiException.getCause() != null) {
                    cause += ExceptionUtils.getStackTrace(apiException.getCause());
                    failedItem.setErrorMessage(cause);
                }
                failedItem.setHttpStatusCode(-1);
                failedItem.setItemId(id);
                failedItem.setItemType(type);
                failedItem.setFailCount(failedItem.getFailCount() + 1);
                failedItem.save();
                break;
            }
            default: {
                if(failedItem == null) {
                    failedItem = new FailedItem();
                }

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
                failedItem.setFailCount(failedItem.getFailCount() + 1);
                failedItem.save();
            }
        }
    }

    public static void handleImportSummaryError(ImportSummary importSummary, String type, int code, long id) {
        FailedItem failedItem = new FailedItem();
        failedItem.setImportSummary(importSummary);
        failedItem.setItemId(id);
        failedItem.setItemType(type);
        failedItem.setHttpStatusCode(code);
        failedItem.save();
        if( failedItem.getImportSummary() != null && failedItem.getImportSummary().getConflicts() != null ) {
            for(Conflict conflict: failedItem.getImportSummary().getConflicts() ) {
                conflict.setImportSummary( failedItem.getImportSummary().getId() );
                conflict.save();
            }
        }
        Log.d("NetworkUtils", "saved item: " + failedItem.getItemId()+ ":" + failedItem.getItemType());
    }
}
