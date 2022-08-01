/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.systeminfo.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.RxAPICallExecutor;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.Transaction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.resource.internal.Resource;
import org.hisp.dhis.android.core.resource.internal.ResourceHandler;
import org.hisp.dhis.android.core.systeminfo.SystemInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import io.reactivex.Single;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;
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
    private RxAPICallExecutor apiCallExecutor;

    @Mock
    private D2Error d2Error;

    @Mock
    private Handler<SystemInfo> systemInfoHandler;

    @Mock
    private ResourceHandler resourceHandler;

    @Mock
    private Single<SystemInfo> systemInfoSingle;

    @Mock
    private Transaction transaction;

    @Captor
    private ArgumentCaptor<Fields<SystemInfo>> filterCaptor;

    @Mock
    private SystemInfo systemInfo;

    @Mock
    private DHISVersionManagerImpl versionManager;

    @Mock
    private Date serverDate;

    private SystemInfoCall systemInfoSyncCall;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        systemInfoSyncCall = new SystemInfoCall(
                databaseAdapter, systemInfoHandler, systemInfoService, resourceHandler, versionManager,
                apiCallExecutor);

        when(systemInfo.version()).thenReturn("2.29");
        when(systemInfo.serverDate()).thenReturn(serverDate);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);
        when(systemInfoService.getSystemInfo(filterCaptor.capture())).thenReturn(systemInfoSingle);
    }

    @Test
    public void pass_correct_fields_to_service() {
        when(apiCallExecutor.wrapSingle(systemInfoSingle, true)).thenReturn(Single.just(systemInfo));
        systemInfoSyncCall.getCompletable(true).subscribe();
        assertThat(filterCaptor.getValue()).isEqualTo(SystemInfoFields.allFields);
    }

    @Test
    public void emit_d2_error_when_api_call_executor_returns_error() {
        when(apiCallExecutor.wrapSingle(systemInfoSingle, true)).thenReturn(Single.error(d2Error));
        systemInfoSyncCall.getCompletable(true).test().assertError(d2Error);
    }

    @Test
    public void never_invoke_handlers_on_call_exception() {
        when(apiCallExecutor.wrapSingle(systemInfoSingle, true)).thenReturn(Single.error(d2Error));

        systemInfoSyncCall.getCompletable(true).onErrorComplete().subscribe();

        verify(databaseAdapter, never()).beginNewTransaction();
        verify(transaction, never()).setSuccessful();
        verify(transaction, never()).end();

        verifyNoMoreInteractions(systemInfoHandler);
        verifyNoMoreInteractions(resourceHandler);
    }

    @Test
    public void invoke_handler_after_successful_call() {
        when(apiCallExecutor.wrapSingle(systemInfoSingle, true)).thenReturn(Single.just(systemInfo));

        systemInfoSyncCall.getCompletable(true).subscribe();

        verify(systemInfoHandler).handle(systemInfo);
        verify(resourceHandler).handleResource(eq(Resource.Type.SYSTEM_INFO));
    }

    @Test
    public void throw_d2_call_exception_when_system_version_not_supported() {
        when(systemInfo.version()).thenReturn("2.28");
        when(apiCallExecutor.wrapSingle(systemInfoSingle, true)).thenReturn(Single.just(systemInfo));

        systemInfoSyncCall.getCompletable(true).test().assertError(D2Error.class);
    }

    @Test
    public void not_call_handler_when_system_version_not_supported() {
        when(systemInfo.version()).thenReturn("2.28");
        when(apiCallExecutor.wrapSingle(systemInfoSingle, true)).thenReturn(Single.just(systemInfo));

        try {
            systemInfoSyncCall.getCompletable(true).blockingAwait();
            fail("It should not get here");
        } catch (RuntimeException e) {
            assertThat(((D2Error) e.getCause()).errorCode())
                    .isEquivalentAccordingToCompareTo(D2ErrorCode.INVALID_DHIS_VERSION);
        }

        verify(systemInfoHandler, never()).handle(systemInfo);
        verify(resourceHandler, never()).handleResource(eq(Resource.Type.SYSTEM_INFO));
    }
}
