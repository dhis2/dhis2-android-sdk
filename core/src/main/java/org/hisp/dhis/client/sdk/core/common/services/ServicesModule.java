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

import org.hisp.dhis.client.sdk.core.common.persistence.IPersistenceModule;
import org.hisp.dhis.client.sdk.core.constant.ConstantService;
import org.hisp.dhis.client.sdk.core.constant.IConstantService;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardService;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardService;
import org.hisp.dhis.client.sdk.core.dataelement.DataElementService;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementService;
import org.hisp.dhis.client.sdk.core.enrollment.EnrollmentService;
import org.hisp.dhis.client.sdk.core.enrollment.IEnrollmentService;
import org.hisp.dhis.client.sdk.core.event.EventService;
import org.hisp.dhis.client.sdk.core.event.IEventService;
import org.hisp.dhis.client.sdk.core.optionset.IOptionSetService;
import org.hisp.dhis.client.sdk.core.optionset.OptionSetService;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitService;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitService;
import org.hisp.dhis.client.sdk.core.program.*;
import org.hisp.dhis.client.sdk.core.relationship.IRelationshipService;
import org.hisp.dhis.client.sdk.core.relationship.IRelationshipTypeService;
import org.hisp.dhis.client.sdk.core.relationship.RelationshipService;
import org.hisp.dhis.client.sdk.core.relationship.RelationshipTypeService;
import org.hisp.dhis.client.sdk.core.trackedentity.*;
import org.hisp.dhis.client.sdk.core.user.IUserAccountService;
import org.hisp.dhis.client.sdk.core.user.UserAccountService;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;

public final class ServicesModule implements IServicesModule {
    private final IDashboardService dashboardService;
    private final IUserAccountService userAccountService;
    private final IProgramService programService;
    private final IOrganisationUnitService organisationUnitService;
    private final IEventService eventService;
    private final IConstantService constantService;
    private final IDataElementService dataElementService;
    private final IEnrollmentService enrollmentService;
    private final IOptionSetService optionSetService;
    private final IProgramStageService programStageService;
    private final IProgramRuleService programRuleService;
    private final IProgramIndicatorService programIndicatorService;
    private final IProgramTrackedEntityAttributeService programTrackedEntityAttributeService;
    private final ITrackedEntityService trackedEntityService;
    private final IProgramStageDataElementService programStageDataElementService;
    private final IProgramStageSectionService programStageSectionService;
    private final IRelationshipService relationshipService;
    private final IRelationshipTypeService relationshipTypeService;
    private final ITrackedEntityAttributeService trackedEntityAttributeService;
    private final ITrackedEntityAttributeValueService trackedEntityAttributeValueService;
    private final ITrackedEntityDataValueService trackedEntityDataValueService;

    public ServicesModule(IPersistenceModule persistenceModule) {
        Preconditions.isNull(persistenceModule, "persistenceModule must not be null");

        dashboardService = new DashboardService(
                persistenceModule.getDashboardStore(),
                persistenceModule.getDashboardItemStore(),
                persistenceModule.getDashboardElementStore(),
                persistenceModule.getStateStore(), null, null);

        userAccountService = new UserAccountService(
                persistenceModule.getUserAccountStore(),
                persistenceModule.getModelStore());

        programService = new ProgramService(
                persistenceModule.getProgramStore());

        organisationUnitService = new OrganisationUnitService(
                persistenceModule.getOrganisationUnitStore());

        eventService = new EventService(
                persistenceModule.getEventStore(),
                persistenceModule.getStateStore());

        constantService = new ConstantService(
                persistenceModule.getConstantStore());

        dataElementService = new DataElementService(
                persistenceModule.getDataElementStore());

        enrollmentService = new EnrollmentService(
                persistenceModule.getEnrollmentStore(),
                persistenceModule.getStateStore(),
                this.getEventService()
        );

        optionSetService =  new OptionSetService(
                persistenceModule.getOptionSetStore(),
                persistenceModule.getOptionStore()
                );

        programStageService = new ProgramStageService(
                persistenceModule.getProgramStageStore());

        programRuleService = new ProgramRuleService(
                persistenceModule.getProgramRuleStore());

        programStageDataElementService = new ProgramStageDataElementService(
                persistenceModule.getProgramStageDataElementStore()
                );

        programStageSectionService = new ProgramStageSectionService(
                persistenceModule.getProgramStageSectionStore()
        );

        programIndicatorService = new ProgramIndicatorService(
                persistenceModule.getProgramIndicatorStore());

        programTrackedEntityAttributeService = new ProgramTrackedEntityAttributeService(
                persistenceModule.getProgramTrackedEntityAttributeStore());

        relationshipService = new RelationshipService(
                persistenceModule.getRelationshipStore(),
                persistenceModule.getStateStore()
        );

        relationshipTypeService = new RelationshipTypeService(
                persistenceModule.getRelationshipTypeStore()
        );

        trackedEntityAttributeService = new TrackedEntityAttributeService(
                persistenceModule.getTrackedEntityAttributeStore()
        );

        trackedEntityAttributeValueService = new TrackedEntityAttributeValueService(
                persistenceModule.getTrackedEntityAttributeValueStore(),
                persistenceModule.getStateStore()
        );

        trackedEntityDataValueService = new TrackedEntityDataValueService(
                persistenceModule.getTrackedEntityDataValueStore(),
                persistenceModule.getStateStore()
        );

        trackedEntityService = new TrackedEntityService(
                persistenceModule.getTrackedEntityStore());
    }

    @Override
    public IUserAccountService getUserAccountService() {
        return userAccountService;
    }

    @Override
    public IDashboardService getDashboardService() {
        return dashboardService;
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
    public IEventService getEventService() {
        return eventService;
    }

    @Override
    public IConstantService getConstantService() {
        return constantService;
    }

    @Override
    public IDataElementService getDataElementService() {
        return dataElementService;
    }

    @Override
    public IEnrollmentService getEnrollmentService() {
        return enrollmentService;
    }

    @Override
    public IOptionSetService getOptionSetService() {
        return optionSetService;
    }

    @Override
    public IProgramStageService getProgramStageService() {
        return programStageService;
    }

    @Override
    public IProgramStageDataElementService getProgramStageDataElementService() {
        return programStageDataElementService;
    }

    @Override
    public IProgramStageSectionService getProgramStageSectionService() {
        return programStageSectionService;
    }

    @Override
    public IRelationshipService getRelationshipService() {
        return relationshipService;
    }

    @Override
    public IRelationshipTypeService getRelationshipTypeService() {
        return relationshipTypeService;
    }

    @Override
    public ITrackedEntityAttributeService getTrackedEntityAttributeService() {
        return trackedEntityAttributeService;
    }

    @Override
    public ITrackedEntityAttributeValueService getTrackedEntityAttributeValueService() {
        return trackedEntityAttributeValueService;
    }

    @Override
    public ITrackedEntityDataValueService getTrackedEntityDataValueService() {
        return trackedEntityDataValueService;
    }

    @Override
    public IProgramIndicatorService getProgramIndicatorService() {
        return programIndicatorService;
    }

    @Override
    public IProgramTrackedEntityAttributeService getProgramTrackedEntityAttributeService() {
        return programTrackedEntityAttributeService;
    }

    @Override
    public ITrackedEntityService getTrackedEntities() {
        return trackedEntityService;
    }

    @Override
    public IProgramRuleService getProgramRuleService() {
        return programRuleService;
    }
}
