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

import android.util.ArraySet;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.common.base.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.common.meta.DbFlowOperation;
import org.hisp.dhis.client.sdk.android.flow.OrganisationUnit$Flow;
import org.hisp.dhis.client.sdk.android.flow.OrganisationUnitToProgramRelation$Flow;
import org.hisp.dhis.client.sdk.android.flow.OrganisationUnitToProgramRelation$Flow$Table;
import org.hisp.dhis.client.sdk.android.flow.Program$Flow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntity$Flow;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.IIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.IStore;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.program.IProgramIndicatorStore;
import org.hisp.dhis.client.sdk.core.program.IProgramStageStore;
import org.hisp.dhis.client.sdk.core.program.IProgramStore;
import org.hisp.dhis.client.sdk.core.program.IProgramTrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.program.ProgramType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

public final class ProgramStore extends AbsIdentifiableObjectStore<Program,
        Program$Flow> implements IProgramStore {
    private final ITransactionManager mTransactionManager;
    private final IMapper<OrganisationUnit, OrganisationUnit$Flow> mOrganisationUnitMapper;
    private final IProgramTrackedEntityAttributeStore mProgramTrackedEntityAttributeStore;
    private final IProgramStageStore mProgramStageStore;
    private final IProgramIndicatorStore mProgramIndicatorStore;

    public ProgramStore(IMapper<Program, Program$Flow> mapper,
                        ITransactionManager transactionManager, IMapper<OrganisationUnit, OrganisationUnit$Flow> organisationUnitMapper, IProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore, IProgramStageStore programStageStore, IProgramIndicatorStore programIndicatorStore) {
        super(mapper);
        this.mTransactionManager = transactionManager;
        this.mOrganisationUnitMapper = organisationUnitMapper;
        this.mProgramTrackedEntityAttributeStore = programTrackedEntityAttributeStore;
        this.mProgramStageStore = programStageStore;
        this.mProgramIndicatorStore = programIndicatorStore;
    }

    @Override
    public boolean insert(Program program) {
        isNull(program, "object must not be null");

        Program$Flow databaseEntity = getMapper().mapToDatabaseEntity(program);
        if (databaseEntity != null) {
            databaseEntity.insert();

            /* setting id which DbFlows' BaseModel generated after insertion */
            program.setId(databaseEntity.getId());

            List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = program.getProgramTrackedEntityAttributes();
            if(programTrackedEntityAttributes != null) {
                for (ProgramTrackedEntityAttribute programTrackedEntityAttribute : programTrackedEntityAttributes) {
                    if (!mProgramTrackedEntityAttributeStore.insert(programTrackedEntityAttribute)) {
                        return false;
                    }
                }
            }

            List<ProgramIndicator> programIndicators = program.getProgramIndicators();
            if(programIndicators != null) {
                for (ProgramIndicator programIndicator : programIndicators) {
                    if (!mProgramIndicatorStore.insert(programIndicator)) {
                        return false;
                    }
                }
            }

            List<ProgramStage> programStages = program.getProgramStages();
            if(programStages != null) {
                for (ProgramStage programStage : programStages) {
                    if (!mProgramStageStore.insert(programStage)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean save(Program program) {
        isNull(program, "object must not be null");

        Program$Flow databaseEntity = getMapper().mapToDatabaseEntity(program);
        if (databaseEntity != null) {
            databaseEntity.save();

            /* setting id which DbFlows' BaseModel generated after insertion */
            program.setId(databaseEntity.getId());

            List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = program.getProgramTrackedEntityAttributes();
            if(programTrackedEntityAttributes != null) {
                for (ProgramTrackedEntityAttribute programTrackedEntityAttribute : programTrackedEntityAttributes) {
                    if (!mProgramTrackedEntityAttributeStore.save(programTrackedEntityAttribute)) {
                        return false;
                    }
                }
            }

            List<ProgramIndicator> programIndicators = program.getProgramIndicators();
            if(programIndicators != null) {
                for (ProgramIndicator programIndicator : programIndicators) {
                    if (!mProgramIndicatorStore.save(programIndicator)) {
                        return false;
                    }
                }
            }

            List<ProgramStage> programStages = program.getProgramStages();
            if(programStages != null) {
                for (ProgramStage programStage : programStages) {
                    if (!mProgramStageStore.save(programStage)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public List<Program> query(OrganisationUnit organisationUnit) {
        List<OrganisationUnitToProgramRelation$Flow> organisationUnitToProgramRelations =
                new Select().from(OrganisationUnitToProgramRelation$Flow.class).
                        where(Condition.column(OrganisationUnitToProgramRelation$Flow$Table
                                .ORGANISATIONUNIT_ORGANISATIONUNIT).is(organisationUnit.getUId()))
                        .queryList();
        List<Program> programs = new ArrayList<>();
        for (OrganisationUnitToProgramRelation$Flow relationFlows : organisationUnitToProgramRelations) {
            programs.add(getMapper().mapToModel(relationFlows.getProgram()));
        }
        return programs;
    }

    @Override
    public List<Program> query(OrganisationUnit organisationUnit, ProgramType... programTypes) {
        List<OrganisationUnitToProgramRelation$Flow> organisationUnitToProgramRelations =
                new Select().from(OrganisationUnitToProgramRelation$Flow.class).
                        where(Condition.column(OrganisationUnitToProgramRelation$Flow$Table
                                .ORGANISATIONUNIT_ORGANISATIONUNIT).is(organisationUnit.getUId())).queryList();
        List<Program> programs = new ArrayList<>();
        List<ProgramType> programTypesSet = Arrays.asList(programTypes);
        for (OrganisationUnitToProgramRelation$Flow relationFlow : organisationUnitToProgramRelations) {
            if(programTypesSet.contains(relationFlow.getProgram().getProgramType())) {
                programs.add(getMapper().mapToModel(relationFlow.getProgram()));
            }
        }
        return programs;
    }

    @Override
    public void assign(Program program, Set<OrganisationUnit> organisationUnits) {
        List<IDbOperation> operations = new ArrayList<>();
        List<OrganisationUnitToProgramRelation$Flow> relationFlows = new Select().from(OrganisationUnitToProgramRelation$Flow.class).
                where(Condition.column
                        (OrganisationUnitToProgramRelation$Flow$Table.PROGRAM_PROGRAM).
                        is(program.getUId())).queryList();
        Map<String, OrganisationUnitToProgramRelation$Flow> relationFlowMap = new HashMap<>();
        for (OrganisationUnitToProgramRelation$Flow relationFlow : relationFlows) {
            relationFlowMap.put(relationFlow.getOrganisationUnit().getUId(), relationFlow);
        }
        for (OrganisationUnit organisationUnit : organisationUnits) {
            if (!relationFlowMap.containsKey(organisationUnit.getUId())) {
                OrganisationUnitToProgramRelation$Flow newRelationFlow = new OrganisationUnitToProgramRelation$Flow();
                newRelationFlow.setOrganisationUnit(mOrganisationUnitMapper.mapToDatabaseEntity(organisationUnit));
                newRelationFlow.setProgram(getMapper().mapToDatabaseEntity(program));

                operations.add(DbFlowOperation.insert(newRelationFlow));
            }
        }
        mTransactionManager.transact(operations);
    }
}
