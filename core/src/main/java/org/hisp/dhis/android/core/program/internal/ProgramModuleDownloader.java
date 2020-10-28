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
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.arch.modules.internal.MetadataModuleByUidDownloader;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionGroup;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;

@Reusable
public class ProgramModuleDownloader implements MetadataModuleByUidDownloader<List<Program>> {

    private final UidsCall<Program> programCall;
    private final UidsCall<ProgramStage> programStageCall;
    private final UidsCall<ProgramRule> programRuleCall;
    private final UidsCall<TrackedEntityType> trackedEntityTypeCall;
    private final UidsCall<TrackedEntityAttribute> trackedEntityAttributeCall;
    private final ListCall<RelationshipType> relationshipTypeCall;
    private final UidsCall<OptionSet> optionSetCall;
    private final UidsCall<Option> optionCall;
    private final UidsCall<OptionGroup> optionGroupCall;

    @Inject
    ProgramModuleDownloader(UidsCall<Program> programCall,
                            UidsCall<ProgramStage> programStageCall,
                            UidsCall<ProgramRule> programRuleCall,
                            UidsCall<TrackedEntityType> trackedEntityTypeCall,
                            UidsCall<TrackedEntityAttribute> trackedEntityAttributeCall,
                            ListCall<RelationshipType> relationshipTypeCall,
                            UidsCall<OptionSet> optionSetCall,
                            UidsCall<Option> optionCall,
                            UidsCall<OptionGroup> optionGroupCall) {
        this.programCall = programCall;
        this.programStageCall = programStageCall;
        this.programRuleCall = programRuleCall;
        this.trackedEntityTypeCall = trackedEntityTypeCall;
        this.trackedEntityAttributeCall = trackedEntityAttributeCall;
        this.relationshipTypeCall = relationshipTypeCall;
        this.optionSetCall = optionSetCall;
        this.optionCall = optionCall;
        this.optionGroupCall = optionGroupCall;
    }

    @Override
    public Single<List<Program>> downloadMetadata(Set<String> orgUnitProgramUids) {
        return programCall.download(orgUnitProgramUids).flatMap(programs -> {
            Set<String> programUids = UidsHelper.getUids(programs);
            return programStageCall.download(programUids).flatMap(programStages -> {
                Set<String> trackedEntityUids = ProgramParentUidsHelper.getAssignedTrackedEntityUids(programs);
                return trackedEntityTypeCall.download(trackedEntityUids).flatMap(trackedEntityTypes ->
                        trackedEntityAttributeCall.download(ProgramParentUidsHelper
                                .getAssignedTrackedEntityAttributeUids(programs, trackedEntityTypes)))
                        .flatMap(attributes -> {
                            Set<String> optionSetUids = ProgramParentUidsHelper.getAssignedOptionSetUids(
                                    attributes, programStages);
                            return Single.merge(
                                    programRuleCall.download(programUids),
                                    relationshipTypeCall.download(),
                                    optionSetCall.download(optionSetUids),
                                    optionCall.download(optionSetUids)
                            ).ignoreElements()
                                    .andThen(optionGroupCall.download(optionSetUids)).map(toIgnore -> programs);
                });
            });
        });
    }
}