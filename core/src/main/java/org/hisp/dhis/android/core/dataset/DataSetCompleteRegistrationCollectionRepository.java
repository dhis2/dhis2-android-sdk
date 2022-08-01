/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUploadCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationTableInfo.Columns;
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationPostCall;
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationStore;
import org.hisp.dhis.android.core.user.UserCredentialsObjectRepository;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;

@Reusable
public final class DataSetCompleteRegistrationCollectionRepository
        extends ReadOnlyCollectionRepositoryImpl<DataSetCompleteRegistration,
        DataSetCompleteRegistrationCollectionRepository>
        implements ReadOnlyWithUploadCollectionRepository<DataSetCompleteRegistration> {

    private final DataSetCompleteRegistrationPostCall postCall;
    private final DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore;
    private final UserCredentialsObjectRepository credentialsRepository;

    @Inject
    DataSetCompleteRegistrationCollectionRepository(
            final DataSetCompleteRegistrationStore store,
            final Map<String, ChildrenAppender<DataSetCompleteRegistration>> childrenAppenders,
            final RepositoryScope scope,
            final Handler<DataSetCompleteRegistration> handler,
            final DataSetCompleteRegistrationPostCall postCall,
            final UserCredentialsObjectRepository credentialsRepository) {

        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new DataSetCompleteRegistrationCollectionRepository(store, childrenAppenders,
                        s, handler, postCall, credentialsRepository)));

        this.postCall = postCall;
        this.dataSetCompleteRegistrationStore = store;
        this.credentialsRepository = credentialsRepository;
    }

    public DataSetCompleteRegistrationObjectRepository value(final String period,
                                                             final String organisationUnit,
                                                             final String dataSet,
                                                             final String attributeOptionCombo) {

        RepositoryScope updatedScope = byPeriod().eq(period)
                .byOrganisationUnitUid().eq(organisationUnit)
                .byDataSetUid().eq(dataSet)
                .byAttributeOptionComboUid().eq(attributeOptionCombo)
                .scope;

        return new DataSetCompleteRegistrationObjectRepository(
                dataSetCompleteRegistrationStore, credentialsRepository, childrenAppenders,
                updatedScope, period, organisationUnit, dataSet, attributeOptionCombo);
    }

    @Override
    public Observable<D2Progress> upload() {
        return Observable.fromCallable(() ->
                bySyncState().in(State.uploadableStatesIncludingError()).blockingGetWithoutChildren()
        ).flatMap(postCall::uploadDataSetCompleteRegistrations);
    }

    @Override
    public void blockingUpload() {
        upload().blockingSubscribe();
    }


    public StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> byPeriod() {
        return cf.string(Columns.PERIOD);
    }

    public StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> byDataSetUid() {
        return cf.string(Columns.DATA_SET);
    }

    public StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> byOrganisationUnitUid() {
        return cf.string(Columns.ORGANISATION_UNIT);
    }

    public StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> byAttributeOptionComboUid() {
        return cf.string(Columns.ATTRIBUTE_OPTION_COMBO);
    }

    public DateFilterConnector<DataSetCompleteRegistrationCollectionRepository> byDate() {
        return cf.date(Columns.DATE);
    }

    public StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> byStoredBy() {
        return cf.string(Columns.STORED_BY);
    }

    public BooleanFilterConnector<DataSetCompleteRegistrationCollectionRepository> byDeleted() {
        return cf.bool(Columns.DELETED);
    }

    /**
     * @deprecated Use {@link #bySyncState()} instead.
     *
     * @return
     */
    @Deprecated
    public EnumFilterConnector<DataSetCompleteRegistrationCollectionRepository, State> byState() {
        return bySyncState();
    }

    public EnumFilterConnector<DataSetCompleteRegistrationCollectionRepository, State> bySyncState() {
        return cf.enumC(Columns.SYNC_STATE);
    }

}