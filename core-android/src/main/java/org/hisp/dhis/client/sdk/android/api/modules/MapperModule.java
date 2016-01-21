/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.client.sdk.android.api.modules;

import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.common.state.IStateMapper;
import org.hisp.dhis.client.sdk.android.common.state.StateMapper;
import org.hisp.dhis.client.sdk.android.constant.ConstantMapper;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardContentMapper;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardElementMapper;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardItemMapper;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardMapper;
import org.hisp.dhis.client.sdk.android.dataelement.DataElementMapper;
import org.hisp.dhis.client.sdk.android.enrollment.EnrollmentMapper;
import org.hisp.dhis.client.sdk.android.event.EventMapper;
import org.hisp.dhis.client.sdk.android.flow.Constant$Flow;
import org.hisp.dhis.client.sdk.android.flow.Dashboard$Flow;
import org.hisp.dhis.client.sdk.android.flow.DashboardContent$Flow;
import org.hisp.dhis.client.sdk.android.flow.DashboardElement$Flow;
import org.hisp.dhis.client.sdk.android.flow.DashboardItem$Flow;
import org.hisp.dhis.client.sdk.android.flow.DataElement$Flow;
import org.hisp.dhis.client.sdk.android.flow.Enrollment$Flow;
import org.hisp.dhis.client.sdk.android.flow.Event$Flow;
import org.hisp.dhis.client.sdk.android.flow.Option$Flow;
import org.hisp.dhis.client.sdk.android.flow.OptionSet$Flow;
import org.hisp.dhis.client.sdk.android.flow.OrganisationUnit$Flow;
import org.hisp.dhis.client.sdk.android.flow.Program$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramIndicator$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramRule$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramRuleAction$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramRuleVariable$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramStage$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramStageDataElement$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramStageSection$Flow;
import org.hisp.dhis.client.sdk.android.flow.ProgramTrackedEntityAttribute$Flow;
import org.hisp.dhis.client.sdk.android.flow.Relationship$Flow;
import org.hisp.dhis.client.sdk.android.flow.RelationshipType$Flow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntity$Flow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityAttribute$Flow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityAttributeValue$Flow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityDataValue$Flow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityInstance$Flow;
import org.hisp.dhis.client.sdk.android.flow.User$Flow;
import org.hisp.dhis.client.sdk.android.flow.UserAccount$Flow;
import org.hisp.dhis.client.sdk.android.optionset.OptionMapper;
import org.hisp.dhis.client.sdk.android.optionset.OptionSetMapper;
import org.hisp.dhis.client.sdk.android.organisationunit.OrganisationUnitMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramIndicatorMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleActionMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleVariableMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramStageDataElementMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramStageMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramStageSectionMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramTrackedEntityAttributeMapper;
import org.hisp.dhis.client.sdk.android.relationship.RelationshipMapper;
import org.hisp.dhis.client.sdk.android.relationship.RelationshipTypeMapper;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeMapper;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeValueMapper;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityDataValueMapper;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityInstanceMapper;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityMapper;
import org.hisp.dhis.client.sdk.android.user.UserAccountMapper;
import org.hisp.dhis.client.sdk.android.user.UserMapper;
import org.hisp.dhis.client.sdk.models.constant.Constant;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.optionset.Option;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.relationship.Relationship;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.client.sdk.models.user.User;
import org.hisp.dhis.client.sdk.models.user.UserAccount;

public class MapperModule {

    private final IMapper<UserAccount, UserAccount$Flow> userAccountMapper;
    private final IStateMapper stateMapper;
    private final IMapper<Dashboard, Dashboard$Flow> dashboardMapper;
    private final IMapper<DashboardItem, DashboardItem$Flow> dashboardItemMapper;
    private final IMapper<DashboardElement, DashboardElement$Flow> dashboardElementMapper;
    private final IMapper<DashboardContent, DashboardContent$Flow> dashboardContentMapper;

    private final IMapper<Event, Event$Flow> eventMapper;
    private final IMapper<Enrollment, Enrollment$Flow> enrollmentMapper;
    private final IMapper<TrackedEntityInstance, TrackedEntityInstance$Flow> trackedEntityInstanceMapper;
    private final IMapper<TrackedEntityDataValue, TrackedEntityDataValue$Flow> trackedEntityDataValueMapper;
    private final IMapper<TrackedEntityAttributeValue, TrackedEntityAttributeValue$Flow> trackedEntityAttributeValueMapper;
    private final IMapper<Relationship, Relationship$Flow> relationshipMapper;

    private final IMapper<Constant, Constant$Flow> constantMapper;
    private final IMapper<DataElement, DataElement$Flow> dataElementMapper;
    private final IMapper<Option, Option$Flow> optionMapper;
    private final IMapper<OptionSet, OptionSet$Flow> optionSetMapper;
    private final IMapper<OrganisationUnit, OrganisationUnit$Flow> organisationUnitMapper;
    private final IMapper<Program, Program$Flow> programMapper;
    private final IMapper<TrackedEntity, TrackedEntity$Flow> trackedEntityMapper;
    private final IMapper<TrackedEntityAttribute, TrackedEntityAttribute$Flow> trackedEntityAttributeMapper;
    private final IMapper<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttribute$Flow> programTrackedEntityAttributeMapper;
    private final IMapper<ProgramStageDataElement, ProgramStageDataElement$Flow> programStageDataElementMapper;
    private final IMapper<ProgramIndicator, ProgramIndicator$Flow> programIndicatorMapper;
    private final IMapper<ProgramStageSection, ProgramStageSection$Flow> programStageSectionMapper;
    private final IMapper<ProgramStage, ProgramStage$Flow> programStageMapper;
    private final IMapper<ProgramRule, ProgramRule$Flow> programRuleMapper;
    private final IMapper<ProgramRuleAction, ProgramRuleAction$Flow> programRuleActionMapper;
    private final IMapper<ProgramRuleVariable, ProgramRuleVariable$Flow> programRuleVariableMapper;
    private final IMapper<RelationshipType, RelationshipType$Flow> relationshipTypeMapper;
    //    private final IMapper<DataSet, DataSet$Flow> dataSetMapper;

    private final IMapper<User, User$Flow> userMapper;

    public MapperModule() {
        userAccountMapper = new UserAccountMapper();
        stateMapper = new StateMapper();
        dashboardMapper = new DashboardMapper();
        dashboardItemMapper = new DashboardItemMapper();
        dashboardElementMapper = new DashboardElementMapper();
        dashboardContentMapper = new DashboardContentMapper();
        trackedEntityDataValueMapper = new TrackedEntityDataValueMapper();
        eventMapper = new EventMapper();
        enrollmentMapper = new EnrollmentMapper();
        trackedEntityInstanceMapper = new TrackedEntityInstanceMapper();
        relationshipMapper = new RelationshipMapper();
        constantMapper = new ConstantMapper();
        optionMapper = new OptionMapper();
        optionSetMapper = new OptionSetMapper();
        dataElementMapper = new DataElementMapper();
        organisationUnitMapper = new OrganisationUnitMapper();
        trackedEntityMapper = new TrackedEntityMapper();
        trackedEntityAttributeMapper = new TrackedEntityAttributeMapper();
        programTrackedEntityAttributeMapper = new ProgramTrackedEntityAttributeMapper();
        programStageDataElementMapper = new ProgramStageDataElementMapper();
        trackedEntityAttributeValueMapper = new TrackedEntityAttributeValueMapper();

        programRuleActionMapper = new ProgramRuleActionMapper();
        programRuleMapper = new ProgramRuleMapper();
        programRuleVariableMapper = new ProgramRuleVariableMapper();
        relationshipTypeMapper = new RelationshipTypeMapper();
//        dataSetMapper = null;//new DataSetMapper();

        programMapper = new ProgramMapper();
        programStageMapper = new ProgramStageMapper();
        programIndicatorMapper = new ProgramIndicatorMapper();
        programStageSectionMapper = new ProgramStageSectionMapper();

        userMapper = new UserMapper();
    }

    public IMapper<UserAccount, UserAccount$Flow> getUserAccountMapper() {
        return userAccountMapper;
    }

    public IStateMapper getStateMapper() {
        return stateMapper;
    }

    public IMapper<Dashboard, Dashboard$Flow> getDashboardMapper() {
        return dashboardMapper;
    }

    public IMapper<DashboardItem, DashboardItem$Flow> getDashboardItemMapper() {
        return dashboardItemMapper;
    }

    public IMapper<DashboardElement, DashboardElement$Flow> getDashboardElementMapper() {
        return dashboardElementMapper;
    }

    public IMapper<DashboardContent, DashboardContent$Flow> getDashboardContentMapper() {
        return dashboardContentMapper;
    }

    public IMapper<Event, Event$Flow> getEventMapper() {
        return eventMapper;
    }

    public IMapper<Enrollment, Enrollment$Flow> getEnrollmentMapper() {
        return enrollmentMapper;
    }

    public IMapper<TrackedEntityInstance, TrackedEntityInstance$Flow> getTrackedEntityInstanceMapper() {
        return trackedEntityInstanceMapper;
    }

    public IMapper<TrackedEntityDataValue, TrackedEntityDataValue$Flow> getTrackedEntityDataValueMapper() {
        return trackedEntityDataValueMapper;
    }

    public IMapper<TrackedEntityAttributeValue, TrackedEntityAttributeValue$Flow> getTrackedEntityAttributeValueMapper() {
        return trackedEntityAttributeValueMapper;
    }

    public IMapper<Relationship, Relationship$Flow> getRelationshipMapper() {
        return relationshipMapper;
    }

    public IMapper<Constant, Constant$Flow> getConstantMapper() {
        return constantMapper;
    }

    public IMapper<DataElement, DataElement$Flow> getDataElementMapper() {
        return dataElementMapper;
    }

    public IMapper<Option, Option$Flow> getOptionMapper() {
        return optionMapper;
    }

    public IMapper<OptionSet, OptionSet$Flow> getOptionSetMapper() {
        return optionSetMapper;
    }

    public IMapper<OrganisationUnit, OrganisationUnit$Flow> getOrganisationUnitMapper() {
        return organisationUnitMapper;
    }

    public IMapper<Program, Program$Flow> getProgramMapper() {
        return programMapper;
    }

    public IMapper<TrackedEntity, TrackedEntity$Flow> getTrackedEntityMapper() {
        return trackedEntityMapper;
    }

    public IMapper<TrackedEntityAttribute, TrackedEntityAttribute$Flow> getTrackedEntityAttributeMapper() {
        return trackedEntityAttributeMapper;
    }

    public IMapper<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttribute$Flow> getProgramTrackedEntityAttributeMapper() {
        return programTrackedEntityAttributeMapper;
    }

    public IMapper<ProgramStageDataElement, ProgramStageDataElement$Flow> getProgramStageDataElementMapper() {
        return programStageDataElementMapper;
    }

    public IMapper<ProgramIndicator, ProgramIndicator$Flow> getProgramIndicatorMapper() {
        return programIndicatorMapper;
    }

    public IMapper<ProgramStageSection, ProgramStageSection$Flow> getProgramStageSectionMapper() {
        return programStageSectionMapper;
    }

    public IMapper<ProgramStage, ProgramStage$Flow> getProgramStageMapper() {
        return programStageMapper;
    }

    public IMapper<ProgramRule, ProgramRule$Flow> getProgramRuleMapper() {
        return programRuleMapper;
    }

    public IMapper<ProgramRuleAction, ProgramRuleAction$Flow> getProgramRuleActionMapper() {
        return programRuleActionMapper;
    }

    public IMapper<ProgramRuleVariable, ProgramRuleVariable$Flow> getProgramRuleVariableMapper() {
        return programRuleVariableMapper;
    }

    public IMapper<RelationshipType, RelationshipType$Flow> getRelationshipTypeMapper() {
        return relationshipTypeMapper;
    }

    public IMapper<User, User$Flow> getUserMapper() {
        return userMapper;
    }
}
