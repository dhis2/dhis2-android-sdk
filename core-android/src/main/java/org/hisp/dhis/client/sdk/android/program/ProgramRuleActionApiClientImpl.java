package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.android.api.network.ApiResource;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleActionApiClient;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.getCollection;

public class ProgramRuleActionApiClientImpl implements ProgramRuleActionApiClient {
    private final ProgramRuleActionApiClientRetrofit programRuleActionApiClientRetrofit;

    public ProgramRuleActionApiClientImpl(
            ProgramRuleActionApiClientRetrofit programRuleActionApiClientRetrofit) {
        this.programRuleActionApiClientRetrofit = programRuleActionApiClientRetrofit;
    }

    @Override
    public List<ProgramRuleAction> getProgramRuleActions(Fields fields, DateTime lastUpdated, Set<String> uids) throws ApiException {
        return getCollection(apiResource, fields, lastUpdated, uids);
    }

    @Override
    public List<ProgramRuleAction> getProgramRuleActions(Fields fields, Set<String> programRuleActionUids) throws ApiException {
        return getCollection(apiResource, fields, null, programRuleActionUids);
    }

    private final ApiResource<ProgramRuleAction> apiResource =
            new ApiResource<ProgramRuleAction>() {

                @Override
                public String getResourceName() {
                    return "programRuleActions";
                }

                @Override
                public String getBasicProperties() {
                    return "id";
                }

                @Override
                public String getAllProperties() {
                    return "id,created,lastUpdated,access,content,programRuleActionType" +
                            "programStage[id],programStageSection[id],dataElement[id]"
                            + "programRule[id],trackedEntityAttribute[id],programIndicator[id]";
                }

                public Call<Map<String, List<ProgramRuleAction>>> getEntities(
                        Map<String, String> queryMap, List<String> filters) throws ApiException {
                    return programRuleActionApiClientRetrofit
                            .getProgramRuleActions(queryMap, filters);
                }
            };
}
