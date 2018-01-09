package org.hisp.dhis.android.core.deleteobject;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hisp.dhis.android.core.calls.Call.MAX_UIDS;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.api.FilterConverterFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.deletedobject.DeletedObject;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectEndPointCall;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectHandler;
import org.hisp.dhis.android.core.deletedobject.DeletedObjectService;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventEndPointCall;
import org.hisp.dhis.android.core.event.EventHandler;
import org.hisp.dhis.android.core.event.EventQuery;
import org.hisp.dhis.android.core.event.EventService;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
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
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class DeleteObjectEndPointCallShould {
    private Call<Response<Payload<DeletedObject>>> deleteObject;

    @Mock
    private DeletedObjectService deletedObjectService;

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private ResourceStore resourceStore;

    @Mock
    private DeletedObjectHandler deleteObjectHandler;

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

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throw_illegal_argument_exception_if_type_is_not_supported() throws Exception {
        new DeletedObjectEndPointCall(deletedObjectService, databaseAdapter,
                resourceStore,
                deleteObjectHandler, serverDate, "wrong_klass").call();
    }

}
