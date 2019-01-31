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
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.LinkModelHandlerImpl;
import org.hisp.dhis.android.core.common.OrderedLinkModelHandler;
import org.hisp.dhis.android.core.common.OrderedLinkModelHandlerImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.dataelement.DataElement;

import java.util.List;

public class ProgramStageSectionHandler extends IdentifiableSyncHandlerImpl<ProgramStageSection> {
    private final SyncHandler<ProgramIndicator> programIndicatorHandler;
    private final LinkModelHandler<ProgramIndicator, ProgramStageSectionProgramIndicatorLinkModel>
            programStageSectionProgramIndicatorLinkHandler;
    private final OrderedLinkModelHandler<DataElement, ProgramStageSectionDataElementLinkModel>
            programStageSectionDataElementLinkHandler;

    ProgramStageSectionHandler(IdentifiableObjectStore<ProgramStageSection> programStageSectionStore,
                               SyncHandler<ProgramIndicator> programIndicatorHandler,
                               LinkModelHandler<ProgramIndicator, ProgramStageSectionProgramIndicatorLinkModel>
                                       programStageSectionProgramIndicatorLinkHandler,
                               OrderedLinkModelHandler<DataElement, ProgramStageSectionDataElementLinkModel>
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
            ProgramStageSectionDataElementLinkModelBuilder programStageSectionDataElementLinkModelBuilder =
                    new ProgramStageSectionDataElementLinkModelBuilder(programStageSection);

            programStageSectionDataElementLinkHandler.handleMany(programStageSection.uid(),
                    dataElements, programStageSectionDataElementLinkModelBuilder);
        }

        ProgramStageSectionProgramIndicatorLinkModelBuilder programStageSectionProgramIndicatorLinkModelBuilder =
                new ProgramStageSectionProgramIndicatorLinkModelBuilder(programStageSection);

        programIndicatorHandler.handleMany(programStageSection.programIndicators());
        programStageSectionProgramIndicatorLinkHandler.handleMany(programStageSection.uid(),
                programStageSection.programIndicators(), programStageSectionProgramIndicatorLinkModelBuilder);
    }

    public static ProgramStageSectionHandler create(DatabaseAdapter databaseAdapter) {
        return new ProgramStageSectionHandler(
                ProgramStageSectionStore.create(databaseAdapter),
                ProgramIndicatorHandler.create(databaseAdapter),
                new LinkModelHandlerImpl<ProgramIndicator, ProgramStageSectionProgramIndicatorLinkModel>(
                        ProgramStageSectionProgramIndicatorLinkStore.create(databaseAdapter)),
                new OrderedLinkModelHandlerImpl<DataElement, ProgramStageSectionDataElementLinkModel>(
                        ProgramStageSectionDataElementLinkStore.create(databaseAdapter))
        );
    }
}