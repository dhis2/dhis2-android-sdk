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
package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.call.factories.internal.ListCall;
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall;
import org.hisp.dhis.android.core.common.BaseCallShould;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionGroup;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramModuleDownloaderShould extends BaseCallShould {
    @Mock
    private Program program;

    @Mock
    private TrackedEntityType trackedEntityType;

    @Mock
    private TrackedEntityAttribute trackedEntityAttribute;

    @Mock
    private UidsCall<Program> programCall;

    @Mock
    private UidsCall<ProgramStage> programStageCall;

    @Mock
    private UidsCall<ProgramRule> programRuleCall;

    @Mock
    private UidsCall<TrackedEntityType> trackedEntityTypeCall;

    @Mock
    private UidsCall<TrackedEntityAttribute> trackedEntityAttributeCall;

    @Mock
    private ListCall<RelationshipType> relationshipTypeCall;

    @Mock
    private UidsCall<OptionSet> optionSetCall;

    @Mock
    private UidsCall<Option> optionCall;

    @Mock
    private UidsCall<OptionGroup> optionGroupCall;

    // object to test
    private ProgramModuleDownloader programModuleDownloader;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        errorResponse = Response.error(
                HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
                ResponseBody.create(MediaType.parse("application/json"), "{}"));

        // Calls
        returnSingletonList(programCall, program);
        returnSingletonList(trackedEntityTypeCall, trackedEntityType);
        returnSingletonList(trackedEntityAttributeCall, trackedEntityAttribute);
        returnSingletonList(programCall, program);
        when(relationshipTypeCall.download()).thenReturn(Single.just(Collections.emptyList()));
        returnEmptyList(optionSetCall);
        returnEmptyList(optionCall);
        returnEmptyList(optionGroupCall);
        returnEmptyList(programRuleCall);
        returnEmptyList(programStageCall);

        programModuleDownloader = new ProgramModuleDownloader(
                programCall,
                programStageCall,
                programRuleCall,
                trackedEntityTypeCall,
                trackedEntityAttributeCall,
                relationshipTypeCall,
                optionSetCall,
                optionCall,
                optionGroupCall);
    }

    private void returnEmptyList(UidsCall<?> call) {
        when(call.download(anySet())).thenReturn(Single.just(Collections.emptyList()));
    }

    private <O> void returnSingletonList(UidsCall<O> call, O o) {
        when(call.download(anySet())).thenReturn(Single.just(Collections.singletonList(o)));
    }

    private void returnError(UidsCall<?> call) {
        when(call.download(anySet())).thenReturn(Single.error(new RuntimeException()));
    }

    @Test
    public void succeed_when_endpoint_calls_succeed() {
        programModuleDownloader.downloadMetadata(anySet()).blockingGet();
    }

    @Test
    public void return_programs() {
        List<Program> programs = programModuleDownloader.downloadMetadata(anySet()).blockingGet();
        assertTrue(!programs.isEmpty());
        assertThat(programs.get(0)).isEqualTo(program);
    }

    @Test(expected = Exception.class)
    public void fail_when_program_call_fails() {
        returnError(programCall);
        programModuleDownloader.downloadMetadata(anySet()).blockingGet();
    }

    @Test(expected = Exception.class)
    public void fail_when_program_stage_call_fails() {
        returnError(programStageCall);
        programModuleDownloader.downloadMetadata(anySet()).blockingGet();
    }

    @Test(expected = Exception.class)
    public void fail_when_program_rule_call_fails() {
        returnError(programRuleCall);
        programModuleDownloader.downloadMetadata(anySet()).blockingGet();
    }

    @Test(expected = Exception.class)
    public void fail_when_tracked_entity_types_call_fails() {
        returnError(trackedEntityTypeCall);
        programModuleDownloader.downloadMetadata(anySet()).blockingGet();
    }

    @Test(expected = Exception.class)
    public void fail_when_tracked_entity_attributes_call_fails() {
        returnError(trackedEntityAttributeCall);
        programModuleDownloader.downloadMetadata(anySet()).blockingGet();
    }

    @Test(expected = Exception.class)
    public void fail_when_relationship_type_call_fails() {
        when(relationshipTypeCall.download()).thenReturn(Single.error(new RuntimeException()));
        programModuleDownloader.downloadMetadata(anySet()).blockingGet();
    }

    @Test(expected = Exception.class)
    public void fail_when_option_call_fails() {
        returnError(optionCall);
        programModuleDownloader.downloadMetadata(anySet()).blockingGet();
    }
}
