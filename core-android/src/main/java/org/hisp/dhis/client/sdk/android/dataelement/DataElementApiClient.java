package org.hisp.dhis.client.sdk.android.dataelement;

import org.hisp.dhis.client.sdk.android.api.network.ApiResource;
import org.hisp.dhis.client.sdk.core.common.Fields;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementApiClient;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

import static org.hisp.dhis.client.sdk.android.api.network.NetworkUtils.getCollection;

public class DataElementApiClient implements IDataElementApiClient {
    private final IDataElementApiClientRetrofit dataElementApiClientRetrofit;

    public DataElementApiClient(IDataElementApiClientRetrofit dataElementApiClientRetrofit) {
        this.dataElementApiClientRetrofit = dataElementApiClientRetrofit;
    }
    @Override
    public List<DataElement> getDataElements(Fields fields, DateTime lastUpdated, String... uids) throws ApiException {
        ApiResource<DataElement> apiResource = new ApiResource<DataElement>() {

            @Override
            public String getResourceName() {
                return "dataElements";
            }

            @Override
            public String getBasicProperties() {
                return "id,displayName";
            }

            @Override
            public String getAllProperties() {
                return "id,name,displayName,created,lastUpdated,access," +
                        "shortName,valueType,zeroIsSignificant,aggregationOperator" +
                        "formName,numberType,domainType,dimension,displayFormName" +
                        "optionSet[id]";
            }

            @Override
            public Call<Map<String, List<DataElement>>> getEntities(
                    Map<String, String> queryMap, List<String> filters) throws ApiException {
                return dataElementApiClientRetrofit.getDataElements(queryMap, filters);
            }
        };

        return getCollection(apiResource, fields, lastUpdated, uids);
    }
}
