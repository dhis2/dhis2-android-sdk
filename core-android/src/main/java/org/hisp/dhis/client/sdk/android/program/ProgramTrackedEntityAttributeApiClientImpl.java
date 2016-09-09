package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.android.api.network.ApiResource;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.program.ProgramTrackedEntityAttributeApiClient;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.getCollection;

public class ProgramTrackedEntityAttributeApiClientImpl implements ProgramTrackedEntityAttributeApiClient {
    private final ProgramTrackedEntityAttributeApiClientRetrofit programTrackedEntityAttributeApiClientRetrofit;

    public ProgramTrackedEntityAttributeApiClientImpl(ProgramTrackedEntityAttributeApiClientRetrofit retrofitClient) {
        this.programTrackedEntityAttributeApiClientRetrofit = retrofitClient;
    }

    private ApiResource<ProgramTrackedEntityAttribute> apiResource = new ApiResource<ProgramTrackedEntityAttribute>() {
        static final String IDENTIFIABLE_PROPERTIES =
                "id,name,displayName,created,lastUpdated,access";
        @Override
        public String getResourceName() {
            return "programTrackedEntityAttributes";
        }

        @Override
        public String getBasicProperties() {
            return "id";
        }

        @Override
        public String getAllProperties() {
            return "id,name,displayName,created,lastUpdated,access,mandatory,displayShortName" +
                    "externalAccess,valueType,allowFutureDate,displayInList,program[id],trackedEntityAttribute[id]";
        }

        /**
         * @return programTrackedEntityAttributes and TrackedEntityAttributes
         */
        @Override
        public String getDescendantProperties() {
            return "programTrackedEntityAttributes[" + IDENTIFIABLE_PROPERTIES + ",mandatory," +
                    "displayShortName,externalAccess,valueType,allowFutureDate,displayInList,program[id]," +
                    "trackedEntityAttribute["+ IDENTIFIABLE_PROPERTIES + ",unique,programScope," + // start trackedEntityAttribute of parent programTrackedEntityAttributes
                    "orgunitScope,displayInListNoProgram,displayOnVisitSchedule,externalAccess," +
                    "valueType,confidential,inherit,sortOrderVisitSchedule,dimension,sortOrderInListNoProgram]]"; //end
        }

        public Call<Map<String, List<ProgramTrackedEntityAttribute>>> getEntities(
                Map<String, String> queryMap, List<String> filters) throws ApiException {
            return programTrackedEntityAttributeApiClientRetrofit
                    .getProgramTrackedEntityAttributes(queryMap, filters);
        }
    };

    @Override
    public List<ProgramTrackedEntityAttribute> getProgramTrackedEntityAttributes(Fields fields, DateTime lastUpdated, Set<String> uids) throws ApiException {
        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = getCollection(apiResource, fields, lastUpdated, uids);
        for(int i = 0; i < programTrackedEntityAttributes.size(); i++) {
            programTrackedEntityAttributes.get(i).setApiSortOrder(i);
        }

        return programTrackedEntityAttributes;
    }

    @Override
    public List<ProgramTrackedEntityAttribute> getProgramTrackedEntityAttributes(Fields fields, Set<String> programTrackedEntityAttributeUids) throws ApiException {
        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = getCollection(apiResource, "programTrackedEntityAttributes.id", fields, null, programTrackedEntityAttributeUids);
        for(int i = 0; i < programTrackedEntityAttributes.size(); i++) {
            programTrackedEntityAttributes.get(i).setApiSortOrder(i);
        }

        return programTrackedEntityAttributes;// getCollection(apiResource, "programTrackedEntityAttributes.id", fields, null, programTrackedEntityAttributeUids);
    }
}
