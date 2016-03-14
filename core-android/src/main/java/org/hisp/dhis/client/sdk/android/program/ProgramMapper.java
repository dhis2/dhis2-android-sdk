/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.program;

import org.hisp.dhis.client.sdk.android.api.utils.MapperModuleProvider;
import org.hisp.dhis.client.sdk.android.common.base.AbsMapper;
import org.hisp.dhis.client.sdk.android.flow.ProgramFlow;
import org.hisp.dhis.client.sdk.models.program.Program;

public class ProgramMapper extends AbsMapper<Program, ProgramFlow> {

    @Override
    public ProgramFlow mapToDatabaseEntity(Program program) {
        if (program == null) {
            return null;
        }

        ProgramFlow programFlow = new ProgramFlow();
        programFlow.setId(program.getId());
        programFlow.setUId(program.getUId());
        programFlow.setCreated(program.getCreated());
        programFlow.setLastUpdated(program.getLastUpdated());
        programFlow.setName(program.getName());
        programFlow.setDisplayName(program.getDisplayName());
        programFlow.setAccess(program.getAccess());
        programFlow.setTrackedEntity(MapperModuleProvider.getInstance()
                .getTrackedEntityMapper().mapToDatabaseEntity(program.getTrackedEntity()));
        programFlow.setProgramType(program.getProgramType());
        programFlow.setVersion(program.getVersion());
        programFlow.setEnrollmentDateLabel(program.getEnrollmentDateLabel());
        programFlow.setDescription(program.getDescription());
        programFlow.setOnlyEnrollOnce(program.isOnlyEnrollOnce());
        programFlow.setExternalAccess(program.isExternalAccess());
        programFlow.setDisplayIncidentDate(program.isDisplayIncidentDate());
        programFlow.setIncidentDateLabel(program.getIncidentDateLabel());
        programFlow.setRegistration(program.isRegistration());
        programFlow.setSelectEnrollmentDatesInFuture(program.isSelectEnrollmentDatesInFuture());
        programFlow.setDataEntryMethod(program.isDataEntryMethod());
        programFlow.setSingleEvent(program.isSingleEvent());
        programFlow.setIgnoreOverdueEvents(program.isIgnoreOverdueEvents());
        programFlow.setRelationshipFromA(program.isRelationshipFromA());
        programFlow.setSelectIncidentDatesInFuture(program.isSelectIncidentDatesInFuture());
        programFlow.setIsAssignedToUser(program.isAssignedToUser());
        return programFlow;
    }

    @Override
    public Program mapToModel(ProgramFlow programFlow) {
        if (programFlow == null) {
            return null;
        }

        Program program = new Program();
        program.setId(programFlow.getId());
        program.setUId(programFlow.getUId());
        program.setCreated(programFlow.getCreated());
        program.setLastUpdated(programFlow.getLastUpdated());
        program.setName(programFlow.getName());
        program.setDisplayName(programFlow.getDisplayName());
        program.setAccess(programFlow.getAccess());
        program.setTrackedEntity(MapperModuleProvider.getInstance()
                .getTrackedEntityMapper().mapToModel(programFlow.getTrackedEntity()));
        program.setProgramType(programFlow.getProgramType());
        program.setVersion(programFlow.getVersion());
        program.setEnrollmentDateLabel(programFlow.getEnrollmentDateLabel());
        program.setDescription(programFlow.getDescription());
        program.setOnlyEnrollOnce(programFlow.isOnlyEnrollOnce());
        program.setExternalAccess(programFlow.isExternalAccess());
        program.setDisplayIncidentDate(programFlow.isDisplayIncidentDate());
        program.setIncidentDateLabel(programFlow.getIncidentDateLabel());
        program.setRegistration(programFlow.isRegistration());
        program.setSelectEnrollmentDatesInFuture(programFlow.isSelectEnrollmentDatesInFuture());
        program.setDataEntryMethod(programFlow.isDataEntryMethod());
        program.setSingleEvent(programFlow.isSingleEvent());
        program.setIgnoreOverdueEvents(programFlow.isIgnoreOverdueEvents());
        program.setRelationshipFromA(programFlow.isRelationshipFromA());
        program.setSelectIncidentDatesInFuture(programFlow.isSelectIncidentDatesInFuture());
        program.setIsAssignedToUser(programFlow.isAssignedToUser());
        return program;
    }

    @Override
    public Class<Program> getModelTypeClass() {
        return Program.class;
    }

    @Override
    public Class<ProgramFlow> getDatabaseEntityTypeClass() {
        return ProgramFlow.class;
    }
}
