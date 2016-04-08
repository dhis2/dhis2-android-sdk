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

package org.hisp.dhis.client.sdk.android.api.persistence;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.ConstantFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardContentFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardElementFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DashboardItemFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DataElementFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DataSetFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EnrollmentFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OptionFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OptionSetFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramIndicatorFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramRuleActionFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramRuleFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramRuleVariableFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramTrackedEntityAttributeFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.RelationshipFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.RelationshipTypeFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityAttributeFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityAttributeValueFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityInstanceFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.UserFlow;
import org.hisp.dhis.client.sdk.android.common.Mapper;
import org.hisp.dhis.client.sdk.android.constant.ConstantMapper;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardContentMapper;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardElementMapper;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardItemMapper;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardMapper;
import org.hisp.dhis.client.sdk.android.dataelement.DataElementMapper;
import org.hisp.dhis.client.sdk.android.dataset.DataSetMapper;
import org.hisp.dhis.client.sdk.android.enrollment.EnrollmentMapper;
import org.hisp.dhis.client.sdk.android.optionset.OptionMapper;
import org.hisp.dhis.client.sdk.android.optionset.OptionSetMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramIndicatorMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleActionMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleVariableMapper;
import org.hisp.dhis.client.sdk.android.program.ProgramTrackedEntityAttributeMapper;
import org.hisp.dhis.client.sdk.android.relationship.RelationshipMapper;
import org.hisp.dhis.client.sdk.android.relationship.RelationshipTypeMapper;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeMapper;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeValueMapper;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityInstanceMapper;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityMapper;
import org.hisp.dhis.client.sdk.android.user.UserMapper;
import org.hisp.dhis.client.sdk.models.constant.Constant;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardContent;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardElement;
import org.hisp.dhis.client.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.dataset.DataSet;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.optionset.Option;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.client.sdk.models.program.ProgramRule;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleAction;
import org.hisp.dhis.client.sdk.models.program.ProgramRuleVariable;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.relationship.Relationship;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.client.sdk.models.user.User;

public class MapperModule {
    private final Mapper<Dashboard, DashboardFlow> dashboardMapper;
    private final Mapper<DashboardItem, DashboardItemFlow> dashboardItemMapper;
    private final Mapper<DashboardElement, DashboardElementFlow> dashboardElementMapper;
    private final Mapper<DashboardContent, DashboardContentFlow> dashboardContentMapper;

    private final Mapper<Enrollment, EnrollmentFlow> enrollmentMapper;
    private final Mapper<TrackedEntityInstance, TrackedEntityInstanceFlow>
            trackedEntityInstanceMapper;
    private final Mapper<TrackedEntityAttributeValue, TrackedEntityAttributeValueFlow>
            trackedEntityAttributeValueMapper;
    private final Mapper<Relationship, RelationshipFlow> relationshipMapper;

    private final Mapper<Constant, ConstantFlow> constantMapper;
    private final Mapper<DataElement, DataElementFlow> dataElementMapper;
    private final Mapper<Option, OptionFlow> optionMapper;
    private final Mapper<OptionSet, OptionSetFlow> optionSetMapper;
    private final Mapper<TrackedEntity, TrackedEntityFlow> trackedEntityMapper;
    private final Mapper<TrackedEntityAttribute, TrackedEntityAttributeFlow>
            trackedEntityAttributeMapper;
    private final Mapper<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttributeFlow>
            programTrackedEntityAttributeMapper;
    private final Mapper<ProgramIndicator, ProgramIndicatorFlow> programIndicatorMapper;
    private final Mapper<ProgramRule, ProgramRuleFlow> programRuleMapper;
    private final Mapper<ProgramRuleAction, ProgramRuleActionFlow> programRuleActionMapper;
    private final Mapper<ProgramRuleVariable, ProgramRuleVariableFlow> programRuleVariableMapper;
    private final Mapper<RelationshipType, RelationshipTypeFlow> relationshipTypeMapper;
    private final Mapper<DataSet, DataSetFlow> dataSetMapper;

    private final Mapper<User, UserFlow> userMapper;

    public MapperModule() {
        dashboardMapper = new DashboardMapper();
        dashboardItemMapper = new DashboardItemMapper();
        dashboardElementMapper = new DashboardElementMapper();
        dashboardContentMapper = new DashboardContentMapper();
        enrollmentMapper = new EnrollmentMapper();
        trackedEntityInstanceMapper = new TrackedEntityInstanceMapper();
        relationshipMapper = new RelationshipMapper();
        constantMapper = new ConstantMapper();
        optionMapper = new OptionMapper();
        optionSetMapper = new OptionSetMapper();
        dataElementMapper = new DataElementMapper();
        trackedEntityMapper = new TrackedEntityMapper();
        trackedEntityAttributeMapper = new TrackedEntityAttributeMapper();
        programTrackedEntityAttributeMapper = new ProgramTrackedEntityAttributeMapper();
        trackedEntityAttributeValueMapper = new TrackedEntityAttributeValueMapper();

        programRuleActionMapper = new ProgramRuleActionMapper();
        programRuleMapper = new ProgramRuleMapper();
        programRuleVariableMapper = new ProgramRuleVariableMapper();
        relationshipTypeMapper = new RelationshipTypeMapper();
        dataSetMapper = new DataSetMapper();

        programIndicatorMapper = new ProgramIndicatorMapper();

        userMapper = new UserMapper();
    }

    public Mapper<Dashboard, DashboardFlow> getDashboardMapper() {
        return dashboardMapper;
    }

    public Mapper<DashboardItem, DashboardItemFlow> getDashboardItemMapper() {
        return dashboardItemMapper;
    }

    public Mapper<DashboardElement, DashboardElementFlow> getDashboardElementMapper() {
        return dashboardElementMapper;
    }

    public Mapper<DashboardContent, DashboardContentFlow> getDashboardContentMapper() {
        return dashboardContentMapper;
    }

    public Mapper<Enrollment, EnrollmentFlow> getEnrollmentMapper() {
        return enrollmentMapper;
    }

    public Mapper<TrackedEntityInstance, TrackedEntityInstanceFlow>
    getTrackedEntityInstanceMapper() {
        return trackedEntityInstanceMapper;
    }

    public Mapper<TrackedEntityAttributeValue, TrackedEntityAttributeValueFlow>
    getTrackedEntityAttributeValueMapper() {
        return trackedEntityAttributeValueMapper;
    }

    public Mapper<Relationship, RelationshipFlow> getRelationshipMapper() {
        return relationshipMapper;
    }

    public Mapper<Constant, ConstantFlow> getConstantMapper() {
        return constantMapper;
    }

    public Mapper<DataElement, DataElementFlow> getDataElementMapper() {
        return dataElementMapper;
    }

    public Mapper<Option, OptionFlow> getOptionMapper() {
        return optionMapper;
    }

    public Mapper<OptionSet, OptionSetFlow> getOptionSetMapper() {
        return optionSetMapper;
    }

    public Mapper<TrackedEntity, TrackedEntityFlow> getTrackedEntityMapper() {
        return trackedEntityMapper;
    }

    public Mapper<TrackedEntityAttribute, TrackedEntityAttributeFlow>
    getTrackedEntityAttributeMapper() {
        return trackedEntityAttributeMapper;
    }

    public Mapper<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttributeFlow>
    getProgramTrackedEntityAttributeMapper() {
        return programTrackedEntityAttributeMapper;
    }

    public Mapper<ProgramIndicator, ProgramIndicatorFlow> getProgramIndicatorMapper() {
        return programIndicatorMapper;
    }

    public Mapper<ProgramRule, ProgramRuleFlow> getProgramRuleMapper() {
        return programRuleMapper;
    }

    public Mapper<ProgramRuleAction, ProgramRuleActionFlow> getProgramRuleActionMapper() {
        return programRuleActionMapper;
    }

    public Mapper<ProgramRuleVariable, ProgramRuleVariableFlow> getProgramRuleVariableMapper() {
        return programRuleVariableMapper;
    }

    public Mapper<RelationshipType, RelationshipTypeFlow> getRelationshipTypeMapper() {
        return relationshipTypeMapper;
    }

    public Mapper<User, UserFlow> getUserMapper() {
        return userMapper;
    }

    public Mapper<DataSet, DataSetFlow> getDataSetMapper() {
        return dataSetMapper;
    }
}
