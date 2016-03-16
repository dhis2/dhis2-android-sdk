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

import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OrganisationUnitFlow;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;


public class OrganisationUnitMapper extends AbsMapper<OrganisationUnit, OrganisationUnitFlow> {

    public OrganisationUnitMapper() {
        // empty constructor
    }

    @Override
    public OrganisationUnitFlow mapToDatabaseEntity(OrganisationUnit organisationUnit) {
        if (organisationUnit == null) {
            return null;
        }

        OrganisationUnitFlow organisationUnitFlow = new OrganisationUnitFlow();
        organisationUnitFlow.setId(organisationUnit.getId());
        organisationUnitFlow.setUId(organisationUnit.getUId());
        organisationUnitFlow.setCreated(organisationUnit.getCreated());
        organisationUnitFlow.setLastUpdated(organisationUnit.getLastUpdated());
        organisationUnitFlow.setName(organisationUnit.getName());
        organisationUnitFlow.setDisplayName(organisationUnit.getDisplayName());
        organisationUnitFlow.setAccess(organisationUnit.getAccess());
        organisationUnitFlow.setLevel(organisationUnit.getLevel());
        organisationUnitFlow.setParent(mapToDatabaseEntity(organisationUnit.getParent()));
        organisationUnitFlow.setIsAssignedToUser(organisationUnit.isAssignedToUser());
        return organisationUnitFlow;
    }

    @Override
    public OrganisationUnit mapToModel(OrganisationUnitFlow organisationUnitFlow) {
        if (organisationUnitFlow == null) {
            return null;
        }

        OrganisationUnit organisationUnit = new OrganisationUnit();
        organisationUnit.setId(organisationUnitFlow.getId());
        organisationUnit.setUId(organisationUnitFlow.getUId());
        organisationUnit.setCreated(organisationUnitFlow.getCreated());
        organisationUnit.setLastUpdated(organisationUnitFlow.getLastUpdated());
        organisationUnit.setName(organisationUnitFlow.getName());
        organisationUnit.setDisplayName(organisationUnitFlow.getDisplayName());
        organisationUnit.setAccess(organisationUnitFlow.getAccess());
        organisationUnit.setLevel(organisationUnitFlow.getLevel());
        organisationUnit.setParent(mapToModel(organisationUnitFlow.getParent()));
        organisationUnit.setIsAssignedToUser(organisationUnitFlow.isAssignedToUser());
        return organisationUnit;
    }

    @Override
    public Class<OrganisationUnit> getModelTypeClass() {
        return OrganisationUnit.class;
    }

    @Override
    public Class<OrganisationUnitFlow> getDatabaseEntityTypeClass() {
        return OrganisationUnitFlow.class;
    }
}
