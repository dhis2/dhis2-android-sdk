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

package org.hisp.dhis.android.core.arch.repositories.filters.internal;

import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ScopedRepositoryFactory;
import org.hisp.dhis.android.core.arch.repositories.scope.BaseScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.BaseScopeFactory;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem;
import org.hisp.dhis.android.core.common.DateFilterPeriod;

import java.util.List;

public class ScopedFilterConnectorFactory<R extends BaseRepository, S extends BaseScope> {

    public final ScopedRepositoryFactory<R, S> repositoryFactory;

    public ScopedFilterConnectorFactory(ScopedRepositoryFactory<R, S> repositoryFactory) {
        this.repositoryFactory = repositoryFactory;
    }

    public <T> EqFilterConnector<R, T> eqConnector(BaseScopeFactory<S, T> baseScopeFactory) {
        return new EqFilterConnector<>(value -> repositoryFactory.updated(baseScopeFactory.updated(value)));
    }

    public <T> ListFilterConnector<R, T> listConnector(BaseScopeFactory<S, List<T>> baseScopeFactory) {
        return new ListFilterConnector<>(list -> repositoryFactory.updated(baseScopeFactory.updated(list)));
    }

    public BoolFilterConnector<R> booleanConnector(BaseScopeFactory<S, Boolean> baseScopeFactory) {
        return new BoolFilterConnector<>(bool -> repositoryFactory.updated(baseScopeFactory.updated(bool)));
    }

    public EqLikeItemFilterConnector<R> eqLikeItemC(String key, BaseScopeFactory<S,
            RepositoryScopeFilterItem> baseScopeFactory) {
        return new EqLikeItemFilterConnector<>(key, item -> repositoryFactory.updated(baseScopeFactory.updated(item)));
    }

    public PeriodFilterConnector<R> periodConnector(BaseScopeFactory<S, DateFilterPeriod> baseScopeFactory) {
        return new PeriodFilterConnector<>(filter -> repositoryFactory.updated(baseScopeFactory.updated(filter)));
    }
}
