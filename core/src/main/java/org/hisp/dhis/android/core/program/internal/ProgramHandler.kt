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

import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.attribute.AttributeValueUtils
import org.hisp.dhis.android.core.attribute.ProgramAttributeValueLink
import org.hisp.dhis.android.core.attribute.internal.ProgramAttributeValueLinkHandler
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramInternalAccessor
import org.hisp.dhis.android.core.program.ProgramType
import org.koin.core.annotation.Singleton

@Singleton
internal class ProgramHandler(
    programStore: ProgramStore,
    private val programRuleVariableHandler: ProgramRuleVariableHandler,
    private val programTrackedEntityAttributeHandler: ProgramTrackedEntityAttributeHandler,
    private val programSectionHandler: ProgramSectionHandler,
    private val orphanCleaner: ProgramOrphanCleaner,
    private val collectionCleaner: ProgramCollectionCleaner,
    private val linkCleaner: ProgramOrganisationUnitLinkCleaner,
    private val programAttributeLinkHandler: ProgramAttributeValueLinkHandler,
) : IdentifiableHandlerImpl<Program>(programStore) {

    override suspend fun afterObjectHandled(o: Program, action: HandleAction) {
        programTrackedEntityAttributeHandler.handleMany(
            ProgramInternalAccessor
                .accessProgramTrackedEntityAttributes(o),
        )
        programRuleVariableHandler.handleMany(ProgramInternalAccessor.accessProgramRuleVariables(o))
        programSectionHandler.handleMany(ProgramInternalAccessor.accessProgramSections(o))

        if (action === HandleAction.Update) {
            orphanCleaner.deleteOrphan(o)
        }
        if (o.attributeValues() != null) {
            val attributes = AttributeValueUtils.extractAttributes(o.attributeValues())
            programAttributeLinkHandler.handleMany(
                o.uid(),
                attributes,
            ) { attribute: ObjectWithUid ->
                ProgramAttributeValueLink.builder()
                    .program(o.uid())
                    .attribute(attribute.uid())
                    .value(AttributeValueUtils.extractValue(o.attributeValues(), attribute.uid()))
                    .build()
            }
        }
    }

    override suspend fun beforeCollectionHandled(oCollection: Collection<Program>): Collection<Program> {
        val filteredPrograms: MutableList<Program> = ArrayList(oCollection.size)
        for (p in oCollection) {
            if (!(p.programType() == ProgramType.WITH_REGISTRATION && p.trackedEntityType() == null)) {
                filteredPrograms.add(p)
            }
        }
        return filteredPrograms
    }

    override suspend fun afterCollectionHandled(oCollection: Collection<Program>?) {
        collectionCleaner.deleteNotPresent(oCollection)
        linkCleaner.deleteNotPresent(oCollection)
    }
}
