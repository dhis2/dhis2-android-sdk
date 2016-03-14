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

import org.hisp.dhis.client.sdk.android.common.base.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.ModelLinkFlow;
import org.hisp.dhis.client.sdk.android.flow.ProgramFlow;
import org.hisp.dhis.client.sdk.android.flow.ProgramFlow_Table;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.program.IProgramStore;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProgramStore2 extends AbsIdentifiableObjectStore<Program, ProgramFlow>
        implements IProgramStore {

    /* Relationship type between programs and organisation units */
    private static final String PROGRAM_TO_ORGANISATION_UNITS = "programToOrganisationUnits";

    private final ITransactionManager transactionManager;


    public ProgramStore2(IMapper<Program, ProgramFlow> mapper,
                         ITransactionManager transactionManager) {
        super(mapper);

        this.transactionManager = transactionManager;
    }

    @Override
    public List<Program> queryAll() {
        return queryProgramRelationships(super.queryAll());
    }

    @Override
    public List<Program> query(boolean assignedToCurrentUser) {
        List<ProgramFlow> programFlows = new Select()
                .from(ProgramFlow.class)
                .where(ProgramFlow_Table
                        .isAssignedToUser.is(assignedToCurrentUser))
                .queryList();

        List<Program> programs = getMapper().mapToModels(programFlows);
        return queryProgramRelationships(programs);
    }

    @Override
    public List<Program> query(OrganisationUnit... organisationUnits) {
        List<ProgramFlow> programFlows = ModelLinkFlow.queryRelatedModels(ProgramFlow.class,
                PROGRAM_TO_ORGANISATION_UNITS, Arrays.asList(organisationUnits));

        System.out.println("*** PROGRAM_FLOWS *** " + programFlows);
        List<Program> programs = getMapper().mapToModels(programFlows);
        System.out.println("*** PROGRAMS *** " + programs);
        return queryProgramRelationships(programs);
    }

    @Override
    public Program queryById(long id) {
        return queryProgramRelationships(super.queryById(id));
    }

    @Override
    public Program queryByUid(String uid) {
        return queryProgramRelationships(super.queryByUid(uid));
    }

    @Override
    public boolean insert(Program object) {
        boolean isSuccess = super.insert(object);

        if (isSuccess) {
            updateProgramRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean update(Program object) {
        boolean isSuccess = super.update(object);

        if (isSuccess) {
            updateProgramRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean delete(Program object) {
        boolean isSuccess = super.delete(object);

        if (isSuccess) {
            ModelLinkFlow.deleteRelatedModels(object, PROGRAM_TO_ORGANISATION_UNITS);
        }

        return isSuccess;
    }

    @Override
    public boolean save(Program object) {
        boolean isSuccess = super.save(object);

        if (isSuccess) {
            updateProgramRelationships(object);
        }

        return isSuccess;
    }

    private void updateProgramRelationships(Program program) {
        List<IDbOperation> dbOperations = ModelLinkFlow.updateLinksToModel(program,
                program.getOrganisationUnits(), PROGRAM_TO_ORGANISATION_UNITS);
        transactionManager.transact(dbOperations);
    }

    private List<Program> queryProgramRelationships(List<Program> programs) {

        // resolving relationships with organisation units
        if (programs != null) {
            Map<String, List<OrganisationUnit>> programsToUnits = ModelLinkFlow
                    .queryLinksForModel(OrganisationUnit.class, PROGRAM_TO_ORGANISATION_UNITS);
            for (Program program : programs) {
                program.setOrganisationUnits(programsToUnits.get(program.getUId()));
            }
        }

        return programs;
    }

    private Program queryProgramRelationships(Program program) {

        if (program != null) {
            List<OrganisationUnit> organisationUnits = ModelLinkFlow.queryLinksForModel(
                    OrganisationUnit.class, PROGRAM_TO_ORGANISATION_UNITS, program.getUId());
            program.setOrganisationUnits(organisationUnits);
        }

        return program;
    }
}
