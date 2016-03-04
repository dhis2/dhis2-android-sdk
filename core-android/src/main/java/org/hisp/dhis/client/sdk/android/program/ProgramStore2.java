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

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.common.base.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.ModelLink$Flow;
import org.hisp.dhis.client.sdk.android.flow.ModelLink$Flow$Table;
import org.hisp.dhis.client.sdk.android.flow.Program$Flow;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.program.IProgramStore;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramStore2 extends AbsIdentifiableObjectStore<Program, Program$Flow>
        implements IProgramStore {

    /* Relationship type between programs and organisation units */
    private static final String PROGRAM_TO_ORGANISATION_UNITS = "programToOrganisationUnits";

    private final ITransactionManager transactionManager;

    public ProgramStore2(IMapper<Program, Program$Flow> mapper,
                         ITransactionManager transactionManager) {
        super(mapper);

        this.transactionManager = transactionManager;
    }

    @Override
    public Program queryById(long id) {
        Program program = super.queryById(id);

        if (program != null) {
            List<OrganisationUnit> organisationUnits = queryRelatedOrganisationUnits(program);
            program.setOrganisationUnits(organisationUnits);
        }

        return program;
    }

    @Override
    public Program queryByUid(String uid) {
        Program program = super.queryByUid(uid);

        if (program != null) {
            List<OrganisationUnit> organisationUnits = queryRelatedOrganisationUnits(program);
            program.setOrganisationUnits(organisationUnits);
        }

        return program;
    }

    @Override
    public List<Program> queryAll() {
        List<Program> programs = super.queryAll();

        // resolving relationships with organisation units
        Map<String, List<OrganisationUnit>> programsToUnits = queryRelatedOrganisationUnits();
        for (Program program : programs) {
            program.setOrganisationUnits(programsToUnits.get(program.getUId()));
        }

        return programs;
    }

    @Override
    public boolean insert(Program object) {
        boolean isSuccess = super.insert(object);

        if (isSuccess) {
            updateProgramRelationShips(object);
        }

        return isSuccess;
    }

    @Override
    public boolean update(Program object) {
        boolean isSuccess = super.update(object);

        if (isSuccess) {
            updateProgramRelationShips(object);
        }

        return isSuccess;
    }

    @Override
    public boolean delete(Program object) {
        boolean isSuccess = super.delete(object);

        if (isSuccess) {
            deleteRelatedOrganisationUnits(object);
        }

        return isSuccess;
    }

    @Override
    public boolean save(Program object) {
        boolean isSuccess = super.save(object);

        if (isSuccess) {
            updateProgramRelationShips(object);
        }

        return isSuccess;
    }

    private void updateProgramRelationShips(Program program) {
        List<IDbOperation> dbOperations = updateLinksToOrganisationUnits(program);
        transactionManager.transact(dbOperations);
    }

    private List<IDbOperation> updateLinksToOrganisationUnits(Program program) {
        // organisation unit to program relation ships
        // create generic link table with UID to UID mapping?
        // then it will be impossible to perform joins on tables.

        List<ModelLink$Flow> links = new ArrayList<>();
        if (program.getOrganisationUnits() != null) {
            for (OrganisationUnit orgUnit : program.getOrganisationUnits()) {
                ModelLink$Flow linkModel = new ModelLink$Flow();
                linkModel.setKeyOne(program.getUId());
                linkModel.setKeyTwo(orgUnit.getUId());
                linkModel.setLinkMimeType(PROGRAM_TO_ORGANISATION_UNITS);
                links.add(linkModel);
            }
        }

        List<ModelLink$Flow> persistedLinks = new Select()
                .from(ModelLink$Flow.class)
                .where(Condition.column(ModelLink$Flow$Table
                        .LINKMIMETYPE).is(PROGRAM_TO_ORGANISATION_UNITS))
                .queryList();

        return ModelLink$Flow.createOperations(persistedLinks, links);
    }

    private List<OrganisationUnit> queryRelatedOrganisationUnits(Program program) {
        List<ModelLink$Flow> persistedLinks = new Select()
                .from(ModelLink$Flow.class)
                .where(Condition.column(ModelLink$Flow$Table
                        .LINKMIMETYPE).is(PROGRAM_TO_ORGANISATION_UNITS))
                .and(Condition.column(ModelLink$Flow$Table.MODELKEYONE).is(program.getUId()))
                .queryList();

        List<OrganisationUnit> organisationUnits = new ArrayList<>();
        for (ModelLink$Flow linkModel : persistedLinks) {
            OrganisationUnit organisationUnit = new OrganisationUnit();
            organisationUnit.setUId(linkModel.getKeyTwo());
            organisationUnits.add(organisationUnit);
        }

        return organisationUnits;
    }

    private Map<String, List<OrganisationUnit>> queryRelatedOrganisationUnits() {
        List<ModelLink$Flow> persistedLinks = new Select()
                .from(ModelLink$Flow.class)
                .where(Condition.column(ModelLink$Flow$Table
                        .LINKMIMETYPE).is(PROGRAM_TO_ORGANISATION_UNITS))
                .queryList();

        Map<String, List<OrganisationUnit>> programsToUnits = new HashMap<>();
        for (ModelLink$Flow linkModel : persistedLinks) {
            OrganisationUnit organisationUnit = new OrganisationUnit();
            organisationUnit.setUId(linkModel.getKeyTwo());

            if (programsToUnits.get(linkModel.getKeyOne()) == null) {
                programsToUnits.put(linkModel.getKeyOne(), new ArrayList<OrganisationUnit>());
            }

            programsToUnits.get(linkModel.getKeyOne()).add(organisationUnit);
        }

        return programsToUnits;
    }

    private void deleteRelatedOrganisationUnits(Program program) {
        new Delete().from(ModelLink$Flow.class)
                .where(Condition.column(ModelLink$Flow$Table
                        .LINKMIMETYPE).is(PROGRAM_TO_ORGANISATION_UNITS))
                .and(Condition.column(ModelLink$Flow$Table.MODELKEYONE).is(program.getUId()))
                .query();
    }

    // * as soon as any write type of action happens, which should consider syncing relationships.
    // * but it also means that we have to fetch related elements on each read operation
    //   (Potentially can result in performance problem)
}
