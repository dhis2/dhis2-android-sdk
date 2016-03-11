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
import org.hisp.dhis.client.sdk.android.common.ModelStore;
import org.hisp.dhis.client.sdk.android.common.state.StateStore;
import org.hisp.dhis.client.sdk.android.constant.ConstantStore;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardContentStore;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardElementStore;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardItemStore;
import org.hisp.dhis.client.sdk.android.dashboard.DashboardStore;
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
import org.hisp.dhis.client.sdk.android.program.ProgramStore2;
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
import org.hisp.dhis.client.sdk.core.common.persistence.IPersistenceModule;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.constant.IConstantStore;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardElementStore;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardItemContentStore;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardItemStore;
import org.hisp.dhis.client.sdk.core.dashboard.IDashboardStore;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementStore;
import org.hisp.dhis.client.sdk.core.dataset.IDataSetStore;
import org.hisp.dhis.client.sdk.core.enrollment.IEnrollmentStore;
import org.hisp.dhis.client.sdk.core.event.IEventStore;
import org.hisp.dhis.client.sdk.core.interpretation.IInterpretationCommentStore;
import org.hisp.dhis.client.sdk.core.interpretation.IInterpretationElementStore;
import org.hisp.dhis.client.sdk.core.optionset.IOptionSetStore;
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
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeValueStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityInstanceStore;
import org.hisp.dhis.client.sdk.core.user.IUserAccountStore;
import org.hisp.dhis.client.sdk.core.user.IUserStore;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

public class PersistenceModule implements IPersistenceModule {

    // Utility classes.
    private final ITransactionManager transactionManager;

    // UserAccount related dependencies.
    private final IUserAccountStore userAccountStore;


    private final IStateStore stateStore;
    private final IDashboardStore dashboardStore;
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;
    private final IDashboardItemContentStore dashboardItemContentStore;

    // Meta data store objects
    private final IConstantStore constantStore;
    private final IDataElementStore dataElementStore;
    private final IOptionStore optionStore;
    private final IOptionSetStore optionSetStore;
    private final IOrganisationUnitStore organisationUnitStore;
    private final IProgramStore programStore;
    private final IIdentifiableObjectStore<TrackedEntity> trackedEntityStore;
    private final ITrackedEntityAttributeStore trackedEntityAttributeStore;
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

    // Interpretation store objects
    private final IIdentifiableObjectStore<Interpretation> interpretationStore;
    private final IInterpretationCommentStore interpretationCommentStore;
    private final IInterpretationElementStore interpretationElementStore;

    // User store object
    private final IUserStore userStore;
    private final IFailedItemStore failedItemStore;
    private final IModelsStore modelsStore;

    public PersistenceModule(Context context) {
        FlowManager.init(context);

        transactionManager = new TransactionManager();

//        programStore = new ProgramStore(
//                MapperModuleProvider.getInstance().getProgramMapper(),
//                transactionManager, MapperModuleProvider.getInstance()
// .getOrganisationUnitMapper(), programTrackedEntityAttributeStore,
//                programStageStore, programIndicatorStore);

        programStore = new ProgramStore2(MapperModuleProvider
                .getInstance().getProgramMapper(), transactionManager);

        userAccountStore = new UserAccountStore(MapperModuleProvider.getInstance()
                .getUserAccountMapper());

        stateStore = new StateStore(MapperModuleProvider.getInstance().getStateMapper(),
                MapperModuleProvider.getInstance().getDashboardMapper(),
                MapperModuleProvider.getInstance().getDashboardItemMapper(),
                MapperModuleProvider.getInstance().getDashboardElementMapper(),
                MapperModuleProvider.getInstance().getEventMapper(),
                MapperModuleProvider.getInstance().getEnrollmentMapper(),
                MapperModuleProvider.getInstance().getTrackedEntityInstanceMapper());
        dashboardStore = new DashboardStore(MapperModuleProvider.getInstance()
                .getDashboardMapper());
        dashboardItemStore = new DashboardItemStore(MapperModuleProvider.getInstance()
                .getDashboardItemMapper());
        dashboardElementStore = new DashboardElementStore(MapperModuleProvider.getInstance()
                .getDashboardElementMapper());
        dashboardItemContentStore = new DashboardContentStore(MapperModuleProvider.getInstance()
                .getDashboardContentMapper());
        constantStore = new ConstantStore(MapperModuleProvider.getInstance().getConstantMapper());
        optionStore = new OptionStore(MapperModuleProvider.getInstance().getOptionMapper());
        optionSetStore = new OptionSetStore(MapperModuleProvider.getInstance()
                .getOptionSetMapper(), optionStore);
        dataElementStore = new DataElementStore(MapperModuleProvider.getInstance()
                .getDataElementMapper(), optionSetStore);
        organisationUnitStore = new OrganisationUnitStore(MapperModuleProvider.getInstance()
                .getOrganisationUnitMapper(), transactionManager);
        trackedEntityStore = new TrackedEntityStore(MapperModuleProvider.getInstance()
                .getTrackedEntityMapper());
        trackedEntityAttributeStore = new TrackedEntityAttributeStore(MapperModuleProvider
                .getInstance().getTrackedEntityAttributeMapper());
        programTrackedEntityAttributeStore = new ProgramTrackedEntityAttributeStore
                (MapperModuleProvider.getInstance().getProgramTrackedEntityAttributeMapper());
        programStageDataElementStore = new ProgramStageDataElementStore(MapperModuleProvider
                .getInstance().getProgramStageDataElementMapper(), dataElementStore);
        programIndicatorStore = new ProgramIndicatorStore(MapperModuleProvider.getInstance()
                .getProgramIndicatorMapper());
        programStageSectionStore = new ProgramStageSectionStore(MapperModuleProvider
                .getInstance().getProgramStageSectionMapper(), programIndicatorStore,
                programStageDataElementStore);
        programStageStore = new ProgramStageStore(MapperModuleProvider.getInstance()
                .getProgramStageMapper(), programIndicatorStore, programStageDataElementStore,
                programStageSectionStore);
        programRuleActionStore = new ProgramRuleActionStore(MapperModuleProvider.getInstance()
                .getProgramRuleActionMapper());
        programRuleStore = new ProgramRuleStore(MapperModuleProvider.getInstance()
                .getProgramRuleMapper(), programRuleActionStore, MapperModuleProvider
                .getInstance().getProgramRuleActionMapper());
        programRuleVariableStore = new ProgramRuleVariableStore(MapperModuleProvider
                .getInstance().getProgramRuleVariableMapper());
        relationshipTypeStore = new RelationshipTypeStore(MapperModuleProvider.getInstance()
                .getRelationshipTypeMapper());
        dataSetStore = new DataSetStore(MapperModuleProvider.getInstance().getDataSetMapper(),
                MapperModuleProvider.getInstance().getOrganisationUnitMapper());
        trackedEntityAttributeValueStore = new TrackedEntityAttributeValueStore
                (MapperModuleProvider.getInstance().getTrackedEntityAttributeValueMapper(),
                        stateStore, programStore);
        relationshipStore = new RelationshipStore(MapperModuleProvider.getInstance()
                .getRelationshipMapper(), stateStore);
        trackedEntityInstanceStore = new TrackedEntityInstanceStore(MapperModuleProvider
                .getInstance().getTrackedEntityInstanceMapper(), stateStore);
        trackedEntityDataValueStore = new TrackedEntityDataValueStore(MapperModuleProvider
                .getInstance().getTrackedEntityDataValueMapper(), stateStore);
        eventStore = new EventStore(MapperModuleProvider.getInstance().getEventMapper(),
                stateStore);
        enrollmentStore = new EnrollmentStore(stateStore, MapperModuleProvider.getInstance()
                .getEnrollmentMapper());
        interpretationStore = new InterpretationStore();
        interpretationCommentStore = new InterpretationCommentStore();
        interpretationElementStore = new InterpretationElementStore();
        failedItemStore = new FailedItemStore();
        userStore = new UserStore(MapperModuleProvider.getInstance().getUserMapper());
        modelsStore = new ModelStore();
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
        return stateStore;
    }

    @Override
    public IDashboardStore getDashboardStore() {
        return dashboardStore;
    }

    @Override
    public IDashboardItemStore getDashboardItemStore() {
        return dashboardItemStore;
    }

    @Override
    public IDashboardElementStore getDashboardElementStore() {
        return dashboardElementStore;
    }

    @Override
    public IDashboardItemContentStore getDashboardContentStore() {
        return dashboardItemContentStore;
    }

    @Override
    public IConstantStore getConstantStore() {
        return constantStore;
    }

    @Override
    public IDataElementStore getDataElementStore() {
        return dataElementStore;
    }

    @Override
    public IOptionStore getOptionStore() {
        return optionStore;
    }

    @Override
    public IOptionSetStore getOptionSetStore() {
        return optionSetStore;
    }

    @Override
    public IOrganisationUnitStore getOrganisationUnitStore() {
        return organisationUnitStore;
    }

    @Override
    public IProgramStore getProgramStore() {
        return programStore;
    }

    @Override
    public IIdentifiableObjectStore<TrackedEntity> getTrackedEntityStore() {
        return trackedEntityStore;
    }

    @Override
    public ITrackedEntityAttributeStore getTrackedEntityAttributeStore() {
        return trackedEntityAttributeStore;
    }

    @Override
    public IProgramTrackedEntityAttributeStore getProgramTrackedEntityAttributeStore() {
        return programTrackedEntityAttributeStore;
    }

    @Override
    public IProgramStageDataElementStore getProgramStageDataElementStore() {
        return programStageDataElementStore;
    }

    @Override
    public IProgramIndicatorStore getProgramIndicatorStore() {
        return programIndicatorStore;
    }

    @Override
    public IProgramStageSectionStore getProgramStageSectionStore() {
        return programStageSectionStore;
    }

    @Override
    public IProgramStageStore getProgramStageStore() {
        return programStageStore;
    }

    @Override
    public IProgramRuleStore getProgramRuleStore() {
        return programRuleStore;
    }

    @Override
    public IProgramRuleActionStore getProgramRuleActionStore() {
        return programRuleActionStore;
    }

    @Override
    public IProgramRuleVariableStore getProgramRuleVariableStore() {
        return programRuleVariableStore;
    }

    @Override
    public IIdentifiableObjectStore<RelationshipType> getRelationshipTypeStore() {
        return relationshipTypeStore;
    }

    @Override
    public IDataSetStore getDataStore() {
        return dataSetStore;
    }

    @Override
    public ITrackedEntityAttributeValueStore getTrackedEntityAttributeValueStore() {
        return trackedEntityAttributeValueStore;
    }

    @Override
    public IRelationshipStore getRelationshipStore() {
        return relationshipStore;
    }

    @Override
    public ITrackedEntityInstanceStore getTrackedEntityInstanceStore() {
        return trackedEntityInstanceStore;
    }

    @Override
    public ITrackedEntityDataValueStore getTrackedEntityDataValueStore() {
        return trackedEntityDataValueStore;
    }

    @Override
    public IEventStore getEventStore() {
        return eventStore;
    }

    @Override
    public IEnrollmentStore getEnrollmentStore() {
        return enrollmentStore;
    }

    @Override
    public IIdentifiableObjectStore<Interpretation> getInterpretationStore() {
        return interpretationStore;
    }

    @Override
    public IInterpretationCommentStore getInterpretationCommentStore() {
        return interpretationCommentStore;
    }

    @Override
    public IInterpretationElementStore getInterpretationElementStore() {
        return interpretationElementStore;
    }

    @Override
    public IModelsStore getModelStore() {
        return modelsStore;
    }

    @Override
    public IUserStore getUserStore() {
        return userStore;
    }

    @Override
    public IFailedItemStore getFailedItemStore() {
        return failedItemStore;
    }
}
