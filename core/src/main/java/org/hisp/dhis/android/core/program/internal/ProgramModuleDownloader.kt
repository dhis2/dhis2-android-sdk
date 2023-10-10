/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.program.internal

import dagger.Reusable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.arch.modules.internal.UntypedModuleDownloaderCoroutines
import org.hisp.dhis.android.core.event.internal.EventFilterCall
import org.hisp.dhis.android.core.option.internal.OptionCall
import org.hisp.dhis.android.core.option.internal.OptionGroupCall
import org.hisp.dhis.android.core.option.internal.OptionSetCall
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitProgramLinkStore
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageWorkingListCall
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeCall
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeCall
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFilterCall
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeCall
import javax.inject.Inject

@Reusable
@Suppress("LongParameterList")
internal class ProgramModuleDownloader @Inject constructor(
    private val programCall: ProgramCall,
    private val programStageCall: ProgramStageCall,
    private val programRuleCall: ProgramRuleCall,
    private val trackedEntityTypeCall: TrackedEntityTypeCall,
    private val trackedEntityAttributeCall: TrackedEntityAttributeCall,
    private val trackedEntityInstanceFilterCall: TrackedEntityInstanceFilterCall,
    private val eventFilterCall: EventFilterCall,
    private val programStageWorkingListCall: ProgramStageWorkingListCall,
    private val relationshipTypeCall: RelationshipTypeCall,
    private val optionSetCall: OptionSetCall,
    private val optionCall: OptionCall,
    private val optionGroupCall: OptionGroupCall,
    private val programOrganisationUnitLinkStore: OrganisationUnitProgramLinkStore,
) : UntypedModuleDownloaderCoroutines {

    override suspend fun downloadMetadata() {

        val orgUnitProgramUids = programOrganisationUnitLinkStore
            .selectDistinctSlaves(OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM)

        val programs = programCall.download(orgUnitProgramUids)

        val programUids = getUids(programs)
        val programStages = programStageCall.download(programUids)
        val trackedEntityUids = ProgramParentUidsHelper.getAssignedTrackedEntityUids(programs)
        val trackedEntityTypes = trackedEntityTypeCall.download(trackedEntityUids)

        val attributes = trackedEntityAttributeCall.download(
            ProgramParentUidsHelper
                .getAssignedTrackedEntityAttributeUids(programs, trackedEntityTypes),
        )


        val optionSetUids = ProgramParentUidsHelper.getAssignedOptionSetUids(
            attributes,
            programStages,
        )
        flowOf(
            programRuleCall.download(programUids),
            relationshipTypeCall.download(),
            optionSetCall.download(optionSetUids),
            optionCall.download(optionSetUids),
            optionGroupCall.download(optionSetUids),

            ).collect()

        downloadFiltersAndWorkingLists(programUids)

    }

    private suspend fun downloadFiltersAndWorkingLists(programUids: Set<String>) {
        flowOf(
            trackedEntityInstanceFilterCall.download(programUids),
            eventFilterCall.download(programUids),
            programStageWorkingListCall.download(programUids),
        ).collect()
    }
}
