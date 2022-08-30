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
package org.hisp.dhis.android.core.indicator.internal

import dagger.Reusable
import java.util.*
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.MultipleTableQueryBuilder
import org.hisp.dhis.android.core.dataset.SectionIndicatorLinkTableInfo
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkTableInfo
import org.hisp.dhis.android.core.visualization.DataDimensionItemTableInfo

@Reusable
internal class IndicatorUidsSeeker @Inject constructor(private val databaseAdapter: DatabaseAdapter) {

    fun seekUids(): Set<String> {
        val tableNames = Arrays.asList(
            DataSetIndicatorLinkTableInfo.TABLE_INFO.name(),
            SectionIndicatorLinkTableInfo.TABLE_INFO.name(),
            DataDimensionItemTableInfo.TABLE_INFO.name()
        )
        val query = MultipleTableQueryBuilder()
            .generateQuery(DataSetIndicatorLinkTableInfo.Columns.INDICATOR, tableNames).build()

        val cursor = databaseAdapter.rawQuery(query)

        val indicators: MutableSet<String> = HashSet()
        cursor.use { cursor ->
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    indicators.add(cursor.getString(0))
                } while (cursor.moveToNext())
            }
        }

        return indicators
    }
}
