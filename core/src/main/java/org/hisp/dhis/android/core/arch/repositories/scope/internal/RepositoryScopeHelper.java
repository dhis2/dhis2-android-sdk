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

package org.hisp.dhis.android.core.arch.repositories.scope.internal;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.IdentifiableColumns;

import java.util.ArrayList;
import java.util.List;

public final class RepositoryScopeHelper {

    private RepositoryScopeHelper() {
    }

    public static RepositoryScope withFilterItem(RepositoryScope scope, RepositoryScopeFilterItem item) {
        List<RepositoryScopeFilterItem> copiedItems = new ArrayList<>(scope.filters());
        copiedItems.add(item);
        return scope.toBuilder().filters(copiedItems).build();
    }

    public static RepositoryScope withUidFilterItem(RepositoryScope scope, String uid) {
        RepositoryScopeFilterItem filterItem = RepositoryScopeFilterItem.builder()
                .key(IdentifiableColumns.UID)
                .operator(FilterItemOperator.EQ)
                .value("'" + uid + "'")
                .build();

        return RepositoryScopeHelper.withFilterItem(scope, filterItem);
    }

    public static RepositoryScope withComplexFilterItem(RepositoryScope scope, RepositoryScopeComplexFilterItem item) {
        List<RepositoryScopeComplexFilterItem> copiedItems = new ArrayList<>(scope.complexFilters());
        copiedItems.add(item);
        return scope.toBuilder().complexFilters(copiedItems).build();
    }

    public static RepositoryScope withChild(RepositoryScope scope, String child) {
        return scope.toBuilder().children(scope.children().withChild(child)).build();
    }

    public static RepositoryScope withOrderBy(RepositoryScope scope, RepositoryScopeOrderByItem item) {
        List<RepositoryScopeOrderByItem> newItems = new ArrayList<>(scope.orderBy().size() + 1);
        for (RepositoryScopeOrderByItem i: scope.orderBy()) {
            if (!i.column().equals(item.column())) {
                newItems.add(i);
            }
        }
        newItems.add(item);
        return scope.toBuilder().orderBy(newItems).build();
    }
}
