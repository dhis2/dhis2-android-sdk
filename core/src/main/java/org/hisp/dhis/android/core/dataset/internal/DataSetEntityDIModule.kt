/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.dataset.internal

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleanerImpl
import org.hisp.dhis.android.core.arch.cleaners.internal.LinkCleaner
import org.hisp.dhis.android.core.arch.cleaners.internal.LinkCleanerImpl
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.dataelement.internal.DataElementOperandHandler
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetOrganisationUnitLinkTableInfo
import org.hisp.dhis.android.core.dataset.DataSetTableInfo
import org.hisp.dhis.android.core.indicator.internal.DataSetIndicatorChildrenAppender
import org.hisp.dhis.android.core.indicator.internal.DataSetIndicatorLinkHandler

@Module
internal class DataSetEntityDIModule {
    @Provides
    @Reusable
    fun store(databaseAdapter: DatabaseAdapter): DataSetStore {
        return DataSetStoreImpl(databaseAdapter)
    }

    @Provides
    @Reusable
    @Suppress("LongParameterList")
    fun handler(
        dataSetStore: DataSetStore,
        sectionHandler: SectionHandler,
        sectionOrphanCleaner: SectionOrphanCleaner,
        compulsoryDataElementOperandHandler: DataElementOperandHandler,
        dataSetCompulsoryDataElementOperandLinkHandler: DataSetCompulsoryDataElementOperandHandler,
        dataInputPeriodHandler: DataInputPeriodHandler,
        dataSetElementLinkHandler: DataSetElementHandler,
        dataSetIndicatorLinkHandler: DataSetIndicatorLinkHandler,
        collectionCleaner: CollectionCleaner<DataSet>,
        linkCleaner: LinkCleaner<DataSet>
    ): DataSetHandler {
        return DataSetHandler(
            dataSetStore,
            sectionHandler,
            sectionOrphanCleaner,
            compulsoryDataElementOperandHandler,
            dataSetCompulsoryDataElementOperandLinkHandler,
            dataInputPeriodHandler,
            dataSetElementLinkHandler,
            dataSetIndicatorLinkHandler,
            collectionCleaner,
            linkCleaner
        )
    }

    @Provides
    @Reusable
    fun collectionCleaner(databaseAdapter: DatabaseAdapter): CollectionCleaner<DataSet> {
        return CollectionCleanerImpl(DataSetTableInfo.TABLE_INFO.name(), databaseAdapter)
    }

    @Provides
    @Reusable
    fun linkCleaner(
        dataSetStore: DataSetStore,
        databaseAdapter: DatabaseAdapter
    ): LinkCleaner<DataSet> {
        return LinkCleanerImpl(
            DataSetOrganisationUnitLinkTableInfo.TABLE_INFO.name(),
            DataSetOrganisationUnitLinkTableInfo.Columns.DATA_SET,
            dataSetStore,
            databaseAdapter
        )
    }

    @Provides
    @Reusable
    fun childrenAppenders(databaseAdapter: DatabaseAdapter): Map<String, ChildrenAppender<DataSet>> {
        return mapOf(
            DataSetFields.COMPULSORY_DATA_ELEMENT_OPERANDS to
                DataSetCompulsoryDataElementOperandChildrenAppender.create(databaseAdapter),

            DataSetFields.DATA_INPUT_PERIODS to DataInputPeriodChildrenAppender.create(databaseAdapter),
            DataSetFields.DATA_SET_ELEMENTS to DataSetElementChildrenAppender.create(databaseAdapter),
            DataSetFields.INDICATORS to DataSetIndicatorChildrenAppender.create(databaseAdapter)
        )
    }
}
