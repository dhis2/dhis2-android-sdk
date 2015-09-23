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

package org.hisp.dhis.android.sdk.core.api;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;

import org.hisp.dhis.android.sdk.core.persistence.models.common.ModelsStore;
import org.hisp.dhis.android.sdk.core.persistence.models.constant.ConstantStore;
import org.hisp.dhis.android.sdk.core.persistence.models.dashboard.DashboardElementStore;
import org.hisp.dhis.android.sdk.core.persistence.models.dashboard.DashboardItemContentStore;
import org.hisp.dhis.android.sdk.core.persistence.models.dashboard.DashboardItemStore;
import org.hisp.dhis.android.sdk.core.persistence.models.dashboard.DashboardStore;
import org.hisp.dhis.android.sdk.core.persistence.models.dataelement.DataElementStore;
import org.hisp.dhis.android.sdk.core.persistence.models.dataset.DataSetStore;
import org.hisp.dhis.android.sdk.core.persistence.models.enrollment.EnrollmentStore;
import org.hisp.dhis.android.sdk.core.persistence.models.event.EventStore;
import org.hisp.dhis.android.sdk.core.persistence.models.interpretation.InterpretationCommentStore;
import org.hisp.dhis.android.sdk.core.persistence.models.interpretation.InterpretationElementStore;
import org.hisp.dhis.android.sdk.core.persistence.models.interpretation.InterpretationStore;
import org.hisp.dhis.android.sdk.core.persistence.models.option.OptionStore;
import org.hisp.dhis.android.sdk.core.persistence.models.optionset.OptionSetStore;
import org.hisp.dhis.android.sdk.core.persistence.models.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.sdk.core.persistence.models.program.ProgramStore;
import org.hisp.dhis.android.sdk.core.persistence.models.programindicator.ProgramIndicatorStore;
import org.hisp.dhis.android.sdk.core.persistence.models.programrule.ProgramRuleStore;
import org.hisp.dhis.android.sdk.core.persistence.models.programruleaction.ProgramRuleActionStore;
import org.hisp.dhis.android.sdk.core.persistence.models.programrulevariable.ProgramRuleVariableStore;
import org.hisp.dhis.android.sdk.core.persistence.models.programstage.ProgramStageStore;
import org.hisp.dhis.android.sdk.core.persistence.models.programstagedataelement.ProgramStageDataElementStore;
import org.hisp.dhis.android.sdk.core.persistence.models.programstagesection.ProgramStageSectionStore;
import org.hisp.dhis.android.sdk.core.persistence.models.programtrackedentityattribute.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.sdk.core.persistence.models.relationship.RelationshipStore;
import org.hisp.dhis.android.sdk.core.persistence.models.relationshiptype.RelationshipTypeStore;
import org.hisp.dhis.android.sdk.core.persistence.models.state.StateStore;
import org.hisp.dhis.android.sdk.core.persistence.models.trackedentityattribute.TrackedEntityAttributeStore;
import org.hisp.dhis.android.sdk.core.persistence.models.trackedentity.TrackedEntityStore;
import org.hisp.dhis.android.sdk.core.persistence.models.trackedentityattributevalue.TrackedEntityAttributeValueStore;
import org.hisp.dhis.android.sdk.core.persistence.models.trackedentitydatavalue.TrackedEntityDataValueStore;
import org.hisp.dhis.android.sdk.core.persistence.models.trackedentityinstance.TrackedEntityInstanceStore;
import org.hisp.dhis.android.sdk.core.persistence.models.user.UserAccountStore;
import org.hisp.dhis.android.sdk.core.persistence.models.user.UserStore;
import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.common.IModelsStore;
import org.hisp.dhis.android.sdk.models.common.IStore;
import org.hisp.dhis.android.sdk.models.constant.Constant;
import org.hisp.dhis.android.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardElementStore;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardItemContentStore;
import org.hisp.dhis.android.sdk.models.dashboard.IDashboardItemStore;
import org.hisp.dhis.android.sdk.models.dataelement.DataElement;
import org.hisp.dhis.android.sdk.models.dataset.IDataSetStore;
import org.hisp.dhis.android.sdk.models.enrollment.IEnrollmentStore;
import org.hisp.dhis.android.sdk.models.event.IEventStore;
import org.hisp.dhis.android.sdk.models.interpretation.IInterpretationCommentStore;
import org.hisp.dhis.android.sdk.models.interpretation.IInterpretationElementStore;
import org.hisp.dhis.android.sdk.models.interpretation.IInterpretationStore;
import org.hisp.dhis.android.sdk.models.option.IOptionStore;
import org.hisp.dhis.android.sdk.models.optionset.OptionSet;
import org.hisp.dhis.android.sdk.models.organisationunit.IOrganisationUnitStore;
import org.hisp.dhis.android.sdk.models.program.IProgramStore;
import org.hisp.dhis.android.sdk.models.program.Program;
import org.hisp.dhis.android.sdk.models.programindicator.IProgramIndicatorStore;
import org.hisp.dhis.android.sdk.models.programrule.IProgramRuleStore;
import org.hisp.dhis.android.sdk.models.programruleaction.IProgramRuleActionStore;
import org.hisp.dhis.android.sdk.models.programrulevariable.IProgramRuleVariableStore;
import org.hisp.dhis.android.sdk.models.programstage.IProgramStageStore;
import org.hisp.dhis.android.sdk.models.programstagedataelement.IProgramStageDataElementStore;
import org.hisp.dhis.android.sdk.models.programstagesection.IProgramStageSectionStore;
import org.hisp.dhis.android.sdk.models.programtrackedentityattribute.IProgramTrackedEntityAttributeStore;
import org.hisp.dhis.android.sdk.models.relationship.IRelationshipStore;
import org.hisp.dhis.android.sdk.models.relationshiptype.RelationshipType;
import org.hisp.dhis.android.sdk.models.state.IStateStore;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.android.sdk.models.trackedentityattribute.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.models.trackedentityattributevalue.ITrackedEntityAttributeValueStore;
import org.hisp.dhis.android.sdk.models.trackedentitydatavalue.ITrackedEntityDataValueStore;
import org.hisp.dhis.android.sdk.models.trackedentityinstance.ITrackedEntityInstanceStore;
import org.hisp.dhis.android.sdk.models.user.IUserAccountStore;
import org.hisp.dhis.android.sdk.models.user.IUserStore;

public final class Models {
    private static Models models;

    // Meta data store objects
    private final IIdentifiableObjectStore<Constant> constantStore;
    private final IIdentifiableObjectStore<DataElement> dataElementStore;
    private final IOptionStore optionStore;
    private final IIdentifiableObjectStore<OptionSet> optionSetStore;
    private final IOrganisationUnitStore organisationUnitStore;
    private final IProgramStore programStore;
    private final IIdentifiableObjectStore<TrackedEntity> trackedEntityStore;
    private final IIdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore;
    private final IProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;
    private final IProgramStageDataElementStore programStageDataElementStore;
    private final IProgramIndicatorStore programIndicatorStore;
    private final IProgramStageSectionStore programStageSectionStore;
    private final IProgramStageStore programStageStore;
    private final IProgramRuleStore programRuleStore;
    private final IProgramRuleActionStore programRuleActionStore;
    private final IProgramRuleVariableStore programRuleVariableStore;
    private final IIdentifiableObjectStore<RelationshipType> relationshipTypeStore;

    private final IDataSetStore dataSetStore;

    //Tracker store objects
    private final ITrackedEntityAttributeValueStore trackedEntityAttributeValueStore;
    private final IRelationshipStore relationshipStore;
    private final ITrackedEntityInstanceStore trackedEntityInstanceStore;
    private final ITrackedEntityDataValueStore trackedEntityDataValueStore;
    private final IEventStore eventStore;
    private final IEnrollmentStore enrollmentStore;

    // Dashboard store objects
    private final IIdentifiableObjectStore<Dashboard> dashboardStore;
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;
    private final IDashboardItemContentStore dashboardItemContentStore;

    // Interpretation store objects
    private final IInterpretationStore interpretationStore;
    private final IInterpretationCommentStore interpretationCommentStore;
    private final IInterpretationElementStore interpretationElementStore;

    // User store object
    private final IUserAccountStore userAccountStore;
    private final IUserStore userStore;

    // Models store
    private final IModelsStore modelsStore;

    private final IStateStore stateStore;

    public Models(Context context) {
        FlowManager.init(context);

        constantStore = new ConstantStore();
        dataElementStore = new DataElementStore();
        optionStore = new OptionStore();
        optionSetStore = new OptionSetStore(optionStore);
        organisationUnitStore = new OrganisationUnitStore();
        trackedEntityStore = new TrackedEntityStore();
        trackedEntityAttributeStore = new TrackedEntityAttributeStore();
        programTrackedEntityAttributeStore = new ProgramTrackedEntityAttributeStore();
        programStageDataElementStore = new ProgramStageDataElementStore();
        programIndicatorStore = new ProgramIndicatorStore();
        programStageSectionStore = new ProgramStageSectionStore(programStageDataElementStore);
        programStageStore = new ProgramStageStore(programStageDataElementStore,
                programStageSectionStore);
        programStore = new ProgramStore(programStageStore, programTrackedEntityAttributeStore);
        programRuleActionStore = new ProgramRuleActionStore();
        programRuleStore = new ProgramRuleStore(programRuleActionStore);
        programRuleVariableStore = new ProgramRuleVariableStore();
        relationshipTypeStore = new RelationshipTypeStore();

        dataSetStore = new DataSetStore();

        trackedEntityAttributeValueStore = new TrackedEntityAttributeValueStore(programStore);
        relationshipStore = new RelationshipStore();
        trackedEntityInstanceStore = new TrackedEntityInstanceStore(relationshipStore,
                trackedEntityAttributeValueStore);
        trackedEntityDataValueStore = new TrackedEntityDataValueStore();
        eventStore = new EventStore(trackedEntityDataValueStore);
        enrollmentStore = new EnrollmentStore(eventStore, trackedEntityAttributeValueStore);

        dashboardStore = new DashboardStore();
        dashboardItemStore = new DashboardItemStore();
        dashboardElementStore = new DashboardElementStore();
        dashboardItemContentStore = new DashboardItemContentStore();

        interpretationStore = new InterpretationStore();
        interpretationCommentStore = new InterpretationCommentStore();
        interpretationElementStore = new InterpretationElementStore();

        userAccountStore = new UserAccountStore();
        userStore = new UserStore();

        modelsStore = new ModelsStore();

        stateStore = new StateStore();
    }

    public static void init(Context context) {
        models = new Models(context);
    }

    private static Models getInstance() {
        if (models == null) {
            throw new IllegalArgumentException("You should call inti method first");
        }

        return models;
    }

    public static IEnrollmentStore enrollments() {
        return getInstance().enrollmentStore;
    }

    public static IEventStore events() {
        return getInstance().eventStore;
    }

    public static ITrackedEntityDataValueStore trackedEntityDataValues() {
        return getInstance().trackedEntityDataValueStore;
    }

    public static ITrackedEntityInstanceStore trackedEntityInstances() {
        return getInstance().trackedEntityInstanceStore;
    }

    public static IRelationshipStore relationships() {
        return getInstance().relationshipStore;
    }

    public static ITrackedEntityAttributeValueStore trackedEntityAttributeValues() {
        return getInstance().trackedEntityAttributeValueStore;
    }

    public static IIdentifiableObjectStore<RelationshipType> relationshipTypes() {
        return getInstance().relationshipTypeStore;
    }

    public static IProgramRuleStore programRules() {
        return getInstance().programRuleStore;
    }

    public static IProgramRuleVariableStore programRuleVariables() {
        return getInstance().programRuleVariableStore;
    }

    public static IProgramRuleActionStore programRuleActions() {
        return getInstance().programRuleActionStore;
    }

    public static IProgramStageStore programStages() {
        return getInstance().programStageStore;
    }

    public static IProgramStageSectionStore programStageSections() {
        return getInstance().programStageSectionStore;
    }

    public static IProgramIndicatorStore programIndicators() {
        return getInstance().programIndicatorStore;
    }

    public static IProgramStageDataElementStore programStageDataElements() {
        return getInstance().programStageDataElementStore;
    }

    public static IProgramTrackedEntityAttributeStore programTrackedEntityAttributes() {
        return getInstance().programTrackedEntityAttributeStore;
    }

    public static IIdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributes() {
        return getInstance().trackedEntityAttributeStore;
    }

    public static IIdentifiableObjectStore<TrackedEntity> trackedEntities() {
        return getInstance().trackedEntityStore;
    }

    public static IProgramStore programs() {
        return getInstance().programStore;
    }

    public static IOrganisationUnitStore organisationUnits() {
        return getInstance().organisationUnitStore;
    }

    public static IIdentifiableObjectStore<OptionSet> optionSets() {
        return getInstance().optionSetStore;
    }

    public static IOptionStore options() {
        return getInstance().optionStore;
    }

    public static IIdentifiableObjectStore<DataElement> dataElements() {
        return getInstance().dataElementStore;
    }

    public static IIdentifiableObjectStore<Constant> constants() {
        return getInstance().constantStore;
    }

    public static IDataSetStore dataSets() {
        return getInstance().dataSetStore;
    }

    public static IIdentifiableObjectStore<Dashboard> dashboards() {
        return getInstance().dashboardStore;
    }

    public static IDashboardItemStore dashboardItems() {
        return getInstance().dashboardItemStore;
    }

    public static IDashboardElementStore dashboardElements() {
        return getInstance().dashboardElementStore;
    }

    public static IDashboardItemContentStore dashboardItemContent() {
        return getInstance().dashboardItemContentStore;
    }

    public static IInterpretationStore interpretations() {
        return getInstance().interpretationStore;
    }

    public static IInterpretationCommentStore interpretationComments() {
        return getInstance().interpretationCommentStore;
    }

    public static IInterpretationElementStore interpretationElements() {
        return getInstance().interpretationElementStore;
    }

    public static IUserAccountStore userAccount() {
        return getInstance().userAccountStore;
    }

    public static IUserStore users() {
        return getInstance().userStore;
    }

    public static IModelsStore modelsStore() {
        return getInstance().modelsStore;
    }

    public static IStateStore stateStore() {
        return getInstance().stateStore;
    }
}
