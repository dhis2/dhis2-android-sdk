package org.hisp.dhis.android.sdk.synchronization.data.enrollment;


import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.network.DhisApi;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.synchronization.data.common.ARemoteDataSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Response;


public class EnrollmentRemoteDataSource extends ARemoteDataSource {

    public EnrollmentRemoteDataSource(DhisApi dhisApi) {
        this.dhisApi = dhisApi;
    }

    public Enrollment getEnrollment(String enrollment) {
        final Map<String, String> QUERY_PARAMS = new HashMap<>();
        QUERY_PARAMS.put("fields", "created,lastUpdated");
        Enrollment updatedEnrollment = null;
        try {
            updatedEnrollment = dhisApi
                    .getEnrollment(enrollment, QUERY_PARAMS).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return updatedEnrollment;
    }

    public ImportSummary save(Enrollment enrollment) {
        if (enrollment.getCreated() == null) {
            return postEnrollment(enrollment, dhisApi);
        } else {
            return putEnrollment(enrollment, dhisApi);
        }
    }

    private ImportSummary postEnrollment(Enrollment enrollment, DhisApi dhisApi) throws APIException{
        Response response = null;
        try {
            response = dhisApi.postEnrollment(enrollment).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getImportSummary(response);
    }

    private ImportSummary putEnrollment(Enrollment enrollment, DhisApi dhisApi) throws APIException{
        Response response = null;
        try {
            response = dhisApi.putEnrollment(enrollment.getEnrollment(), enrollment).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getImportSummary(response);
    }

}
