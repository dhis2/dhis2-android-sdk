package org.hisp.dhis.android.core.trackedentity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class TrackedEntityInstanceEndPointCallShould {
    @Mock
    private TrackedEntityInstanceService trackedEntityInstanceService;

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private TrackedEntityInstanceHandler trackedEntityInstanceHandler;

    @Mock
    private Date serverDate;

    Dhis2MockServer dhis2MockServer;
    Retrofit retrofit;

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

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_uid_is_null() {
        givenATrackedEntityInstanceEndPointCall(null);
    }

    @Test
    public void create_call_if_uid_is_not_null() {
        TrackedEntityInstanceEndPointCall trackedEntityInstanceEndPointCall =
                givenATrackedEntityInstanceEndPointCall("PgmUFEQYZdt");
        assertThat(trackedEntityInstanceEndPointCall, is(notNullValue()));
    }

    private TrackedEntityInstanceEndPointCall givenATrackedEntityInstanceEndPointCall(
            String trackedEntityInstanceUid) {
        TrackedEntityInstanceEndPointCall trackedEntityInstanceEndPointCall =
                new TrackedEntityInstanceEndPointCall(
                        trackedEntityInstanceService, databaseAdapter, trackedEntityInstanceHandler,
                        resourceHandler, serverDate, trackedEntityInstanceUid);

        return trackedEntityInstanceEndPointCall;
    }
}
