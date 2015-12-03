/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.program;

import org.hisp.dhis.android.sdk.common.base.AbsMapper;
import org.hisp.dhis.android.sdk.flow.ProgramTrackedEntityAttribute$Flow;
import org.hisp.dhis.java.sdk.models.program.ProgramTrackedEntityAttribute;

public class ProgramTrackedEntityAttributeMapper extends AbsMapper<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttribute$Flow> {

    public ProgramTrackedEntityAttributeMapper() {
        // empty constructor
    }

    @Override
    public ProgramTrackedEntityAttribute$Flow mapToDatabaseEntity(ProgramTrackedEntityAttribute programTrackedEntityAttribute) {
        if (programTrackedEntityAttribute == null) {
            return null;
        }

        ProgramTrackedEntityAttribute$Flow programTrackedEntityAttributeFlow = new ProgramTrackedEntityAttribute$Flow();
        programTrackedEntityAttributeFlow.setTrackedEntityAttribute(programTrackedEntityAttribute.getTrackedEntityAttribute());
        programTrackedEntityAttributeFlow.setProgram(programTrackedEntityAttribute.getProgram());
        programTrackedEntityAttributeFlow.setSortOrder(programTrackedEntityAttribute.getSortOrder());
        programTrackedEntityAttributeFlow.setAllowFutureDate(programTrackedEntityAttribute.isAllowFutureDate());
        programTrackedEntityAttributeFlow.setDisplayInList(programTrackedEntityAttribute.isDisplayInList());
        programTrackedEntityAttributeFlow.setMandatory(programTrackedEntityAttribute.isMandatory());
        return programTrackedEntityAttributeFlow;
    }

    @Override
    public ProgramTrackedEntityAttribute mapToModel(ProgramTrackedEntityAttribute$Flow programTrackedEntityAttributeFlow) {
        if (programTrackedEntityAttributeFlow == null) {
            return null;
        }

        ProgramTrackedEntityAttribute programTrackedEntityAttribute = new ProgramTrackedEntityAttribute();
        programTrackedEntityAttribute.setTrackedEntityAttribute(programTrackedEntityAttributeFlow.getTrackedEntityAttribute());
        programTrackedEntityAttribute.setProgram(programTrackedEntityAttributeFlow.getProgram());
        programTrackedEntityAttribute.setSortOrder(programTrackedEntityAttributeFlow.getSortOrder());
        programTrackedEntityAttribute.setAllowFutureDate(programTrackedEntityAttributeFlow.isAllowFutureDate());
        programTrackedEntityAttribute.setDisplayInList(programTrackedEntityAttributeFlow.isDisplayInList());
        programTrackedEntityAttribute.setMandatory(programTrackedEntityAttributeFlow.isMandatory());
        return programTrackedEntityAttribute;
    }

    @Override
    public Class<ProgramTrackedEntityAttribute> getModelTypeClass() {
        return ProgramTrackedEntityAttribute.class;
    }

    @Override
    public Class<ProgramTrackedEntityAttribute$Flow> getDatabaseEntityTypeClass() {
        return ProgramTrackedEntityAttribute$Flow.class;
    }
}
