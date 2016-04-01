package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.android.api.network.ApiResource;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleApiClient;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.getCollection;

public class ProgramRuleApiClient implements IProgramRuleApiClient {
    private IProgramRuleApiClientRetrofit programRuleApiClientRetrofit;

    public ProgramRuleApiClient(IProgramRuleApiClientRetrofit programRuleApiClientRetrofit) {
        this.programRuleApiClientRetrofit = programRuleApiClientRetrofit;
    }

    @Override
    public List<ProgramRule> getProgramRules(Fields fields, DateTime lastUpdated, Set<String> uids)
            throws ApiException {
        return getCollection(apiResource, fields, lastUpdated, uids);

    }

    @Override
    public List<ProgramRule> getProgramRules(Fields fields, Set<String> programRuleUids) throws ApiException {
        return getCollection(apiResource, fields, null, programRuleUids);
    }

    @Override
    public List<ProgramRule> getProgramRules(Fields fields, DateTime lastUpdated, List<Program> programs) throws ApiException {
        Set<String> programUidSet = new HashSet<>();
        for(Program program : programs) {
            programUidSet.add(program.getUId());
        }

        return getCollection(apiResource, "program.id", fields, lastUpdated, programUidSet);
    }

    private final ApiResource<ProgramRule> apiResource =
            new ApiResource<ProgramRule>() {

                @Override
                public String getResourceName() {
                    return "programRules";
                }

                @Override
                public String getBasicProperties() {
                    return "id,displayName";
                }

                @Override
                public String getAllProperties() {
                    return "id,name,displayName,created,lastUpdated,access," +
                            "condition,externalAccess,description,program," +
                            "programRuleActions,programStage";
                }

                public Call<Map<String, List<ProgramRule>>> getEntities(
                        Map<String, String> queryMap, List<String> filters) throws ApiException {
                    return programRuleApiClientRetrofit
                            .getProgramRules(queryMap, filters);
                }
            };
}
