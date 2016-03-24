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

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.DbFlowOperation;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OrganisationUnitFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OrganisationUnitToProgramRelationFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow
        .OrganisationUnitToProgramRelationFlow_Table;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramFlow;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.android.common.IMapper;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

public final class ProgramStore extends AbsIdentifiableObjectStore<Program, ProgramFlow>
        implements IProgramStore {

    private final ITransactionManager mTransactionManager;
    private final IMapper<OrganisationUnit, OrganisationUnitFlow> mOrganisationUnitMapper;
    private final IProgramTrackedEntityAttributeStore mProgramTrackedEntityAttributeStore;
    private final IProgramStageStore mProgramStageStore;
    private final IProgramIndicatorStore mProgramIndicatorStore;

    public ProgramStore(IMapper<Program, ProgramFlow> mapper,
                        ITransactionManager transactionManager,
                        IMapper<OrganisationUnit, OrganisationUnitFlow> organisationUnitMapper,
                        IProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore,
                        IProgramStageStore programStageStore,
                        IProgramIndicatorStore programIndicatorStore) {
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

        ProgramFlow databaseEntity = getMapper().mapToDatabaseEntity(program);
        if (databaseEntity != null) {
            databaseEntity.insert();

            /* setting id which DbFlows' BaseModel generated after insertion */
            program.setId(databaseEntity.getId());

            List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = program
                    .getProgramTrackedEntityAttributes();
            if (programTrackedEntityAttributes != null) {
                for (ProgramTrackedEntityAttribute programTrackedEntityAttribute :
                        programTrackedEntityAttributes) {
                    if (!mProgramTrackedEntityAttributeStore.insert
                            (programTrackedEntityAttribute)) {
                        return false;
                    }
                }
            }

            List<ProgramIndicator> programIndicators = program.getProgramIndicators();
            if (programIndicators != null) {
                for (ProgramIndicator programIndicator : programIndicators) {
                    if (!mProgramIndicatorStore.insert(programIndicator)) {
                        return false;
                    }
                }
            }

            List<ProgramStage> programStages = program.getProgramStages();
            if (programStages != null) {
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

        ProgramFlow databaseEntity = getMapper().mapToDatabaseEntity(program);
        if (databaseEntity != null) {
            databaseEntity.save();

            /* setting id which DbFlows' BaseModel generated after insertion */
            program.setId(databaseEntity.getId());

            List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = program
                    .getProgramTrackedEntityAttributes();
            if (programTrackedEntityAttributes != null) {
                for (ProgramTrackedEntityAttribute programTrackedEntityAttribute :
                        programTrackedEntityAttributes) {
                    if (!mProgramTrackedEntityAttributeStore.save(programTrackedEntityAttribute)) {
                        return false;
                    }
                }
            }

            List<ProgramIndicator> programIndicators = program.getProgramIndicators();
            if (programIndicators != null) {
                for (ProgramIndicator programIndicator : programIndicators) {
                    if (!mProgramIndicatorStore.save(programIndicator)) {
                        return false;
                    }
                }
            }

            List<ProgramStage> programStages = program.getProgramStages();
            if (programStages != null) {
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

//    @Override
//    public List<Program> query(OrganisationUnit organisationUnit) {
//        List<OrganisationUnitToProgramRelation_Flow> organisationUnitToProgramRelations =
//                new Select().from(OrganisationUnitToProgramRelation_Flow.class).
//                        where(Condition.column(OrganisationUnitToProgramRelation_Flow_Table
//                                .ORGANISATIONUNIT_ORGANISATIONUNIT).is(organisationUnit.getUId()))
//                        .queryList();
//        List<Program> programs = new ArrayList<>();
//        for (OrganisationUnitToProgramRelation_Flow relationFlows :
//                organisationUnitToProgramRelations) {
//            programs.add(getMapper().mapToModel(relationFlows.getProgram()));
//        }
//        return programs;
//    }

    // @Override
    public List<Program> query(OrganisationUnit organisationUnit, ProgramType... programTypes) {
        List<OrganisationUnitToProgramRelationFlow> organisationUnitToProgramRelations =
                new Select().from(OrganisationUnitToProgramRelationFlow.class)
                        .where((OrganisationUnitToProgramRelationFlow_Table
                                .organisationUnit).is(organisationUnit.getUId()))
                        .queryList();

        List<Program> programs = new ArrayList<>();
        List<ProgramType> programTypesSet = Arrays.asList(programTypes);
        for (OrganisationUnitToProgramRelationFlow relationFlow :
                organisationUnitToProgramRelations) {
            if (programTypesSet.contains(relationFlow.getProgram().getProgramType())) {
                programs.add(getMapper().mapToModel(relationFlow.getProgram()));
            }
        }

        return programs;
    }

    // @Override
    public void assign(Program program, Set<OrganisationUnit> organisationUnits) {
        List<IDbOperation> operations = new ArrayList<>();
        List<OrganisationUnitToProgramRelationFlow> relationFlows = new Select()
                .from(OrganisationUnitToProgramRelationFlow.class)
                .where(OrganisationUnitToProgramRelationFlow_Table
                        .program.is(program.getUId())).queryList();

        Map<String, OrganisationUnitToProgramRelationFlow> relationFlowMap = new HashMap<>();
        for (OrganisationUnitToProgramRelationFlow relationFlow : relationFlows) {
            relationFlowMap.put(relationFlow.getOrganisationUnit().getUId(), relationFlow);
        }

        for (OrganisationUnit organisationUnit : organisationUnits) {
            if (!relationFlowMap.containsKey(organisationUnit.getUId())) {
                OrganisationUnitToProgramRelationFlow newRelationFlow =
                        new OrganisationUnitToProgramRelationFlow();
                newRelationFlow.setOrganisationUnit(mOrganisationUnitMapper
                        .mapToDatabaseEntity(organisationUnit));
                newRelationFlow.setProgram(getMapper().mapToDatabaseEntity(program));
                operations.add(DbFlowOperation.insert(newRelationFlow));
            }
        }

        mTransactionManager.transact(operations);
    }

    @Override
    public List<Program> query(boolean assignedToCurrentUser) {
        return null;
    }

    @Override
    public List<Program> query(OrganisationUnit... organisationUnits) {
        return null;
    }
}
