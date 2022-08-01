/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.user.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserInternalAccessor;

import java.util.List;
import java.util.Set;

import static org.hisp.dhis.android.core.organisationunit.OrganisationUnitTree.findRoots;

public final class UserOrganisationUnitLinkHelper {

    private UserOrganisationUnitLinkHelper() {
    }

    public static boolean userIsAssigned(
            OrganisationUnit.Scope scope,
            User user,
            OrganisationUnit organisationUnit
    ) {

        List<OrganisationUnit> selectedScopeOrganisationUnits = null;

        switch (scope) {
            case SCOPE_TEI_SEARCH:
                selectedScopeOrganisationUnits = UserInternalAccessor.accessTeiSearchOrganisationUnits(user);
                break;

            case SCOPE_DATA_CAPTURE:
                selectedScopeOrganisationUnits = UserInternalAccessor.accessOrganisationUnits(user);
                break;

            default:
                break;
        }

        if (selectedScopeOrganisationUnits == null) {
            return false;
        } else {
            Set<String> assignedOrgUnitUids = UidsHelper.getUids(selectedScopeOrganisationUnits);
            return assignedOrgUnitUids.contains(organisationUnit.uid());
        }
    }

    public static boolean isRoot(@NonNull OrganisationUnit.Scope scope,
                                 @NonNull User user,
                                 @NonNull OrganisationUnit organisationUnit) {

        List<OrganisationUnit> selectedScopeOrganisationUnits = null;

        switch (scope) {

            case SCOPE_TEI_SEARCH:
                selectedScopeOrganisationUnits = UserInternalAccessor.accessTeiSearchOrganisationUnits(user);
                break;

            case SCOPE_DATA_CAPTURE:
                selectedScopeOrganisationUnits = UserInternalAccessor.accessOrganisationUnits(user);
                break;

            default:
                break;
        }

        if (selectedScopeOrganisationUnits == null) {
            return false;
        } else {
            Set<String> rootOrgUnitUids = UidsHelper.getUids(findRoots(selectedScopeOrganisationUnits));
            return rootOrgUnitUids.contains(organisationUnit.uid());
        }
    }
}