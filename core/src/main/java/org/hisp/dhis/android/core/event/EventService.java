package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Which;
import org.hisp.dhis.android.core.imports.WebResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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
    String STRATEGY = "strategy";
    String EVENT_UID = "eventUid";

    @POST(EVENTS)
    Call<WebResponse> postEvents(@Body EventPayload events, @Query(STRATEGY) String strategy);

    @GET(EVENTS)
    Call<Payload<Event>> getEvents(
            @Query(ORG_UNIT) String orgUnit,
            @Query(PROGRAM) String program,
            @Query(TRACKED_ENTITY_INSTANCE) String trackedEntityInstance,
            @Query(FIELDS) @Which Fields<Event> fields,
            @Query(PAGING) Boolean paging,
            @Query(PAGE) int page,
            @Query(PAGE_SIZE) int pageSize);

    @GET(EVENTS)
    Call<Payload<Event>> getEvents(@Query(ORG_UNIT) String orgUnit,
            @Query(PROGRAM) String program,
            @Query(TRACKED_ENTITY_INSTANCE) String trackedEntityInstance,
            @Query(FIELDS) @Which Fields<Event> fields,
            @Query(PAGING) Boolean paging, @Query(PAGE) int page,
            @Query(PAGE_SIZE) int pageSize, @Query(ATTRIBUTE_CATEGORY_COMBO) String categoryCombo);

    @GET(EVENTS + "/{" + EVENT_UID + "}")
    Call<Event> getEvent(
            @Path(EVENT_UID) String eventUid,
            @Query(FIELDS) @Which Fields<Event> fields);
}
