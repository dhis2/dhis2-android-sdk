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

package org.hisp.dhis.client.sdk.core.common.services;

import org.hisp.dhis.client.sdk.core.common.persistence.PersistenceModule;
import org.hisp.dhis.client.sdk.core.dataelement.DataElementService;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementService;
import org.hisp.dhis.client.sdk.core.event.EventService;
import org.hisp.dhis.client.sdk.core.event.IEventService;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitService;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitService;
import org.hisp.dhis.client.sdk.core.program.IProgramIndicatorService;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleActionService;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleService;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleVariableService;
import org.hisp.dhis.client.sdk.core.program.IProgramService;
import org.hisp.dhis.client.sdk.core.program.IProgramStageDataElementService;
import org.hisp.dhis.client.sdk.core.program.IProgramStageSectionService;
import org.hisp.dhis.client.sdk.core.program.IProgramStageService;
import org.hisp.dhis.client.sdk.core.program.ProgramIndicatorService;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleActionService;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleService;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableService;
import org.hisp.dhis.client.sdk.core.program.ProgramService;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementService;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionService;
import org.hisp.dhis.client.sdk.core.program.ProgramStageService;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeService;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityDataValueService;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueService2;
import org.hisp.dhis.client.sdk.core.user.IUserAccountService;
import org.hisp.dhis.client.sdk.core.user.UserAccountService;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

public final class ServicesModule implements IServicesModule {
    private final IUserAccountService userAccountService;
    private final IProgramService programService;
    private final IProgramStageService programStageService;
    private final IProgramStageSectionService programStageSectionService;
    private final IOrganisationUnitService organisationUnitService;
    private final IEventService eventService;
    private final IProgramStageDataElementService programStageDataElementService;
    private final IDataElementService dataElementService;
    private final ITrackedEntityAttributeService trackedEntityAttributeService;
    private final IProgramIndicatorService programIndicatorService;
    private final IProgramRuleVariableService programRuleVariableService;
    private final IProgramRuleService programRuleService;
    private final IProgramRuleActionService programRuleActionService;
    private final ITrackedEntityDataValueService trackedEntityDataValueService;

    public ServicesModule(PersistenceModule persistenceModule) {
        isNull(persistenceModule, "persistenceModule must not be null");

        userAccountService = new UserAccountService(
                persistenceModule.getUserAccountStore());
        programService = new ProgramService(
                persistenceModule.getProgramStore());
        programStageService = new ProgramStageService(
                persistenceModule.getProgramStageStore());

        programStageDataElementService = new ProgramStageDataElementService(
                persistenceModule.getProgramStageDataElementStore());

        programStageSectionService = new ProgramStageSectionService(
                persistenceModule.getProgramStageSectionStore());

        programRuleService = new ProgramRuleService(
                persistenceModule.getProgramRuleStore());

        programRuleActionService = new ProgramRuleActionService(
                persistenceModule.getProgramRuleActionStore());

        programRuleVariableService = new ProgramRuleVariableService(
                persistenceModule.getProgramRuleVariableStore());

        programIndicatorService = new ProgramIndicatorService(
                persistenceModule.getProgramIndicatorStore());

        organisationUnitService = new OrganisationUnitService(
                persistenceModule.getOrganisationUnitStore());

        eventService = new EventService(
                persistenceModule.getEventStore(),
                persistenceModule.getStateStore());
        dataElementService = new DataElementService(
                persistenceModule.getDataElementStore());

        trackedEntityAttributeService = new TrackedEntityAttributeService(
                persistenceModule.getTrackedEntityAttributeStore());

        trackedEntityDataValueService = new TrackedEntityDataValueService2(
                persistenceModule.getTrackedEntityDataValueStore(),
                persistenceModule.getEventStore(),
                persistenceModule.getStateStore());
    }

    @Override
    public IUserAccountService getUserAccountService() {
        return userAccountService;
    }

    @Override
    public IProgramService getProgramService() {
        return programService;
    }

    @Override
    public IOrganisationUnitService getOrganisationUnitService() {
        return organisationUnitService;
    }

    @Override
    public IProgramStageService getProgramStageService() {
        return programStageService;
    }

    @Override
    public IProgramStageDataElementService getProgramStageDataElementService() {
        return programStageDataElementService;
    }

    public IProgramStageSectionService getProgramStageSectionService() {
        return programStageSectionService;
    }

    @Override
    public IEventService getEventService() {
        return eventService;
    }

    @Override
    public IDataElementService getDataElementService() {
        return dataElementService;
    }

    @Override
    public IProgramRuleService getProgramRuleService() {
        return programRuleService;
    }

    @Override
    public IProgramRuleActionService getProgramRuleActionService() {
        return programRuleActionService;
    }

    @Override
    public IProgramRuleVariableService getProgramRuleVariableService() {
        return programRuleVariableService;
    }

    @Override
    public IProgramIndicatorService getProgramIndicatorService() {
        return programIndicatorService;
    }

    @Override
    public ITrackedEntityAttributeService getTrackedEntityAttributeService() {
        return trackedEntityAttributeService;
    }

    @Override
    public ITrackedEntityDataValueService getTrackedEntityDataValueService() {
        return null;
    }
}
