package org.hisp.dhis.android.core.trackedentity;

import static org.hisp.dhis.android.core.translation.api.Constants.QUERY_LOCALE;
import static org.hisp.dhis.android.core.translation.api.Constants.QUERY_TRANSLATION;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.api.Where;
import org.hisp.dhis.android.core.data.api.Which;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TrackedEntityAttributeService {
    String FILTER = "filter";
    String FIELDS = "fields";

    @GET("trackedEntityAttributes")
    Call<Payload<TrackedEntityAttribute>> getTrackedEntityAttributes(
            @NonNull @Query(FIELDS) @Which Fields<TrackedEntityAttribute> fields,
            @NonNull @Query(FILTER) @Where Filter<TrackedEntityAttribute, String> idFilter,
            @NonNull @Query(FILTER) @Where Filter<TrackedEntityAttribute, String> lastUpdated,
            @Query(QUERY_TRANSLATION) boolean isTranslationOn,
            @NonNull @Query(QUERY_LOCALE) String locale);
}
