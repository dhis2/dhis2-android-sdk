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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.LinkSyncHandler;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.dataelement.DataElement;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class ProgramStageSectionHandler extends IdentifiableSyncHandlerImpl<ProgramStageSection> {
    private final SyncHandler<ProgramIndicator> programIndicatorHandler;
    private final LinkSyncHandler<ProgramStageSectionProgramIndicatorLink>
            programStageSectionProgramIndicatorLinkHandler;
    private final LinkSyncHandler<ProgramStageSectionDataElementLink> programStageSectionDataElementLinkHandler;

    @Inject
    ProgramStageSectionHandler(IdentifiableObjectStore<ProgramStageSection> programStageSectionStore,
                               SyncHandler<ProgramIndicator> programIndicatorHandler,
                               LinkSyncHandler<ProgramStageSectionProgramIndicatorLink>
                                       programStageSectionProgramIndicatorLinkHandler,
                               LinkSyncHandler<ProgramStageSectionDataElementLink>
                                       programStageSectionDataElementLinkHandler) {
        super(programStageSectionStore);
        this.programIndicatorHandler = programIndicatorHandler;
        this.programStageSectionProgramIndicatorLinkHandler = programStageSectionProgramIndicatorLinkHandler;
        this.programStageSectionDataElementLinkHandler = programStageSectionDataElementLinkHandler;
    }

    @Override
    protected void afterObjectHandled(ProgramStageSection programStageSection, HandleAction action) {
        List<DataElement> dataElements = programStageSection.dataElements();
        if (dataElements != null) {
            List<ProgramStageSectionDataElementLink> programStageSectionDataElementLinks = new ArrayList<>();
            for (int i = 0; i < dataElements.size(); i++) {
                programStageSectionDataElementLinks.add(ProgramStageSectionDataElementLink.builder()
                .programStageSection(programStageSection.uid()).dataElement(dataElements.get(i).uid()).sortOrder(i + 1)
                        .build());
            }

            programStageSectionDataElementLinkHandler.handleMany(programStageSection.uid(),
                    programStageSectionDataElementLinks);
        }

        List<ProgramIndicator> programIndicators = programStageSection.programIndicators();
        if (programIndicators != null) {
            programIndicatorHandler.handleMany(programIndicators);

            List<ProgramStageSectionProgramIndicatorLink> programStageSectionProgramIndicatorLinks = new ArrayList<>();
            for (ProgramIndicator programIndicator : programIndicators) {
                programStageSectionProgramIndicatorLinks.add(ProgramStageSectionProgramIndicatorLink.builder()
                .programStageSection(programStageSection.uid()).programIndicator(programIndicator.uid()).build());
            }

            programStageSectionProgramIndicatorLinkHandler.handleMany(programStageSection.uid(),
                    programStageSectionProgramIndicatorLinks);
        }
    }
}