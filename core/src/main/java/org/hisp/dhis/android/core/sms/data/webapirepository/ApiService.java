package org.hisp.dhis.android.core.sms.data.webapirepository;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface ApiService {
    String GET_IDS = "id";

    @SuppressWarnings("PMD") // That's an API call and looks like an API endpoint
    @GET("metadata")
    Call<MetadataResponseModel> getMetadataIds(
            @Query("dataElements:fields") String dataElements,
            @Query("categoryOptionCombos:fields") String categoryOptionCombos,
            @Query("organisationUnits:fields") String organisationUnits,
            @Query("users:fields") String users,
            @Query("trackedEntityTypes:fields") String trackedEntityTypes,
            @Query("trackedEntityAttributes:fields") String trackedEntityAttributes,
            @Query("programs:fields") String programs
    );
}