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

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.helpers.DateUtils;
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator;
import org.hisp.dhis.android.core.period.DatePeriod;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.internal.InPeriodQueryHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class DateFilterConnector<R extends BaseRepository> extends BaseAbstractFilterConnector<R, Date> {

    DateFilterConnector(BaseRepositoryFactory<R> repositoryFactory,
                        RepositoryScope scope,
                        String key) {
        super(repositoryFactory, scope, key);
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The before filter checks if the given field has a date value which is before to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R before(Date value) {
        return newWithWrappedScope(FilterItemOperator.LT, value);
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The before filter checks if the given field has a date value which is before or equal to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R beforeOrEqual(Date value) {
        return newWithWrappedScope(FilterItemOperator.LE, value);
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The after filter checks if the given field has a date value which is after to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R after(Date value) {
        return newWithWrappedScope(FilterItemOperator.GT, value);
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The after filter checks if the given field has a date value which is after or equal to the one provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R afterOrEqual(Date value) {
        return newWithWrappedScope(FilterItemOperator.GE, value);
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The inDatePeriods filter checks if the given field has a date value which is within one of the provided
     * DatePeriods.
     * @param datePeriods date periods to compare with the target field
     * @return the new repository
     */
    public R inDatePeriods(@NonNull List<DatePeriod> datePeriods) {
        return newWithWrappedScope(InPeriodQueryHelper.buildInPeriodsQuery(key, datePeriods));
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The inDatePeriods filter checks if the given field has a date value which is within one of the provided
     * Periods.
     * @param periods periods to compare with the target field
     * @return the new repository
     */
    public R inPeriods(@NonNull List<Period> periods) {
        List<DatePeriod> datePeriods = new ArrayList<>();
        for (Period period : periods) {
            datePeriods.add(DatePeriod.builder().startDate(period.startDate()).endDate(period.endDate()).build());
        }
        return inDatePeriods(datePeriods);
    }

    protected String wrapValue(Date value) {
        return "'" + DateUtils.DATE_FORMAT.format(value) + "'";
    }
}