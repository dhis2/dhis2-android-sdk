package org.hisp.dhis.android.sdk.synchronization.data.event;

import com.fasterxml.jackson.databind.JsonNode;

import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.ApiResponse;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.utils.StringConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit.client.Response;
import retrofit.converter.ConversionException;

public class EventRemoteDataSource {
    DhisApi dhisApi;

    public EventRemoteDataSource(DhisApi dhisApi) {
        this.dhisApi = dhisApi;
    }

    public Event getEvent(String event) {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "created,lastUpdated");
        Event updatedEvent = dhisApi
                .getEvent(event, QUERY_PARAMS);

        return updatedEvent;
    }

    public ImportSummary Save(Event event) {
        if (event.getCreated() == null) {
            return postEvent(event, dhisApi);
        } else {
            return putEvent(event, dhisApi);
        }
    }

    private ImportSummary postEvent(Event event, DhisApi dhisApi) throws APIException {
        Response response = dhisApi.postEvent(event);

        return getImportSummary(response);
    }

    private ImportSummary putEvent(Event event, DhisApi dhisApi) throws APIException {
        Response response = dhisApi.putEvent(event.getEvent(), event);

        return getImportSummary(response);
    }

    private static ImportSummary getImportSummary(Response response) {
        //because the web api almost randomly gives the responses in different forms, this
        //method checks which one it is that is being returned, and parses accordingly.
        try {
            JsonNode node = DhisController.getInstance().getObjectMapper().
                    readTree(new StringConverter().fromBody(response.getBody(), String.class));
            if (node == null) {
                return null;
            }
            if (node.has("response")) {
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
            //Log.d(CLASS_TAG, body);
            importSummary = DhisController.getInstance().getObjectMapper().
                    readValue(body, ImportSummary.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConversionException e) {
            e.printStackTrace();
        }
        return importSummary;
    }

    private static ImportSummary getPutImportSummary(Response response) {
        ApiResponse apiResponse = null;
        try {
            String body = new StringConverter().fromBody(response.getBody(), String.class);
            //Log.d(CLASS_TAG, body);
            apiResponse = DhisController.getInstance().getObjectMapper().
                    readValue(body, ApiResponse.class);
            if (apiResponse != null && apiResponse.getImportSummaries() != null
                    && !apiResponse.getImportSummaries().isEmpty()) {
                return (apiResponse.getImportSummaries().get(0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ConversionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
