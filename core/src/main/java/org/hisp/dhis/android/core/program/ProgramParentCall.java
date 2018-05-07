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
import org.hisp.dhis.android.core.calls.TransactionalCall;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SimpleCallFactory;
import org.hisp.dhis.android.core.common.UidsCallFactory;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetCall;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeEndpointCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeCall;

import java.util.List;
import java.util.Set;

import retrofit2.Response;

public class ProgramParentCall extends TransactionalCall {

    private final GenericCallData genericCallData;
    private final UidsCallFactory<Program> programCallFactory;
    private final Set<String> programUids;
    private final UidsCallFactory<ProgramStage> programStageCallFactory;
    private final UidsCallFactory<TrackedEntityType> trackedEntityTypeCallFactory;
    private final SimpleCallFactory<Payload<RelationshipType>> relationshipTypeCallFactory;
    private final UidsCallFactory<OptionSet> optionSetCallFactory;

    ProgramParentCall(GenericCallData genericCallData,
                      UidsCallFactory<Program> programCallFactory,
                      UidsCallFactory<ProgramStage> programStageCallFactory,
                      UidsCallFactory<TrackedEntityType> trackedEntityTypeCallFactory,
                      SimpleCallFactory<Payload<RelationshipType>> relationshipTypeCallFactory,
                      UidsCallFactory<OptionSet> optionSetCallFactory,
                      Set<String> programUids) {
        super(genericCallData.databaseAdapter());
        this.genericCallData = genericCallData;
        this.programCallFactory = programCallFactory;
        this.programUids = programUids;
        this.programStageCallFactory = programStageCallFactory;
        this.trackedEntityTypeCallFactory = trackedEntityTypeCallFactory;
        this.relationshipTypeCallFactory = relationshipTypeCallFactory;
        this.optionSetCallFactory = optionSetCallFactory;
    }

    @Override
    public Response callBody() throws Exception {
        Call<Response<Payload<Program>>> programEndpointCall = programCallFactory.create(genericCallData, programUids);
        Response<Payload<Program>> programResponse = programEndpointCall.call();

        List<Program> programs = programResponse.body().items();
        Set<String> assignedProgramStageUids = ProgramParentUidsHelper.getAssignedProgramStageUids(programs);
        Response<Payload<ProgramStage>> programStageResponse = programStageCallFactory.create(genericCallData,
                assignedProgramStageUids).call();

        Set<String> trackedEntityUids = ProgramParentUidsHelper.getAssignedTrackedEntityUids(programs);
        trackedEntityTypeCallFactory.create(genericCallData, trackedEntityUids).call();

        relationshipTypeCallFactory.create(genericCallData).call();

        List<ProgramStage> programStages = programStageResponse.body().items();
        Set<String> optionSetUids = ProgramParentUidsHelper.getAssignedOptionSetUids(programs, programStages);
        return optionSetCallFactory.create(genericCallData, optionSetUids).call();
    }

    public interface Factory {
        Call<Response> create(GenericCallData genericCallData, Set<String> uids);
    }

    public static final Factory FACTORY = new Factory() {
        @Override
        public Call<Response> create(GenericCallData genericCallData, Set<String> programUids) {
            return new ProgramParentCall(
                    genericCallData,
                    ProgramEndpointCall.FACTORY,
                    ProgramStageEndpointCall.FACTORY,
                    TrackedEntityTypeCall.FACTORY,
                    RelationshipTypeEndpointCall.FACTORY,
                    OptionSetCall.FACTORY,
                    programUids);
        }
    };
}