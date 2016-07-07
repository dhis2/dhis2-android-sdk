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

import org.hisp.dhis.client.sdk.core.common.network.NetworkModule;
import org.hisp.dhis.client.sdk.core.common.persistence.PersistenceModule;
import org.hisp.dhis.client.sdk.core.common.preferences.PreferencesModule;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardController;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardControllerImpl;
import org.hisp.dhis.client.sdk.core.dataelement.DataElementController;
import org.hisp.dhis.client.sdk.core.dataelement.DataElementControllerImpl;
import org.hisp.dhis.client.sdk.core.event.EventController;
import org.hisp.dhis.client.sdk.core.event.EventControllerImpl;
import org.hisp.dhis.client.sdk.core.optionset.OptionSetController;
import org.hisp.dhis.client.sdk.core.optionset.OptionSetControllerImpl;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitController;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitControllerImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramController;
import org.hisp.dhis.client.sdk.core.program.ProgramControllerImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramIndicatorController;
import org.hisp.dhis.client.sdk.core.program.ProgramIndicatorControllerImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleActionController;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleActionControllerImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleController;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleControllerImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableController;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableControllerImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramStageController;
import org.hisp.dhis.client.sdk.core.program.ProgramStageControllerImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementController;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementControllerImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionController;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionControllerImpl;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoControllerImpl;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeController;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeControllerImpl;
import org.hisp.dhis.client.sdk.core.user.AssignedOrganisationUnitControllerImpl;
import org.hisp.dhis.client.sdk.core.user.AssignedOrganisationUnitsController;
import org.hisp.dhis.client.sdk.core.user.AssignedProgramsController;
import org.hisp.dhis.client.sdk.core.user.AssignedProgramsControllerImpl;
import org.hisp.dhis.client.sdk.core.user.UserAccountController;
import org.hisp.dhis.client.sdk.core.user.UserAccountControllerImpl;
import org.hisp.dhis.client.sdk.utils.Logger;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class ControllersModuleImpl implements ControllersModule {
    private final SystemInfoController systemInfoController;
    private final UserAccountController userAccountController;
    private final ProgramController programController;
    private final ProgramStageController programStageController;
    private final ProgramStageSectionController programStageSectionController;
    private final ProgramRuleController programRuleController;
    private final ProgramRuleActionController programRuleActionController;
    private final ProgramRuleVariableController programRuleVariableController;
    private final ProgramIndicatorController programIndicatorController;
    private final ProgramStageDataElementController programStageDataElementController;
    private final OrganisationUnitController organisationUnitController;
    private final AssignedProgramsController assignedProgramsController;
    private final AssignedOrganisationUnitsController assignedOrganisationUnitsController;
    private final DataElementController dataElementController;
    private final OptionSetController optionSetController;
    private final TrackedEntityAttributeController trackedEntityAttributeController;
    private final EventController eventController;
    private final DashboardController dashboardController;

    public ControllersModuleImpl(NetworkModule networkModule,
                                 PersistenceModule persistenceModule,
                                 PreferencesModule preferencesModule, Logger logger) {
        isNull(networkModule, "networkModule must not be null");
        isNull(persistenceModule, "persistenceModule must not be null");
        isNull(preferencesModule, "preferencesModule must not be null");
        isNull(logger, "Logger must not be null");

        systemInfoController = new SystemInfoControllerImpl(
                networkModule.getSystemInfoApiClient(),
                preferencesModule.getSystemInfoPreferences(),
                preferencesModule.getLastUpdatedPreferences());

        programController = new ProgramControllerImpl(systemInfoController,
                networkModule.getProgramApiClient(),
                networkModule.getUserApiClient(), persistenceModule.getProgramStore(),
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences());

        programStageController = new ProgramStageControllerImpl(
                programController, systemInfoController,
                networkModule.getProgramStageApiClient(),
                persistenceModule.getProgramStageStore(),
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences());

        programStageSectionController = new ProgramStageSectionControllerImpl(
                programStageController, systemInfoController,
                networkModule.getProgramStageSectionApiClient(),
                persistenceModule.getProgramStageSectionStore(),
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences());

        optionSetController = new OptionSetControllerImpl(
                systemInfoController,
                networkModule.getOptionSetApiClient(),
                persistenceModule.getOptionStore(),
                persistenceModule.getOptionSetStore(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getTransactionManager());

        dataElementController = new DataElementControllerImpl(
                systemInfoController, optionSetController,
                networkModule.getDataElementApiClient(),
                persistenceModule.getDataElementStore(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getTransactionManager());

        programStageDataElementController = new ProgramStageDataElementControllerImpl(
                systemInfoController, programStageController,
                programStageSectionController, dataElementController,
                networkModule.getProgramStageSectionApiClient(),
                networkModule.getProgramStageDataElementApiClient(),
                persistenceModule.getProgramStageDataElementStore(),
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences());

        programRuleController = new ProgramRuleControllerImpl(
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getProgramRuleStore(),
                systemInfoController,
                networkModule.getProgramRuleApiClient(),
                programController,
                programStageController);


        assignedProgramsController = new AssignedProgramsControllerImpl(
                programController, networkModule.getUserApiClient());

        organisationUnitController = new OrganisationUnitControllerImpl(
                systemInfoController, networkModule.getOrganisationUnitApiClient(),
                networkModule.getUserApiClient(),
                persistenceModule.getOrganisationUnitStore(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getTransactionManager());

        assignedOrganisationUnitsController = new AssignedOrganisationUnitControllerImpl(
                networkModule.getUserApiClient(), organisationUnitController);

        userAccountController = new UserAccountControllerImpl(
                networkModule.getUserApiClient(),
                persistenceModule.getUserAccountStore(),
                persistenceModule.getStateStore(), logger);

        trackedEntityAttributeController = new TrackedEntityAttributeControllerImpl(
                networkModule.getTrackedEntityAttributeApiClient(),
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getTrackedEntityAttributeStore(),
                systemInfoController,
                optionSetController);

        programIndicatorController = new ProgramIndicatorControllerImpl(
                persistenceModule.getProgramIndicatorStore(),
                preferencesModule.getLastUpdatedPreferences(),
                systemInfoController,
                networkModule.getProgramIndicatorApiClient(),
                persistenceModule.getTransactionManager(),
                programController,
                programStageController,
                programStageSectionController);

        programRuleVariableController = new ProgramRuleVariableControllerImpl(
                networkModule.getProgramRuleVariableApiClient(),
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences(),
                systemInfoController,
                persistenceModule.getProgramRuleVariableStore(),
                programController,
                programStageController,
                dataElementController,
                trackedEntityAttributeController);

        programRuleActionController = new ProgramRuleActionControllerImpl(
                networkModule.getProgramRuleActionApiClient(),
                persistenceModule.getTransactionManager(),
                systemInfoController,
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getProgramRuleActionStore(),
                programStageController,
                programStageSectionController,
                dataElementController,
                trackedEntityAttributeController,
                programRuleController,
                programIndicatorController);

        eventController = new EventControllerImpl(systemInfoController,

                networkModule.getEventApiClient(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getEventStore(),
                persistenceModule.getStateStore(),
                persistenceModule.getTransactionManager(), logger);

        dashboardController = new DashboardControllerImpl(systemInfoController,
                persistenceModule.getDashboardStore(),
                persistenceModule.getDashboardItemStore(),
                persistenceModule.getDashboardElementStore(),
                persistenceModule.getDashboardContentStore(),
                persistenceModule.getStateStore(),
                networkModule.getDashboardApiClient(),
                networkModule.getSystemInfoApiClient(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getTransactionManager(), logger);

    }

    @Override
    public SystemInfoController getSystemInfoController() {
        return systemInfoController;
    }

    @Override
    public UserAccountController getUserAccountController() {
        return userAccountController;
    }

    @Override
    public ProgramController getProgramController() {
        return programController;
    }

    @Override
    public ProgramStageController getProgramStageController() {
        return programStageController;
    }

    @Override
    public ProgramStageSectionController getProgramStageSectionController() {
        return programStageSectionController;
    }

    @Override
    public OrganisationUnitController getOrganisationUnitController() {
        return organisationUnitController;
    }

    @Override
    public AssignedProgramsController getAssignedProgramsController() {
        return assignedProgramsController;
    }

    @Override
    public AssignedOrganisationUnitsController getAssignedOrganisationUnitsController() {
        return assignedOrganisationUnitsController;
    }

    @Override
    public EventController getEventController() {
        return eventController;
    }

    @Override
    public DashboardController getDashboardController() {
        return dashboardController;
    }

    @Override
    public DataElementController getDataElementController() {
        return dataElementController;
    }

    @Override
    public ProgramStageDataElementController getProgramStageDataElementController() {
        return programStageDataElementController;
    }

    @Override
    public ProgramRuleController getProgramRuleController() {
        return programRuleController;
    }

    @Override
    public ProgramRuleActionController getProgramRuleActionController() {
        return programRuleActionController;
    }

    @Override
    public ProgramRuleVariableController getProgramRuleVariableController() {
        return programRuleVariableController;
    }

    @Override
    public ProgramIndicatorController getProgramIndicatorController() {
        return programIndicatorController;
    }

    @Override
    public TrackedEntityAttributeController getTrackedEntityAttributeController() {
        return trackedEntityAttributeController;
    }

    @Override
    public OptionSetController getOptionSetController() {
        return optionSetController;
    }
}
