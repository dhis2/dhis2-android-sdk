/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.indicator

import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection
import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.common.CoreColumns
import org.hisp.dhis.android.core.legendset.LegendSetTableInfo

class IndicatorLegendSetLinkTableInfo {

    companion object {
        val TABLE_INFO: TableInfo = object : TableInfo() {
            override fun name(): String {
                return "IndicatorLegendSetLink"
            }

            override fun columns(): Columns {
                return Columns()
            }
        }

        val CHILD_PROJECTION = LinkTableChildProjection(
            LegendSetTableInfo.TABLE_INFO,
            Columns.INDICATOR,
            Columns.LEGEND_SET
        )
    }

    class Columns : CoreColumns() {
        override fun all(): Array<String> {
            return CollectionsHelper.appendInNewArray(
                super.all(),
                INDICATOR, LEGEND_SET, SORT_ORDER
            )
        }

        override fun whereUpdate(): Array<String> {
            return arrayOf(INDICATOR, LEGEND_SET, SORT_ORDER)
        }

        companion object {
            const val LEGEND_SET = "legendSet"
            const val INDICATOR = "indicator"
            const val SORT_ORDER = "sortOrder"
        }
    }
}
