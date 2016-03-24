package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.android.api.network.ApiResource;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.program.IProgramStageDataElementApiClient;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.getCollection;

public class ProgramStageDataElementApiClient implements IProgramStageDataElementApiClient {
    private final IProgramStageDataElementApiClientRetrofit programStageDataElementApiClientRetrofit;

    public ProgramStageDataElementApiClient(IProgramStageDataElementApiClientRetrofit retrofitClient) {
        this.programStageDataElementApiClientRetrofit = retrofitClient;
    }
    @Override
    public List<ProgramStageDataElement> getProgramStageDataElements(Fields fields, DateTime lastUpdated, String... uids) throws ApiException {
        ApiResource<ProgramStageDataElement> apiResource = new ApiResource<ProgramStageDataElement>() {
            @Override
            public String getResourceName() {
                return "programStageDataElements";
            }

            @Override
            public String getBasicProperties() {
                return "id";
            }

            @Override
            public String getAllProperties() {
                return "id,created,lastUpdated,access," +
                        "programStage[id],dataElement[id],allowFutureDate,"
                        + "sortOrder,displayInReports,allowProvidedElsewhere,compulsory";
            }

            @Override
            public Call<Map<String, List<ProgramStageDataElement>>> getEntities(
                    Map<String, String> queryMap, List<String> filters) throws ApiException {
                return programStageDataElementApiClientRetrofit
                        .getProgramStageDataElements(queryMap, filters);
            }
        };

        return getCollection(apiResource, fields, lastUpdated, uids);
    }
}
