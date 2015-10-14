/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.core.api.api;

import org.hisp.dhis.android.sdk.corejava.AssignedProgramsController;
import org.hisp.dhis.android.sdk.corejava.ConstantController;
import org.hisp.dhis.android.sdk.corejava.DashboardController;
import org.hisp.dhis.android.sdk.corejava.DataElementController;
import org.hisp.dhis.android.sdk.corejava.EnrollmentController;
import org.hisp.dhis.android.sdk.corejava.EventController;
import org.hisp.dhis.android.sdk.corejava.IEnrollmentController;
import org.hisp.dhis.android.sdk.corejava.IEventController;
import org.hisp.dhis.android.sdk.corejava.IOrganisationUnitController;
import org.hisp.dhis.android.sdk.corejava.IProgramController;
import org.hisp.dhis.android.sdk.corejava.ITrackedEntityInstanceController;
import org.hisp.dhis.android.sdk.corejava.InterpretationController;
import org.hisp.dhis.android.sdk.corejava.OptionSetController;
import org.hisp.dhis.android.sdk.corejava.OrganisationUnitController;
import org.hisp.dhis.android.sdk.corejava.ProgramController;
import org.hisp.dhis.android.sdk.corejava.ProgramRuleActionController;
import org.hisp.dhis.android.sdk.corejava.ProgramRuleController;
import org.hisp.dhis.android.sdk.corejava.ProgramRuleVariableController;
import org.hisp.dhis.android.sdk.corejava.RelationshipTypeController;
import org.hisp.dhis.android.sdk.corejava.TrackedEntityAttributeController;
import org.hisp.dhis.android.sdk.corejava.TrackedEntityInstanceController;
import org.hisp.dhis.android.sdk.corejava.common.IDataController;
import org.hisp.dhis.android.sdk.corejava.user.IUserAccountController;
import org.hisp.dhis.android.sdk.corejava.user.UserAccountController;
import org.hisp.dhis.android.sdk.core.network.IDhisApi;
import org.hisp.dhis.android.sdk.models.constant.Constant;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dataelement.DataElement;
import org.hisp.dhis.android.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.android.sdk.models.optionset.OptionSet;
import org.hisp.dhis.android.sdk.models.program.Program;
import org.hisp.dhis.android.sdk.models.program.ProgramRule;
import org.hisp.dhis.android.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.android.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.android.sdk.models.relationship.RelationshipType;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.core.api.modules.Models;

final class Controllers {
    private static Controllers controllers;

    private final IDataController<Dashboard> dashboardController;
    private final IDataController<Interpretation> interpretationController;
    private final IUserAccountController userAccountController;
    private final IDataController<RelationshipType> relationshipTypeController;
    private final IDataController<OptionSet> optionSetController;
    private final IProgramController programController;
    private final IOrganisationUnitController organisationUnitController;
    private final IDataController<Program> assignedProgramsController;
    private final IEventController eventController;
    private final IEnrollmentController enrollmentController;
    private final ITrackedEntityInstanceController trackedEntityInstanceController;
    private final IDataController<DataElement> dataElementController;
    private final IDataController<Constant> constantController;
    private final IDataController<TrackedEntityAttribute> trackedEntityAttributeController;
    private final IDataController<ProgramRule> programRuleController;
    private final IDataController<ProgramRuleVariable> programRuleVariableController;
    private final IDataController<ProgramRuleAction> programRuleActionController;

    private Controllers(IDhisApi dhisApi) {
        dashboardController = new DashboardController(dhisApi, Models.dashboards(),
                Models.dashboardItems(), Models.dashboardElements(),
                Models.dashboardItemContent(), Models.stateStore());
        interpretationController = new InterpretationController(dhisApi,
                Services.interpretations(), Services.userAccount(), Models.interpretations(),
                Models.interpretationElements(), Models.interpretationComments(), Models.users());
        userAccountController = new UserAccountController(dhisApi, Models.userAccount());
        relationshipTypeController = new RelationshipTypeController(dhisApi, Models.relationshipTypes());
        optionSetController = new OptionSetController(dhisApi, Models.options(), Models.optionSets());
        programController = new ProgramController(dhisApi, Models.programs(), Models.programIndicators(),
                Models.programStageDataElements(), Models.programTrackedEntityAttributes(), Models.programStages(), Models.programStageSections());
        organisationUnitController = new OrganisationUnitController(dhisApi, Models.organisationUnits());
        assignedProgramsController = new AssignedProgramsController(dhisApi,
                programController, organisationUnitController, Models.organisationUnits(), Models.programs());
        eventController = new EventController(dhisApi, Models.stateStore(), Models.events(),
                Models.trackedEntityDataValues(), Models.organisationUnits(), Models.programs(),
                Models.failedItems());
        enrollmentController = new EnrollmentController(dhisApi, eventController,
                Models.enrollments(), Models.events(), Models.stateStore(), Models.failedItems());
        trackedEntityInstanceController = new TrackedEntityInstanceController(dhisApi,
                enrollmentController, Models.trackedEntityInstances(), Models.stateStore(),
                Models.failedItems(), Models.relationships(), Models.trackedEntityAttributeValues(),
                Models.enrollments());
        dataElementController = new DataElementController(dhisApi, Models.dataElements());
        constantController = new ConstantController(dhisApi, Models.constants());
        trackedEntityAttributeController = new TrackedEntityAttributeController(dhisApi, Models.trackedEntityAttributes());
        programRuleController = new ProgramRuleController(dhisApi, Models.programRules());
        programRuleVariableController = new ProgramRuleVariableController(dhisApi, Models.programRuleVariables());
        programRuleActionController = new ProgramRuleActionController(dhisApi, Models.programRuleActions());
    }

    public static void init(IDhisApi dhisApi) {
        if (controllers == null) {
            controllers = new Controllers(dhisApi);
        }
    }

    private static Controllers getInstance() {
        if (controllers == null) {
            throw new IllegalArgumentException("You have to call init() method first");
        }

        return controllers;
    }

    public static IDataController assignedPrograms() {
        return getInstance().assignedProgramsController;
    }

    public static IOrganisationUnitController organisationUnits() {
        return getInstance().organisationUnitController;
    }

    public static IProgramController programs() {
        return getInstance().programController;
    }

    public static IDataController<OptionSet> optionSets() {
        return getInstance().optionSetController;
    }

    public static IDataController<RelationshipType> relationshipTypes() {
        return getInstance().relationshipTypeController;
    }

    public static IDataController<Dashboard> dashboards() {
        return getInstance().dashboardController;
    }

    public static IDataController<Interpretation> interpretations() {
        return getInstance().interpretationController;
    }

    public static IUserAccountController userAccount() {
        return getInstance().userAccountController;
    }
}
