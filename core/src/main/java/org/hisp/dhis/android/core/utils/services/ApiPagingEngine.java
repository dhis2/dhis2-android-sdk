/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.utils.services;

import java.util.ArrayList;
import java.util.List;

public class ApiPagingEngine {

    public static List<Paging> getPaginationList(int currentPageSize, int itemsCount)
            throws IllegalArgumentException {

        if (currentPageSize <= 0 || itemsCount <= 0) {
            throw new IllegalArgumentException("Argument is negative. ItemsCount: " + itemsCount +
            ", CurrentPageSize: " + currentPageSize + ".");
        }

        List<Paging> pagingList = new ArrayList<>();

        int numberOfCalls = (int) Math.ceil((double) itemsCount / currentPageSize);

        for (int call = 1; call < numberOfCalls; call++) {
            pagingList.add(Paging.create(call, currentPageSize, 0, 0));
        }

        pagingList.add(calculateLastPagination(currentPageSize, itemsCount, numberOfCalls));

        return pagingList;
    }

    static Paging calculateLastPagination(int currentPageSize, int itemsCount, int numberOfCalls)
            throws IllegalStateException {

        int requestedItems = (numberOfCalls - 1) * currentPageSize;
        int itemsToRequest = itemsCount - requestedItems;

        for (int pageSize = itemsToRequest; pageSize <= currentPageSize; pageSize++) {
            for (int page = (int) Math.floor((double) itemsCount / pageSize);
                 page <= (int) Math.floor((double) requestedItems / pageSize) + 1; page++) {

                int previousItemsToSkipCount =  requestedItems - pageSize * (page - 1);
                int posteriorItemsToSkipCount = pageSize * page - itemsCount;

                if (previousItemsToSkipCount >= 0 && posteriorItemsToSkipCount >= 0) {
                    return Paging.create(page, pageSize, previousItemsToSkipCount, posteriorItemsToSkipCount);
                }
            }
        }

        throw new IllegalStateException("Paging couldn't be calculated.");
    }
}