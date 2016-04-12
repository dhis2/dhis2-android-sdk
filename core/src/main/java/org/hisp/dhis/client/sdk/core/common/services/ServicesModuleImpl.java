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
import org.hisp.dhis.client.sdk.core.dataelement.DataElementServiceImpl;
import org.hisp.dhis.client.sdk.core.event.EventService;
import org.hisp.dhis.client.sdk.core.event.EventServiceImpl;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitService;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitServiceImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramIndicatorService;
import org.hisp.dhis.client.sdk.core.program.ProgramIndicatorServiceImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleActionService;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleActionServiceImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleService;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleServiceImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableService;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableServiceImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramService;
import org.hisp.dhis.client.sdk.core.program.ProgramServiceImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementService;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementServiceImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionService;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionServiceImpl;
import org.hisp.dhis.client.sdk.core.program.ProgramStageService;
import org.hisp.dhis.client.sdk.core.program.ProgramStageServiceImpl;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeServiceImpl;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueService;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueService2;
import org.hisp.dhis.client.sdk.core.user.UserAccountService;
import org.hisp.dhis.client.sdk.core.user.UserAccountServiceImpl;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public final class ServicesModuleImpl implements ServicesModule {
    private final UserAccountService userAccountService;
    private final ProgramService programService;
    private final ProgramStageService programStageService;
    private final ProgramStageSectionService programStageSectionService;
    private final OrganisationUnitService organisationUnitService;
    private final EventService eventService;
    private final ProgramStageDataElementService programStageDataElementService;
    private final DataElementService dataElementService;
    private final TrackedEntityAttributeService trackedEntityAttributeService;
    private final ProgramIndicatorService programIndicatorService;
    private final ProgramRuleVariableService programRuleVariableService;
    private final ProgramRuleService programRuleService;
    private final ProgramRuleActionService programRuleActionService;
    private final TrackedEntityDataValueService trackedEntityDataValueService;

    public ServicesModuleImpl(PersistenceModule persistenceModule) {
        isNull(persistenceModule, "persistenceModule must not be null");

        userAccountService = new UserAccountServiceImpl(
                persistenceModule.getUserAccountStore());
        programService = new ProgramServiceImpl(
                persistenceModule.getProgramStore());
        programStageService = new ProgramStageServiceImpl(
                persistenceModule.getProgramStageStore());

        programStageDataElementService = new ProgramStageDataElementServiceImpl(
                persistenceModule.getProgramStageDataElementStore());

        programStageSectionService = new ProgramStageSectionServiceImpl(
                persistenceModule.getProgramStageSectionStore());

        programRuleService = new ProgramRuleServiceImpl(
                persistenceModule.getProgramRuleStore());

        programRuleActionService = new ProgramRuleActionServiceImpl(
                persistenceModule.getProgramRuleActionStore());

        programRuleVariableService = new ProgramRuleVariableServiceImpl(
                persistenceModule.getProgramRuleVariableStore());

        programIndicatorService = new ProgramIndicatorServiceImpl(
                persistenceModule.getProgramIndicatorStore());

        organisationUnitService = new OrganisationUnitServiceImpl(
                persistenceModule.getOrganisationUnitStore());

        eventService = new EventServiceImpl(
                persistenceModule.getEventStore(),
                persistenceModule.getStateStore());
        dataElementService = new DataElementServiceImpl(
                persistenceModule.getDataElementStore());

        trackedEntityAttributeService = new TrackedEntityAttributeServiceImpl(
                persistenceModule.getTrackedEntityAttributeStore());

        trackedEntityDataValueService = new TrackedEntityDataValueService2(
                persistenceModule.getTrackedEntityDataValueStore(),
                persistenceModule.getEventStore(),
                persistenceModule.getStateStore());
    }

    @Override
    public UserAccountService getUserAccountService() {
        return userAccountService;
    }

    @Override
    public ProgramService getProgramService() {
        return programService;
    }

    @Override
    public OrganisationUnitService getOrganisationUnitService() {
        return organisationUnitService;
    }

    @Override
    public ProgramStageService getProgramStageService() {
        return programStageService;
    }

    @Override
    public ProgramStageDataElementService getProgramStageDataElementService() {
        return programStageDataElementService;
    }

    public ProgramStageSectionService getProgramStageSectionService() {
        return programStageSectionService;
    }

    @Override
    public EventService getEventService() {
        return eventService;
    }

    @Override
    public DataElementService getDataElementService() {
        return dataElementService;
    }

    @Override
    public ProgramRuleService getProgramRuleService() {
        return programRuleService;
    }

    @Override
    public ProgramRuleActionService getProgramRuleActionService() {
        return programRuleActionService;
    }

    @Override
    public ProgramRuleVariableService getProgramRuleVariableService() {
        return programRuleVariableService;
    }

    @Override
    public ProgramIndicatorService getProgramIndicatorService() {
        return programIndicatorService;
    }

    @Override
    public TrackedEntityAttributeService getTrackedEntityAttributeService() {
        return trackedEntityAttributeService;
    }

    @Override
    public TrackedEntityDataValueService getTrackedEntityDataValueService() {
        return null;
    }
}
