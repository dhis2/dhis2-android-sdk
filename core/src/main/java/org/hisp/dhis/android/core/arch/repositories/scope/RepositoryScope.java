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

package org.hisp.dhis.android.core.arch.repositories.scope;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenSelection;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeComplexFilterItem;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeOrderByItem;
import org.hisp.dhis.android.core.common.CoreColumns;

import java.util.Collections;
import java.util.List;

@AutoValue
public abstract class RepositoryScope {

    public enum OrderByDirection {
        ASC("asc"),
        DESC("desc");

        private String api;

        OrderByDirection(String api) {
            this.api = api;
        }

        public String getApi() {
            return api;
        }
    }

    @NonNull
    public abstract List<RepositoryScopeFilterItem> filters();

    @NonNull
    public abstract List<RepositoryScopeComplexFilterItem> complexFilters();

    @NonNull
    public abstract List<RepositoryScopeOrderByItem> orderBy();

    @NonNull
    public abstract String pagingKey();

    @NonNull
    public abstract ChildrenSelection children();

    public boolean hasFilters() {
        return !filters().isEmpty() || !complexFilters().isEmpty();
    }

    public static RepositoryScope empty() {
        return RepositoryScope.builder()
                .children(ChildrenSelection.empty())
                .filters(Collections.emptyList())
                .complexFilters(Collections.emptyList())
                .orderBy(Collections.emptyList())
                .pagingKey(CoreColumns.ID)
                .build();
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_RepositoryScope.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder filters(List<RepositoryScopeFilterItem> filters);

        public abstract Builder complexFilters(List<RepositoryScopeComplexFilterItem> complexFilters);

        public abstract Builder children(ChildrenSelection children);

        public abstract Builder orderBy(List<RepositoryScopeOrderByItem> orderBy);

        public abstract Builder pagingKey(String pagingKey);

        public abstract RepositoryScope build();
    }
}
