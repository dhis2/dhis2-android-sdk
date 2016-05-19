package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.android.api.network.ApiResource;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableApiClient;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.getCollection;

public class ProgramRuleVariableApiClientImpl implements ProgramRuleVariableApiClient {
    private final ProgramRuleVariableApiClientRetrofit programRuleVariableApiClientRetrofit;

    public ProgramRuleVariableApiClientImpl(ProgramRuleVariableApiClientRetrofit apiClientRetrofit) {
        this.programRuleVariableApiClientRetrofit = apiClientRetrofit;
    }

    @Override
    public List<ProgramRuleVariable> getProgramRuleVariables(
            Fields fields, DateTime lastUpdated, Set<String> uids) throws ApiException {
        return getCollection(apiResource, fields, lastUpdated, uids);
    }

    @Override
    public List<ProgramRuleVariable> getProgramRuleVariables(
            Fields fields, Set<String> programRuleVariableUids) throws ApiException {
        return getCollection(apiResource, fields, null, programRuleVariableUids);

    }

    @Override
    public List<ProgramRuleVariable> getProgramRuleVariables(
            Fields fields, DateTime lastUpdated, List<Program> programs) throws ApiException {

        Set<String> programUidSet = new HashSet<>();

        if (programs != null && !programs.isEmpty()) {
            for (Program program : programs) {
                programUidSet.add(program.getUId());
            }
        }
        return getCollection(apiResource, "program.id", fields, lastUpdated, programUidSet);
    }

    private ApiResource<ProgramRuleVariable> apiResource = new ApiResource<ProgramRuleVariable>() {

        @Override
        public String getResourceName() {
            return "programRuleVariables";
        }

        @Override
        public String getBasicProperties() {
            return "id";
        }

        @Override
        public String getAllProperties() {
            return "id,name,displayName,created,lastUpdated,access," +
                    "programRuleVariableSourceType,program[id],programStage[id]," +
                    "dataElement[id],trackedEntityAttribute[id]";
        }

        public Call<Map<String, List<ProgramRuleVariable>>> getEntities(
                Map<String, String> queryMap, List<String> filters) throws ApiException {
            return programRuleVariableApiClientRetrofit.getProgramRuleVariables(queryMap, filters);
        }
    };
}
