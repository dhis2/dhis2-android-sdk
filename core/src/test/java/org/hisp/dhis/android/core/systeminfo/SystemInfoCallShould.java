/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.systeminfo;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_IS_TRANSLATION_ON;
import static org.hisp.dhis.android.core.data.TestConstants.DEFAULT_TRANSLATION_LOCALE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.support.annotation.NonNull;

import org.hamcrest.MatcherAssert;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.RetrofitFactory;
import org.hisp.dhis.android.core.data.server.api.Dhis2MockServer;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;
import retrofit2.Retrofit;

@RunWith(JUnit4.class)
public class SystemInfoCallShould {

    @Mock
    private SystemInfoService systemInfoService;

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private SystemInfoStore systemInfoStore;

    @Mock
    private ResourceStore resourceStore;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<SystemInfo> systemInfoCall;

    @Mock
    private Transaction transaction;

    @Captor
    private ArgumentCaptor<Fields<SystemInfo>> filterCaptor;

    @Mock
    private SystemInfo systemInfo;

    @Mock
    private Date serverDate;

    private Call<Response<SystemInfo>> systemInfoSyncCall;

    private Dhis2MockServer dhis2MockServer;

    private Retrofit retrofit;

    private SystemInfoQuery systemInfoQuery;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());
        retrofit = RetrofitFactory.build(dhis2MockServer.getBaseEndpoint());

        MockitoAnnotations.initMocks(this);

        systemInfoQuery = SystemInfoQuery.defaultQuery(DEFAULT_IS_TRANSLATION_ON,
                DEFAULT_TRANSLATION_LOCALE);

        systemInfoSyncCall = new SystemInfoCall(
                databaseAdapter, systemInfoStore, systemInfoService, resourceStore,
                systemInfoQuery
        );

        when(systemInfo.version()).thenReturn("test.version-SNAPSHOT");
        when(systemInfo.serverDate()).thenReturn(serverDate);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);

        when(systemInfoService.getSystemInfo(any(Fields.class), anyBoolean(),
                anyString())).thenReturn(systemInfoCall);
    }

    @After
    public void tearDown() throws IOException {
        dhis2MockServer.shutdown();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void return_correct_fields_after_call() throws Exception {
        when(systemInfoCall.execute()).thenReturn(Response.success(systemInfo));
        when(systemInfoService.getSystemInfo(filterCaptor.capture(), anyBoolean(),
                anyString())).thenReturn(systemInfoCall);

        systemInfoSyncCall.call();

        assertThat(filterCaptor.getValue().fields()).contains(
                SystemInfo.serverDateTime,
                SystemInfo.dateFormat,
                SystemInfo.version,
                SystemInfo.contextPath
        );

    }

    @Test
    @SuppressWarnings("unchecked")
    public void never_invoke_handlers_on_call_io_exception() throws Exception {
        when(systemInfoCall.execute()).thenThrow(IOException.class);

        try {
            systemInfoSyncCall.call();
            fail("Exception was not thrown");
        } catch (IOException ioexception) {
            verify(databaseAdapter, never()).beginNewTransaction();
            verify(transaction, never()).begin();
            verify(transaction, never()).setSuccessful();
            verify(transaction, never()).end();

            verify(systemInfoStore, never()).insert(any(Date.class), anyString(), anyString(),
                    anyString());
            verify(resourceStore, never()).insert(anyString(), any(Date.class));
            verify(resourceStore, never()).update(anyString(), any(Date.class), anyString());
            verify(resourceStore, never()).delete(anyString());

        }
    }

    @Test
    public void never_invoke_handlers_if_request_fail() throws Exception {
        // unauthorized
        when(systemInfoCall.execute()).thenReturn(
                Response.<SystemInfo>error(HttpURLConnection.HTTP_UNAUTHORIZED,
                        ResponseBody.create(MediaType.parse("application/json"), "{}")));

        Response<SystemInfo> response = systemInfoSyncCall.call();

        // check that response code is equal to unauthorized
        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_UNAUTHORIZED);

        // verify that adapter and handlers was not touched
        verify(databaseAdapter, never()).beginNewTransaction();
        verify(transaction, never()).end();
        verify(transaction, never()).setSuccessful();

        verify(systemInfoStore, never()).insert(any(Date.class), anyString(), anyString(),
                anyString());
        verify(resourceStore, never()).insert(anyString(), any(Date.class));
        verify(resourceStore, never()).update(anyString(), any(Date.class), anyString());
        verify(resourceStore, never()).delete(anyString());

    }

    @Test
    public void
    return_true_when_ask_if_is_executed_before_throw_illegal_state_exception_on_consecutive_calls()
            throws Exception {
        when(systemInfoCall.execute()).thenReturn(Response.success(systemInfo));

        systemInfoSyncCall.call();

        assertThat(systemInfoSyncCall.isExecuted()).isTrue();

        try {
            systemInfoSyncCall.call();
            fail("Multiple executions of a call should throw exception");
        } catch (IllegalStateException ex) {
            // do nothing
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void return_true_when_ask_if_is_executed_before_io_exception() throws Exception {
        when(systemInfoCall.execute()).thenThrow(IOException.class);

        try {
            systemInfoSyncCall.call();
        } catch (IOException ioexception) {
            // do nothing
        }

        assertThat(systemInfoSyncCall.isExecuted()).isTrue();

        try {
            systemInfoSyncCall.call();
            fail("Multiple executions of a call should throw exception");
        } catch (Exception exception) {
            // ignore exception
        }
    }

    @Test
    public void invoke_stores_after_successful_call() throws Exception {
        when(systemInfoCall.execute()).thenReturn(Response.success(systemInfo));

        systemInfoSyncCall.call();

        verify(systemInfoStore, times(1)).insert(any(Date.class), anyString(), anyString(),
                anyString());
        verify(resourceStore, times(1)).insert(anyString(), any(Date.class));

    }

    @Test
    public void append_translation_variables_to_the_query_string()
            throws Exception {

        whenCalSystemInfoCallWithMockWebservice();

        thenAssertTranslationParametersAreIncluded();
    }

    private void thenAssertTranslationParametersAreIncluded() throws InterruptedException {
        RecordedRequest request = dhis2MockServer.takeRequest();

        MatcherAssert.assertThat(request.getPath(), containsString(
                "translation=" + DEFAULT_IS_TRANSLATION_ON + "&locale="
                        + DEFAULT_TRANSLATION_LOCALE));
    }

    private void whenCalSystemInfoCallWithMockWebservice() throws Exception {
        SystemInfoCall callWithMockWebservice = provideSystemInfoCallWithMockWebservice();

        dhis2MockServer.enqueueMockResponse("system_info.json");
        callWithMockWebservice.call();
    }

    @NonNull
    private SystemInfoCall provideSystemInfoCallWithMockWebservice() {
        SystemInfoService mockService = retrofit.create(SystemInfoService.class);

        return new SystemInfoCall(
                databaseAdapter, systemInfoStore, mockService, resourceStore,
                systemInfoQuery
        );
    }
}
