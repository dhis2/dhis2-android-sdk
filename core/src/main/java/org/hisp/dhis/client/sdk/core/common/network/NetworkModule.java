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

package org.hisp.dhis.client.sdk.core.common.network;

import org.hisp.dhis.client.sdk.core.dataelement.DataElementApiClient;
import org.hisp.dhis.client.sdk.core.enrollment.EnrollmentApiClient;
import org.hisp.dhis.client.sdk.core.event.EventApiClient;
import org.hisp.dhis.client.sdk.core.optionset.OptionSetApiClient;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramIndicatorApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleActionApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramStageApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionApiClient;
import org.hisp.dhis.client.sdk.core.program.ProgramTrackedEntityAttributeApiClient;
import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoApiClient;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityApiClient;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeApiClient;
import org.hisp.dhis.client.sdk.core.user.UserApiClient;

public interface NetworkModule {
    SystemInfoApiClient getSystemInfoApiClient();

    UserApiClient getUserApiClient();

    OrganisationUnitApiClient getOrganisationUnitApiClient();

    ProgramApiClient getProgramApiClient();

    ProgramStageApiClient getProgramStageApiClient();

    ProgramStageSectionApiClient getProgramStageSectionApiClient();

    EventApiClient getEventApiClient();

    DataElementApiClient getDataElementApiClient();

    ProgramStageDataElementApiClient getProgramStageDataElementApiClient();

    OptionSetApiClient getOptionSetApiClient();

    TrackedEntityAttributeApiClient getTrackedEntityAttributeApiClient();

    ProgramRuleApiClient getProgramRuleApiClient();

    ProgramRuleActionApiClient getProgramRuleActionApiClient();

    ProgramRuleVariableApiClient getProgramRuleVariableApiClient();

    ProgramIndicatorApiClient getProgramIndicatorApiClient();

    TrackedEntityApiClient getTrackedEntityApiClient();

    ProgramTrackedEntityAttributeApiClient getProgramTrackedEntityAttributeApiClient();

    EnrollmentApiClient getEnrollmentApiClient();
}
