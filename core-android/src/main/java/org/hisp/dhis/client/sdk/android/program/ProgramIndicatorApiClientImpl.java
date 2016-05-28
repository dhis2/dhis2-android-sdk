package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.android.api.network.ApiResource;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.program.ProgramIndicatorApiClient;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.getCollection;

public class ProgramIndicatorApiClientImpl implements ProgramIndicatorApiClient {
    private ProgramIndicatorApiClientRetrofit programIndicatorApiClientRetrofit;

    public ProgramIndicatorApiClientImpl(ProgramIndicatorApiClientRetrofit
                                             programIndicatorApiClientRetrofit) {
        this.programIndicatorApiClientRetrofit = programIndicatorApiClientRetrofit;
    }

    @Override
    public List<ProgramIndicator> getProgramIndicators(
            Fields fields, DateTime lastUpdated, Set<String> uids) throws ApiException {
        return getCollection(apiResource, fields, lastUpdated, uids);
    }

    @Override
    public List<ProgramIndicator> getProgramIndicators(
            Fields fields, Set<String> programIndicatorUids) throws ApiException {
        return getCollection(apiResource, fields, null, programIndicatorUids);
    }

    private final ApiResource<ProgramIndicator> apiResource = new ApiResource<ProgramIndicator>() {

        @Override
        public String getResourceName() {
            return "programIndicators";
        }

        @Override
        public String getBasicProperties() {
            return "id,displayName";
        }

        @Override
        public String getAllProperties() {
            return "id,name,displayName,created,lastUpdated,access," +
                    "code,expression,displayDescription," +
                    "rootDate,externalAccess,valueType,displayShortName,program[id]";
        }

        public Call<Map<String, List<ProgramIndicator>>> getEntities(
                Map<String, String> queryMap, List<String> filters) throws ApiException {
            return programIndicatorApiClientRetrofit
                    .getProgramIndicators(queryMap, filters);
        }
    };
}
