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

package org.hisp.dhis.client.sdk.core.common.controllers;

import org.hisp.dhis.client.sdk.core.common.network.INetworkModule;
import org.hisp.dhis.client.sdk.core.common.persistence.IPersistenceModule;
import org.hisp.dhis.client.sdk.core.common.preferences.IPreferencesModule;
import org.hisp.dhis.client.sdk.core.dataelement.DataElementController;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementController;
import org.hisp.dhis.client.sdk.core.event.EventController;
import org.hisp.dhis.client.sdk.core.event.IEventController;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitController;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitController;
import org.hisp.dhis.client.sdk.core.program.IProgramController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageDataElementController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageSectionController;
import org.hisp.dhis.client.sdk.core.program.ProgramController;
import org.hisp.dhis.client.sdk.core.program.ProgramStageController;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementController;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionController;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoController;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.core.user.AssignedOrganisationUnitController;
import org.hisp.dhis.client.sdk.core.user.AssignedProgramsController;
import org.hisp.dhis.client.sdk.core.user.IAssignedOrganisationUnitsController;
import org.hisp.dhis.client.sdk.core.user.IAssignedProgramsController;
import org.hisp.dhis.client.sdk.core.user.IUserAccountController;
import org.hisp.dhis.client.sdk.core.user.UserAccountController;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

public class ControllersModule implements IControllersModule {
    private final ISystemInfoController systemInfoController;
    private final IUserAccountController userAccountController;
    private final IProgramController programController;
    private final IProgramStageController programStageController;
    private final IProgramStageSectionController programStageSectionController;
    private final IOrganisationUnitController organisationUnitController;
    private final IAssignedProgramsController assignedProgramsController;
    private final IAssignedOrganisationUnitsController assignedOrganisationUnitsController;
    private final IEventController eventController;
    private final IDataElementController dataElementController;
    private final IProgramStageDataElementController programStageDataElementController;

    public ControllersModule(INetworkModule networkModule,
                             IPersistenceModule persistenceModule,
                             IPreferencesModule preferencesModule) {
        isNull(networkModule, "networkModule must not be null");
        isNull(persistenceModule, "persistenceModule must not be null");
        isNull(preferencesModule, "preferencesModule must not be null");

        systemInfoController = new SystemInfoController(
                networkModule.getSystemInfoApiClient(),
                preferencesModule.getSystemInfoPreferences(),
                preferencesModule.getLastUpdatedPreferences());

        programController = new ProgramController(networkModule.getProgramApiClient(),
                networkModule.getUserApiClient(), persistenceModule.getProgramStore(),
                systemInfoController, persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences());

        programStageController = new ProgramStageController(
                networkModule.getSystemInfoApiClient(),
                networkModule.getProgramStageApiClient(),
                persistenceModule.getProgramStageStore(),
                programController, persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences());

        dataElementController = new DataElementController(
                networkModule.getDataElementApiClient(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getDataElementStore(),
                systemInfoController, persistenceModule.getTransactionManager());


        programStageDataElementController = new ProgramStageDataElementController(
                networkModule.getSystemInfoApiClient(),
                networkModule.getProgramStageDataElementApiClient(),
                persistenceModule.getProgramStageDataElementStore(),
                dataElementController,
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences());

        programStageSectionController = new ProgramStageSectionController(
                networkModule.getProgramStageSectionApiClient(),
                persistenceModule.getProgramStageSectionStore(),
                programStageController,
                programStageDataElementController,
                systemInfoController,
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences());

        assignedProgramsController = new AssignedProgramsController(
                networkModule.getUserApiClient(), programController);

        organisationUnitController = new OrganisationUnitController(
                networkModule.getOrganisationUnitApiClient(),
                networkModule.getUserApiClient(),
                persistenceModule.getOrganisationUnitStore(),
                preferencesModule.getLastUpdatedPreferences(), systemInfoController,
                persistenceModule.getTransactionManager());

        assignedOrganisationUnitsController = new AssignedOrganisationUnitController(
                networkModule.getUserApiClient(), organisationUnitController);

        userAccountController = new UserAccountController(
                networkModule.getUserApiClient(),
                persistenceModule.getUserAccountStore());

        eventController = new EventController(
                networkModule.getEventApiClient(),
                networkModule.getSystemInfoApiClient(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getTransactionManager(),
                null, //persistenceModule.getStateStore(),
                persistenceModule.getEventStore(),
                null, //persistenceModule.getTrackedEntityDataValueStore(),
                persistenceModule.getOrganisationUnitStore(),
                persistenceModule.getProgramStore(),
                null //persistenceModule.getFailedItemStore()
        );




        /*

        IEventApiClient eventApiClient,
                   ISystemInfoApiClient systemInfoApiClient,
                   ILastUpdatedPreferences lastUpdatedPreferences,
                   ITransactionManager transactionManager,
                   IStateStore stateStore, IEventStore eventStore,
                   ITrackedEntityDataValueStore trackedEntityDataValueStore,
                   IOrganisationUnitStore organisationUnitStore, IProgramStore programStore,
                   IFailedItemStore failedItemStore)
         */

    }

    @Override
    public ISystemInfoController getSystemInfoController() {
        return systemInfoController;
    }

    @Override
    public IUserAccountController getUserAccountController() {
        return userAccountController;
    }

    @Override
    public IProgramController getProgramController() {
        return programController;
    }

    @Override
    public IProgramStageController getProgramStageController() {
        return programStageController;
    }

    @Override
    public IProgramStageSectionController getProgramStageSectionController() {
        return programStageSectionController;
    }

    @Override
    public IOrganisationUnitController getOrganisationUnitController() {
        return organisationUnitController;
    }

    @Override
    public IAssignedProgramsController getAssignedProgramsController() {
        return assignedProgramsController;
    }

    @Override
    public IAssignedOrganisationUnitsController getAssignedOrganisationUnitsController() {
        return assignedOrganisationUnitsController;
    }

    @Override
    public IEventController getEventController() {
        return eventController;
    }

    @Override
    public IDataElementController getDataElementController() {
        return dataElementController;
    }

    @Override
    public IProgramStageDataElementController getProgramStageDataElementController() {
        return programStageDataElementController;
    }
}
