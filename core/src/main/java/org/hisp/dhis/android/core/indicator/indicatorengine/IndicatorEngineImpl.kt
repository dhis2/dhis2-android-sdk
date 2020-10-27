/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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
package org.hisp.dhis.android.core.indicator.indicatorengine

import io.reactivex.Single
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.mapByUid
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.constant.ConstantCollectionRepository
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueCollectionRepository
import org.hisp.dhis.android.core.indicator.IndicatorCollectionRepository
import org.hisp.dhis.android.core.indicator.IndicatorTypeCollectionRepository
import org.hisp.dhis.android.core.parser.internal.service.ExpressionService
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject
import org.hisp.dhis.android.core.parser.internal.service.utils.ExpressionHelper
import org.hisp.dhis.android.core.validation.MissingValueStrategy
import java.util.*
import javax.inject.Inject

internal class IndicatorEngineImpl @Inject constructor(
    private val indicatorRepository: IndicatorCollectionRepository,
    private val indicatorTypeRepository: IndicatorTypeCollectionRepository,
    private val dataValueRepository: DataValueCollectionRepository,
    private val dataSetRepository: DataSetCollectionRepository,
    private val constantRepository: ConstantCollectionRepository,
    private val expressionService: ExpressionService
) : IndicatorEngine {

    override fun evaluateInDataSet(indicatorUid: String,
                                   dataSetUid: String,
                                   periodId: String,
                                   orgUnitUid: String,
                                   attributeOptionComboUid: String): Single<Double> {
        return Single.fromCallable {
            blockingEvaluateInDataSet(indicatorUid, dataSetUid, periodId, orgUnitUid, attributeOptionComboUid)
        }
    }

    override fun blockingEvaluateInDataSet(indicatorUid: String,
                                           dataSetUid: String,
                                           periodId: String,
                                           orgUnitUid: String,
                                           attributeOptionComboUid: String): Double {
        val indicator = indicatorRepository.uid(indicatorUid).blockingGet()
        val indicatorType = indicatorTypeRepository.uid(indicator.indicatorType()?.uid()).blockingGet()

        val valueMap = getValueMap(dataSetUid, attributeOptionComboUid, orgUnitUid, periodId)
        val constantMap = getConstantMap()

        val numerator = expressionService.getExpressionValue(indicator.numerator(), valueMap, constantMap,
            emptyMap(), 0, MissingValueStrategy.NEVER_SKIP) as Double

        val denominator = expressionService.getExpressionValue(indicator.numerator(), valueMap, constantMap,
            emptyMap(), 0, MissingValueStrategy.NEVER_SKIP) as Double

        val formula = "$numerator * ${indicatorType.factor() ?: 1} / $denominator"

        return expressionService.getExpressionValue(formula) as Double
    }

    private fun getValueMap(dataSetUid: String, attributeOptionComboUid: String,
                            orgUnitUid: String, periodId: String): Map<DimensionalItemObject, Double> {
        val dataSet: DataSet = dataSetRepository
            .byUid().eq(dataSetUid)
            .withDataSetElements()
            .one().blockingGet()
        val dataElementUids: MutableList<String> = ArrayList()
        if (dataSet != null && dataSet.dataSetElements() != null) {
            for (dataSetElement in dataSet.dataSetElements()!!) {
                dataElementUids.add(dataSetElement.dataElement().uid())
            }
        }
        val dataValues: List<DataValue> = dataValueRepository
            .byDataElementUid().`in`(dataElementUids)
            .byAttributeOptionComboUid().eq(attributeOptionComboUid)
            .byOrganisationUnitUid().eq(orgUnitUid)
            .byPeriod().eq(periodId)
            .byDeleted().isFalse()
            .blockingGet()
        return ExpressionHelper.getValueMap(dataValues)
    }

    private fun getConstantMap(): Map<String, Constant> {
        val constants: List<Constant> = constantRepository.blockingGet()
        return mapByUid(constants)
    }
}