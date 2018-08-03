package org.hisp.dhis.android.sdk.synchronization.data.common;


import com.fasterxml.jackson.databind.JsonNode;

import org.apache.commons.beanutils.ConversionException;
import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.ApiResponse;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.SystemInfo;
import org.hisp.dhis.android.sdk.utils.NetworkUtils;
import org.hisp.dhis.android.sdk.utils.StringConverter;
import org.joda.time.DateTime;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;


public abstract class ARemoteDataSource {

    public DhisApi dhisApi;

    public DateTime getServerTime(){
        SystemInfo systemInfo = null;
        try {
            systemInfo = dhisApi.getSystemInfo().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DateTime UploadTime = systemInfo.getServerDate();
        return UploadTime;
    }

    public ImportSummary getImportSummary(Response response) {
        //because the web api almost randomly gives the responses in different forms, this
        //method checks which one it is that is being returned, and parses accordingly.
        if (response.code() == 200) {
            try {
                String body = ((ResponseBody)response.body()).string();
                JsonNode node = DhisController.getInstance().getObjectMapper().
                        readTree(body);
                if (node == null) {
                    return null;
                }
                if (node.has("response")) {
                    return getPutImportSummary(body);
                } else {
                    return getPostImportSummary(body);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ConversionException e) {
                e.printStackTrace();
            }
        }else if (response.code() == 409){
            throw APIException.fromRetrofitError(409, response, null);
        }
        return null;
    }

    private ImportSummary getPostImportSummary(String body) {
        ImportSummary importSummary = null;
        try {
            //String body = new StringConverter().convert(response.body()).toString();
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

    private ImportSummary getPutImportSummary(String body) {
        ApiResponse apiResponse = null;
        try {
            //String body = new StringConverter().convert(((ResponseBody)response.body()).byteStream()).toString();
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
