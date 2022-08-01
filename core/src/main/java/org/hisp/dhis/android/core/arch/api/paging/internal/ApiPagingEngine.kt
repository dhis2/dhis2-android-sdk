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
package org.hisp.dhis.android.core.arch.api.paging.internal

import kotlin.IllegalArgumentException
import kotlin.IllegalStateException
import kotlin.math.ceil
import kotlin.math.floor

object ApiPagingEngine {
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun getPaginationList(currentPageSize: Int, requiredItemsCount: Int, itemsSkippedCount: Int): List<Paging> {
        require(!(currentPageSize <= 0 || requiredItemsCount <= 0)) {
            "Argument is negative. ItemsCount: $requiredItemsCount, CurrentPageSize: $currentPageSize."
        }
        val numberOfCallsDone: Double = itemsSkippedCount.toDouble() / currentPageSize
        val numberOfFullCallsDone: Int = floor(numberOfCallsDone).toInt()
        val numberOfCalls = ceil((requiredItemsCount + itemsSkippedCount).toDouble() / currentPageSize).toInt()
        val pagingList: MutableList<Paging> = mutableListOf()

        if (floor(numberOfCallsDone) != numberOfCallsDone) {
            pagingList.add(
                calculateFirstPagination(
                    currentPageSize, numberOfFullCallsDone, requiredItemsCount, itemsSkippedCount
                )
            )
        }

        for (call in numberOfFullCallsDone + pagingList.size + 1 until numberOfCalls) {
            pagingList.add(
                Paging.create(call, currentPageSize, 0, 0, false)
            )
        }

        if (numberOfFullCallsDone + pagingList.size + 1 == numberOfCalls) {
            pagingList.add(
                calculateLastPagination(
                    currentPageSize, requiredItemsCount + itemsSkippedCount, numberOfCalls
                )
            )
        }

        return pagingList
    }

    @JvmStatic
    @Throws(IllegalStateException::class)
    fun calculateFirstPagination(
        maxPageSize: Int,
        numberOfFullCallsDone: Int,
        requiredItemsCount: Int,
        itemsSkippedCount: Int
    ): Paging {

        val upperLimit = minOf(itemsSkippedCount + requiredItemsCount, (numberOfFullCallsDone + 1) * maxPageSize)
        val minimumPageSize = upperLimit - itemsSkippedCount

        for (pageSize in minimumPageSize..maxPageSize) {
            val page = ceil(upperLimit.toDouble() / pageSize).toInt()
            val previousItemsToSkipCount = itemsSkippedCount - (page - 1) * pageSize
            val posteriorItemsToSkipCount = page * pageSize - upperLimit
            if (previousItemsToSkipCount >= 0 && posteriorItemsToSkipCount >= 0) {
                return Paging.create(
                    page,
                    pageSize,
                    previousItemsToSkipCount,
                    posteriorItemsToSkipCount,
                    false
                )
            }
        }
        throw IllegalStateException("Paging couldn't be calculated.")
    }

    @JvmStatic
    @Throws(IllegalStateException::class)
    fun calculateLastPagination(currentPageSize: Int, requiredItemsCount: Int, numberOfCalls: Int): Paging {
        val requestedItems = (numberOfCalls - 1) * currentPageSize
        val itemsToRequest = requiredItemsCount - requestedItems
        for (pageSize in itemsToRequest..currentPageSize) {
            for (
                page in floor(requiredItemsCount.toDouble() / pageSize)
                    .toInt()..floor(requestedItems.toDouble() / pageSize).toInt() + 1
            ) {
                val previousItemsToSkipCount = requestedItems - pageSize * (page - 1)
                val posteriorItemsToSkipCount = pageSize * page - requiredItemsCount
                if (previousItemsToSkipCount >= 0 && posteriorItemsToSkipCount >= 0) {
                    return Paging.create(
                        page, pageSize, previousItemsToSkipCount, posteriorItemsToSkipCount, true
                    )
                }
            }
        }
        throw IllegalStateException("Paging couldn't be calculated.")
    }
}
