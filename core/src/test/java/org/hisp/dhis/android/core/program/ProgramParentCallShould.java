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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.BaseCallShould;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.GenericCallFactory;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.UidsCallFactory;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramParentCallShould extends BaseCallShould {
    @Mock
    private Program program;

    @Mock
    private ObjectWithUid programStageWithUid;

    @Mock
    private TrackedEntityType trackedEntityType;

    @Mock
    private Call<List<Program>> programEndpointCall;

    @Mock
    private Call<List<ProgramStage>> programStageEndpointCall;

    @Mock
    private Call<List<TrackedEntityType>> trackedEntityTypeCall;

    @Mock
    private Call<List<RelationshipType>> relationshipTypeCall;

    @Mock
    private Call<List<OptionSet>> optionSetCall;

    @Mock
    private GenericCallFactory<List<Program>> programCallFactory;

    @Mock
    private UidsCallFactory<ProgramStage> programStageCallFactory;

    @Mock
    private UidsCallFactory<TrackedEntityType> trackedEntityCallFactory;

    @Mock
    private GenericCallFactory<List<RelationshipType>> relationshiptTypeCallFactory;

    @Mock
    private UidsCallFactory<OptionSet> optionSetCallFactory;

    // object to test
    private ProgramParentCall programParentCall;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        super.setUp();

        errorResponse = Response.error(
                HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
                ResponseBody.create(MediaType.parse("application/json"), "{}"));

        // Payload data
        when(program.trackedEntityType()).thenReturn(trackedEntityType);
        when(program.programStages()).thenReturn(Collections.singletonList(programStageWithUid));
        when(programStageWithUid.uid()).thenReturn("program_stage_uid");
        when(trackedEntityType.uid()).thenReturn("test_tracked_entity_uid");

        // Call factories
        when(programCallFactory.create(same(genericCallData)))
                .thenReturn(programEndpointCall);
        when(programStageCallFactory.create(same(genericCallData), any(Set.class)))
                .thenReturn(programStageEndpointCall);
        when(trackedEntityCallFactory.create(same(genericCallData), any(Set.class)))
                .thenReturn(trackedEntityTypeCall);
        when(relationshiptTypeCallFactory.create(same(genericCallData)))
                .thenReturn(relationshipTypeCall);
        when(optionSetCallFactory.create(same(genericCallData), any(Set.class)))
                .thenReturn(optionSetCall);

        // Calls
        when(programEndpointCall.call()).thenReturn(Collections.singletonList(program));
        when(trackedEntityTypeCall.call()).thenReturn(Collections.singletonList(trackedEntityType));
        when(relationshipTypeCall.call()).thenReturn(Collections.<RelationshipType>emptyList());
        when(optionSetCall.call()).thenReturn(Collections.<OptionSet>emptyList());
        when(programStageEndpointCall.call()).thenReturn(Collections.<ProgramStage>emptyList());

        // Metadata call
        programParentCall = new ProgramParentCall(
                genericCallData,
                programCallFactory,
                programStageCallFactory,
                trackedEntityCallFactory,
                relationshiptTypeCallFactory,
                optionSetCallFactory);
    }

    @After
    public void tearDown() throws IOException {
        super.tearDown();
    }

    @Test
    public void succeed_when_endpoint_calls_succeed() throws Exception {
        programParentCall.call();
    }

    @Test
    public void return_programs() throws Exception {
        List<Program> programs = programParentCall.call();
        assertTrue(!programs.isEmpty());
        assertThat(programs.get(0)).isEqualTo(program);
    }

    @Test(expected = D2CallException.class)
    public void fail_when_program_call_fail() throws Exception {
        whenEndpointCallFails(programEndpointCall);
        programParentCall.call();
    }

    @Test(expected = D2CallException.class)
    public void fail_when_program_stage_call_fail() throws Exception {
        whenEndpointCallFails(programStageEndpointCall);
        programParentCall.call();
    }

    @Test(expected = D2CallException.class)
    public void fail_when_tracked_entity_types_call_fail() throws Exception {
        whenEndpointCallFails(trackedEntityTypeCall);
        programParentCall.call();
    }

    @Test(expected = D2CallException.class)
    public void fail_when_relationship_type_call_fail() throws Exception {
        whenEndpointCallFails(relationshipTypeCall);
        programParentCall.call();
    }

    @Test(expected = D2CallException.class)
    public void fail_when_option_set_call_fail() throws Exception {
        whenEndpointCallFails(optionSetCall);
        programParentCall.call();
    }
}
