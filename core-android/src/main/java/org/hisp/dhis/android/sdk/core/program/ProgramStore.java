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

package org.hisp.dhis.android.sdk.core.program;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.models.common.meta.IDbOperation;
import org.hisp.dhis.android.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.sdk.models.program.IProgramStageStore;
import org.hisp.dhis.android.sdk.models.program.IProgramStore;
import org.hisp.dhis.android.sdk.models.program.IProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.sdk.models.program.Program;
import org.hisp.dhis.android.sdk.core.common.meta.DbFlowOperation;
import org.hisp.dhis.android.sdk.core.flow.Program$Flow$Table;
import org.hisp.dhis.android.sdk.core.flow.OrganisationUnitToProgramRelation$Flow$Table;
import org.hisp.dhis.android.sdk.core.flow.OrganisationUnit$Flow;
import org.hisp.dhis.android.sdk.core.flow.OrganisationUnitToProgramRelation$Flow;
import org.hisp.dhis.android.sdk.core.flow.Program$Flow;
import org.hisp.dhis.android.sdk.core.flow.ProgramStage$Flow;
import org.hisp.dhis.android.sdk.core.flow.ProgramTrackedEntityAttribute$Flow;
import org.hisp.dhis.android.sdk.core.api.utils.DbUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ProgramStore implements IProgramStore {

    private final IProgramStageStore programStageStore;
    private final IProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;

    public ProgramStore(IProgramStageStore programStageStore,
                        IProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore) {
        this.programStageStore = programStageStore;
        this.programTrackedEntityAttributeStore = programTrackedEntityAttributeStore;
    }

    @Override
    public boolean insert(Program object) {
        Program$Flow programFlow = Program$Flow.fromModel(object);
        programFlow.insert();

        object.setId(programFlow.getId());
        return true;
    }

    @Override
    public boolean update(Program object) {
        Program$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(Program object) {
        Program$Flow programFlow =
                Program$Flow.fromModel(object);
        programFlow.save();

        object.setId(programFlow.getId());
        return true;
    }

    @Override
    public boolean delete(Program object) {
        Program$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public List<Program> queryAll() {
        List<Program$Flow> programFlows = new Select()
                .from(Program$Flow.class)
                .queryList();
        for (Program$Flow programFlow : programFlows) {
            setProgramStages(programFlow);
            setProgramTrackedEntityAttributes(programFlow);
        }
        return Program$Flow.toModels(programFlows);
    }

    @Override
    public Program queryById(long id) {
        Program$Flow programFlow = new Select()
                .from(Program$Flow.class)
                .where(Condition.column(Program$Flow$Table.ID).is(id))
                .querySingle();
        setProgramStages(programFlow);
        setProgramTrackedEntityAttributes(programFlow);
        return Program$Flow.toModel(programFlow);
    }

    @Override
    public Program queryByUid(String uid) {
        Program$Flow programFlow = new Select()
                .from(Program$Flow.class)
                .where(Condition.column(Program$Flow$Table.UID).is(uid))
                .querySingle();
        setProgramStages(programFlow);
        setProgramTrackedEntityAttributes(programFlow);
        return Program$Flow.toModel(programFlow);
    }

    private void setProgramStages(Program$Flow programFlow) {
        if (programFlow == null) {
            return;
        }
        programFlow.setProgramStages(ProgramStage$Flow
                .fromModels(programStageStore
                        .query(Program$Flow.toModel(programFlow))));
    }

    private void setProgramTrackedEntityAttributes(Program$Flow programFlow) {
        if (programFlow == null) {
            return;
        }
        programFlow.setProgramTrackedEntityAttributes(ProgramTrackedEntityAttribute$Flow
                .fromModels(programTrackedEntityAttributeStore
                        .query(Program$Flow.toModel(programFlow))));
    }

    @Override
    public List<Program> query(OrganisationUnit organisationUnit) {
        List<OrganisationUnitToProgramRelation$Flow> organisationUnitToProgramRelations =
                new Select().from(OrganisationUnitToProgramRelation$Flow.class).
                        where(Condition.column(OrganisationUnitToProgramRelation$Flow$Table
                                .ORGANISATIONUNIT_ORGANISATIONUNIT).is(OrganisationUnit$Flow
                                .fromModel(organisationUnit))).queryList();
        List<Program> programs = new ArrayList<>();
        for (OrganisationUnitToProgramRelation$Flow relationFlows : organisationUnitToProgramRelations) {
            programs.add(Program$Flow.toModel(relationFlows.getProgram()));
        }
        return programs;
    }

    @Override
    public void assign(Program program, List<OrganisationUnit> organisationUnits) {
        List<IDbOperation> operations = new ArrayList<>();
        List<OrganisationUnitToProgramRelation$Flow> relationFlows = new Select().from(OrganisationUnitToProgramRelation$Flow.class).
                where(Condition.column
                        (OrganisationUnitToProgramRelation$Flow$Table.PROGRAM_PROGRAM).
                        is(Program$Flow.fromModel(program))).queryList();
        Map<String, OrganisationUnitToProgramRelation$Flow> relationFlowMap = new HashMap<>();
        for (OrganisationUnitToProgramRelation$Flow relationFlow : relationFlows) {
            relationFlowMap.put(relationFlow.getOrganisationUnit().getUId(), relationFlow);
        }
        for (OrganisationUnit organisationUnit : organisationUnits) {
            if (!relationFlowMap.containsValue(organisationUnit.getUId())) {
                OrganisationUnitToProgramRelation$Flow newRelationFlow = new OrganisationUnitToProgramRelation$Flow();
                newRelationFlow.setOrganisationUnit(OrganisationUnit$Flow.fromModel(organisationUnit));
                newRelationFlow.setProgram(Program$Flow.fromModel(program));
                operations.add(DbFlowOperation.insert(newRelationFlow));
            }
        }
        DbUtils.applyBatch(operations);
    }
}
