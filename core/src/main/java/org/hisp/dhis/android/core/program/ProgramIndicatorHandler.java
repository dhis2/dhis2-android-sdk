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

import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

public class ProgramIndicatorHandler {
    private final ProgramIndicatorStore programIndicatorStore;
    private final ProgramStageSectionProgramIndicatorLinkStore programStageSectionProgramIndicatorLinkStore;

    public ProgramIndicatorHandler(ProgramIndicatorStore programIndicatorStore,
                                   ProgramStageSectionProgramIndicatorLinkStore
                                           programStageSectionProgramIndicatorLinkStore) {
        this.programIndicatorStore = programIndicatorStore;
        this.programStageSectionProgramIndicatorLinkStore = programStageSectionProgramIndicatorLinkStore;
    }

    public void handleProgramIndicator(String programStageSectionUid, List<ProgramIndicator> programIndicators) {
        if (programIndicators == null) {
            return;
        }
        deleteOrPersistProgramIndicators(programStageSectionUid, programIndicators);
    }

    /**
     * This method deletes or persists program indicators and the link table between program stage sections
     * and program indicators
     *
     * @param programStageSectionUid
     * @param programIndicators
     */
    //TODO: Review if this is necessary with saving through sections and then saving through programs
    //TODO: Morten should have implemented the link between program indicator and program stage section in March.
    private void deleteOrPersistProgramIndicators(String programStageSectionUid,
                                                  List<ProgramIndicator> programIndicators) {
        int size = programIndicators.size();

        for (int i = 0; i < size; i++) {
            ProgramIndicator programIndicator = programIndicators.get(i);

            if (isDeleted(programIndicator)) {
                programIndicatorStore.delete(programIndicator.uid());
            } else {
                int updatedRow = programIndicatorStore.update(
                        programIndicator.uid(), programIndicator.code(),
                        programIndicator.name(), programIndicator.displayName(), programIndicator.created(),
                        programIndicator.lastUpdated(), programIndicator.shortName(),
                        programIndicator.displayShortName(), programIndicator.description(),
                        programIndicator.displayDescription(), programIndicator.displayInForm(),
                        programIndicator.expression(), programIndicator.dimensionItem(), programIndicator.filter(),
                        programIndicator.decimals(), programIndicator.program().uid(), programIndicator.uid()
                );

                if (updatedRow <= 0) {
                    programIndicatorStore.insert(
                            programIndicator.uid(), programIndicator.code(),
                            programIndicator.name(), programIndicator.displayName(),
                            programIndicator.created(), programIndicator.lastUpdated(),
                            programIndicator.shortName(), programIndicator.displayShortName(),
                            programIndicator.description(),
                            programIndicator.displayDescription(), programIndicator.displayInForm(),
                            programIndicator.expression(), programIndicator.dimensionItem(),
                            programIndicator.filter(),
                            programIndicator.decimals(), programIndicator.program().uid()
                    );
                }
            }

            if (programStageSectionUid != null) {
                // since this is many-to-many relationship we need to update link table

                int updatedLink = programStageSectionProgramIndicatorLinkStore.update(
                        programStageSectionUid, programIndicator.uid(),
                        programStageSectionUid, programIndicator.uid()
                );

                if (updatedLink <= 0) {
                    programStageSectionProgramIndicatorLinkStore.insert(
                            programStageSectionUid, programIndicator.uid()
                    );
                }
            }
        }
    }
}
