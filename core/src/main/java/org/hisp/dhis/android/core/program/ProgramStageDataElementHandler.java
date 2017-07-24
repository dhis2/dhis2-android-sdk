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

import org.hisp.dhis.android.core.dataelement.DataElementHandler;

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class ProgramStageDataElementHandler {
    private final ProgramStageDataElementStore programStageDataElementStore;
    private final DataElementHandler dataElementHandler;

    ProgramStageDataElementHandler(ProgramStageDataElementStore programStageDataElementStore,
                                   DataElementHandler dataElementHandler) {
        this.programStageDataElementStore = programStageDataElementStore;
        this.dataElementHandler = dataElementHandler;
    }

    void handleProgramStageDataElements(List<ProgramStageDataElement> programStageDataElements) {
        if (programStageDataElements == null) {
            return;
        }
        deleteOrPersistProgramStageDataElements(programStageDataElements);
    }

    /**
     * This method deletes or persists program stage data elements and applies the changes to database.
     * Method will call update with or without programStageSectionUid depending if it exists.
     *
     *
     * @param programStageDataElements
     */
    private void deleteOrPersistProgramStageDataElements(List<ProgramStageDataElement> programStageDataElements) {
        int size = programStageDataElements.size();
        for (int i = 0; i < size; i++) {
            ProgramStageDataElement programStageDataElement = programStageDataElements.get(i);

            if (isDeleted(programStageDataElement)) {
                programStageDataElementStore.delete(programStageDataElement.uid());
            } else {
                int updatedRow;

                    updatedRow = programStageDataElementStore.update(
                            programStageDataElement.uid(),
                            programStageDataElement.code(), programStageDataElement.name(),
                            programStageDataElement.displayName(), programStageDataElement.created(),
                            programStageDataElement.lastUpdated(), programStageDataElement.displayInReports(),
                            programStageDataElement.compulsory(), programStageDataElement.allowProvidedElsewhere(),
                            programStageDataElement.sortOrder(), programStageDataElement.allowFutureDate(),
                            programStageDataElement.dataElement().uid(), programStageDataElement.programStage().uid(),
                            programStageDataElement.uid());


                if (updatedRow <= 0) {
                    programStageDataElementStore.insert(
                            programStageDataElement.uid(), programStageDataElement.code(),
                            programStageDataElement.name(), programStageDataElement.displayName(),
                            programStageDataElement.created(), programStageDataElement.lastUpdated(),
                            programStageDataElement.displayInReports(), programStageDataElement.compulsory(),
                            programStageDataElement.allowProvidedElsewhere(), programStageDataElement.sortOrder(),
                            programStageDataElement.allowFutureDate(), programStageDataElement.dataElement().uid(),
                            programStageDataElement.programStage().uid(), null
                    );
                }
            }
            dataElementHandler.handleDataElement(programStageDataElement.dataElement());
        }
    }

    void updateProgramStageDataElementWithProgramStageSectionLink(@NonNull String programStageSectionUid,
                                                                  @NonNull String dataElementUid) {
        programStageDataElementStore.updateWithProgramStageSectionLink(programStageSectionUid, dataElementUid);
    }
}
