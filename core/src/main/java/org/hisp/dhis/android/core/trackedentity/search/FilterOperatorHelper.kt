/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.core.trackedentity.search

import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.common.FilterOperators
import org.hisp.dhis.android.core.common.FilterOperatorsHelper
import org.koin.core.annotation.Singleton

@Singleton
internal class FilterOperatorHelper(
    private val dateFilterPeriodHelper: DateFilterPeriodHelper,
) {
    fun getFilterItems(
        filterKey: String,
        filter: FilterOperators,
    ): List<RepositoryScopeFilterItem> {
        val filterBuilder = RepositoryScopeFilterItem.builder().key(filterKey)
        val filterItems: MutableList<RepositoryScopeFilterItem> = mutableListOf()

        filter.eq()?.let {
            filterItems.add(filterBuilder.operator(FilterItemOperator.EQ).value(it).build())
        }
        filter.like()?.let {
            filterItems.add(filterBuilder.operator(FilterItemOperator.LIKE).value(it).build())
        }
        filter.le()?.let {
            filterItems.add(filterBuilder.operator(FilterItemOperator.LE).value(it).build())
        }
        filter.lt()?.let {
            filterItems.add(filterBuilder.operator(FilterItemOperator.LT).value(it).build())
        }
        filter.ge()?.let {
            filterItems.add(filterBuilder.operator(FilterItemOperator.GE).value(it).build())
        }
        filter.gt()?.let {
            filterItems.add(filterBuilder.operator(FilterItemOperator.GT).value(it).build())
        }
        filter.`in`()?.let {
            if (it.isNotEmpty()) {
                val strValue = FilterOperatorsHelper.listToStr(it)
                filterItems.add(filterBuilder.operator(FilterItemOperator.IN).value(strValue).build())
            }
        }
        filter.dateFilter()?.let { dateFilter ->
            dateFilterPeriodHelper.getStartDate(dateFilter)?.let {
                val dateValue = DateUtils.DATE_FORMAT.format(it)
                filterItems.add(filterBuilder.operator(FilterItemOperator.GE).value(dateValue).build())
            }
            dateFilterPeriodHelper.getEndDate(dateFilter)?.let {
                val dateValue = DateUtils.DATE_FORMAT.format(it)
                filterItems.add(filterBuilder.operator(FilterItemOperator.LE).value(dateValue).build())
            }
        }

        return filterItems
    }
}
