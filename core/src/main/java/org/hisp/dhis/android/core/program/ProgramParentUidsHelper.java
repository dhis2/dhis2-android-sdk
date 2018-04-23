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

import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.ObjectWithUid;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ProgramParentUidsHelper {

    private ProgramParentUidsHelper() {}

    static Set<String> getAssignedOptionSetUids(List<Program> programs, List<ProgramStage> programStages) {
        Set<String> uids = new HashSet<>();

        if (programs != null) {
            for (Program program : programs) {
                getOptionSetUidsForAttributes(uids, program);
            }
        }

        if (programStages != null) {
            getOptionSetUidsForDataElements(uids, programStages);
        }
        return uids;
    }

    private static void getOptionSetUidsForDataElements(Set<String> uids, List<ProgramStage> programStages) {
        int programStagesSize = programStages.size();

        for (int j = 0; j < programStagesSize; j++) {
            ProgramStage programStage = programStages.get(j);
            List<ProgramStageDataElement> programStageDataElements =
                    programStage.programStageDataElements();
            int programStageDataElementSize = programStageDataElements.size();

            for (int k = 0; k < programStageDataElementSize; k++) {
                ProgramStageDataElement programStageDataElement = programStageDataElements.get(k);

                if (programStageDataElement.dataElement() != null &&
                        programStageDataElement.dataElement().optionSet() != null) {
                    uids.add(programStageDataElement.dataElement().optionSet().uid());
                }
            }
        }
    }

    private static void getOptionSetUidsForAttributes(Set<String> uids, Program program) {
        int programTrackedEntityAttributeSize = program.programTrackedEntityAttributes().size();
        List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes =
                program.programTrackedEntityAttributes();

        for (int j = 0; j < programTrackedEntityAttributeSize; j++) {
            ProgramTrackedEntityAttribute programTrackedEntityAttribute =
                    programTrackedEntityAttributes.get(j);

            if (programTrackedEntityAttribute.trackedEntityAttribute() != null &&
                    programTrackedEntityAttribute.trackedEntityAttribute().optionSet() != null) {
                uids.add(programTrackedEntityAttribute.trackedEntityAttribute().optionSet().uid());
            }
        }
    }

    static Set<String> getAssignedTrackedEntityUids(List<Program> programs) {
        if (programs == null) {
            return null;
        }

        Set<String> uids = new HashSet<>();

        int size = programs.size();
        for (int i = 0; i < size; i++) {
            Program program = programs.get(i);

            if (program.trackedEntityType() != null) {
                uids.add(program.trackedEntityType().uid());
            }
        }
        return uids;
    }

    static Set<String> getAssignedProgramStageUids(List<Program> programs) {
        Set<String> programStageUids = new HashSet<>();

        for (Program program : programs) {
            for (ObjectWithUid programStage : program.programStages()) {
                programStageUids.add(programStage.uid());
            }
        }

        return programStageUids;
    }

    public static Set<String> getProgramUidsWithDataReadAccess(List<Program> programsWithAccess) {
        Set<String> programUids = new HashSet<>();
        for (Program program: programsWithAccess) {
            Access access = program.access();
            if (access != null && access.data().read()) {
                programUids.add(program.uid());
            }
        }

        return programUids;
    }
}
