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

package org.hisp.dhis.client.sdk.android.organisationunit;

import android.support.annotation.Nullable;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.ModelLinkFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OrganisationUnitFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OrganisationUnitFlow_Table;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitStore;
import org.hisp.dhis.client.sdk.models.dataset.DataSet;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class OrganisationUnitStore extends AbsIdentifiableObjectStore<OrganisationUnit,
        OrganisationUnitFlow> implements IOrganisationUnitStore {

    private static final String UNITS_TO_PROGRAMS = "organisationUnitsToPrograms";
    private final ITransactionManager transactionManager;

    public OrganisationUnitStore(ITransactionManager transactionManager) {
        super(OrganisationUnitFlow.MAPPER);

        this.transactionManager = transactionManager;
    }

    @Override
    public List<OrganisationUnit> queryAll() {
        return queryUnitRelationships(super.queryAll());
    }

    @Override
    public List<OrganisationUnit> query(boolean assignedToCurrentUser) {
        List<OrganisationUnitFlow> organisationUnitFlows = new Select()
                .from(OrganisationUnitFlow.class)
                .where(OrganisationUnitFlow_Table
                        .isAssignedToUser.is(assignedToCurrentUser))
                .queryList();

        List<OrganisationUnit> orgUnits = getMapper().mapToModels(organisationUnitFlows);
        return queryUnitRelationships(orgUnits);
    }

    @Override
    public List<OrganisationUnit> queryByPrograms(List<Program> programs) {
        List<OrganisationUnitFlow> orgUnitFlows = ModelLinkFlow.queryRelatedModels(
                OrganisationUnitFlow.class, UNITS_TO_PROGRAMS, programs);

        List<OrganisationUnit> organisationUnits = getMapper().mapToModels(orgUnitFlows);
        return queryUnitRelationships(organisationUnits);
    }

    @Override
    public List<OrganisationUnit> queryByDataSets(List<DataSet> dataSets) {
        throw new RuntimeException("Unimplemented method");
    }

    @Override
    public OrganisationUnit queryById(long id) {
        return queryUnitRelationships(super.queryById(id));
    }

    @Override
    public OrganisationUnit queryByUid(String uid) {
        return queryUnitRelationships(super.queryByUid(uid));
    }

    @Override
    public boolean insert(OrganisationUnit object) {
        boolean isSuccess = super.insert(object);

        if (isSuccess) {
            updateOrganisationUnitRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean update(OrganisationUnit object) {
        boolean isSuccess = super.update(object);

        if (isSuccess) {
            updateOrganisationUnitRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean save(OrganisationUnit object) {
        boolean isSuccess = super.save(object);

        if (isSuccess) {
            updateOrganisationUnitRelationships(object);
        }

        return isSuccess;
    }

    @Override
    public boolean delete(OrganisationUnit object) {
        boolean isSuccess = super.delete(object);

        if (isSuccess) {
            ModelLinkFlow.deleteRelatedModels(object, UNITS_TO_PROGRAMS);
        }

        return isSuccess;
    }

    @Override
    public boolean deleteAll() {
        boolean isSuccess = super.deleteAll();

        if (isSuccess) {
            ModelLinkFlow.deleteModels(UNITS_TO_PROGRAMS);
        }

        return isSuccess;
    }

    private void updateOrganisationUnitRelationships(OrganisationUnit organisationUnit) {
        List<IDbOperation> dbOperations = ModelLinkFlow.updateLinksToModel(organisationUnit,
                organisationUnit.getPrograms(), UNITS_TO_PROGRAMS);
        transactionManager.transact(dbOperations);
    }

    @Nullable
    private List<OrganisationUnit> queryUnitRelationships(@Nullable List<OrganisationUnit> units) {
        if (units != null) {
            Map<String, List<Program>> organisationUnitsToPrograms = ModelLinkFlow
                    .queryLinksForModel(Program.class, UNITS_TO_PROGRAMS);

            for (OrganisationUnit organisationUnit : units) {
                organisationUnit.setPrograms(organisationUnitsToPrograms
                        .get(organisationUnit.getUId()));
            }
        }

        return units;
    }

    @Nullable
    private OrganisationUnit queryUnitRelationships(@Nullable OrganisationUnit organisationUnit) {
        if (organisationUnit != null) {
            List<Program> programs = ModelLinkFlow.queryLinksForModel(
                    Program.class, UNITS_TO_PROGRAMS, organisationUnit.getUId());
            organisationUnit.setPrograms(programs);
        }

        return organisationUnit;
    }
}
