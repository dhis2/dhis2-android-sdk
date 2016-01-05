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

import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.UserAccount$Flow;
import org.hisp.dhis.client.sdk.android.user.UserAccountMapper;
import org.hisp.dhis.client.sdk.android.user.UserAccountStore;
import org.hisp.dhis.client.sdk.core.common.IFailedItemStore;
import org.hisp.dhis.client.sdk.core.common.IModelsStore;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.common.persistence.IIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.IPersistenceModule;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
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
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.user.UserAccount;
import org.hisp.dhis.client.sdk.models.utils.IModelUtils;
import org.hisp.dhis.client.sdk.models.utils.ModelUtils;

public class PersistenceModule implements IPersistenceModule {

    // Utility classes.
    private final ITransactionManager transactionManager;
    private final IModelUtils modelUtils;

    // UserAccount related dependencies.
    private final IMapper<UserAccount, UserAccount$Flow> userAccountMapper;
    private final IUserAccountStore userAccountStore;


//    private final IStateStore stateStore;
//    private final IDashboardStore dashboardStore;
//    private final IDashboardItemStore dashboardItemStore;
//    private final IDashboardElementStore dashboardElementStore;
//    private final IDashboardItemContentStore dashboardItemContentStore;
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
////    private final IDataSetStore dataSetStore;
//
//    //Tracker store objects
//    private final ITrackedEntityAttributeValueStore trackedEntityAttributeValueStore;
//    private final IRelationshipStore relationshipStore;
//    private final ITrackedEntityInstanceStore trackedEntityInstanceStore;
//    private final ITrackedEntityDataValueStore trackedEntityDataValueStore;
//    private final IEventStore eventStore;
//    private final IEnrollmentStore enrollmentStore;
//
//    // Interpretation store objects
//    private final IIdentifiableObjectStore<Interpretation> interpretationStore;
//    private final IInterpretationCommentStore interpretationCommentStore;
//    private final IInterpretationElementStore interpretationElementStore;

    // User store object
//    private final IUserStore userStore;
//
//    private final IFailedItemStore failedItemStore;
//
//
//    private final IStateMapper stateMapper;
//    private final IMapper<Dashboard, Dashboard$Flow> dashboardMapper;
//    private final IMapper<DashboardItem, DashboardItem$Flow> dashboardItemMapper;
//    private final IMapper<DashboardElement, DashboardElement$Flow> dashboardElementMapper;
//    private final IMapper<DashboardContent, DashboardContent$Flow> dashboardContentMapper;
//
//    private IMapper<Event, Event$Flow> eventMapper = null;
//    private IMapper<Enrollment, Enrollment$Flow> enrollmentMapper = null;
//    private IMapper<TrackedEntityInstance, TrackedEntityInstance$Flow> trackedEntityInstanceMapper = null;
//    private IMapper<TrackedEntityDataValue, TrackedEntityDataValue$Flow> trackedEntityDataValueMapper = null;
//    private IMapper<TrackedEntityAttributeValue, TrackedEntityAttributeValue$Flow> trackedEntityAttributeValueMapper = null;
//    private IMapper<Relationship, Relationship$Flow> relationshipMapper = null;
//
//    private final IMapper<Constant, Constant$Flow> constantMapper;
//    private final IMapper<DataElement, DataElement$Flow> dataElementMapper;
//    private final IMapper<Option, Option$Flow> optionMapper;
//    private final IMapper<OptionSet, OptionSet$Flow> optionSetMapper;
//    private final IMapper<OrganisationUnit, OrganisationUnit$Flow> organisationUnitMapper;
//    private final IMapper<Program, Program$Flow> programMapper;
//    private final IMapper<TrackedEntity, TrackedEntity$Flow> trackedEntityMapper;
//    private final IMapper<TrackedEntityAttribute, TrackedEntityAttribute$Flow> trackedEntityAttributeMapper;
//    private final IMapper<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttribute$Flow> programTrackedEntityAttributeMapper;
//    private final IMapper<ProgramStageDataElement, ProgramStageDataElement$Flow> programStageDataElementMapper;
//    private final IMapper<ProgramIndicator, ProgramIndicator$Flow> programIndicatorMapper;
//    private final IMapper<ProgramStageSection, ProgramStageSection$Flow> programStageSectionMapper;
//    private final IMapper<ProgramStage, ProgramStage$Flow> programStageMapper;
//    private final IMapper<ProgramRule, ProgramRule$Flow> programRuleMapper;
//    private final IMapper<ProgramRuleAction, ProgramRuleAction$Flow> programRuleActionMapper;
//    private final IMapper<ProgramRuleVariable, ProgramRuleVariable$Flow> programRuleVariableMapper;
//    private final IMapper<RelationshipType, RelationshipType$Flow> relationshipTypeMapper;
//    //    private final IMapper<DataSet, DataSet$Flow> dataSetMapper;
//
//    private final IMapper<User, User$Flow> userMapper;
//
//    private final IModelsStore modelsStore;

    public PersistenceModule(Context context) {
        FlowManager.init(context);

        modelUtils = new ModelUtils();
        transactionManager = new TransactionManager(modelUtils);

        userAccountMapper = new UserAccountMapper();
        userAccountStore = new UserAccountStore(userAccountMapper);

//        stateMapper = new StateMapper();
//        dashboardMapper = new DashboardMapper();
//        dashboardItemMapper = new DashboardItemMapper(dashboardMapper);
//        dashboardElementMapper = new DashboardElementMapper(dashboardItemMapper);
//        dashboardContentMapper = new DashboardContentMapper();
//        trackedEntityDataValueMapper = new TrackedEntityDataValueMapper(eventMapper);
//        eventMapper = new EventMapper(enrollmentMapper, trackedEntityInstanceMapper, trackedEntityDataValueMapper);
//        enrollmentMapper = new EnrollmentMapper(trackedEntityInstanceMapper, eventMapper, trackedEntityAttributeValueMapper);
//        trackedEntityInstanceMapper = new TrackedEntityInstanceMapper(trackedEntityAttributeValueMapper, relationshipMapper);
//        constantMapper = new ConstantMapper();
//        dataElementMapper = new DataElementMapper();
//        optionMapper = new OptionMapper();
//        optionSetMapper = new OptionSetMapper(optionMapper);
//        organisationUnitMapper = new OrganisationUnitMapper();
//        trackedEntityMapper = new TrackedEntityMapper();
//        trackedEntityAttributeMapper = new TrackedEntityAttributeMapper();
//        programTrackedEntityAttributeMapper = new ProgramTrackedEntityAttributeMapper();
//        programStageDataElementMapper = new ProgramStageDataElementMapper();
//        programIndicatorMapper = new ProgramIndicatorMapper();
//        programStageSectionMapper = new ProgramStageSectionMapper(programStageDataElementMapper, programIndicatorMapper);
//        programStageMapper = new ProgramStageMapper(programStageDataElementMapper, programStageSectionMapper, programIndicatorMapper);
//        programRuleActionMapper = new ProgramRuleActionMapper();
//        programRuleMapper = new ProgramRuleMapper(programRuleActionMapper);
//        programRuleVariableMapper = new ProgramRuleVariableMapper();
//        relationshipTypeMapper = new RelationshipTypeMapper();
//        programMapper = new ProgramMapper(programStageMapper, programTrackedEntityAttributeMapper);
////        dataSetMapper = null;//new DataSetMapper();
//
//        userMapper = new UserMapper();
//
//        stateStore = new StateStore(stateMapper, dashboardMapper, dashboardItemMapper,
//                dashboardElementMapper, eventMapper, enrollmentMapper, trackedEntityInstanceMapper);
//        dashboardStore = new DashboardStore(dashboardMapper);
//        dashboardItemStore = new DashboardItemStore(dashboardItemMapper);
//        dashboardElementStore = new DashboardElementStore(dashboardElementMapper);
//        dashboardItemContentStore = new DashboardContentStore(dashboardContentMapper);
//        constantStore = new ConstantStore(constantMapper);
//        dataElementStore = new DataElementStore(dataElementMapper);
//        optionStore = new OptionStore(optionMapper);
//        optionSetStore = new OptionSetStore(optionSetMapper, optionStore);
//        organisationUnitStore = new OrganisationUnitStore(organisationUnitMapper);
//        programStore = new ProgramStore(programMapper, transactionManager, organisationUnitMapper);
//        trackedEntityStore = new TrackedEntityStore(trackedEntityMapper);
//        trackedEntityAttributeStore = new TrackedEntityAttributeStore(trackedEntityAttributeMapper);
//        programTrackedEntityAttributeStore = new ProgramTrackedEntityAttributeStore(programTrackedEntityAttributeMapper);
//        programStageDataElementStore = new ProgramStageDataElementStore(programStageDataElementMapper);
//        programIndicatorStore = new ProgramIndicatorStore(programIndicatorMapper);
//        programStageSectionStore = new ProgramStageSectionStore(programStageSectionMapper);
//        programStageStore = new ProgramStageStore(programStageMapper);
//        programRuleActionStore = new ProgramRuleActionStore(programRuleActionMapper);
//        programRuleStore = new ProgramRuleStore(programRuleMapper, programRuleActionStore, programRuleActionMapper);
//        programRuleVariableStore = new ProgramRuleVariableStore(programRuleVariableMapper);
//        relationshipTypeStore = new RelationshipTypeStore(relationshipTypeMapper);
////        dataSetStore = new DataSetStore(dataSetMapper, organisationUnitMapper);
//        trackedEntityAttributeValueStore = new TrackedEntityAttributeValueStore(trackedEntityAttributeValueMapper, stateStore, programStore);
//        relationshipStore = new RelationshipStore(relationshipMapper, stateStore);
//        trackedEntityInstanceStore = new TrackedEntityInstanceStore(trackedEntityInstanceMapper, stateStore);
//        trackedEntityDataValueStore = new TrackedEntityDataValueStore(trackedEntityDataValueMapper, stateStore);
//        eventStore = new EventStore(eventMapper, stateStore);
//        enrollmentStore = new EnrollmentStore(eventStore, stateStore, trackedEntityAttributeValueStore, enrollmentMapper);
//        interpretationStore = new InterpretationStore();
//        interpretationCommentStore = new InterpretationCommentStore();
//        interpretationElementStore = new InterpretationElementStore();
//        failedItemStore = new FailedItemStore();
//        userStore = new UserStore(userMapper);
//
//        modelsStore = new ModelStore();
    }

    @Override
    public ITransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public IUserAccountStore getUserAccountStore() {
        return userAccountStore;
    }


    @Override
    public IStateStore getStateStore() {
//        return stateStore;
        return null;
    }

    @Override
    public IDashboardStore getDashboardStore() {
//        return dashboardStore;
        return null;
    }

    @Override
    public IDashboardItemStore getDashboardItemStore() {
//        return dashboardItemStore;
        return null;
    }

    @Override
    public IDashboardElementStore getDashboardElementStore() {
//        return dashboardElementStore;
        return null;
    }

    @Override
    public IDashboardItemContentStore getDashboardContentStore() {
//        return dashboardItemContentStore;
        return null;
    }

    @Override
    public IIdentifiableObjectStore<Constant> getConstantStore() {
//        return constantStore;
        return null;
    }

    @Override
    public IIdentifiableObjectStore<DataElement> getDataElementStore() {
//        return dataElementStore;
        return null;
    }

    @Override
    public IOptionStore getOptionStore() {
//        return optionStore;
        return null;
    }

    @Override
    public IIdentifiableObjectStore<OptionSet> getOptionSetStore() {
//        return optionSetStore;
        return null;
    }

    @Override
    public IOrganisationUnitStore getOrganisationUnitStore() {
//        return organisationUnitStore;
        return null;
    }

    @Override
    public IProgramStore getProgramStore() {
//        return programStore;
        return null;
    }

    @Override
    public IIdentifiableObjectStore<TrackedEntity> getTrackedEntityStore() {
//        return trackedEntityStore;
        return null;
    }

    @Override
    public IIdentifiableObjectStore<TrackedEntityAttribute> getTrackedEntityAttributeStore() {
//        return trackedEntityAttributeStore;
        return null;
    }

    @Override
    public IProgramTrackedEntityAttributeStore getProgramTrackedEntityAttributeStore() {
//        return programTrackedEntityAttributeStore;
        return null;
    }

    @Override
    public IProgramStageDataElementStore getProgramStageDataElementStore() {
//        return programStageDataElementStore;
        return null;
    }

    @Override
    public IProgramIndicatorStore getProgramIndicatorStore() {
//        return programIndicatorStore;
        return null;
    }

    @Override
    public IProgramStageSectionStore getProgramStageSectionStore() {
//        return programStageSectionStore;
        return null;
    }

    @Override
    public IProgramStageStore getProgramStageStore() {
//        return programStageStore;
        return null;
    }

    @Override
    public IProgramRuleStore getProgramRuleStore() {
//        return programRuleStore;
        return null;
    }

    @Override
    public IProgramRuleActionStore getProgramRuleActionStore() {
//        return programRuleActionStore;
        return null;
    }

    @Override
    public IProgramRuleVariableStore getProgramRuleVariableStore() {
//        return programRuleVariableStore;
        return null;
    }

    @Override
    public IIdentifiableObjectStore<RelationshipType> getRelationshipTypeStore() {
//        return relationshipTypeStore;
        return null;
    }

    @Override
    public IDataSetStore getDataStore() {
//        return dataSetStore;
        return null;
    }

    @Override
    public ITrackedEntityAttributeValueStore getTrackedEntityAttributeValueStore() {
//        return trackedEntityAttributeValueStore;
        return null;
    }

    @Override
    public IRelationshipStore getRelationshipStore() {
//        return relationshipStore;
        return null;
    }

    @Override
    public ITrackedEntityInstanceStore getTrackedEntityInstanceStore() {
//        return trackedEntityInstanceStore;
        return null;
    }

    @Override
    public ITrackedEntityDataValueStore getTrackedEntityDataValueStore() {
//        return trackedEntityDataValueStore;
        return null;
    }

    @Override
    public IEventStore getEventStore() {
//        return eventStore;
        return null;
    }

    @Override
    public IEnrollmentStore getEnrollmentStore() {
//        return enrollmentStore;
        return null;
    }

    @Override
    public IIdentifiableObjectStore<Interpretation> getInterpretationStore() {
//        return interpretationStore;
        return null;
    }

    @Override
    public IInterpretationCommentStore getInterpretationCommentStore() {
//        return interpretationCommentStore;
        return null;
    }

    @Override
    public IInterpretationElementStore getInterpretationElementStore() {
//        return interpretationElementStore;
        return null;
    }

    @Override
    public IModelsStore getModelStore() {
//        return modelsStore;
        return null;
    }

    @Override
    public IUserStore getUserStore() {
//        return userStore;
        return null;
    }

    @Override
    public IFailedItemStore getFailedItemStore() {
//        return failedItemStore;
        return null;
    }
}
