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

import org.hisp.dhis.client.sdk.core.dashboard.DashboardContentService;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardElementService;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardItemService;
import org.hisp.dhis.client.sdk.core.dashboard.DashboardService;
import org.hisp.dhis.client.sdk.core.dataelement.DataElementService;
import org.hisp.dhis.client.sdk.core.event.EventService;
import org.hisp.dhis.client.sdk.core.optionset.OptionSetService;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitService;
import org.hisp.dhis.client.sdk.core.program.ProgramIndicatorService;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleActionService;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleService;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableService;
import org.hisp.dhis.client.sdk.core.program.ProgramService;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementService;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionService;
import org.hisp.dhis.client.sdk.core.program.ProgramStageService;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueService;
import org.hisp.dhis.client.sdk.core.user.UserAccountService;

public interface ServicesModule {
    UserAccountService getUserAccountService();

    ProgramService getProgramService();

    ProgramStageService getProgramStageService();

    ProgramStageDataElementService getProgramStageDataElementService();

    ProgramStageSectionService getProgramStageSectionService();

    OrganisationUnitService getOrganisationUnitService();

    EventService getEventService();

    DashboardService getDashboardService();

    DashboardElementService getDashboardElementService();

    DashboardItemService getDashboardItemService();

    DashboardContentService getDashboardContentService();

    DataElementService getDataElementService();

    ProgramRuleService getProgramRuleService();

    ProgramRuleActionService getProgramRuleActionService();

    ProgramRuleVariableService getProgramRuleVariableService();

    ProgramIndicatorService getProgramIndicatorService();

    TrackedEntityAttributeService getTrackedEntityAttributeService();

    TrackedEntityDataValueService getTrackedEntityDataValueService();

    OptionSetService getOptionSetService();
}
