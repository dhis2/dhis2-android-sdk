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

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.dataelement.DataElement;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class ProgramStageSectionHandler {
    private final ProgramStageSectionStore programStageSectionStore;
    private final ProgramStageDataElementHandler programStageDataElementHandler;
    private final ProgramIndicatorHandler programIndicatorHandler;

    public ProgramStageSectionHandler(@NonNull ProgramStageSectionStore programStageSectionStore,
                                      @NonNull ProgramStageDataElementHandler programStageDataElementHandler,
                                      @NonNull ProgramIndicatorHandler programIndicatorHandler) {
        this.programStageSectionStore = programStageSectionStore;
        this.programStageDataElementHandler = programStageDataElementHandler;
        this.programIndicatorHandler = programIndicatorHandler;
    }

    public void handleProgramStageSection(String programStageUid, List<ProgramStageSection> programStageSections) {
        if (programStageUid == null || programStageSections == null) {
            return;
        }
        for (int i = 0, size = programStageSections.size(); i < size; i++) {
            ProgramStageSection programStageSection = programStageSections.get(i);

            if (isDeleted(programStageSection)) {
                programStageSectionStore.delete(programStageSection.uid());
            } else {
                int updatedRow = programStageSectionStore.update(
                        programStageSection.uid(), programStageSection.code(),
                        programStageSection.name(), programStageSection.displayName(), programStageSection.created(),
                        programStageSection.lastUpdated(), programStageSection.sortOrder(),
                        programStageUid, programStageSection.uid()
                );

                if (updatedRow <= 0) {
                    programStageSectionStore.insert(
                            programStageSection.uid(), programStageSection.code(),
                            programStageSection.name(), programStageSection.displayName(),
                            programStageSection.created(), programStageSection.lastUpdated(),
                            programStageSection.sortOrder(), programStageUid
                    );
                }
            }


            //Loop over the list and add all entries
            String programStageSectionUid = programStageSection.uid();
            List<DataElement> dataElements = programStageSection.dataElements();
            int dataElementSize = dataElements.size();

            for (int j = 0; j < dataElementSize; j++) {
                String dataElementUid = dataElements.get(j).uid();

                programStageDataElementHandler.updateProgramStageDataElementWithProgramStageSectionLink(
                        programStageSectionUid, dataElementUid
                );

            }
            programIndicatorHandler.handleProgramIndicator(programStageSection.uid(),
                    programStageSection.programIndicators());
        }
    }
}
