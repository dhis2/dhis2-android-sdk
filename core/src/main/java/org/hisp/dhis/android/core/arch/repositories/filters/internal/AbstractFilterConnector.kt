/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeComplexFilterItem
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper

abstract class AbstractFilterConnector<R : BaseRepository, V> internal constructor(
    private val repositoryFactory: BaseRepositoryFactory<R>,
    protected val scope: RepositoryScope,
    val key: String,
) {
    abstract fun wrapValue(value: V?): String?
    fun updatedUnwrappedScope(operator: FilterItemOperator, valueStr: String?): RepositoryScope {
        return RepositoryScopeHelper.withFilterItem(
            scope,
            RepositoryScopeFilterItem.builder().key(key).operator(operator).value(valueStr).build(),
        )
    }

    fun newWithWrappedScope(operator: FilterItemOperator, value: V?): R {
        return repositoryFactory.updated(updatedUnwrappedScope(operator, wrapValue(value)))
    }

    fun updatePassedScope(
        operator: FilterItemOperator,
        valueStr: String?,
        scope: RepositoryScope,
    ): RepositoryScope {
        return RepositoryScopeHelper.withFilterItem(
            scope,
            RepositoryScopeFilterItem.builder().key(key).operator(operator).value(valueStr).build(),
        )
    }

    fun newWithPassedScope(operator: FilterItemOperator, value: V, scope: RepositoryScope): R {
        return repositoryFactory.updated(updatePassedScope(operator, wrapValue(value), scope))
    }

    private fun updatedUnwrappedScope(whereClause: String): RepositoryScope {
        return RepositoryScopeHelper.withComplexFilterItem(
            scope,
            RepositoryScopeComplexFilterItem.builder().whereQuery(whereClause).build(),
        )
    }

    fun newWithWrappedScope(whereClause: String): R {
        return repositoryFactory.updated(updatedUnwrappedScope(whereClause))
    }

    fun getCommaSeparatedValues(values: Collection<V>?): String {
        return values?.let {
            val wrappedValues = values.map { wrapValue(it) }
            CollectionsHelper.commaAndSpaceSeparatedCollectionValues(wrappedValues)
        } ?: ""
    }

    fun newWithUnwrappedScope(operator: FilterItemOperator, value: String?): R {
        return repositoryFactory.updated(updatedUnwrappedScope(operator, value))
    }
}
