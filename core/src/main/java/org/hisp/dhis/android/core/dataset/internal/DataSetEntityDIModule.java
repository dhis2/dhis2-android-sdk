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

package org.hisp.dhis.android.core.dataset.internal;

import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleanerImpl;
import org.hisp.dhis.android.core.arch.cleaners.internal.LinkCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.LinkCleanerImpl;
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleanerImpl;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.dataset.DataSet;
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkTableInfo;
import org.hisp.dhis.android.core.dataset.DataSetTableInfo;
import org.hisp.dhis.android.core.dataset.Section;
import org.hisp.dhis.android.core.dataset.SectionTableInfo;
import org.hisp.dhis.android.core.indicator.internal.DataSetIndicatorChildrenAppender;

import java.util.HashMap;
import java.util.Map;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public final class DataSetEntityDIModule {

    @Provides
    @Reusable
    IdentifiableObjectStore<DataSet> store(DatabaseAdapter databaseAdapter) {
        return DataSetStore.create(databaseAdapter);
    }

    @Provides
    @Reusable
    Handler<DataSet> handler(DataSetHandler impl) {
        return impl;
    }

    @Provides
    @Reusable
    OrphanCleaner<DataSet, Section> sectionOrphanCleaner(DatabaseAdapter databaseAdapter) {
        return new OrphanCleanerImpl<>(SectionTableInfo.TABLE_INFO.name(), SectionTableInfo.Columns.DATA_SET,
                databaseAdapter);
    }

    @Provides
    @Reusable
    public CollectionCleaner<DataSet> collectionCleaner(DatabaseAdapter databaseAdapter) {
        return new CollectionCleanerImpl<>(DataSetTableInfo.TABLE_INFO.name(), databaseAdapter);
    }

    @Provides
    @Reusable
    public LinkCleaner<DataSet> linkCleaner(IdentifiableObjectStore<DataSet> dataSetStore,
                                            DatabaseAdapter databaseAdapter) {
        return new LinkCleanerImpl<>(DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
                DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET,
                dataSetStore,
                databaseAdapter);
    }

    @Provides
    @Reusable
    @SuppressWarnings("PMD.NonStaticInitializer")
    Map<String, ChildrenAppender<DataSet>> childrenAppenders(DatabaseAdapter databaseAdapter) {

        return new HashMap<String, ChildrenAppender<DataSet>>() {{
            put(DataSetFields.COMPULSORY_DATA_ELEMENT_OPERANDS,
                    DataSetCompulsoryDataElementOperandChildrenAppender.create(databaseAdapter));
            put(DataSetFields.DATA_INPUT_PERIODS, DataInputPeriodChildrenAppender.create(databaseAdapter));
            put(DataSetFields.DATA_SET_ELEMENTS, DataSetElementChildrenAppender.create(databaseAdapter));
            put(DataSetFields.INDICATORS, DataSetIndicatorChildrenAppender.create(databaseAdapter));
        }};
    }
}