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

import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator

abstract class BaseAbstractFilterConnector<R : BaseRepository, V> internal constructor(
    repositoryFactory: BaseRepositoryFactory<R>,
    scope: RepositoryScope,
    key: String,
) : AbstractFilterConnector<R, V>(
    repositoryFactory,
    scope,
    key,
) {
    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The eq filter checks if the given field has a value which is equal to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    fun eq(value: V?): R {
        return newWithWrappedScope(FilterItemOperator.EQ, value)
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The neq filter checks if the given field has a value which is not equal to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    fun neq(value: V?): R {
        return newWithWrappedScope(FilterItemOperator.NOT_EQ, value)
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The in filter checks if the given field has a value which is equal to one of the provided values.
     * @param values list of values to compare with the target field
     * @return the new repository
     */
    fun `in`(values: Collection<V>?): R {
        return newWithUnwrappedScope(
            FilterItemOperator.IN,
            "(" + getCommaSeparatedValues(values) + ")",
        )
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The in filter checks if the given field has a value which is equal to one of the provided values.
     * @param values list of values to compare with the target field
     * @return the new repository
     */
    @SafeVarargs
    fun `in`(vararg values: V): R {
        return `in`(listOf(*values))
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The notIn filter checks if the given field has a value which is not equal to any of the provided values.
     * @param values list of values to compare with the target field
     * @return the new repository
     */
    fun notIn(values: Collection<V>?): R {
        return newWithUnwrappedScope(
            FilterItemOperator.NOT_IN,
            "(" + getCommaSeparatedValues(values) + ")",
        )
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The notIn filter checks if the given field has a value which is not equal to any of the provided values.
     * @param values list of values to compare with the target field
     * @return the new repository
     */
    @SafeVarargs
    fun notIn(vararg values: V): R {
        return notIn(listOf(*values))
    }

    val isNull: R
        /**
         * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
         * The isNull filter checks if the given field has a null value.
         * @return the new repository
         */
        get() = newWithUnwrappedScope(FilterItemOperator.VOID, "IS NULL")
    val isNotNull: R
        /**
         * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
         * The isNotNull filter checks if the given field has a non-null value.
         * @return the new repository
         */
        get() = newWithUnwrappedScope(FilterItemOperator.VOID, "IS NOT NULL")

    companion object {
        @JvmStatic
        fun escapeQuotes(value: String?): String? {
            return value?.replace("'".toRegex(), "''")
        }
    }
}
