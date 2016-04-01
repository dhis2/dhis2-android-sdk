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

import com.raizlabs.android.dbflow.config.FlowManager;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.common.StateStore;
import org.hisp.dhis.client.sdk.android.dataelement.DataElementStore;
import org.hisp.dhis.client.sdk.android.event.EventStore;
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
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.android.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.android.user.UserAccountStore;
import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.common.persistence.IPersistenceModule;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.dataelement.IDataElementStore;
import org.hisp.dhis.client.sdk.core.event.IEventStore;
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
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityAttributeStore;
import org.hisp.dhis.client.sdk.core.trackedentity.ITrackedEntityDataValueStore;
import org.hisp.dhis.client.sdk.core.user.IUserAccountStore;

public class PersistenceModule implements IPersistenceModule {
    private final ITransactionManager transactionManager;
    private final IStateStore stateStore;
    private final IUserAccountStore userAccountStore;
    private final IProgramStore programStore;
    private final IProgramStageStore programStageStore;
    private final IProgramStageSectionStore programStageSectionStore;
    private final IProgramRuleStore programRuleStore;
    private final IProgramRuleActionStore programRuleActionStore;
    private final IProgramRuleVariableStore programRuleVariableStore;
    private final IProgramIndicatorStore programIndicatorStore;
    private final ITrackedEntityAttributeStore trackedEntityAttributeStore;
    private final IProgramStageDataElementStore programStageDataElementStore;
    private final IOrganisationUnitStore organisationUnitStore;
    private final IEventStore eventStore;
    private final ITrackedEntityDataValueStore trackedEntityDataValueStore;
    private final IDataElementStore dataElementStore;
    private final IOptionStore optionStore;
    private final IOptionSetStore optionSetStore;

    public PersistenceModule(Context context) {
        FlowManager.init(context);

        transactionManager = new TransactionManager();
        stateStore = new StateStore(EventFlow.MAPPER);
        programStore = new ProgramStore(transactionManager);
        programStageStore = new ProgramStageStore(transactionManager);
        programStageSectionStore = new ProgramStageSectionStore(transactionManager);
        programRuleStore = new ProgramRuleStore(transactionManager);
        programRuleActionStore = new ProgramRuleActionStore();
        programRuleVariableStore = new ProgramRuleVariableStore();
        programIndicatorStore = new ProgramIndicatorStore();
        trackedEntityAttributeStore = new TrackedEntityAttributeStore();
        programStageDataElementStore = new ProgramStageDataElementStore(transactionManager);
        dataElementStore = new DataElementStore();

        userAccountStore = new UserAccountStore();
        organisationUnitStore = new OrganisationUnitStore(transactionManager);

        trackedEntityDataValueStore = new TrackedEntityDataValueStore();
        eventStore = new EventStore(stateStore, trackedEntityDataValueStore, transactionManager);

        optionStore = new OptionStore();
        optionSetStore = new OptionSetStore();
    }

    @Override
    public ITransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public IStateStore getStateStore() {
        return stateStore;
    }

    @Override
    public IUserAccountStore getUserAccountStore() {
        return userAccountStore;
    }

    @Override
    public IProgramStore getProgramStore() {
        return programStore;
    }

    @Override
    public IProgramStageStore getProgramStageStore() {
        return programStageStore;
    }

    @Override
    public IProgramStageSectionStore getProgramStageSectionStore() {
        return programStageSectionStore;
    }

    @Override
    public IProgramStageDataElementStore getProgramStageDataElementStore() {
        return programStageDataElementStore;
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
    public IProgramIndicatorStore getProgramIndicatorStore() {
        return programIndicatorStore;
    }

    @Override
    public ITrackedEntityAttributeStore getTrackedEntityAttributeStore() {
        return trackedEntityAttributeStore;
    }

    @Override
    public IOrganisationUnitStore getOrganisationUnitStore() {
        return organisationUnitStore;
    }

    @Override
    public IEventStore getEventStore() {
        return eventStore;
    }

    @Override
    public ITrackedEntityDataValueStore getTrackedEntityDataValueStore() {
        return trackedEntityDataValueStore;
    }

    @Override
    public IDataElementStore getDataElementStore() {
        return dataElementStore;
    }

    @Override
    public IOptionSetStore getOptionSetStore() {
        return optionSetStore;
    }

    @Override
    public IOptionStore getOptionStore() {
        return optionStore;
    }

    @Override
    public boolean deleteAllTables() {
        return userAccountStore.deleteAll() &&
                organisationUnitStore.deleteAll() &&
                stateStore.deleteAll() &&
                programStore.deleteAll() &&
                programStageStore.deleteAll() &&
                programStageSectionStore.deleteAll() &&
                dataElementStore.deleteAll() &&
                eventStore.deleteAll() &&
                trackedEntityDataValueStore.deleteAll();
    }
}
