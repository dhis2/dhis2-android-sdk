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
import org.hisp.dhis.android.core.calls.factories.ListCallFactory;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.calls.factories.GenericCallFactory;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.calls.factories.UidsCallFactory;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetCall;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeEndpointCall;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeCall;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class ProgramParentCall extends SyncCall<List<Program>> {

    private final GenericCallData genericCallData;
    private final ListCallFactory<Program> programCallFactory;
    private final UidsCallFactory<ProgramStage> programStageCallFactory;
    private final UidsCallFactory<TrackedEntityType> trackedEntityTypeCallFactory;
    private final ListCallFactory<RelationshipType> relationshipTypeCallFactory;
    private final UidsCallFactory<OptionSet> optionSetCallFactory;

    ProgramParentCall(GenericCallData genericCallData,
                      ListCallFactory<Program> programCallFactory,
                      UidsCallFactory<ProgramStage> programStageCallFactory,
                      UidsCallFactory<TrackedEntityType> trackedEntityTypeCallFactory,
                      ListCallFactory<RelationshipType> relationshipTypeCallFactory,
                      UidsCallFactory<OptionSet> optionSetCallFactory) {
        this.genericCallData = genericCallData;
        this.programCallFactory = programCallFactory;
        this.programStageCallFactory = programStageCallFactory;
        this.trackedEntityTypeCallFactory = trackedEntityTypeCallFactory;
        this.relationshipTypeCallFactory = relationshipTypeCallFactory;
        this.optionSetCallFactory = optionSetCallFactory;
    }

    @Override
    public List<Program> call() throws Exception {
        setExecuted();

        final D2CallExecutor executor = new D2CallExecutor();

        return executor.executeD2CallTransactionally(genericCallData.databaseAdapter(), new Callable<List<Program>>() {
            @Override
            public List<Program> call() throws D2CallException {
                List<Program> programs = executor.executeD2Call(programCallFactory.create(genericCallData));

                Set<String> assignedProgramStageUids = ProgramParentUidsHelper.getAssignedProgramStageUids(programs);
                List<ProgramStage> programStages = executor.executeD2Call(
                        programStageCallFactory.create(genericCallData, assignedProgramStageUids));

                Set<String> trackedEntityUids = ProgramParentUidsHelper.getAssignedTrackedEntityUids(programs);

                executor.executeD2Call(trackedEntityTypeCallFactory.create(genericCallData, trackedEntityUids));
                executor.executeD2Call(relationshipTypeCallFactory.create(genericCallData));

                Set<String> optionSetUids = ProgramParentUidsHelper.getAssignedOptionSetUids(programs, programStages);
                executor.executeD2Call(optionSetCallFactory.create(genericCallData, optionSetUids));

                return programs;
            }
        });
    }

    public static final GenericCallFactory<List<Program>> FACTORY = new GenericCallFactory<List<Program>>() {
        @Override
        public Call<List<Program>> create(GenericCallData genericCallData) {
            return new ProgramParentCall(
                    genericCallData,
                    ProgramEndpointCall.factory(genericCallData.retrofit().create(ProgramService.class)),
                    ProgramStageEndpointCall.FACTORY,
                    TrackedEntityTypeCall.FACTORY,
                    RelationshipTypeEndpointCall.FACTORY,
                    OptionSetCall.FACTORY);
        }
    };
}