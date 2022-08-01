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
package org.hisp.dhis.android.core.indicator.datasetindicatorengine

import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.mapByUid
import org.hisp.dhis.android.core.constant.Constant
import org.hisp.dhis.android.core.constant.ConstantCollectionRepository
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueCollectionRepository
import org.hisp.dhis.android.core.indicator.IndicatorCollectionRepository
import org.hisp.dhis.android.core.indicator.IndicatorTypeCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLink
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitOrganisationUnitGroupLinkTableInfo
import org.hisp.dhis.android.core.parser.internal.service.dataobject.DimensionalItemObject
import org.hisp.dhis.android.core.parser.internal.service.utils.ExpressionHelper
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.internal.PeriodHelper

@Reusable
internal class DataSetIndicatorEngineImpl @Inject constructor(
    private val indicatorRepository: IndicatorCollectionRepository,
    private val indicatorTypeRepository: IndicatorTypeCollectionRepository,
    private val dataValueRepository: DataValueCollectionRepository,
    private val constantRepository: ConstantCollectionRepository,
    private val orgunitGroupLinkStore: LinkStore<OrganisationUnitOrganisationUnitGroupLink>,
    private val periodHelper: PeriodHelper,
    private val dataSetIndicatorEvaluator: DataSetIndicatorEvaluator
) : DataSetIndicatorEngine {

    override fun evaluate(
        indicatorUid: String,
        dataSetUid: String,
        periodId: String,
        orgUnitUid: String,
        attributeOptionComboUid: String
    ): Single<Double> {
        return Single.fromCallable {
            blockingEvaluate(indicatorUid, dataSetUid, periodId, orgUnitUid, attributeOptionComboUid)
        }
    }

    override fun blockingEvaluate(
        indicatorUid: String,
        dataSetUid: String,
        periodId: String,
        orgUnitUid: String,
        attributeOptionComboUid: String
    ): Double {
        val indicator = indicatorRepository.uid(indicatorUid).blockingGet()
        val indicatorType = indicatorTypeRepository.uid(indicator.indicatorType()?.uid()).blockingGet()

        val valueMap = getValueMap(dataSetUid, attributeOptionComboUid, orgUnitUid, periodId)
        val constantMap = getConstantMap()
        val orgunitGroupCountMap = getOrgunitGroupMap()
        val period = getPeriod(periodId)

        return dataSetIndicatorEvaluator.evaluate(
            indicator = indicator,
            indicatorType = indicatorType,
            valueMap = valueMap,
            constantMap = constantMap,
            orgUnitCountMap = orgunitGroupCountMap,
            days = PeriodHelper.getDays(period)
        )
    }

    private fun getValueMap(
        dataSetUid: String,
        attributeOptionComboUid: String,
        orgUnitUid: String,
        periodId: String
    ): Map<DimensionalItemObject, Double> {
        val dataValues: List<DataValue> = dataValueRepository
            .byDataSetUid(dataSetUid)
            .byPeriod().eq(periodId)
            .byOrganisationUnitUid().eq(orgUnitUid)
            .byAttributeOptionComboUid().eq(attributeOptionComboUid)
            .byDeleted().isFalse
            .blockingGet()

        return ExpressionHelper.getValueMap(dataValues)
    }

    private fun getConstantMap(): Map<String, Constant> {
        val constants: List<Constant> = constantRepository.blockingGet()
        return mapByUid(constants)
    }

    private fun getOrgunitGroupMap(): Map<String, Int> {
        return orgunitGroupLinkStore.groupAndGetCountBy(
            OrganisationUnitOrganisationUnitGroupLinkTableInfo.Columns.ORGANISATION_UNIT_GROUP
        )
    }

    private fun getPeriod(periodId: String): Period {
        return periodHelper.blockingGetPeriodForPeriodId(periodId)
    }
}
