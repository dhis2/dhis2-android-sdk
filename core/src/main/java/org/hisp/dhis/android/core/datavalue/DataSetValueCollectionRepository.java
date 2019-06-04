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

import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;

import java.util.Collections;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class DataSetValueCollectionRepository
        extends ReadOnlyCollectionRepositoryImpl<DataSetValueSummary, DataSetValueCollectionRepository>
        implements ReadOnlyCollectionRepository<DataSetValueSummary> {

    @Inject
    DataSetValueCollectionRepository(final DataSetValueSummaryStore store,
                                     final RepositoryScope scope) {
        super(store, Collections.emptyMap(), scope, new FilterConnectorFactory<>(scope,
                s -> new DataSetValueCollectionRepository(store, s)));
    }




    public StringFilterConnector<DataSetValueCollectionRepository> byDataElementUid() {
        return cf.string(DataValueFields.DATA_ELEMENT);
    }

    public StringFilterConnector<DataSetValueCollectionRepository> byPeriod() {
        return cf.string(DataValueFields.PERIOD);
    }

    public StringFilterConnector<DataSetValueCollectionRepository> byOrganisationUnitUid() {
        return cf.string(DataValueTableInfo.ORGANISATION_UNIT);
    }

    public StringFilterConnector<DataSetValueCollectionRepository> byCategoryOptionComboUid() {
        return cf.string(DataValueFields.CATEGORY_OPTION_COMBO);
    }

    public StringFilterConnector<DataSetValueCollectionRepository> byAttributeOptionComboUid() {
        return cf.string(DataValueFields.ATTRIBUTE_OPTION_COMBO);
    }

    public StringFilterConnector<DataSetValueCollectionRepository> byValue() {
        return cf.string(DataValueFields.VALUE);
    }

    public StringFilterConnector<DataSetValueCollectionRepository> byStoredBy() {
        return cf.string(DataValueFields.STORED_BY);
    }

    public DateFilterConnector<DataSetValueCollectionRepository> byCreated() {
        return cf.date(DataValueFields.CREATED);
    }

    public DateFilterConnector<DataSetValueCollectionRepository> byLastUpdated() {
        return cf.date(DataValueFields.LAST_UPDATED);
    }

    public StringFilterConnector<DataSetValueCollectionRepository> byComment() {
        return cf.string(DataValueFields.COMMENT);
    }

    public BooleanFilterConnector<DataSetValueCollectionRepository> byFollowUp() {
        return cf.bool(DataValueFields.FOLLOW_UP);
    }

    public EnumFilterConnector<DataSetValueCollectionRepository, State> byState() {
        return cf.enumC(BaseDataModel.Columns.STATE);
    }
}