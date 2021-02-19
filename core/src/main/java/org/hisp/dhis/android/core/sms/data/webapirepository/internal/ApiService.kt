package org.hisp.dhis.android.core.sms.data.webapirepository.internal

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

@Suppress("LongParameterList")
interface ApiService {

    // That's an API call and looks like an API endpoint
    @GET("metadata")
    fun getMetadataIds(
        @Query("dataElements:fields") dataElements: String?,
        @Query("categoryOptionCombos:fields") categoryOptionCombos: String?,
        @Query("organisationUnits:fields") organisationUnits: String?,
        @Query("users:fields") users: String?,
        @Query("trackedEntityTypes:fields") trackedEntityTypes: String?,
        @Query("trackedEntityAttributes:fields") trackedEntityAttributes: String?,
        @Query("programs:fields") programs: String?
    ): Single<MetadataResponse>

    companion object {
        const val GET_IDS = "id"
    }
}
