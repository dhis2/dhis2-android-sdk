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
import org.hisp.dhis.client.sdk.android.dataset.DataSetMapper;
import org.hisp.dhis.client.sdk.android.enrollment.EnrollmentMapper;
import org.hisp.dhis.client.sdk.android.event.EventMapper;
import org.hisp.dhis.client.sdk.android.flow.ConstantFlow;
import org.hisp.dhis.client.sdk.android.flow.DashboardFlow;
import org.hisp.dhis.client.sdk.android.flow.DashboardContentFlow;
import org.hisp.dhis.client.sdk.android.flow.DashboardElementFlow;
import org.hisp.dhis.client.sdk.android.flow.DashboardItemFlow;
import org.hisp.dhis.client.sdk.android.flow.DataElementFlow;
import org.hisp.dhis.client.sdk.android.flow.DataSetFlow;
import org.hisp.dhis.client.sdk.android.flow.EnrollmentFlow;
import org.hisp.dhis.client.sdk.android.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.flow.OptionFlow;
import org.hisp.dhis.client.sdk.android.flow.OptionSetFlow;
import org.hisp.dhis.client.sdk.android.flow.OrganisationUnitFlow;
import org.hisp.dhis.client.sdk.android.flow.ProgramFlow;
import org.hisp.dhis.client.sdk.android.flow.ProgramIndicatorFlow;
import org.hisp.dhis.client.sdk.android.flow.ProgramRuleFlow;
import org.hisp.dhis.client.sdk.android.flow.ProgramRuleActionFlow;
import org.hisp.dhis.client.sdk.android.flow.ProgramRuleVariableFlow;
import org.hisp.dhis.client.sdk.android.flow.ProgramStageDataElementFlow;
import org.hisp.dhis.client.sdk.android.flow.ProgramStageFlow;
import org.hisp.dhis.client.sdk.android.flow.ProgramStageSectionFlow;
import org.hisp.dhis.client.sdk.android.flow.ProgramTrackedEntityAttributeFlow;
import org.hisp.dhis.client.sdk.android.flow.RelationshipFlow;
import org.hisp.dhis.client.sdk.android.flow.RelationshipTypeFlow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityAttributeValueFlow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityFlow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityAttributeFlow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityDataValueFlow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityInstanceFlow;
import org.hisp.dhis.client.sdk.android.flow.UserFlow;
import org.hisp.dhis.client.sdk.android.flow.UserAccountFlow;
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
import org.hisp.dhis.client.sdk.models.dataset.DataSet;
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

    private final IMapper<UserAccount, UserAccountFlow> userAccountMapper;
    private final IStateMapper stateMapper;
    private final IMapper<Dashboard, DashboardFlow> dashboardMapper;
    private final IMapper<DashboardItem, DashboardItemFlow> dashboardItemMapper;
    private final IMapper<DashboardElement, DashboardElementFlow> dashboardElementMapper;
    private final IMapper<DashboardContent, DashboardContentFlow> dashboardContentMapper;

    private final IMapper<Event, EventFlow> eventMapper;
    private final IMapper<Enrollment, EnrollmentFlow> enrollmentMapper;
    private final IMapper<TrackedEntityInstance, TrackedEntityInstanceFlow> trackedEntityInstanceMapper;
    private final IMapper<TrackedEntityDataValue, TrackedEntityDataValueFlow> trackedEntityDataValueMapper;
    private final IMapper<TrackedEntityAttributeValue, TrackedEntityAttributeValueFlow> trackedEntityAttributeValueMapper;
    private final IMapper<Relationship, RelationshipFlow> relationshipMapper;

    private final IMapper<Constant, ConstantFlow> constantMapper;
    private final IMapper<DataElement, DataElementFlow> dataElementMapper;
    private final IMapper<Option, OptionFlow> optionMapper;
    private final IMapper<OptionSet, OptionSetFlow> optionSetMapper;
    private final IMapper<OrganisationUnit, OrganisationUnitFlow> organisationUnitMapper;
    private final IMapper<Program, ProgramFlow> programMapper;
    private final IMapper<TrackedEntity, TrackedEntityFlow> trackedEntityMapper;
    private final IMapper<TrackedEntityAttribute, TrackedEntityAttributeFlow> trackedEntityAttributeMapper;
    private final IMapper<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttributeFlow> programTrackedEntityAttributeMapper;
    private final IMapper<ProgramStageDataElement, ProgramStageDataElementFlow> programStageDataElementMapper;
    private final IMapper<ProgramIndicator, ProgramIndicatorFlow> programIndicatorMapper;
    private final IMapper<ProgramStageSection, ProgramStageSectionFlow> programStageSectionMapper;
    private final IMapper<ProgramStage, ProgramStageFlow> programStageMapper;
    private final IMapper<ProgramRule, ProgramRuleFlow> programRuleMapper;
    private final IMapper<ProgramRuleAction, ProgramRuleActionFlow> programRuleActionMapper;
    private final IMapper<ProgramRuleVariable, ProgramRuleVariableFlow> programRuleVariableMapper;
    private final IMapper<RelationshipType, RelationshipTypeFlow> relationshipTypeMapper;
    private final IMapper<DataSet, DataSetFlow> dataSetMapper;

    private final IMapper<User, UserFlow> userMapper;

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
        dataSetMapper = new DataSetMapper();

        programMapper = new ProgramMapper();
        programStageMapper = new ProgramStageMapper();
        programIndicatorMapper = new ProgramIndicatorMapper();
        programStageSectionMapper = new ProgramStageSectionMapper();

        userMapper = new UserMapper();
    }

    public IMapper<UserAccount, UserAccountFlow> getUserAccountMapper() {
        return userAccountMapper;
    }

    public IStateMapper getStateMapper() {
        return stateMapper;
    }

    public IMapper<Dashboard, DashboardFlow> getDashboardMapper() {
        return dashboardMapper;
    }

    public IMapper<DashboardItem, DashboardItemFlow> getDashboardItemMapper() {
        return dashboardItemMapper;
    }

    public IMapper<DashboardElement, DashboardElementFlow> getDashboardElementMapper() {
        return dashboardElementMapper;
    }

    public IMapper<DashboardContent, DashboardContentFlow> getDashboardContentMapper() {
        return dashboardContentMapper;
    }

    public IMapper<Event, EventFlow> getEventMapper() {
        return eventMapper;
    }

    public IMapper<Enrollment, EnrollmentFlow> getEnrollmentMapper() {
        return enrollmentMapper;
    }

    public IMapper<TrackedEntityInstance, TrackedEntityInstanceFlow> getTrackedEntityInstanceMapper() {
        return trackedEntityInstanceMapper;
    }

    public IMapper<TrackedEntityDataValue, TrackedEntityDataValueFlow> getTrackedEntityDataValueMapper() {
        return trackedEntityDataValueMapper;
    }

    public IMapper<TrackedEntityAttributeValue, TrackedEntityAttributeValueFlow> getTrackedEntityAttributeValueMapper() {
        return trackedEntityAttributeValueMapper;
    }

    public IMapper<Relationship, RelationshipFlow> getRelationshipMapper() {
        return relationshipMapper;
    }

    public IMapper<Constant, ConstantFlow> getConstantMapper() {
        return constantMapper;
    }

    public IMapper<DataElement, DataElementFlow> getDataElementMapper() {
        return dataElementMapper;
    }

    public IMapper<Option, OptionFlow> getOptionMapper() {
        return optionMapper;
    }

    public IMapper<OptionSet, OptionSetFlow> getOptionSetMapper() {
        return optionSetMapper;
    }

    public IMapper<OrganisationUnit, OrganisationUnitFlow> getOrganisationUnitMapper() {
        return organisationUnitMapper;
    }

    public IMapper<Program, ProgramFlow> getProgramMapper() {
        return programMapper;
    }

    public IMapper<TrackedEntity, TrackedEntityFlow> getTrackedEntityMapper() {
        return trackedEntityMapper;
    }

    public IMapper<TrackedEntityAttribute, TrackedEntityAttributeFlow> getTrackedEntityAttributeMapper() {
        return trackedEntityAttributeMapper;
    }

    public IMapper<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttributeFlow> getProgramTrackedEntityAttributeMapper() {
        return programTrackedEntityAttributeMapper;
    }

    public IMapper<ProgramStageDataElement, ProgramStageDataElementFlow> getProgramStageDataElementMapper() {
        return programStageDataElementMapper;
    }

    public IMapper<ProgramIndicator, ProgramIndicatorFlow> getProgramIndicatorMapper() {
        return programIndicatorMapper;
    }

    public IMapper<ProgramStageSection, ProgramStageSectionFlow> getProgramStageSectionMapper() {
        return programStageSectionMapper;
    }

    public IMapper<ProgramStage, ProgramStageFlow> getProgramStageMapper() {
        return programStageMapper;
    }

    public IMapper<ProgramRule, ProgramRuleFlow> getProgramRuleMapper() {
        return programRuleMapper;
    }

    public IMapper<ProgramRuleAction, ProgramRuleActionFlow> getProgramRuleActionMapper() {
        return programRuleActionMapper;
    }

    public IMapper<ProgramRuleVariable, ProgramRuleVariableFlow> getProgramRuleVariableMapper() {
        return programRuleVariableMapper;
    }

    public IMapper<RelationshipType, RelationshipTypeFlow> getRelationshipTypeMapper() {
        return relationshipTypeMapper;
    }

    public IMapper<User, UserFlow> getUserMapper() {
        return userMapper;
    }

    public IMapper<DataSet, DataSetFlow> getDataSetMapper() {
        return dataSetMapper;
    }
}
