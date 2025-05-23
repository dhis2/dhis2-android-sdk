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
package org.hisp.dhis.android.core.indicator.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.NameableWithStyleStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStoreImpl
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidOrNull
import org.hisp.dhis.android.core.dataset.SectionIndicatorLinkTableInfo
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkTableInfo
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.indicator.IndicatorTableInfo
import org.hisp.dhis.android.core.indicator.IndicatorTableInfo.TABLE_INFO
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("MagicNumber")
internal class IndicatorStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : IndicatorStore,
    IdentifiableObjectStoreImpl<Indicator>(
        databaseAdapter,
        TABLE_INFO,
        BINDER,
        { cursor: Cursor -> Indicator.create(cursor) },
    ) {
    companion object {
        private val BINDER: StatementBinder<Indicator> = object : NameableWithStyleStatementBinder<Indicator>() {
            override fun bindToStatement(o: Indicator, w: StatementWrapper) {
                super.bindToStatement(o, w)
                w.bind(13, o.annualized())
                w.bind(14, getUidOrNull(o.indicatorType()))
                w.bind(15, o.numerator())
                w.bind(16, o.numeratorDescription())
                w.bind(17, o.denominator())
                w.bind(18, o.denominatorDescription())
                w.bind(19, o.url())
                w.bind(20, o.decimals())
            }
        }
    }

    override fun getForDataSet(dataSetUid: String): List<Indicator> {
        val projection = LinkTableChildProjection(
            TABLE_INFO,
            DataSetIndicatorLinkTableInfo.Columns.DATA_SET,
            DataSetIndicatorLinkTableInfo.Columns.INDICATOR,
        )
        val sectionSqlBuilder = SQLStatementBuilderImpl(DataSetIndicatorLinkTableInfo.TABLE_INFO)
        val query = sectionSqlBuilder.selectChildrenWithLinkTable(
            projection,
            dataSetUid,
            null,
        )
        return selectRawQuery(query)
    }

    override fun getForSection(sectionUid: String): List<Indicator> {
        val projection = LinkTableChildProjection(
            IndicatorTableInfo.TABLE_INFO,
            SectionIndicatorLinkTableInfo.Columns.SECTION,
            SectionIndicatorLinkTableInfo.Columns.INDICATOR,
        )
        val sectionSqlBuilder = SQLStatementBuilderImpl(SectionIndicatorLinkTableInfo.TABLE_INFO)
        val query = sectionSqlBuilder.selectChildrenWithLinkTable(
            projection,
            sectionUid,
            null,
        )
        return selectRawQuery(query)
    }
}
