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

import org.hisp.dhis.client.sdk.core.common.ILogger;
import org.hisp.dhis.client.sdk.core.common.network.INetworkModule;
import org.hisp.dhis.client.sdk.core.common.persistence.IPersistenceModule;
import org.hisp.dhis.client.sdk.core.common.preferences.IPreferencesModule;
import org.hisp.dhis.client.sdk.core.dataelement.DataElementController;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementController;
import org.hisp.dhis.client.sdk.core.event.EventController;
import org.hisp.dhis.client.sdk.core.event.IEventController;
import org.hisp.dhis.client.sdk.core.optionset.IOptionSetController;
import org.hisp.dhis.client.sdk.core.optionset.OptionSetController;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitController;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitController;
import org.hisp.dhis.client.sdk.core.program.IProgramController;
import org.hisp.dhis.client.sdk.core.program.IProgramIndicatorController;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleActionController;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleController;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleVariableController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageDataElementController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageSectionController;
import org.hisp.dhis.client.sdk.core.program.ProgramController;
import org.hisp.dhis.client.sdk.core.program.ProgramIndicatorController;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleActionController;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleController;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableController;
import org.hisp.dhis.client.sdk.core.program.ProgramStageController;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementController;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionController;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoController;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoController;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeController;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeController;
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
    private final IProgramRuleController programRuleController;
    private final IProgramRuleActionController programRuleActionController;
    private final IProgramRuleVariableController programRuleVariableController;
    private final IProgramIndicatorController programIndicatorController;
    private final IProgramStageDataElementController programStageDataElementController;
    private final IOrganisationUnitController organisationUnitController;
    private final IAssignedProgramsController assignedProgramsController;
    private final IAssignedOrganisationUnitsController assignedOrganisationUnitsController;
    private final IEventController eventController;
    private final IDataElementController dataElementController;
    private final IOptionSetController optionSetController;
    private final ITrackedEntityAttributeController trackedEntityAttributeController;

    public ControllersModule(INetworkModule networkModule,
                             IPersistenceModule persistenceModule,
                             IPreferencesModule preferencesModule, ILogger logger) {
        isNull(networkModule, "networkModule must not be null");
        isNull(persistenceModule, "persistenceModule must not be null");
        isNull(preferencesModule, "preferencesModule must not be null");
        isNull(logger, "ILogger must not be null");

        systemInfoController = new SystemInfoController(
                networkModule.getSystemInfoApiClient(),
                preferencesModule.getSystemInfoPreferences(),
                preferencesModule.getLastUpdatedPreferences());

        programController = new ProgramController(systemInfoController,
                networkModule.getProgramApiClient(),
                networkModule.getUserApiClient(), persistenceModule.getProgramStore(),
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences());

        programStageController = new ProgramStageController(
                programController, systemInfoController,
                networkModule.getProgramStageApiClient(),
                persistenceModule.getProgramStageStore(),
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences());

        programStageSectionController = new ProgramStageSectionController(
                programStageController, systemInfoController,
                networkModule.getProgramStageSectionApiClient(),
                persistenceModule.getProgramStageSectionStore(),
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences());

        optionSetController = new OptionSetController(
                systemInfoController,
                networkModule.getOptionSetApiClient(),
                persistenceModule.getOptionStore(),
                persistenceModule.getOptionSetStore(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getTransactionManager());

        dataElementController = new DataElementController(
                systemInfoController, optionSetController,
                networkModule.getDataElementApiClient(),
                persistenceModule.getDataElementStore(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getTransactionManager());

        programStageDataElementController = new ProgramStageDataElementController(
                systemInfoController, programStageController,
                programStageSectionController, dataElementController,
                networkModule.getProgramStageSectionApiClient(),
                networkModule.getProgramStageDataElementApiClient(),
                persistenceModule.getProgramStageDataElementStore(),
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences());

        programRuleController = new ProgramRuleController(
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getProgramRuleStore(),
                systemInfoController,
                networkModule.getProgramRuleApiClient(),
                programController,
                programStageController);


        assignedProgramsController = new AssignedProgramsController(
                programController, networkModule.getUserApiClient());

        organisationUnitController = new OrganisationUnitController(
                systemInfoController, networkModule.getOrganisationUnitApiClient(),
                networkModule.getUserApiClient(),
                persistenceModule.getOrganisationUnitStore(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getTransactionManager());

        assignedOrganisationUnitsController = new AssignedOrganisationUnitController(
                networkModule.getUserApiClient(), organisationUnitController);

        userAccountController = new UserAccountController(
                networkModule.getUserApiClient(),
                persistenceModule.getUserAccountStore());


        trackedEntityAttributeController = new TrackedEntityAttributeController(
                networkModule.getTrackedEntityAttributeApiClient(),
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getTrackedEntityAttributeStore(),
                systemInfoController,
                optionSetController);

        programIndicatorController = new ProgramIndicatorController(
                persistenceModule.getProgramIndicatorStore(),
                preferencesModule.getLastUpdatedPreferences(),
                systemInfoController,
                networkModule.getProgramIndicatorApiClient(),
                persistenceModule.getTransactionManager(),
                programController,
                programStageController,
                programStageSectionController);

        programRuleVariableController = new ProgramRuleVariableController(
                networkModule.getProgramRuleVariableApiClient(),
                persistenceModule.getTransactionManager(),
                preferencesModule.getLastUpdatedPreferences(),
                systemInfoController,
                persistenceModule.getProgramRuleVariableStore(),
                programController,
                programStageController,
                dataElementController,
                trackedEntityAttributeController);

        programRuleActionController = new ProgramRuleActionController(
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

        eventController = new EventController(systemInfoController,

                networkModule.getEventApiClient(),
                preferencesModule.getLastUpdatedPreferences(),
                persistenceModule.getEventStore(),
                persistenceModule.getStateStore(),
                persistenceModule.getTransactionManager(), logger);
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

    @Override
    public IProgramRuleController getProgramRuleController() {
        return programRuleController;
    }

    @Override
    public IProgramRuleActionController getProgramRuleActionController() {
        return programRuleActionController;
    }

    @Override
    public IProgramRuleVariableController getProgramRuleVariableController() {
        return programRuleVariableController;
    }

    @Override
    public IProgramIndicatorController getProgramIndicatorController() {
        return programIndicatorController;
    }

    @Override
    public ITrackedEntityAttributeController getTrackedEntityAttributeController() {
        return trackedEntityAttributeController;
    }

    @Override
    public IOptionSetController getOptionSetController() {
        return optionSetController;
    }
}
