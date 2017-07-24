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

public class ProgramRuleVariableHandler {
    private final ProgramRuleVariableStore programRuleVariableStore;

    public ProgramRuleVariableHandler(ProgramRuleVariableStore programRuleVariableStore) {
        this.programRuleVariableStore = programRuleVariableStore;
    }

    public void handleProgramRuleVariables(List<ProgramRuleVariable> programRuleVariables) {
        if (programRuleVariables == null) {
            return;
        }

        deleteOrPersistProgramRuleVariables(programRuleVariables);
    }

    private void deleteOrPersistProgramRuleVariables(List<ProgramRuleVariable> programRuleVariables) {
        int size = programRuleVariables.size();

        for (int i = 0; i < size; i++) {
            ProgramRuleVariable programRuleVariable = programRuleVariables.get(i);

            if (isDeleted(programRuleVariable)) {
                programRuleVariableStore.delete(programRuleVariable.uid());
            } else {
                String programStageUid = null;
                if(programRuleVariable.programStage() != null) {
                    programStageUid = programRuleVariable.programStage().uid();
                }

                String dataElementUid = null;
                if(programRuleVariable.dataElement() != null) {
                    dataElementUid = programRuleVariable.dataElement().uid();
                }

                String trackedEntityAttributeUid = null;
                if(programRuleVariable.trackedEntityAttribute() != null) {
                    trackedEntityAttributeUid = programRuleVariable.trackedEntityAttribute().uid();
                }

                int updatedRow = programRuleVariableStore.update(
                        programRuleVariable.uid(), programRuleVariable.code(),
                        programRuleVariable.name(), programRuleVariable.displayName(),
                        programRuleVariable.created(), programRuleVariable.lastUpdated(),
                        programRuleVariable.useCodeForOptionSet(),
                        programRuleVariable.program().uid(), programStageUid,
                        dataElementUid,
                        trackedEntityAttributeUid,
                        programRuleVariable.programRuleVariableSourceType(), programRuleVariable.uid()
                );

                if (updatedRow <= 0) {
                    programRuleVariableStore.insert(
                            programRuleVariable.uid(), programRuleVariable.code(),
                            programRuleVariable.name(), programRuleVariable.displayName(),
                            programRuleVariable.created(), programRuleVariable.lastUpdated(),
                            programRuleVariable.useCodeForOptionSet(),
                            programRuleVariable.program().uid(), programStageUid,
                            dataElementUid,
                            trackedEntityAttributeUid,
                            programRuleVariable.programRuleVariableSourceType()
                    );
                }
            }

        }
    }
}
