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
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.attribute.Attribute
import org.hisp.dhis.android.core.attribute.AttributeValueUtils
import org.hisp.dhis.android.core.attribute.ProgramStageAttributeValueLink
import org.hisp.dhis.android.core.attribute.internal.AttributeHandler
import org.hisp.dhis.android.core.attribute.internal.ProgramStageAttributeValueLinkHandler
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.program.ProgramStageInternalAccessor
import org.hisp.dhis.android.core.program.ProgramStageSection
import javax.inject.Inject

@Reusable
internal class ProgramStageHandler @Inject constructor(
    programStageStore: ProgramStageStore,
    private val programStageSectionHandler: ProgramStageSectionHandler,
    private val programStageDataElementHandler: ProgramStageDataElementHandler,
    private val programStageDataElementCleaner: ProgramStageDataElementOrphanCleaner,
    private val programStageSectionCleaner: ProgramStageSectionOrphanCleaner,
    private val programStageCleaner: ProgramStageSubCollectionCleaner,
    private val attributeHandler: AttributeHandler,
    private val programStageAttributeValueLinkHandler: ProgramStageAttributeValueLinkHandler
) : IdentifiableHandlerImpl<ProgramStage>(programStageStore) {

    override fun afterObjectHandled(o: ProgramStage, action: HandleAction) {
        programStageDataElementHandler.handleMany(
            ProgramStageInternalAccessor.accessProgramStageDataElements(o)
        )
        programStageSectionHandler.handleMany(
            ProgramStageInternalAccessor.accessProgramStageSections(o)
        ) { programStageSection: ProgramStageSection ->
            programStageSection.toBuilder()
                .programStage(ObjectWithUid.create(o.uid()))
                .build()
        }

        if (action === HandleAction.Update) {
            programStageDataElementCleaner.deleteOrphan(
                o,
                ProgramStageInternalAccessor.accessProgramStageDataElements(o)
            )
            programStageSectionCleaner.deleteOrphan(
                o,
                ProgramStageInternalAccessor.accessProgramStageSections(o)
            )
        }

        if (o.attributeValues() != null) {
            val attributes = AttributeValueUtils.extractAttributes(o.attributeValues())
            attributeHandler.handleMany(attributes)
            programStageAttributeValueLinkHandler.handleMany(
                o.uid(), attributes
            ) { attribute: Attribute ->
                ProgramStageAttributeValueLink.builder()
                    .programStage(o.uid())
                    .attribute(attribute.uid())
                    .value(AttributeValueUtils.extractValue(o.attributeValues(), attribute.uid()))
                    .build()
            }
        }
    }

    override fun afterCollectionHandled(oCollection: Collection<ProgramStage>?) {
        programStageCleaner.deleteNotPresent(oCollection)
    }
}
