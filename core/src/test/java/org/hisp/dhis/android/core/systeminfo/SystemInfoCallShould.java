/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.resource.Resource;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class SystemInfoCallShould {

    @Mock
    private SystemInfoService systemInfoService;

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private APICallExecutor apiCallExecutor;

    @Mock
    private D2Error d2Error;

    @Mock
    private SyncHandler<SystemInfo> systemInfoHandler;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<SystemInfo> systemInfoCall;

    @Mock
    private Transaction transaction;

    @Captor
    private ArgumentCaptor<Fields<SystemInfo>> filterCaptor;

    @Mock
    private SystemInfo systemInfo;

    @Mock
    private DHISVersionManager versionManager;

    @Mock
    private Date serverDate;

    private Callable<Unit> systemInfoSyncCall;


    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        systemInfoSyncCall = new SystemInfoCall(
                databaseAdapter, systemInfoHandler, systemInfoService, resourceHandler, versionManager,
                apiCallExecutor);

        when(systemInfo.version()).thenReturn("2.29");
        when(systemInfo.serverDate()).thenReturn(serverDate);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);

        when(systemInfoService.getSystemInfo(any(Fields.class))).thenReturn(systemInfoCall);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void return_correct_fields_after_call() throws Exception {
        when(apiCallExecutor.executeObjectCall(systemInfoCall)).thenReturn(systemInfo);
        when(systemInfoService.getSystemInfo(filterCaptor.capture())).thenReturn(systemInfoCall);

        systemInfoSyncCall.call();

        assertThat(filterCaptor.getValue()).isEqualTo(SystemInfoFields.allFields);

    }

    @Test(expected = D2Error.class)
    @SuppressWarnings("unchecked")
    public void throw_d2_call_exception_on_call_io_exception() throws Exception {
        when(apiCallExecutor.executeObjectCall(systemInfoCall)).thenThrow(d2Error);
        systemInfoSyncCall.call();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void never_invoke_handlers_on_call_exception() throws Exception {
        when(apiCallExecutor.executeObjectCall(systemInfoCall)).thenThrow(d2Error);

        try {
            systemInfoSyncCall.call();
            fail("Exception was not thrown");
        } catch (D2Error d2Error) {
            verify(databaseAdapter, never()).beginNewTransaction();
            verify(transaction, never()).begin();
            verify(transaction, never()).setSuccessful();
            verify(transaction, never()).end();

            verifyNoMoreInteractions(systemInfoHandler);
            verifyNoMoreInteractions(resourceHandler);
        }
    }

    @Test
    public void invoke_handler_after_successful_call() throws Exception {
        when(apiCallExecutor.executeObjectCall(systemInfoCall)).thenReturn(systemInfo);

        systemInfoSyncCall.call();

        verify(systemInfoHandler).handle(systemInfo);
        verify(resourceHandler).handleResource(eq(Resource.Type.SYSTEM_INFO));

    }

    @Test(expected = D2Error.class)
    public void throw_d2_call_exception_when_system_version_different_to_2_29() throws Exception {
        when(systemInfo.version()).thenReturn("2.28");
        when(apiCallExecutor.executeObjectCall(systemInfoCall)).thenReturn(systemInfo);

        systemInfoSyncCall.call();

        verify(systemInfoHandler).handle(systemInfo);
        verify(resourceHandler).handleResource(eq(Resource.Type.SYSTEM_INFO));
    }
}
