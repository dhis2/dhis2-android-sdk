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

import java.util.*
import org.hisp.dhis.android.core.arch.dateformat.internal.SafeDateFormat
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.period.DatePeriod
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.internal.InPeriodQueryHelper

abstract class DateFilterConnector<R : BaseRepository> internal constructor(
    repositoryFactory: BaseRepositoryFactory<R>,
    scope: RepositoryScope,
    key: String,
    val formatter: SafeDateFormat
) : BaseAbstractFilterConnector<R, Date>(repositoryFactory, scope, key) {

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The before filter checks if the given field has a date value which is before to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    fun before(value: Date): R {
        return newWithWrappedScope(FilterItemOperator.LT, value)
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The before filter checks if the given field has a date value which is before or equal to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    fun beforeOrEqual(value: Date): R {
        return newWithWrappedScope(FilterItemOperator.LE, value)
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The after filter checks if the given field has a date value which is after to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    fun after(value: Date): R {
        return newWithWrappedScope(FilterItemOperator.GT, value)
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The after filter checks if the given field has a date value which is after or equal to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    fun afterOrEqual(value: Date): R {
        return newWithWrappedScope(FilterItemOperator.GE, value)
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The inDatePeriods filter checks if the given field has a date value which is within one of the provided
     * DatePeriods.
     * @param datePeriods date periods to compare with the target field
     * @return the new repository
     */
    fun inDatePeriods(datePeriods: List<DatePeriod>): R {
        return newWithWrappedScope(InPeriodQueryHelper.buildInPeriodsQuery(key, datePeriods, formatter))
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The inDatePeriods filter checks if the given field has a date value which is within one of the provided
     * Periods.
     * @param periods periods to compare with the target field
     * @return the new repository
     */
    fun inPeriods(periods: List<Period>): R {
        val datePeriods: MutableList<DatePeriod> = ArrayList()
        for (period in periods) {
            datePeriods.add(DatePeriod.builder().startDate(period.startDate()).endDate(period.endDate()).build())
        }
        return inDatePeriods(datePeriods)
    }

    override fun wrapValue(value: Date): String {
        return "'${formatter.format(value)}'"
    }
}
