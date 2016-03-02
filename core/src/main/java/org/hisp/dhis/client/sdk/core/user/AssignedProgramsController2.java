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

import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.program.IProgramController;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.user.UserAccount;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;

import java.util.List;
import java.util.Set;

/**
 * This class is inteded to build relationships between organisation units and programs.
 */
public class AssignedProgramsController2 implements IAssignedProgramsController {
    /* Api clients */
    private final IUserApiClient userApiClient;

    /* Program controller */
    private final IProgramController programController;

    public AssignedProgramsController2(IUserApiClient userApiClient,
                                       IProgramController programController) {
        this.userApiClient = userApiClient;
        this.programController = programController;
    }

    @Override
    public void sync() throws ApiException {
        UserAccount userAccount = userApiClient.getUserAccount();

        /* get list of assigned programs */
        List<Program> assignedPrograms = userAccount.getPrograms();

        /* convert them to set of ids */
        Set<String> ids = ModelUtils.toUidSet(assignedPrograms);

        /* get them through program controller */
        programController.sync(ids);
    }


    /* we need only list of unit ids */
    // List<OrganisationUnit> assignedOrganisationUnits = userAccount.getOrganisationUnits();

        /* and list of program ids to download */
        /* further we will mark programs as assigned */


    // * fetch user credentials, filter out programs from organisation
    //   units to which current user does not have access to

    // * pass down information about organisation units and
    //   programs to corresponding controllers which will take
    //   care of syncing other properties of model.


    // * Consider granular approach for syncing to monolith abomination:
    //    - Consider different API for work with resources:
    //       - For example, D2.organisationUnits.sync(). It should be fetch only basic
    //         information about organisation units with connections to other resources
    //         (links to related elements) If ou would like to get particular resource
    //         like programs, then you have to extract all program ids from organisation
    //         units and pass it down to D2.programs.sync(List<String> ids). This call should
    //         do the same, fetch only connections ann direct properties of the model.
    //         The complex part here is that you have to respect previous sync results
    //         (for example we can call programs.sync() first and only aftwer that a method
    //         with specified list of program ids. But this approach seems to be a way to go.

    // You should also give an option to select, which fields exactly
    // you want to sync (for example Fields.BASIC, Fields.ALL);

        /*
        // will include only links to other resource which
        // might not exist in local database

        D2.me().organisationUnits().sync(Fields.BASIC);

        // Get all related fields (it will include all directly associated properties)
        // TODO try to avoid nested properties, they don't support translations

        // D2.me().programs().sync(Fields.ALL); DON'T DO THIS!

        // D2.me().programs().sync();
           - Get list of assigned program ids
           - Pass this list to program controller
           - Sync this list of programs to database

        // D2.me().organisationUnits().sync();
          - Get list of assigned organisation units with links to other resources
          - Pass this list ot organisation unit controller
          -

        List<Program> programs = D2.me().programs().list();
        // get program stage ids from programs

        D2.programStages().sync(List<String> programStageIds);
        D2.programSections()
         */
}
