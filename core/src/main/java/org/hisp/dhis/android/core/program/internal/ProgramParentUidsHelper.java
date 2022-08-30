/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramInternalAccessor;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageInternalAccessor;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttribute;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class ProgramParentUidsHelper {

    private ProgramParentUidsHelper() {}

    static Set<String> getAssignedOptionSetUids(List<TrackedEntityAttribute> attributes,
                                                List<ProgramStage> programStages) {
        Set<String> uids = new HashSet<>();

        if (attributes != null) {
            getOptionSetUidsForAttributes(uids, attributes);
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
                    ProgramStageInternalAccessor.accessProgramStageDataElements(programStage);
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

    private static void getOptionSetUidsForAttributes(Set<String> uids, List<TrackedEntityAttribute> attributes) {
        for (TrackedEntityAttribute attribute : attributes) {
            if (attribute.optionSet() != null) {
                uids.add(attribute.optionSet().uid());
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

    static Set<String> getAssignedTrackedEntityAttributeUids(List<Program> programs, List<TrackedEntityType> types) {
        Set<String> attributeUids = new HashSet<>();

        for (Program program : programs) {
            List<ProgramTrackedEntityAttribute> attributes =
                    ProgramInternalAccessor.accessProgramTrackedEntityAttributes(program);
            if (attributes != null) {
                for (ProgramTrackedEntityAttribute programAttribute : attributes) {
                    attributeUids.add(programAttribute.trackedEntityAttribute().uid());
                }
            }
        }

        for (TrackedEntityType type : types) {
            if (type.trackedEntityTypeAttributes() != null) {
                for (TrackedEntityTypeAttribute attribute : type.trackedEntityTypeAttributes()) {
                    attributeUids.add(attribute.trackedEntityAttribute().uid());
                }
            }
        }

        return attributeUids;
    }
}
