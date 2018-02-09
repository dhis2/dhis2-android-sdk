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

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import java.util.List;

public class ProgramRuleActionHandler {
    private final ProgramRuleActionStore programRuleActionStore;

    public ProgramRuleActionHandler(ProgramRuleActionStore programRuleActionStore) {
        this.programRuleActionStore = programRuleActionStore;
    }

    public void handleProgramRuleActions(List<ProgramRuleAction> programRuleActions) {
        if (programRuleActions == null) {
            return;
        }

        deleteOrPersistProgramRuleActions(programRuleActions);
    }

    /**
     * Deletes or persists program rule actions and applies the changes to the database.
     */
    private void deleteOrPersistProgramRuleActions(List<ProgramRuleAction> programRuleActions) {
        int size = programRuleActions.size();

        for (int i = 0; i < size; i++) {
            ProgramRuleAction programRuleAction = programRuleActions.get(i);

            handle(programRuleAction);
        }
    }

    public void handle(ProgramRuleAction programRuleAction) {
        if (isDeleted(programRuleAction)) {
            programRuleActionStore.delete(programRuleAction.uid());
        } else {
            String trackedEntityAttributeUid = null;
            if (programRuleAction.trackedEntityAttribute() != null) {
                trackedEntityAttributeUid = programRuleAction.trackedEntityAttribute().uid();
            }

            String dataElementUid = null;
            if (programRuleAction.dataElement() != null) {
                dataElementUid = programRuleAction.dataElement().uid();
            }

            String programIndicatorUid = null;
            if (programRuleAction.programIndicator() != null) {
                programIndicatorUid = programRuleAction.programIndicator().uid();
            }

            String programStageSectionUid = null;
            if (programRuleAction.programStageSection() != null) {
                programStageSectionUid = programRuleAction.programStageSection().uid();
            }

            String programStageUid = null;
            if (programRuleAction.programStage() != null) {
                programStageUid = programRuleAction.programStage().uid();
            }

            int updatedRow = programRuleActionStore.update(
                    programRuleAction.uid(),
                    programRuleAction.code(),
                    programRuleAction.name(),
                    programRuleAction.displayName(),
                    programRuleAction.created(),
                    programRuleAction.lastUpdated(),
                    programRuleAction.data(),
                    programRuleAction.content(),
                    programRuleAction.location(),
                    trackedEntityAttributeUid,
                    programIndicatorUid,
                    programStageSectionUid,
                    programRuleAction.programRuleActionType(),
                    programStageUid,
                    dataElementUid,
                    programRuleAction.programRule().uid(),
                    programRuleAction.uid()
            );

            if (updatedRow <= 0) {
                programRuleActionStore.insert(
                        programRuleAction.uid(),
                        programRuleAction.code(),
                        programRuleAction.name(),
                        programRuleAction.displayName(),
                        programRuleAction.created(),
                        programRuleAction.lastUpdated(),
                        programRuleAction.data(),
                        programRuleAction.content(),
                        programRuleAction.location(),
                        trackedEntityAttributeUid,
                        programIndicatorUid,
                        programStageSectionUid,
                        programRuleAction.programRuleActionType(),
                        programStageUid,
                        dataElementUid,
                        programRuleAction.programRule().uid()
                );

            }
        }
    }
}
