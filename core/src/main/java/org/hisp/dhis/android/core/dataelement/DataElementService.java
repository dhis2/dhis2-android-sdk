package org.hisp.dhis.android.core.dataelement;

import static org.hisp.dhis.android.core.translation.api.Constants.QUERY_LOCALE;
import static org.hisp.dhis.android.core.translation.api.Constants.QUERY_TRANSLATION;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.api.Where;
import org.hisp.dhis.android.core.data.api.Which;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.Call;

public interface DataElementService {
    String FILTER = "filter";
    String FIELDS = "fields";

    @GET("dataElements")
    Call<Payload<DataElement>> getDataElements(
            @NonNull @Query(FIELDS) @Which Fields<DataElement> fields,
            @NonNull @Query(FILTER) @Where Filter<DataElement, String> idFilter,
            @NonNull @Query(FILTER) @Where Filter<DataElement, String> lastUpdate,
            @Query(QUERY_TRANSLATION) boolean isTranslationOn,
            @NonNull @Query(QUERY_LOCALE) String locale);
}