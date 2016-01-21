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

import org.hisp.dhis.client.sdk.android.api.modules.MapperModule;
import org.hisp.dhis.client.sdk.android.api.utils.MapperModuleProvider;
import org.hisp.dhis.client.sdk.android.common.D2;
import org.hisp.dhis.client.sdk.android.common.base.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.Program$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramStage$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramTrackedEntityAttribute$Flow;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;

public class ProgramMapper extends AbsMapper<Program, Program$Flow> {

    @Override
    public Program$Flow mapToDatabaseEntity(Program program) {
        if (program == null) {
            return null;
        }

        Program$Flow programFlow = new Program$Flow();
        programFlow.setId(program.getId());
        programFlow.setUId(program.getUId());
        programFlow.setCreated(program.getCreated());
        programFlow.setLastUpdated(program.getLastUpdated());
        programFlow.setName(program.getName());
        programFlow.setDisplayName(program.getDisplayName());
        programFlow.setAccess(program.getAccess());
        programFlow.setTrackedEntity(program.getTrackedEntity());
        programFlow.setType(program.getType());
        programFlow.setKind(program.getKind());
        programFlow.setVersion(program.getVersion());
        programFlow.setDateOfEnrollmentDescription(program.getDateOfEnrollmentDescription());
        programFlow.setDescription(program.getDescription());
        programFlow.setOnlyEnrollOnce(program.isOnlyEnrollOnce());
        programFlow.setExtenalAccess(program.isExternalAccess());
        programFlow.setDisplayIncidentDate(program.isDisplayIncidentDate());
        programFlow.setDateOfIncidentDescription(program.getDateOfIncidentDescription());
        programFlow.setRegistration(program.isRegistration());
        programFlow.setSelectEnrollmentDatesInFuture(program.isSelectEnrollmentDatesInFuture());
        programFlow.setDataEntryMethod(program.isDataEntryMethod());
        programFlow.setSingleEvent(program.isSingleEvent());
        programFlow.setIgnoreOverdueEvents(program.isIgnoreOverdueEvents());
        programFlow.setRelationshipFromA(program.isRelationshipFromA());
        programFlow.setSelectIncidentDatesInFuture(program.isSelectIncidentDatesInFuture());
        programFlow.setProgramStages(MapperModuleProvider.getInstance().getProgramStageMapper().mapToDatabaseEntities(program.getProgramStages()));
        programFlow.setProgramTrackedEntityAttributes(MapperModuleProvider.getInstance().getProgramTrackedEntityAttributeMapper().mapToDatabaseEntities(program.getProgramTrackedEntityAttributes()));
        return programFlow;
    }

    @Override
    public Program mapToModel(Program$Flow programFlow) {
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
        program.setTrackedEntity(programFlow.getTrackedEntity());
        program.setType(programFlow.getType());
        program.setKind(programFlow.getKind());
        program.setVersion(programFlow.getVersion());
        program.setDateOfEnrollmentDescription(programFlow.getDateOfEnrollmentDescription());
        program.setDescription(programFlow.getDescription());
        program.setOnlyEnrollOnce(programFlow.isOnlyEnrollOnce());
        program.setExternalAccess(programFlow.isExtenalAccess());
        program.setDisplayIncidentDate(programFlow.isDisplayIncidentDate());
        program.setDateOfIncidentDescription(programFlow.getDateOfIncidentDescription());
        program.setRegistration(programFlow.isRegistration());
        program.setSelectEnrollmentDatesInFuture(programFlow.isSelectEnrollmentDatesInFuture());
        program.setDataEntryMethod(programFlow.isDataEntryMethod());
        program.setSingleEvent(programFlow.isSingleEvent());
        program.setIgnoreOverdueEvents(programFlow.isIgnoreOverdueEvents());
        program.setRelationshipFromA(programFlow.isRelationshipFromA());
        program.setSelectIncidentDatesInFuture(programFlow.isSelectIncidentDatesInFuture());
        program.setProgramStages(MapperModuleProvider.getInstance().getProgramStageMapper().mapToModels(programFlow.getProgramStages()));
        program.setProgramTrackedEntityAttributes(MapperModuleProvider.getInstance().getProgramTrackedEntityAttributeMapper().mapToModels(programFlow.getProgramTrackedEntityAttributes()));
        return program;
    }

    @Override
    public Class<Program> getModelTypeClass() {
        return Program.class;
    }

    @Override
    public Class<Program$Flow> getDatabaseEntityTypeClass() {
        return Program$Flow.class;
    }
}
