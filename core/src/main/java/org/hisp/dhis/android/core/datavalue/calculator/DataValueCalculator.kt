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

package org.hisp.dhis.android.core.datavalue.calculator

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import java.util.*
import javax.inject.Inject

/**
 * This calculator must accept several parameters in a builder pattern and return a result.
 * If the same parameter is provided several times, the evaluator must use the last one and ignore the previous ones.
 *
 * You can inject the class [DataValueStore] with Dagger. This class
 * can be used to retrieve the values from the database using the "select" methods.
 *
 * If you want to build custom "where" statements you can use the class [WhereClauseBuilder].
 * Additionally, you can know the column names looking at this [DataValueTableInfo.Columns].
 *
 */
@Reusable
class DataValueCalculator @Inject constructor() {

    /**
     * Filter the dataValues whose dataElement match exactly this parameter
     */
    fun withDataElement(dataElement: String): DataValueCalculator = TODO()

    /**
     * Filter the dataValues whose period match exactly this parameter
     */
    fun withPeriod(period: String): DataValueCalculator = TODO()

    /**
     * Filter the dataValues whose categoryOptionCombo match exactly this parameter
     */
    fun withCategoryOptionCombo(coc: String): DataValueCalculator = TODO()

    /**
     * Filter the dataValues whose created date is after this parameter.
     */
    fun withCreatedAfter(date: Date): DataValueCalculator = TODO()

    /**
     * Accepted aggregationTypes:
     * - [AggregationType.SUM]
     * - [AggregationType.AVERAGE]
     * - [AggregationType.MAX]
     * - [AggregationType.MIN]
     *
     * If the user does not provide an aggregation type or the aggregation type is not accepted, it must default
     * to [AggregationType.SUM]
     */
    fun withAggregationType(type: AggregationType): DataValueCalculator = TODO()

    /**
     * It must return the evaluation of the existing data values using the parameters provided.
     * If there is no matching dataValues, it must return a 0.0.
     * If any value cannot be converted to float, it must return a 0.0.
     */
    fun evaluate(): Float = TODO()

}