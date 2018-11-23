package org.hisp.dhis.android.core.arch.api.executors;

import org.hisp.dhis.android.core.common.ObjectStore;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.user.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class APICallExecutorShould {

    @Mock
    private ObjectStore<D2Error> errorStore;

    @Mock
    private User user;
    private List<User> users;

    @Mock
    private Call<User> objectAPICall;

    @Mock
    private Call<Payload<User>> payloadAPICall;

    @Mock
    private Payload<User> payload;

    @Mock
    private IOException ioException;

    private APICallExecutor apiCallExecutor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        users = Collections.singletonList(user);

        when(objectAPICall.execute()).thenReturn(Response.success(user));
        when(payloadAPICall.execute()).thenReturn(Response.success(payload));
        when(payload.items()).thenReturn(users);

        apiCallExecutor = new APICallExecutorImpl(errorStore);
    }

    @Test
    public void return_object_when_object_api_call_succeeds() throws D2Error {
        User responseUser = apiCallExecutor.executeObjectCall(objectAPICall);
        assertThat(responseUser).isSameAs(user);
    }

    @Test
    public void throw_d2_error_when_object_api_call_fails() throws IOException {
        when(objectAPICall.execute()).thenThrow(ioException);

        try {
            apiCallExecutor.executeObjectCall(objectAPICall);
        } catch (D2Error d2Error) {
            assertThat(d2Error.errorCode()).isEqualTo(D2ErrorCode.API_RESPONSE_PROCESS_ERROR);
        }
    }

    @Test
    public void persist_thrown_d2_error_when_object_api_call_fails() throws IOException {
        when(objectAPICall.execute()).thenThrow(ioException);

        try {
            apiCallExecutor.executeObjectCall(objectAPICall);
        } catch (D2Error d2Error) {
            verify(errorStore).insert(d2Error);
        }
    }

    @Test
    public void return_list_when_payload_api_call_succeeds() throws D2Error {
        List<User> responseUsers = apiCallExecutor.executePayloadCall(payloadAPICall);
        assertThat(responseUsers).isSameAs(users);
    }

    @Test
    public void throw_d2_error_when_payload_api_call_fails() throws IOException {
        when(payloadAPICall.execute()).thenThrow(ioException);

        try {
            apiCallExecutor.executePayloadCall(payloadAPICall);
        } catch (D2Error d2Error) {
            assertThat(d2Error.errorCode()).isEqualTo(D2ErrorCode.API_RESPONSE_PROCESS_ERROR);
        }
    }

    @Test
    public void persist_thrown_d2_error_when_payload_api_call_fails() throws IOException {
        when(payloadAPICall.execute()).thenThrow(ioException);

        try {
            apiCallExecutor.executePayloadCall(payloadAPICall);
        } catch (D2Error d2Error) {
            verify(errorStore).insert(d2Error);
        }
    }
}