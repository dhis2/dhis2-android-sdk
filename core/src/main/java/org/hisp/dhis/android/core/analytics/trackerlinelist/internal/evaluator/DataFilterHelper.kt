/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.analytics.trackerlinelist.internal.evaluator

import org.hisp.dhis.android.core.analytics.trackerlinelist.DataFilter

internal object DataFilterHelper {
    fun getWhereClause(itemId: String, filters: List<DataFilter>): String {
        return if (filters.isEmpty()) {
            "1"
        } else {
            val filterHelper = FilterHelper(itemId)
            return filters.joinToString(" AND ") { getSqlOperator(filterHelper, it) }
        }
    }

    private fun getSqlOperator(helper: FilterHelper, filter: DataFilter): String {
        return when (filter) {
            is DataFilter.EqualTo -> helper.equalTo(filter.value)
            is DataFilter.NotEqualTo -> helper.notEqualTo(filter.value)
            is DataFilter.EqualToIgnoreCase -> helper.equalToIgnoreCase(filter.value)
            is DataFilter.NotEqualToIgnoreCase -> helper.notEqualToIgnoreCase(filter.value)
            is DataFilter.GreaterThan -> helper.greaterThan(filter.value)
            is DataFilter.GreaterThanOrEqualTo -> helper.greaterThanOrEqualTo(filter.value)
            is DataFilter.LowerThan -> helper.lowerThan(filter.value)
            is DataFilter.LowerThanOrEqualTo -> helper.lowerThanOrEqualTo(filter.value)
            is DataFilter.Like -> helper.like(filter.value)
            is DataFilter.LikeIgnoreCase -> helper.likeIgnoreCase(filter.value)
            is DataFilter.NotLike -> helper.notLike(filter.value)
            is DataFilter.NotLikeIgnoreCase -> helper.notLikeIgnoreCase(filter.value)
            is DataFilter.In -> helper.inValues(filter.values)
        }
    }
}
