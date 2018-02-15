package org.hisp.dhis.android.core.event;

import static org.hisp.dhis.android.core.translation.api.Constants.QUERY_LOCALE;
import static org.hisp.dhis.android.core.translation.api.Constants.QUERY_TRANSLATION;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.api.Where;
import org.hisp.dhis.android.core.data.api.Which;
import org.hisp.dhis.android.core.imports.WebResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EventService {
    String FILTER = "filter";
    String ORG_UNIT = "orgUnit";
    String PROGRAM = "program";
    String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";
    String FIELDS = "fields";
    String PAGING = "paging";
    String PAGE_SIZE = "pageSize";
    String PAGE = "page";
    String EVENTS = "events";
    String ATTRIBUTE_CATEGORY_COMBO = "attributeCc";
    String ATTRIBUTE_CATEGORY_OPTION = "attributeCos";
    String INCLUDED_DELETED = "includeDeleted";

    @POST(EVENTS)
    Call<WebResponse> postEvents(@Body EventPayload events);

    @GET(EVENTS)
    Call<Payload<Event>> getEvents(@Query(ORG_UNIT) String orgUnit,
            @Query(PROGRAM) String program,
            @Query(TRACKED_ENTITY_INSTANCE) String trackedEntityInstance,
            @Query(FIELDS) @Which Fields<Event> fields,
            @Query(FILTER) @Where Filter<Event, String> lastUpdated,
            @Query(FILTER) @Where Filter<Event, String> uids,
            @Query(PAGING) Boolean paging, @Query(PAGE) int page,
            @Query(PAGE_SIZE) int pageSize,
            @Query(INCLUDED_DELETED) boolean includedDeleted,
            @Query(QUERY_TRANSLATION) boolean isTranslationOn,
            @NonNull @Query(QUERY_LOCALE) String locale);

    @GET(EVENTS)
    Call<Payload<Event>> getEvents(@Query(ORG_UNIT) String orgUnit,
            @Query(PROGRAM) String program,
            @Query(TRACKED_ENTITY_INSTANCE) String trackedEntityInstance,
            @Query(FIELDS) @Which Fields<Event> fields,
            @Query(FILTER) @Where Filter<Event, String> lastUpdated,
            @Query(FILTER) @Where Filter<Event, String> uids,
            @Query(PAGING) Boolean paging, @Query(PAGE) int page,
            @Query(PAGE_SIZE) int pageSize, @Query(ATTRIBUTE_CATEGORY_COMBO) String categoryCombo,
            @Query(ATTRIBUTE_CATEGORY_OPTION) String categoryOption,
            @Query(INCLUDED_DELETED) boolean includedDeleted,
            @Query(QUERY_TRANSLATION) boolean isTranslationOn,
            @NonNull @Query(QUERY_LOCALE) String locale);
}
