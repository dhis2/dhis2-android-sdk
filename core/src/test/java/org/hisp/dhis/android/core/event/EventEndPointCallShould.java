package org.hisp.dhis.android.core.event;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hisp.dhis.android.core.calls.Call.MAX_UIDS;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class EventEndPointCallShould {

    @Mock
    private EventService eventService;

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private EventHandler eventHandler;

    @Mock
    private Date serverDate;

    private Dhis2MockServer dhis2MockServer;
    private Retrofit retrofit;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws IOException {
        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        retrofit = new Retrofit.Builder()
                .baseUrl(dhis2MockServer.getBaseEndpoint())
                .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper()))
                .addConverterFactory(FilterConverterFactory.create())
                .addConverterFactory(FieldsConverterFactory.create())
                .build();

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_uids_size_exceeds_the_limit() {
        givenAEventCallByUIds(MAX_UIDS + 1);
    }

    @Test
    public void create_event_call_if_uids_size_does_not_exceeds_the_limit() {
        EventEndPointCall eventEndPointCall = givenAEventCallByUIds(MAX_UIDS);
        assertThat(eventEndPointCall, is(notNullValue()));
    }

    @Test
    public void realize_request_with_page_filters_when_included_in_query()
            throws Exception {
        whenCallEventEndPointCall();

        RecordedRequest request = dhis2MockServer.takeRequest();

        assertThat(request.getPath(), containsString("paging=true&page=2&pageSize=32"));
    }

    @Test
    public void realize_request_with_orgUnit_program_filters_when_included_in_query()
            throws Exception {
        EventEndPointCall eventEndPointCall = givenAEventCallByOrgUnitAndProgram();

        dhis2MockServer.enqueueMockResponse();

        eventEndPointCall.call();

        RecordedRequest request = dhis2MockServer.takeRequest();

        assertThat(request.getPath(), containsString("orgUnit=OU&program=P"));
    }

    @Test
    public void append_translation_variables_to_the_query_string()
            throws Exception {

        whenCallEventEndPointCall();

        thenAssertTranslationParametersAreIncluded();

        whenCallEventEndPointCallWithCategoryComboAndCategoryOption();

        thenAssertTranslationParametersAreIncluded();
    }

    private EventEndPointCall givenAEventCallByUIds(int numUIds) {
        Set<String> uIds = new HashSet<>();

        for (int i = 0; i < numUIds; i++) {
            uIds.add("uid" + i);
        }

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withUIds(uIds)
                .build();

        return new EventEndPointCall(eventService, databaseAdapter, resourceHandler,
                eventHandler, serverDate, eventQuery);
    }

    private EventEndPointCall givenAEventCallByPagination() {
        return givenAEventCallByPagination(null, null);
    }

    private EventEndPointCall givenAEventCallByPagination(@Nullable CategoryCombo categoryCombo,
            @Nullable CategoryOption categoryOption) {
        EventService eventService = retrofit.create(EventService.class);

        EventQuery eventQuery = provideEventQuery(categoryCombo, categoryOption);


        return new EventEndPointCall(eventService, databaseAdapter, resourceHandler,
                eventHandler, serverDate, eventQuery);

    }

    private EventQuery provideEventQuery(@Nullable CategoryCombo categoryCombo,
            @Nullable CategoryOption categoryOption) {
        EventQuery eventQuery;
        if (categoryCombo != null && categoryOption != null) {
            eventQuery = EventQuery.Builder
                    .create()
                    .withPage(2)
                    .withPageSize(32)
                    .withPaging(true)
                    .withTranslationLocale(DEFAULT_TRANSLATION_LOCALE)
                    .withIsTranslationOn(DEFAULT_IS_TRANSLATION_ON)
                    .withCategoryComboAndCategoryOption(categoryCombo, categoryOption)
                    .build();
        } else {
            eventQuery = EventQuery.Builder
                    .create()
                    .withPage(2)
                    .withPageSize(32)
                    .withTranslationLocale(DEFAULT_TRANSLATION_LOCALE)
                    .withIsTranslationOn(DEFAULT_IS_TRANSLATION_ON)
                    .withPaging(true)
                    .build();
        }
        return eventQuery;
    }


    private EventEndPointCall givenAEventCallByOrgUnitAndProgram() {
        EventService eventService = retrofit.create(EventService.class);

        EventQuery eventQuery = EventQuery.Builder
                .create()
                .withOrgUnit("OU")
                .withProgram("P")
                .build();


        return new EventEndPointCall(eventService, databaseAdapter, resourceHandler,
                eventHandler, serverDate, eventQuery);
    }

    private void thenAssertTranslationParametersAreIncluded() throws InterruptedException {
        RecordedRequest request = dhis2MockServer.takeRequest();

        assertThat(request.getPath(), containsString(
                "translation=" + DEFAULT_IS_TRANSLATION_ON + "&locale="
                        + DEFAULT_TRANSLATION_LOCALE));
    }

    private void whenCallEventEndPointCall() throws Exception {
        EventEndPointCall eventEndPointCall = givenAEventCallByPagination();

        dhis2MockServer.enqueueMockResponse();

        eventEndPointCall.call();
    }

    private void whenCallEventEndPointCallWithCategoryComboAndCategoryOption() throws Exception {
        CategoryCombo categoryCombo = CategoryCombo.builder().uid("uid").build();
        CategoryOption categoryOption = CategoryOption.builder().uid("uid").build();

        EventEndPointCall eventEndPointCall = givenAEventCallByPagination(categoryCombo,
                categoryOption);

        dhis2MockServer.enqueueMockResponse();

        eventEndPointCall.call();
    }
}
