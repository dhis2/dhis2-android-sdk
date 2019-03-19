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

package org.hisp.dhis.android.core.datavalue;

import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUploadCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.filters.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.imports.DataValueImportSummary;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class DataValueCollectionRepository
        extends ReadOnlyCollectionRepositoryImpl<DataValue, DataValueCollectionRepository>
        implements ReadWriteWithUploadCollectionRepository<DataValue> {

    private final DataValueStore dataValueStore;
    private final SyncHandler<DataValue> dataValueHandler;
    private final DataValuePostCall postCall;

    @Inject
    DataValueCollectionRepository(final DataValueStore dataValueStore,
                                  final Map<String, ChildrenAppender<DataValue>> childrenAppenders,
                                  final RepositoryScope scope,
                                  final SyncHandler<DataValue> dataValueHandler,
                                  final DataValuePostCall postCall) {
        super(dataValueStore, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new DataValueCollectionRepository(dataValueStore, childrenAppenders, s,
                        dataValueHandler, postCall)));
        this.dataValueHandler = dataValueHandler;
        this.dataValueStore = dataValueStore;
        this.postCall = postCall;
    }

    @Override
    public void add(DataValue dataValue) throws D2Error {

        if (dataValueStore.exists(dataValue)) {
            throw D2Error
                    .builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.CANT_CREATE_EXISTING_OBJECT)
                    .errorDescription("Tried to create already existing DataValue: " + dataValue)
                    .build();
        }

        dataValueHandler.handle(dataValue.toBuilder().state(State.TO_POST).build());
    }

    @Override
    public Callable<DataValueImportSummary> upload() {
        return postCall;
    }


    public StringFilterConnector<DataValueCollectionRepository> byDataElementUid() {
        return cf.string(DataValueFields.DATA_ELEMENT);
    }

    public StringFilterConnector<DataValueCollectionRepository> byPeriod() {
        return cf.string(DataValueFields.PERIOD);
    }

    public StringFilterConnector<DataValueCollectionRepository> byOrganisationUnitUid() {
        return cf.string(DataValueTableInfo.ORGANISATION_UNIT);
    }

    public StringFilterConnector<DataValueCollectionRepository> byCategoryOptionComboUid() {
        return cf.string(DataValueFields.CATEGORY_OPTION_COMBO);
    }

    public StringFilterConnector<DataValueCollectionRepository> byAttributeOptionComboUid() {
        return cf.string(DataValueFields.ATTRIBUTE_OPTION_COMBO);
    }

    public StringFilterConnector<DataValueCollectionRepository> byValue() {
        return cf.string(DataValueFields.VALUE);
    }

    public StringFilterConnector<DataValueCollectionRepository> byStoredBy() {
        return cf.string(DataValueFields.STORED_BY);
    }

    public DateFilterConnector<DataValueCollectionRepository> byCreated() {
        return cf.date(DataValueFields.CREATED);
    }

    public DateFilterConnector<DataValueCollectionRepository> byLastUpdated() {
        return cf.date(DataValueFields.LAST_UPDATED);
    }

    public StringFilterConnector<DataValueCollectionRepository> byComment() {
        return cf.string(DataValueFields.COMMENT);
    }

    public BooleanFilterConnector<DataValueCollectionRepository> byFollowUp() {
        return cf.bool(DataValueFields.FOLLOW_UP);
    }
}
