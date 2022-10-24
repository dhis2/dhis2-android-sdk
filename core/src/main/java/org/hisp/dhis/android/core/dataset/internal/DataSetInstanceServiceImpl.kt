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

package org.hisp.dhis.android.core.dataset.internal

import dagger.Reusable
import io.reactivex.Single
import java.util.Date
import javax.inject.Inject
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.category.CategoryOptionCollectionRepository
import org.hisp.dhis.android.core.category.CategoryOptionComboService
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository
import org.hisp.dhis.android.core.dataset.DataSetEditableStatus
import org.hisp.dhis.android.core.dataset.DataSetInstanceService
import org.hisp.dhis.android.core.dataset.DataSetNonEditableReason
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator
import org.hisp.dhis.android.core.period.internal.PeriodHelper

@Reusable
@Suppress("TooManyFunctions")
internal class DataSetInstanceServiceImpl @Inject constructor(
    private val dataSetCollectionRepository: DataSetCollectionRepository,
    private val categoryOptionRepository: CategoryOptionCollectionRepository,
    private val organisationUnitService: OrganisationUnitService,
    private val periodHelper: PeriodHelper,
    private val categoryOptionComboService: CategoryOptionComboService,
    private val periodGenerator: ParentPeriodGenerator,
) : DataSetInstanceService {

    override fun getEditableStatus(
        dataSetUid: String,
        periodId: String,
        organisationUnitUid: String,
        attributeOptionComboUid: String
    ): Single<DataSetEditableStatus> {
        return Single.fromCallable {
            blockingGetEditableStatus(
                dataSetUid = dataSetUid,
                periodId = periodId,
                organisationUnitUid = organisationUnitUid,
                attributeOptionComboUid = attributeOptionComboUid
            )
        }
    }

    @Suppress("ComplexMethod")
    override fun blockingGetEditableStatus(
        dataSetUid: String,
        periodId: String,
        organisationUnitUid: String,
        attributeOptionComboUid: String
    ): DataSetEditableStatus {
        val dataSet = dataSetCollectionRepository.uid(dataSetUid).blockingGet()
        val period = periodHelper.getPeriodForPeriodId(periodId).blockingGet()
        return when {
            !blockingHasDataWriteAccess(dataSetUid) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.NO_DATASET_DATA_WRITE_ACCESS)
            !blockingIsCategoryOptionHasDataWriteAccess(attributeOptionComboUid) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.NO_ATTRIBUTE_OPTION_COMBO_ACCESS)
            !blockingIsPeriodInCategoryOptionRange(period, attributeOptionComboUid) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.PERIOD_IS_NOT_IN_ATTRIBUTE_OPTION_RANGE)
            !blockingIsOrgUnitInCaptureScope(organisationUnitUid) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.ORGUNIT_IS_NOT_IN_CAPTURE_SCOPE)
            !blockingIsAttributeOptionComboAssignToOrgUnit(attributeOptionComboUid, organisationUnitUid) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.ATTRIBUTE_OPTION_COMBO_NO_ASSIGN_TO_ORGUNIT)
            !blockingIsPeriodInOrgUnitRange(period, organisationUnitUid) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.PERIOD_IS_NOT_IN_ORGUNIT_RANGE)
            !blockingIsExpired(dataSet, period) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.EXPIRED)
            !blockingIsClosed(dataSet, period) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.CLOSED)
            else -> DataSetEditableStatus.Editable
        }
    }

    fun blockingIsCategoryOptionHasDataWriteAccess(categoryOptionComboUid: String): Boolean {
        val categoryOptions = getCategoryOptions(categoryOptionComboUid)
        return categoryOptionComboService.blockingHasWriteAccess(categoryOptions)
    }

    fun blockingIsPeriodInCategoryOptionRange(period: Period, categoryOptionComboUid: String): Boolean {
        val categoryOptions = getCategoryOptions(categoryOptionComboUid)
        val dates = listOf(period.startDate(), period.endDate())
        return dates.all { date ->
            categoryOptionComboService.isInOptionRange(categoryOptions, date)
        }
    }

    fun blockingIsOrgUnitInCaptureScope(orgUnitUid: String): Boolean {
        return organisationUnitService.blockingIsInCaptureScope(orgUnitUid)
    }

    fun blockingIsAttributeOptionComboAssignToOrgUnit(
        categoryOptionComboUid: String,
        orgUnitUid: String
    ): Boolean {
        return categoryOptionComboService.blockingIsAssignedToOrgUnit(
            categoryOptionComboUid = categoryOptionComboUid,
            orgUnitUid = orgUnitUid
        )
    }

    fun blockingIsExpired(dataSet: DataSet, period: Period): Boolean {
        val expiryDays = dataSet.expiryDays() ?: return false
        val generatedPeriod = period.endDate()?.let { endDate ->
            periodGenerator.generatePeriod(
                periodType = PeriodType.Daily,
                date = endDate,
                offset = expiryDays - 1
            )
        }
        return Date().after(generatedPeriod?.endDate())
    }

    fun blockingIsClosed(dataSet: DataSet, period: Period): Boolean {
        val periodType = dataSet.periodType() ?: return true
        val openFuturePeriods = dataSet.openFuturePeriods() ?: 0
        val generatedPeriod = periodGenerator.generatePeriod(
            periodType = periodType,
            date = Date(),
            offset = openFuturePeriods - 1
        )
        return period.endDate()?.before(generatedPeriod?.endDate()) ?: true
    }

    override fun hasDataWriteAccess(dataSetUid: String): Single<Boolean> {
        return Single.just(blockingHasDataWriteAccess(dataSetUid))
    }

    fun blockingHasDataWriteAccess(dataSetUid: String): Boolean {
        val dataSet = dataSetCollectionRepository.uid(dataSetUid).blockingGet() ?: return false
        return dataSet.access().write() ?: false
    }

    fun blockingIsPeriodInOrgUnitRange(period: Period, orgUnitUid: String): Boolean {
        return listOfNotNull(period.startDate(), period.endDate()).all { date ->
            organisationUnitService.blockingIsDateInOrgunitRange(orgUnitUid, date)
        }
    }

    private fun getCategoryOptions(attributeOptionComboUid: String): List<CategoryOption> {
        return categoryOptionRepository
            .byCategoryOptionComboUid(attributeOptionComboUid)
            .blockingGet()
    }
}
