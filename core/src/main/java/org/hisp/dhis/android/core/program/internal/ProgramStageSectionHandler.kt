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
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.ProgramStageSection
import org.hisp.dhis.android.core.program.ProgramStageSectionDataElementLink
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLink
import org.koin.core.annotation.Singleton

@Singleton
internal class ProgramStageSectionHandler(
    programStageSectionStore: ProgramStageSectionStore,
    private val programStageSectionProgramIndicatorLinkHandler: ProgramStageSectionProgramIndicatorLinkHandler,
    private val programStageSectionDataElementLinkHandler: ProgramStageSectionDataElementLinkHandler,
) : IdentifiableHandlerImpl<ProgramStageSection>(programStageSectionStore) {

    override fun afterObjectHandled(o: ProgramStageSection, action: HandleAction) {
        programStageSectionDataElementLinkHandler.handleMany(
            o.uid(),
            o.dataElements(),
        ) { dataElement: DataElement, sortOrder: Int ->
            ProgramStageSectionDataElementLink.builder()
                .programStageSection(o.uid())
                .dataElement(dataElement.uid())
                .sortOrder(sortOrder)
                .build()
        }

        programStageSectionProgramIndicatorLinkHandler.handleMany(
            o.uid(),
            o.programIndicators(),
        ) { programIndicator: ProgramIndicator ->
            ProgramStageSectionProgramIndicatorLink.builder()
                .programStageSection(o.uid())
                .programIndicator(programIndicator.uid())
                .build()
        }
    }
}
