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
package org.hisp.dhis.android.core.program.internal

import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.call.factories.internal.ListCall
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUids
import org.hisp.dhis.android.core.arch.modules.internal.UntypedModuleDownloader
import org.hisp.dhis.android.core.event.EventFilter
import org.hisp.dhis.android.core.option.Option
import org.hisp.dhis.android.core.option.OptionGroup
import org.hisp.dhis.android.core.option.OptionSet
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLinkTableInfo
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramRule
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType

@Reusable
@Suppress("LongParameterList")
internal class ProgramModuleDownloader @Inject constructor(
    private val programCall: UidsCall<Program>,
    private val programStageCall: UidsCall<ProgramStage>,
    private val programRuleCall: UidsCall<ProgramRule>,
    private val trackedEntityTypeCall: UidsCall<TrackedEntityType>,
    private val trackedEntityAttributeCall: UidsCall<TrackedEntityAttribute>,
    private val trackedEntityInstanceFilterCall: UidsCall<TrackedEntityInstanceFilter>,
    private val eventFilterCall: UidsCall<EventFilter>,
    private val relationshipTypeCall: ListCall<RelationshipType>,
    private val optionSetCall: UidsCall<OptionSet>,
    private val optionCall: UidsCall<Option>,
    private val optionGroupCall: UidsCall<OptionGroup>,
    private val programOrganisationUnitLinkStore: LinkStore<OrganisationUnitProgramLink>
) : UntypedModuleDownloader {

    override fun downloadMetadata(): Completable {
        return Completable.defer {
            val orgUnitProgramUids = programOrganisationUnitLinkStore
                .selectDistinctSlaves(OrganisationUnitProgramLinkTableInfo.Columns.PROGRAM)

            programCall.download(orgUnitProgramUids)
                .flatMapCompletable { programs ->
                    val programUids = getUids(programs)
                    programStageCall.download(programUids).flatMapCompletable { programStages ->
                        val trackedEntityUids = ProgramParentUidsHelper.getAssignedTrackedEntityUids(programs)
                        trackedEntityTypeCall.download(trackedEntityUids)
                            .flatMap { trackedEntityTypes ->
                                trackedEntityAttributeCall.download(
                                    ProgramParentUidsHelper
                                        .getAssignedTrackedEntityAttributeUids(programs, trackedEntityTypes)
                                )
                            }
                            .flatMapCompletable { attributes ->
                                val optionSetUids = ProgramParentUidsHelper.getAssignedOptionSetUids(
                                    attributes, programStages
                                )
                                Single.merge(
                                    listOf(
                                        programRuleCall.download(programUids),
                                        trackedEntityInstanceFilterCall.download(programUids),
                                        eventFilterCall.download(programUids),
                                        relationshipTypeCall.download(),
                                        optionSetCall.download(optionSetUids),
                                        optionCall.download(optionSetUids),
                                        optionGroupCall.download(optionSetUids)
                                    )
                                ).ignoreElements()
                            }
                    }
                }
        }
    }
}
