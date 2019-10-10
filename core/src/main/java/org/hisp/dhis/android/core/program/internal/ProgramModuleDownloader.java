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

import org.hisp.dhis.android.core.arch.call.factories.internal.ListCallFactory;
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCallFactory;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.arch.modules.internal.MetadataModuleDownloader;
import org.hisp.dhis.android.core.option.OptionGroup;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public class ProgramModuleDownloader implements MetadataModuleDownloader<List<Program>> {

    private final ListCallFactory<Program> programCallFactory;
    private final UidsCallFactory<ProgramStage> programStageCallFactory;
    private final UidsCallFactory<ProgramRule> programRuleCallFactory;
    private final UidsCallFactory<TrackedEntityType> trackedEntityTypeCallFactory;
    private final UidsCallFactory<TrackedEntityAttribute> trackedEntityAttributeCallFactory;
    private final ListCallFactory<RelationshipType> relationshipTypeCallFactory;
    private final UidsCallFactory<OptionSet> optionSetCallFactory;
    private final UidsCallFactory<OptionGroup> optionGroupCallFactory;
    private final DHISVersionManager versionManager;

    @Inject
    ProgramModuleDownloader(ListCallFactory<Program> programCallFactory,
                            UidsCallFactory<ProgramStage> programStageCallFactory,
                            UidsCallFactory<ProgramRule> programRuleCallFactory,
                            UidsCallFactory<TrackedEntityType> trackedEntityTypeCallFactory,
                            UidsCallFactory<TrackedEntityAttribute> trackedEntityAttributeCallFactory,
                            ListCallFactory<RelationshipType> relationshipTypeCallFactory,
                            UidsCallFactory<OptionSet> optionSetCallFactory,
                            UidsCallFactory<OptionGroup> optionGroupCallFactory,
                            DHISVersionManager versionManager) {
        this.programCallFactory = programCallFactory;
        this.programStageCallFactory = programStageCallFactory;
        this.programRuleCallFactory = programRuleCallFactory;
        this.trackedEntityTypeCallFactory = trackedEntityTypeCallFactory;
        this.trackedEntityAttributeCallFactory = trackedEntityAttributeCallFactory;
        this.relationshipTypeCallFactory = relationshipTypeCallFactory;
        this.optionSetCallFactory = optionSetCallFactory;
        this.optionGroupCallFactory = optionGroupCallFactory;
        this.versionManager = versionManager;
    }

    @Override
    public Callable<List<Program>> downloadMetadata() {
        return () -> {
            List<Program> programs = programCallFactory.create().call();

            Set<String> programUids = UidsHelper.getUids(programs);
            List<ProgramStage> programStages = programStageCallFactory.create(programUids).call();

            programRuleCallFactory.create(programUids).call();

            Set<String> trackedEntityUids = ProgramParentUidsHelper.getAssignedTrackedEntityUids(programs);

            List<TrackedEntityType> trackedEntityTypes = trackedEntityTypeCallFactory.create(trackedEntityUids).call();

            Set<String> attributeUids = ProgramParentUidsHelper.getAssignedTrackedEntityAttributeUids(programs,
                    trackedEntityTypes);

            List<TrackedEntityAttribute> attributes = trackedEntityAttributeCallFactory.create(attributeUids).call();

            relationshipTypeCallFactory.create().call();

            Set<String> optionSetUids = ProgramParentUidsHelper.getAssignedOptionSetUids(attributes, programStages);
            optionSetCallFactory.create(optionSetUids).call();

            if (!versionManager.is2_29()) {
                optionGroupCallFactory.create(optionSetUids).call();
            }

            return programs;
        };
    }
}