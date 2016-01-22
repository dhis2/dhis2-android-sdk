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

package org.hisp.dhis.client.sdk.android.api.modules;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowManager;

import org.hisp.dhis.client.sdk.android.api.utils.MapperModuleProvider;
import org.hisp.dhis.client.sdk.android.common.FailedItemStore;
import org.hisp.dhis.client.sdk.android.common.ModelsStore;
import org.hisp.dhis.client.sdk.android.common.state.StateStore;
import org.hisp.dhis.client.sdk.android.constant.ConstantStore;
import org.hisp.dhis.client.sdk.android.dataelement.DataElementStore;
import org.hisp.dhis.client.sdk.android.dataset.DataSetStore;
import org.hisp.dhis.client.sdk.android.enrollment.EnrollmentStore;
import org.hisp.dhis.client.sdk.android.event.EventStore;
import org.hisp.dhis.client.sdk.android.interpretation.InterpretationCommentStore;
import org.hisp.dhis.client.sdk.android.interpretation.InterpretationElementStore;
import org.hisp.dhis.client.sdk.android.interpretation.InterpretationStore;
import org.hisp.dhis.client.sdk.android.optionset.OptionSetStore;
import org.hisp.dhis.client.sdk.android.optionset.OptionStore;
import org.hisp.dhis.client.sdk.android.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.client.sdk.android.program.ProgramIndicatorStore;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleActionStore;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleStore;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleVariableStore;
import org.hisp.dhis.client.sdk.android.program.ProgramStageDataElementStore;
import org.hisp.dhis.client.sdk.android.program.ProgramStageSectionStore;
import org.hisp.dhis.client.sdk.android.program.ProgramStageStore;
import org.hisp.dhis.client.sdk.android.program.ProgramStore;
import org.hisp.dhis.client.sdk.android.program.ProgramTrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.android.relationship.RelationshipStore;
import org.hisp.dhis.client.sdk.android.relationship.RelationshipTypeStore;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeValueStore;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityStore;
import org.hisp.dhis.client.sdk.android.user.UserAccountStore;
import org.hisp.dhis.client.sdk.android.user.UserStore;
import org.hisp.dhis.client.sdk.core.common.IFailedItemStore;
import org.hisp.dhis.client.sdk.core.common.IModelsStore;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.common.persistence.IIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardElementStore;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardItemContentStore;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardItemStore;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardStore;
import org.hisp.dhis.client.sdk.core.dataset.IDataSetStore;
import org.hisp.dhis.client.sdk.core.enrollment.IEnrollmentStore;
import org.hisp.dhis.client.sdk.core.event.IEventStore;
import org.hisp.dhis.client.sdk.core.interpretation.IInterpretationCommentStore;
import org.hisp.dhis.client.sdk.core.interpretation.IInterpretationElementStore;
import org.hisp.dhis.client.sdk.core.optionset.IOptionStore;
import org.hisp.dhis.client.sdk.core.organisationunit.IOrganisationUnitStore;
import org.hisp.dhis.client.sdk.core.program.IProgramIndicatorStore;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleActionStore;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleStore;
import org.hisp.dhis.client.sdk.core.program.IProgramRuleVariableStore;
import org.hisp.dhis.client.sdk.core.program.IProgramStageDataElementStore;
import org.hisp.dhis.client.sdk.core.program.IProgramStageSectionStore;
import org.hisp.dhis.client.sdk.core.program.IProgramStageStore;
import org.hisp.dhis.client.sdk.core.program.IProgramStore;
import org.hisp.dhis.client.sdk.core.program.IProgramTrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.core.relationship.IRelationshipStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeValueStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityInstanceStore;
import org.hisp.dhis.client.sdk.core.user.IUserAccountStore;
import org.hisp.dhis.client.sdk.core.user.IUserStore;
import org.hisp.dhis.client.sdk.models.constant.Constant;
import org.hisp.dhis.client.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;

@Deprecated
public final class Models {
//    private static Models models;
//
//    // Meta data store objects
//    private final IIdentifiableObjectStore<Constant> constantStore;
//    private final IIdentifiableObjectStore<DataElement> dataElementStore;
//    private final IOptionStore optionStore;
//    private final IIdentifiableObjectStore<OptionSet> optionSetStore;
//    private final IOrganisationUnitStore organisationUnitStore;
//    private final IProgramStore programStore;
//    private final IIdentifiableObjectStore<TrackedEntity> trackedEntityStore;
//    private final IIdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore;
//    private final IProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;
//    private final IProgramStageDataElementStore programStageDataElementStore;
//    private final IProgramIndicatorStore programIndicatorStore;
//    private final IProgramStageSectionStore programStageSectionStore;
//    private final IProgramStageStore programStageStore;
//    private final IProgramRuleStore programRuleStore;
//    private final IProgramRuleActionStore programRuleActionStore;
//    private final IProgramRuleVariableStore programRuleVariableStore;
//    private final IIdentifiableObjectStore<RelationshipType> relationshipTypeStore;
//
//    private final IDataSetStore dataSetStore;
//
//    //Tracker store objects
//    private final ITrackedEntityAttributeValueStore trackedEntityAttributeValueStore;
//    private final IRelationshipStore relationshipStore;
//    private final ITrackedEntityInstanceStore trackedEntityInstanceStore;
//    private final ITrackedEntityDataValueStore trackedEntityDataValueStore;
//    private final IEventStore eventStore;
//    private final IEnrollmentStore enrollmentStore;
//
//    // Dashboard store objects
//    private IDashboardStore dashboardStore;
//    private IDashboardItemStore dashboardItemStore;
//    private IDashboardElementStore dashboardElementStore;
//    private IDashboardItemContentStore dashboardItemContentStore;
//
//    // Interpretation store objects
//    private final IIdentifiableObjectStore<Interpretation> interpretationStore;
//    private final IInterpretationCommentStore interpretationCommentStore;
//    private final IInterpretationElementStore interpretationElementStore;
//
//    // User store object
//    private final IUserAccountStore userAccountStore;
//    private final IUserStore userStore;
//
//    // Models store
//    private final IModelsStore modelsStore;
//
//    private final IStateStore stateStore;
//
//    private final IFailedItemStore failedItemStore;
//
//    public Models(Context context) {
//        FlowManager.init(context);
//
//        stateStore = new StateStore(MapperModuleProvider.getInstance().getStateMapper(),
//                MapperModuleProvider.getInstance().getDashboardMapper(),
//                MapperModuleProvider.getInstance().getDashboardItemMapper(),
//                MapperModuleProvider.getInstance().getDashboardElementMapper(),
//                MapperModuleProvider.getInstance().getEventMapper(),
//                MapperModuleProvider.getInstance().getEnrollmentMapper(),
//                MapperModuleProvider.getInstance().getTrackedEntityInstanceMapper(),
//                MapperModuleProvider.getInstance().getOrganisationUnitMapper());
//        failedItemStore = new FailedItemStore();
//        modelsStore = new ModelsStore();
//
//        relationshipStore = new RelationshipStore(MapperModuleProvider.getInstance().getRelationshipMapper(), stateStore);
//        relationshipTypeStore = new RelationshipTypeStore(MapperModuleProvider.getInstance().getRelationshipTypeMapper());
//
//        optionStore = new OptionStore(MapperModuleProvider.getInstance().getOptionMapper());
//        optionSetStore = new OptionSetStore(MapperModuleProvider.getInstance().getOptionSetMapper(), optionStore);
//
//        organisationUnitStore = new OrganisationUnitStore(MapperModuleProvider.getInstance().getOrganisationUnitMapper());
//        dataSetStore = new DataSetStore(MapperModuleProvider.getInstance().getDataSetMapper(), MapperModuleProvider.getInstance().getOrganisationUnitMapper());
//        dataElementStore = new DataElementStore(MapperModuleProvider.getInstance().getDataElementMapper());
//        constantStore = new ConstantStore(MapperModuleProvider.getInstance().getConstantMapper());
//
//        userAccountStore = new UserAccountStore(MapperModuleProvider.getInstance().getUserAccountMapper());
//        userStore = new UserStore(MapperModuleProvider.getInstance().getUserMapper());
//
//
//        /////////////////////////////////////////////////////////////////////////////////////
//        // Dashboard stores.
//        /////////////////////////////////////////////////////////////////////////////////////
//
//        // dashboardStore = new DashboardStore(stateStore);
//        // dashboardItemStore = new DashboardItemStore(stateStore);
//        // dashboardElementStore = new DashboardElementStore(stateStore);
//        // dashboardItemContentStore = new DashboardContentStore();
//
//
//        /////////////////////////////////////////////////////////////////////////////////////
//        // Interpretation stores.
//        /////////////////////////////////////////////////////////////////////////////////////
//
//        interpretationStore = new InterpretationStore();
//        interpretationCommentStore = new InterpretationCommentStore();
//        interpretationElementStore = new InterpretationElementStore();
//
//
//        /////////////////////////////////////////////////////////////////////////////////////
//        // Program stores.
//        /////////////////////////////////////////////////////////////////////////////////////
//
//        programTrackedEntityAttributeStore = new ProgramTrackedEntityAttributeStore(MapperModuleProvider.getInstance().getProgramTrackedEntityAttributeMapper());
//        programStageDataElementStore = new ProgramStageDataElementStore(MapperModuleProvider.getInstance().getProgramStageDataElementMapper());
//        programIndicatorStore = new ProgramIndicatorStore(MapperModuleProvider.getInstance().getProgramIndicatorMapper());
//        programStageSectionStore = new ProgramStageSectionStore(MapperModuleProvider.getInstance().getProgramStageSectionMapper());
//        programStageStore = new ProgramStageStore(MapperModuleProvider.getInstance().getProgramStageMapper());
//        programStore = new ProgramStore(MapperModuleProvider.getInstance().getProgramMapper(), transactionManager, MapperModuleProvider.getInstance().getOrganisationUnitMapper());
//        programRuleActionStore = new ProgramRuleActionStore(MapperModuleProvider.getInstance().getProgramRuleActionMapper());
//        programRuleStore = new ProgramRuleStore(MapperModuleProvider.getInstance().getProgramRuleMapper(), programRuleActionStore, MapperModuleProvider.getInstance().getProgramRuleActionMapper());
//        programRuleVariableStore = new ProgramRuleVariableStore(MapperModuleProvider.getInstance().getProgramRuleVariableMapper());
//
//
//        /////////////////////////////////////////////////////////////////////////////////////
//        // Tracker meta-data stores.
//        /////////////////////////////////////////////////////////////////////////////////////
//
//        trackedEntityStore = new TrackedEntityStore(MapperModuleProvider.getInstance().getTrackedEntityMapper());
//        trackedEntityAttributeStore = new TrackedEntityAttributeStore();
//        trackedEntityAttributeValueStore = new TrackedEntityAttributeValueStore();
//        trackedEntityInstanceStore = new TrackedEntityInstanceStore();
//        trackedEntityDataValueStore = new TrackedEntityDataValueStore();
//
//
//        /////////////////////////////////////////////////////////////////////////////////////
//        // Tracker data stores.
//        /////////////////////////////////////////////////////////////////////////////////////
//
//        eventStore = new EventStore();
//        enrollmentStore = new EnrollmentStore();
//    }
//
//    public static void init(Context context) {
//        models = new Models(context);
//    }
//
//    private static Models getInstance() {
//        if (models == null) {
//            throw new IllegalArgumentException("You should call inti method first");
//        }
//
//        return models;
//    }
//
//    public static IFailedItemStore failedItems() {
//        return getInstance().failedItemStore;
//    }
//
//    public static IEnrollmentStore enrollments() {
//        return getInstance().enrollmentStore;
//    }
//
//    public static IEventStore events() {
//        return getInstance().eventStore;
//    }
//
//    public static ITrackedEntityDataValueStore trackedEntityDataValues() {
//        return getInstance().trackedEntityDataValueStore;
//    }
//
//    public static ITrackedEntityInstanceStore trackedEntityInstances() {
//        return getInstance().trackedEntityInstanceStore;
//    }
//
//    public static IRelationshipStore relationships() {
//        return getInstance().relationshipStore;
//    }
//
//    public static ITrackedEntityAttributeValueStore trackedEntityAttributeValues() {
//        return getInstance().trackedEntityAttributeValueStore;
//    }
//
//    public static IIdentifiableObjectStore<RelationshipType> relationshipTypes() {
//        return getInstance().relationshipTypeStore;
//    }
//
//    public static IProgramRuleStore programRules() {
//        return getInstance().programRuleStore;
//    }
//
//    public static IProgramRuleVariableStore programRuleVariables() {
//        return getInstance().programRuleVariableStore;
//    }
//
//    public static IProgramRuleActionStore programRuleActions() {
//        return getInstance().programRuleActionStore;
//    }
//
//    public static IProgramStageStore programStages() {
//        return getInstance().programStageStore;
//    }
//
//    public static IProgramStageSectionStore programStageSections() {
//        return getInstance().programStageSectionStore;
//    }
//
//    public static IProgramIndicatorStore programIndicators() {
//        return getInstance().programIndicatorStore;
//    }
//
//    public static IProgramStageDataElementStore programStageDataElements() {
//        return getInstance().programStageDataElementStore;
//    }
//
//    public static IProgramTrackedEntityAttributeStore programTrackedEntityAttributes() {
//        return getInstance().programTrackedEntityAttributeStore;
//    }
//
//    public static IIdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributes() {
//        return getInstance().trackedEntityAttributeStore;
//    }
//
//    public static IIdentifiableObjectStore<TrackedEntity> trackedEntities() {
//        return getInstance().trackedEntityStore;
//    }
//
//    public static IProgramStore programs() {
//        return getInstance().programStore;
//    }
//
//    public static IOrganisationUnitStore organisationUnits() {
//        return getInstance().organisationUnitStore;
//    }
//
//    public static IIdentifiableObjectStore<OptionSet> optionSets() {
//        return getInstance().optionSetStore;
//    }
//
//    public static IOptionStore options() {
//        return getInstance().optionStore;
//    }
//
//    public static IIdentifiableObjectStore<DataElement> dataElements() {
//        return getInstance().dataElementStore;
//    }
//
//    public static IIdentifiableObjectStore<Constant> constants() {
//        return getInstance().constantStore;
//    }
//
//    public static IDataSetStore dataSets() {
//        return getInstance().dataSetStore;
//    }
//
//    public static IIdentifiableObjectStore<Dashboard> dashboards() {
//        return getInstance().dashboardStore;
//    }
//
//    public static IDashboardItemStore dashboardItems() {
//        return getInstance().dashboardItemStore;
//    }
//
//    public static IDashboardElementStore dashboardElements() {
//        return getInstance().dashboardElementStore;
//    }
//
//    public static IDashboardItemContentStore dashboardItemContent() {
//        return getInstance().dashboardItemContentStore;
//    }
//
//    public static IIdentifiableObjectStore<Interpretation> interpretations() {
//        return getInstance().interpretationStore;
//    }
//
//    public static IInterpretationCommentStore interpretationComments() {
//        return getInstance().interpretationCommentStore;
//    }
//
//    public static IInterpretationElementStore interpretationElements() {
//        return getInstance().interpretationElementStore;
//    }
//
//    public static IUserAccountStore userAccount() {
//        return getInstance().userAccountStore;
//    }
//
//    public static IUserStore users() {
//        return getInstance().userStore;
//    }
//
//    public static IModelsStore modelsStore() {
//        return getInstance().modelsStore;
//    }
//
//    public static IStateStore stateStore() {
//        return getInstance().stateStore;
//    }
}
