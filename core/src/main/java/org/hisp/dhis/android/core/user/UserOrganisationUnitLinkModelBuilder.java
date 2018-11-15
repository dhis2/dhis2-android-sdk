/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import java.util.List;
import java.util.Set;

import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.findRoots;

public class UserOrganisationUnitLinkModelBuilder
        extends ModelBuilder<OrganisationUnit, UserOrganisationUnitLinkModel> {

    private final UserOrganisationUnitLinkModel.Builder builder;
    private final User user;
    private final OrganisationUnit.Scope organisationUnitScope;

    public UserOrganisationUnitLinkModelBuilder(OrganisationUnit.Scope scope, User user) {
        this.user = user;
        this.organisationUnitScope = scope;
        this.builder = UserOrganisationUnitLinkModel.builder()
                .organisationUnitScope(scope.name())
                .user(user.uid());
    }

    @Override
    public UserOrganisationUnitLinkModel buildModel(OrganisationUnit organisationUnit) {
        return builder
                .organisationUnit(organisationUnit.uid())
                .root(isRoot(organisationUnit))
                .build();
    }

    @SuppressWarnings("PMD")
    private boolean isRoot(OrganisationUnit organisationUnit) {

        List<OrganisationUnit> selectedScopeOrganisationUnits = null;

        switch (this.organisationUnitScope) {

            case SCOPE_TEI_SEARCH:
                selectedScopeOrganisationUnits = user.teiSearchOrganisationUnits();
                break;

            case SCOPE_DATA_CAPTURE:
                selectedScopeOrganisationUnits = user.organisationUnits();
                break;
        }

        if (selectedScopeOrganisationUnits == null) {
            return false;
        } else {
            Set<String> rootOrgUnitUids = findRoots(selectedScopeOrganisationUnits);
            return rootOrgUnitUids.contains(organisationUnit.uid());
        }
    }
}