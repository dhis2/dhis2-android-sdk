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

import org.hisp.dhis.android.core.analytics.trackerlinelist.EnumFilter

internal abstract class BaseEnumEvaluator<T : Enum<*>>(
    private val itemId: String,
    private val filters: List<EnumFilter<T>>,
) : TrackerLineListEvaluator() {

    fun getWhereClause(): String {
        return if (filters.isEmpty()) {
            "1"
        } else {
            return filters.joinToString(" OR ") { "(${getFilterWhereClause(it)})" }
        }
    }

    private fun getFilterWhereClause(filter: EnumFilter<T>): String {
        val filterHelper = FilterHelper(itemId)
        return when (filter) {
            is EnumFilter.EqualTo -> filterHelper.equalTo(filter.value)
            is EnumFilter.NotEqualTo -> filterHelper.notEqualTo(filter.value)
            is EnumFilter.EqualToIgnoreCase -> filterHelper.notEqualTo(filter.value)
            is EnumFilter.NotEqualToIgnoreCase -> filterHelper.notEqualToIgnoreCase(filter.value)
            is EnumFilter.Like -> filterHelper.like(filter.value)
            is EnumFilter.LikeIgnoreCase -> filterHelper.likeIgnoreCase(filter.value)
            is EnumFilter.NotLike -> filterHelper.notLike(filter.value)
            is EnumFilter.NotLikeIgnoreCase -> filterHelper.notLikeIgnoreCase(filter.value)
            is EnumFilter.In -> filterHelper.inValues(filter.values.map { it.name })
        }
    }
}
