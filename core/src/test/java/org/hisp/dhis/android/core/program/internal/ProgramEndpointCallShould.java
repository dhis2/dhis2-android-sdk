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
package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloader;
import org.hisp.dhis.android.core.arch.api.executors.internal.APIDownloaderImpl;
import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.filters.internal.Filter;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.common.BaseCallShould;
import org.hisp.dhis.android.core.program.Program;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.reactivex.Single;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramEndpointCallShould extends BaseCallShould {

    @Mock
    private ProgramService programService;

    @Mock
    private Handler<Program> programHandler;

    @Captor
    private ArgumentCaptor<Fields<Program>> fieldsCaptor;

    @Captor
    private ArgumentCaptor<Filter<Program, String>> filterCaptor;

    @Captor
    private ArgumentCaptor<String> accessDataReadFilter;

    private Single<Payload<Program>> apiCall = Single.just(Payload.emptyPayload());

    @Mock
    private APIDownloader mockedApiDownloader;

    private Single<List<Program>> programCallResult = Single.just(Collections.emptyList());

    private Set<String> programUids = Sets.newSet("programUid");

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        when(mockedApiDownloader.downloadPartitioned(same(programUids), anyInt(), any(Handler.class), any())).thenReturn(programCallResult);
        when(programService.getPrograms(any(Fields.class), any(Filter.class), anyString(),
                anyBoolean())).thenReturn(apiCall);
    }

    @Test
    public void call_api_downloader() {
        new ProgramCall(programService, programHandler, mockedApiDownloader).download(programUids).blockingGet();

        verify(mockedApiDownloader).downloadPartitioned(same(programUids), anyInt(), any(Handler.class), any());
    }

    @Test
    public void call_service_for_real_api_downloader() {
        when(programService.getPrograms(
                fieldsCaptor.capture(), filterCaptor.capture(), accessDataReadFilter.capture(), anyBoolean())
        ).thenReturn(apiCall);

        new ProgramCall(programService, programHandler, new APIDownloaderImpl(resourceHandler)).download(programUids).blockingGet();

        assertThat(fieldsCaptor.getValue()).isEqualTo(ProgramFields.allFields);
        assertThat(filterCaptor.getValue().values().iterator().next()).isEqualTo("programUid");
        assertThat(accessDataReadFilter.getValue()).isEqualTo("access.data.read:eq:true");
    }
}
