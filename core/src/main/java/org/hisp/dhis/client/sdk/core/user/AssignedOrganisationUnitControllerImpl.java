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

package org.hisp.dhis.client.sdk.core.user;

import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitController;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.user.UserAccount;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;

import java.util.List;
import java.util.Set;

public class AssignedOrganisationUnitControllerImpl implements AssignedOrganisationUnitsController {

    // Api Clients
    private final UserApiClient userApiClient;

    // Controllers
    private final OrganisationUnitController organisationUnitController;

    public AssignedOrganisationUnitControllerImpl(
            UserApiClient userApiClient, OrganisationUnitController
            organisationUnitController) {
        this.userApiClient = userApiClient;
        this.organisationUnitController = organisationUnitController;
    }

    @Override
    public void sync() throws ApiException {
        sync(SyncStrategy.DEFAULT);
    }

    @Override
    public void sync(SyncStrategy strategy) throws ApiException {
        UserAccount userAccount = userApiClient.getUserAccount();

        /* get list of assigned organisation units */
        List<OrganisationUnit> assignedOrganisationUnits = userAccount.getOrganisationUnits();

        /* convert them to set of ids */
        Set<String> ids = ModelUtils.toUidSet(assignedOrganisationUnits);

        /* get them through program controller */
        organisationUnitController.pull(strategy, ids);
    }
}
