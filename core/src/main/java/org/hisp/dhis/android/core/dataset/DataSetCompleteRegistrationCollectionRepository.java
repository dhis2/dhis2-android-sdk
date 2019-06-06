/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUploadCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class DataSetCompleteRegistrationCollectionRepository
        extends ReadOnlyCollectionRepositoryImpl<DataSetCompleteRegistration,
        DataSetCompleteRegistrationCollectionRepository>
        implements ReadWriteCollectionRepository<DataSetCompleteRegistration>,
        ReadOnlyWithUploadCollectionRepository<DataSetCompleteRegistration> {

    private final Handler<DataSetCompleteRegistration> handler;
    private final DataSetCompleteRegistrationPostCall postCall;

    @Inject
    DataSetCompleteRegistrationCollectionRepository(
            final DataSetCompleteRegistrationStore store,
            final Map<String, ChildrenAppender<DataSetCompleteRegistration>> childrenAppenders,
            final RepositoryScope scope,
            final Handler<DataSetCompleteRegistration> handler,
            final DataSetCompleteRegistrationPostCall postCall) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new DataSetCompleteRegistrationCollectionRepository(store, childrenAppenders,
                        s, handler, postCall)));
        this.handler = handler;
        this.postCall = postCall;
    }

    @Override
    public void add(DataSetCompleteRegistration dataSetCompleteRegistration) {
        handler.handle(dataSetCompleteRegistration.toBuilder().state(State.TO_POST).build());
    }

    @Override
    public Callable<DataValueImportSummary> upload() {
        return postCall;
    }


    public StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> byPeriod() {
        return cf.string(DataSetCompleteRegistrationFields.PERIOD);
    }

    public StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> byDataSetUid() {
        return cf.string(DataSetCompleteRegistrationFields.DATA_SET);
    }

    public StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> byOrganisationUnitUid() {
        return cf.string(DataSetCompleteRegistrationFields.ORGANISATION_UNIT);
    }

    public StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> byAttributeOptionComboUid() {
        return cf.string(DataSetCompleteRegistrationFields.ATTRIBUTE_OPTION_COMBO);
    }

    public DateFilterConnector<DataSetCompleteRegistrationCollectionRepository> byDate() {
        return cf.date(DataSetCompleteRegistrationFields.DATE);
    }

    public StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> byStoredBy() {
        return cf.string(DataSetCompleteRegistrationFields.STORED_BY);
    }
}