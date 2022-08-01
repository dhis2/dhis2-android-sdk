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
package org.hisp.dhis.android.core.settings.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory.linkStore
import org.hisp.dhis.android.core.settings.AnalyticsTeiWHONutritionData
import org.hisp.dhis.android.core.settings.AnalyticsTeiWHONutritionDataTableInfo

@Suppress("MagicNumber")
internal object AnalyticsTeiWHONutritionDataStore {

    private val BINDER = StatementBinder { o: AnalyticsTeiWHONutritionData, w: StatementWrapper ->
        w.bind(1, o.teiSetting())
        w.bind(2, o.chartType())
        w.bind(3, o.gender().attribute())
        w.bind(4, o.gender().values().female())
        w.bind(5, o.gender().values().male())
    }

    fun create(databaseAdapter: DatabaseAdapter): LinkStore<AnalyticsTeiWHONutritionData> {
        return linkStore(
            databaseAdapter, AnalyticsTeiWHONutritionDataTableInfo.TABLE_INFO,
            AnalyticsTeiWHONutritionDataTableInfo.Columns.TEI_SETTING, BINDER
        ) { cursor: Cursor -> AnalyticsTeiWHONutritionData.create(cursor) }
    }
}
