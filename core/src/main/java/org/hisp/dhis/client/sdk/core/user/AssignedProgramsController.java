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
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitController;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitStore;
import org.hisp.dhis.client.sdk.core.program.IProgramController;
import org.hisp.dhis.client.sdk.core.program.IProgramStore;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoApiClient;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AssignedProgramsController implements IAssignedProgramsController {
    private final IOrganisationUnitController organisationUnitController;
    private final IProgramController programController;
    private final ITransactionManager transactionManager;
    private final IOrganisationUnitStore organisationUnitStore;
    private final IProgramStore programStore;
    private final IUserApiClient userApiClient;

    private final ILastUpdatedPreferences lastUpdatedPreferences;
    private final ISystemInfoApiClient systemInfoApiClient;


    public AssignedProgramsController(IProgramController programController,
                                      IOrganisationUnitController organisationUnitController,
                                      IOrganisationUnitStore organisationUnitStore,
                                      IProgramStore programStore,
                                      ITransactionManager transactionManager,
                                      IUserApiClient userApiClient,
                                      ILastUpdatedPreferences lastUpdatedPreferences,
                                      ISystemInfoApiClient systemInfoApiClient) {
        this.transactionManager = transactionManager;
        this.userApiClient = userApiClient;
        this.lastUpdatedPreferences = lastUpdatedPreferences;
        this.programController = programController;
        this.organisationUnitController = organisationUnitController;
        this.organisationUnitStore = organisationUnitStore;
        this.programStore = programStore;

        this.systemInfoApiClient = systemInfoApiClient;
    }

    @Override
    public void sync() throws ApiException {
        getAssignedProgramsDataFromServer();
    }

    private void getAssignedProgramsDataFromServer() throws ApiException {
        DateTime serverTime = systemInfoApiClient.getSystemInfo().getServerDate();
        List<OrganisationUnit> organisationUnitsWithAssignedPrograms = null;
//                = userApiClient.getUserAccount();

        Set<String> organisationUnitsToLoad = ModelUtils
                .toUidSet(organisationUnitsWithAssignedPrograms);
        Set<String> programsToLoad = new HashSet<>();
        for (OrganisationUnit organisationUnit : organisationUnitsWithAssignedPrograms) {
            programsToLoad.addAll(ModelUtils.toUidSet(organisationUnit.getPrograms()));
        }

        // Load the programs and organisation units from server with full data
        organisationUnitController.sync(organisationUnitsToLoad);
        programController.sync(programsToLoad);

        Map<Program, Set<OrganisationUnit>> programToUnits = reverseRelationship
                (organisationUnitsWithAssignedPrograms);
        for (Program program : programToUnits.keySet()) {
            Set<OrganisationUnit> units = programToUnits.get(program);
            // programStore.assign(program, units);
        }
    }

    private Map<Program, Set<OrganisationUnit>> reverseRelationship(
            List<OrganisationUnit> organisationUnitsWithAssignedPrograms) {
        Map<Program, Set<OrganisationUnit>> programToOrganisationUnitsMap = new HashMap<>();
        for (OrganisationUnit unit : organisationUnitsWithAssignedPrograms) {
            List<Program> assignedUnitPrograms = unit.getPrograms();

            if (assignedUnitPrograms == null) {
                continue;
            }

            for (Program program : assignedUnitPrograms) {
                if (!programToOrganisationUnitsMap.containsKey(program)) {
                    programToOrganisationUnitsMap.put(program, new HashSet<OrganisationUnit>());
                }

                programToOrganisationUnitsMap.get(program).add(unit);
            }
        }

        return programToOrganisationUnitsMap;
    }
}