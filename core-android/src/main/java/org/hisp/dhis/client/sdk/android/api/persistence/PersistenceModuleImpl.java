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

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.common.StateStoreImpl;
import org.hisp.dhis.client.sdk.android.dataelement.DataElementStoreImpl;
import org.hisp.dhis.client.sdk.android.event.EventStoreImpl;
import org.hisp.dhis.client.sdk.android.optionset.OptionSetStoreImpl;
import org.hisp.dhis.client.sdk.android.optionset.OptionStoreImpl;
import org.hisp.dhis.client.sdk.android.organisationunit.OrganisationUnitStoreImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramIndicatorStoreImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleActionStoreImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleStoreImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramRuleVariableStoreImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStageDataElementStoreImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStageSectionStoreImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStageStoreImpl;
import org.hisp.dhis.client.sdk.android.program.ProgramStoreImpl;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeStoreImpl;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityStoreImpl;
import org.hisp.dhis.client.sdk.android.user.UserAccountStoreImpl;
import org.hisp.dhis.client.sdk.core.common.StateStore;
import org.hisp.dhis.client.sdk.core.common.persistence.PersistenceModule;
import org.hisp.dhis.client.sdk.core.common.persistence.TransactionManager;
import org.hisp.dhis.client.sdk.core.dataelement.DataElementStore;
import org.hisp.dhis.client.sdk.core.event.EventStore;
import org.hisp.dhis.client.sdk.core.optionset.OptionSetStore;
import org.hisp.dhis.client.sdk.core.optionset.OptionStore;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.client.sdk.core.program.ProgramIndicatorStore;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleActionStore;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleStore;
import org.hisp.dhis.client.sdk.core.program.ProgramRuleVariableStore;
import org.hisp.dhis.client.sdk.core.program.ProgramStageDataElementStore;
import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionStore;
import org.hisp.dhis.client.sdk.core.program.ProgramStageStore;
import org.hisp.dhis.client.sdk.core.program.ProgramStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityStore;
import org.hisp.dhis.client.sdk.core.user.UserAccountStore;

public class PersistenceModuleImpl implements PersistenceModule {
    private final TransactionManager transactionManager;
    private final StateStore stateStore;
    private final UserAccountStore userAccountStore;
    private final ProgramStore programStore;
    private final ProgramStageStore programStageStore;
    private final ProgramStageSectionStore programStageSectionStore;
    private final ProgramRuleStore programRuleStore;
    private final ProgramRuleActionStore programRuleActionStore;
    private final ProgramRuleVariableStore programRuleVariableStore;
    private final ProgramIndicatorStore programIndicatorStore;
    private final TrackedEntityAttributeStore trackedEntityAttributeStore;
    private final ProgramStageDataElementStore programStageDataElementStore;
    private final OrganisationUnitStore organisationUnitStore;
    private final EventStore eventStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final DataElementStore dataElementStore;
    private final OptionStore optionStore;
    private final OptionSetStore optionSetStore;
    private final TrackedEntityStore trackedEntityStore;

    public PersistenceModuleImpl(Context context) {
        FlowConfig flowConfig = new FlowConfig
                .Builder(context)
                .build();
        FlowManager.init(flowConfig);

        transactionManager = new TransactionManagerImpl();
        stateStore = new StateStoreImpl(EventFlow.MAPPER);
        programStore = new ProgramStoreImpl(transactionManager);
        programStageStore = new ProgramStageStoreImpl(transactionManager);
        programStageSectionStore = new ProgramStageSectionStoreImpl(transactionManager);
        programRuleStore = new ProgramRuleStoreImpl(transactionManager);
        programRuleActionStore = new ProgramRuleActionStoreImpl();
        programRuleVariableStore = new ProgramRuleVariableStoreImpl();
        programIndicatorStore = new ProgramIndicatorStoreImpl();
        trackedEntityAttributeStore = new TrackedEntityAttributeStoreImpl();
        programStageDataElementStore = new ProgramStageDataElementStoreImpl();
        dataElementStore = new DataElementStoreImpl();

        userAccountStore = new UserAccountStoreImpl(stateStore);
        organisationUnitStore = new OrganisationUnitStoreImpl(transactionManager);

        trackedEntityDataValueStore = new TrackedEntityDataValueStoreImpl();
        eventStore = new EventStoreImpl(stateStore, trackedEntityDataValueStore, transactionManager);
        trackedEntityStore = new TrackedEntityStoreImpl();

        optionStore = new OptionStoreImpl();
        optionSetStore = new OptionSetStoreImpl();
    }

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public StateStore getStateStore() {
        return stateStore;
    }

    @Override
    public UserAccountStore getUserAccountStore() {
        return userAccountStore;
    }

    @Override
    public ProgramStore getProgramStore() {
        return programStore;
    }

    @Override
    public ProgramStageStore getProgramStageStore() {
        return programStageStore;
    }

    @Override
    public ProgramStageSectionStore getProgramStageSectionStore() {
        return programStageSectionStore;
    }

    @Override
    public ProgramStageDataElementStore getProgramStageDataElementStore() {
        return programStageDataElementStore;
    }

    @Override
    public ProgramRuleStore getProgramRuleStore() {
        return programRuleStore;
    }

    @Override
    public ProgramRuleActionStore getProgramRuleActionStore() {
        return programRuleActionStore;
    }

    @Override
    public ProgramRuleVariableStore getProgramRuleVariableStore() {
        return programRuleVariableStore;
    }

    @Override
    public ProgramIndicatorStore getProgramIndicatorStore() {
        return programIndicatorStore;
    }

    @Override
    public TrackedEntityAttributeStore getTrackedEntityAttributeStore() {
        return trackedEntityAttributeStore;
    }

    @Override
    public OrganisationUnitStore getOrganisationUnitStore() {
        return organisationUnitStore;
    }

    @Override
    public EventStore getEventStore() {
        return eventStore;
    }

    @Override
    public TrackedEntityStore getTrackedEntityStore() {
        return trackedEntityStore;
    }

    @Override
    public TrackedEntityDataValueStore getTrackedEntityDataValueStore() {
        return trackedEntityDataValueStore;
    }

    @Override
    public DataElementStore getDataElementStore() {
        return dataElementStore;
    }

    @Override
    public OptionSetStore getOptionSetStore() {
        return optionSetStore;
    }

    @Override
    public OptionStore getOptionStore() {
        return optionStore;
    }

    @Override
    public boolean deleteAllTables() {
        return stateStore.deleteAll() &&
                userAccountStore.deleteAll() &&
                programStore.deleteAll() &&
                programStageStore.deleteAll() &&
                programStageSectionStore.deleteAll() &&
                programRuleStore.deleteAll() &&
                programRuleActionStore.deleteAll() &&
                programRuleVariableStore.deleteAll() &&
                programIndicatorStore.deleteAll() &&
                trackedEntityAttributeStore.deleteAll() &&
                programStageDataElementStore.deleteAll() &&
                organisationUnitStore.deleteAll() &&
                eventStore.deleteAll() &&
                trackedEntityDataValueStore.deleteAll() &&
                dataElementStore.deleteAll() &&
                optionStore.deleteAll() &&
                trackedEntityStore.deleteAll() &&
                optionSetStore.deleteAll();
    }
}
