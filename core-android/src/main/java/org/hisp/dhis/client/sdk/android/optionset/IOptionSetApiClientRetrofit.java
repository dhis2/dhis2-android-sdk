package org.hisp.dhis.client.sdk.android.optionset;

import org.hisp.dhis.client.sdk.models.optionset.OptionSet;

import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

public interface IOptionSetApiClientRetrofit {
    @GET("/optionSets/{optionSetUid}")
    Call<OptionSet> getOptionSet(@Path("optionSetUid") String optionSetUid, @QueryMap Map<String, String> queryMap);

    @GET("/optionSets/")
    Call<List<OptionSet>> getOptionSets(@QueryMap Map<String, String> queryMap);

}
