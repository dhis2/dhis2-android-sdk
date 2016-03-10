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

import org.hisp.dhis.client.sdk.core.constant.IConstantService;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardService;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementService;
import org.hisp.dhis.client.sdk.core.enrollment.IEnrollmentService;
import org.hisp.dhis.client.sdk.core.event.IEventService;
import org.hisp.dhis.client.sdk.core.optionset.IOptionSetService;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitService;
import org.hisp.dhis.client.sdk.core.program.*;
import org.hisp.dhis.client.sdk.core.relationship.IRelationshipService;
import org.hisp.dhis.client.sdk.core.relationship.IRelationshipTypeService;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeService;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeValueService;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityDataValueService;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityService;
import org.hisp.dhis.client.sdk.core.user.IUserAccountService;

public interface IServicesModule {
    IUserAccountService getUserAccountService();

    IDashboardService getDashboardService();

    IProgramService getProgramService();

    IOrganisationUnitService getOrganisationUnitService();

    IEventService getEventService();

    IConstantService getConstantService();

    IDataElementService getDataElementService();

    IEnrollmentService getEnrollmentService();

    IOptionSetService getOptionSetService();

    IProgramStageService getProgramStageService();

    IProgramStageDataElementService getProgramStageDataElementService();

    IProgramStageSectionService getProgramStageSectionService();

    IRelationshipService getRelationshipService();

    IRelationshipTypeService getRelationshipTypeService();

    ITrackedEntityAttributeService getTrackedEntityAttributeService();

    ITrackedEntityAttributeValueService getTrackedEntityAttributeValueService();

    ITrackedEntityDataValueService getTrackedEntityDataValueService();

    IProgramIndicatorService getProgramIndicatorService();

    IProgramTrackedEntityAttributeService getProgramTrackedEntityAttributeService();

    ITrackedEntityService getTrackedEntities();

    IProgramRuleService getProgramRuleService();

}
