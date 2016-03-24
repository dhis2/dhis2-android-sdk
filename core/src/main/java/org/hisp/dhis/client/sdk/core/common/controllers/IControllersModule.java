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

import org.hisp.dhis.client.sdk.core.dataelement.IDataElementController;
import org.hisp.dhis.client.sdk.core.event.IEventController;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitController;
import org.hisp.dhis.client.sdk.core.program.IProgramController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageDataElementController;
import org.hisp.dhis.client.sdk.core.program.IProgramStageSectionController;
import org.hisp.dhis.client.sdk.core.systeminfo.ISystemInfoController;
import org.hisp.dhis.client.sdk.core.user.IAssignedOrganisationUnitsController;
import org.hisp.dhis.client.sdk.core.user.IAssignedProgramsController;
import org.hisp.dhis.client.sdk.core.user.IUserAccountController;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;

public interface IControllersModule {
    ISystemInfoController getSystemInfoController();

    IUserAccountController getUserAccountController();

    IProgramController getProgramController();

    IProgramStageController getProgramStageController();

    IProgramStageSectionController getProgramStageSectionController();

    IOrganisationUnitController getOrganisationUnitController();

    IAssignedProgramsController getAssignedProgramsController();

    IAssignedOrganisationUnitsController getAssignedOrganisationUnitsController();

    IEventController getEventController();

    IDataElementController getDataElementController();

    IProgramStageDataElementController getProgramStageDataElementController();
}
