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
package org.hisp.dhis.android.core.arch.repositories.filters.internal

import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ScopedRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.scope.BaseScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.BaseScopeFactory
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.OrganisationUnitFilter
import org.hisp.dhis.android.core.event.EventDataFilter

internal class ScopedFilterConnectorFactory<R : BaseRepository, S : BaseScope>(
    private val repositoryFactory: ScopedRepositoryFactory<R, S>
) {
    fun <T> eqConnector(baseScopeFactory: BaseScopeFactory<S, T>): EqFilterConnector<R, T> {
        return EqFilterConnector { value: T -> repositoryFactory.updated(baseScopeFactory.updated(value)) }
    }

    fun <T> listConnector(baseScopeFactory: BaseScopeFactory<S, List<T>>): ListFilterConnector<R, T> {
        return ListFilterConnector { list: List<T>? -> repositoryFactory.updated(baseScopeFactory.updated(list)) }
    }

    fun booleanConnector(baseScopeFactory: BaseScopeFactory<S, Boolean>): BoolFilterConnector<R> {
        return BoolFilterConnector { bool: Boolean -> repositoryFactory.updated(baseScopeFactory.updated(bool)) }
    }

    fun eqLikeItemC(
        key: String,
        baseScopeFactory: BaseScopeFactory<S, RepositoryScopeFilterItem>
    ): EqLikeItemFilterConnector<R> {
        return EqLikeItemFilterConnector(key) { item: RepositoryScopeFilterItem ->
            repositoryFactory.updated(
                baseScopeFactory.updated(item)
            )
        }
    }

    fun periodConnector(baseScopeFactory: BaseScopeFactory<S, DateFilterPeriod>): PeriodFilterConnector<R> {
        return PeriodFilterConnector { filter: DateFilterPeriod ->
            repositoryFactory.updated(
                baseScopeFactory.updated(
                    filter
                )
            )
        }
    }

    fun periodsConnector(baseScopeFactory: BaseScopeFactory<S, List<DateFilterPeriod>>): PeriodsFilterConnector<R> {
        return PeriodsFilterConnector { filter: List<DateFilterPeriod> ->
            repositoryFactory.updated(
                baseScopeFactory.updated(
                    filter
                )
            )
        }
    }

    fun organisationUnitConnector(
        baseScopeFactory: BaseScopeFactory<S, List<OrganisationUnitFilter>>
    ): OrganisationUnitFilterConnector<R> {
        return OrganisationUnitFilterConnector { filter: List<OrganisationUnitFilter> ->
            repositoryFactory.updated(
                baseScopeFactory.updated(filter)
            )
        }
    }

    fun eventDataFilterConnector(
        key: String,
        baseScopeFactory: BaseScopeFactory<S, EventDataFilter>
    ): EventDataFilterConnector<R> {
        return EventDataFilterConnector(key) { item: EventDataFilter ->
            repositoryFactory.updated(
                baseScopeFactory.updated(item)
            )
        }
    }
}
