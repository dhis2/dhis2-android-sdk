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

package org.hisp.dhis.android.core.dataset.internal

import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxSingle
import org.hisp.dhis.android.core.arch.cache.internal.ExpirableCache
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.category.CategoryOptionCollectionRepository
import org.hisp.dhis.android.core.category.CategoryOptionComboCollectionRepository
import org.hisp.dhis.android.core.category.CategoryOptionComboService
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataelement.DataElementCollectionRepository
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetCollectionRepository
import org.hisp.dhis.android.core.dataset.DataSetEditableStatus
import org.hisp.dhis.android.core.dataset.DataSetInstanceService
import org.hisp.dhis.android.core.dataset.DataSetNonEditableReason
import org.hisp.dhis.android.core.datavalue.DataValueCollectionRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.koin.core.annotation.Singleton
import java.util.Date
import java.util.concurrent.TimeUnit

@Singleton
@Suppress("TooManyFunctions")
internal class DataSetInstanceServiceImpl(
    private val dataSetCollectionRepository: DataSetCollectionRepository,
    private val dataElementCollectionRepository: DataElementCollectionRepository,
    private val dataValueCollectionRepository: DataValueCollectionRepository,
    private val categoryOptionRepository: CategoryOptionCollectionRepository,
    private val categoryOptionComboCollectionRepository: CategoryOptionComboCollectionRepository,
    private val organisationUnitService: OrganisationUnitService,
    private val periodHelper: PeriodHelper,
    private val categoryOptionComboService: CategoryOptionComboService,
    private val periodGenerator: ParentPeriodGenerator,
) : DataSetInstanceService {

    override fun getEditableStatus(
        dataSetUid: String,
        periodId: String,
        organisationUnitUid: String,
        attributeOptionComboUid: String,
    ): Single<DataSetEditableStatus> {
        return rxSingle {
            suspendGetEditableStatus(dataSetUid, periodId, organisationUnitUid, attributeOptionComboUid)
        }
    }

    override fun blockingGetEditableStatus(
        dataSetUid: String,
        periodId: String,
        organisationUnitUid: String,
        attributeOptionComboUid: String,
    ): DataSetEditableStatus {
        return runBlocking {
            suspendGetEditableStatus(dataSetUid, periodId, organisationUnitUid, attributeOptionComboUid)
        }
    }

    @Suppress("ComplexMethod")
    override suspend fun suspendGetEditableStatus(
        dataSetUid: String,
        periodId: String,
        organisationUnitUid: String,
        attributeOptionComboUid: String,
    ): DataSetEditableStatus {
        val dataSet = dataSetCollectionRepository.withDataInputPeriods().uid(dataSetUid).getInternal()
        val period = periodHelper.suspendGetPeriodForPeriodId(periodId)
        return when {
            !suspendHasDataWriteAccess(dataSetUid) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.NO_DATASET_DATA_WRITE_ACCESS)

            !suspendIsCategoryOptionHasDataWriteAccess(attributeOptionComboUid) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.NO_ATTRIBUTE_OPTION_COMBO_ACCESS)

            !suspendIsPeriodInCategoryOptionRange(period, attributeOptionComboUid) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.PERIOD_IS_NOT_IN_ATTRIBUTE_OPTION_RANGE)

            !suspendIsOrgUnitInCaptureScope(organisationUnitUid) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.ORGUNIT_IS_NOT_IN_CAPTURE_SCOPE)

            !suspendIsAttributeOptionComboAssignToOrgUnit(attributeOptionComboUid, organisationUnitUid) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.ATTRIBUTE_OPTION_COMBO_NO_ASSIGN_TO_ORGUNIT)

            !suspendIsPeriodInOrgUnitRange(period, organisationUnitUid) ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.PERIOD_IS_NOT_IN_ORGUNIT_RANGE)

            dataSet?.let { isExpired(dataSet, period) } ?: false ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.EXPIRED)

            dataSet?.let { isClosed(dataSet, period) } ?: false ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.CLOSED)

            dataSet?.let { !isInDataInputPeriods(dataSet, period) } ?: false ->
                DataSetEditableStatus.NonEditable(DataSetNonEditableReason.PERIOD_NOT_IN_DATA_INPUT_PERIODS)

            else -> DataSetEditableStatus.Editable
        }
    }

    override fun hasDataWriteAccess(dataSetUid: String): Single<Boolean> {
        return rxSingle { suspendHasDataWriteAccess(dataSetUid) }
    }

    override fun blockingHasDataWriteAccess(dataSetUid: String): Boolean {
        return runBlocking { suspendHasDataWriteAccess(dataSetUid) }
    }

    override suspend fun suspendHasDataWriteAccess(dataSetUid: String): Boolean {
        val dataSet = dataSetCollectionRepository.uid(dataSetUid).getInternal() ?: return false
        return dataSet.access().write() ?: false
    }

    override fun getMissingMandatoryDataElementOperands(
        dataSetUid: String,
        periodId: String,
        organisationUnitUid: String,
        attributeOptionComboUid: String,
    ): Single<List<DataElementOperand>> {
        return rxSingle {
            suspendGetMissingMandatoryDataElementOperands(
                dataSetUid, periodId, organisationUnitUid, attributeOptionComboUid,
            )
        }
    }

    override fun blockingGetMissingMandatoryDataElementOperands(
        dataSetUid: String,
        periodId: String,
        organisationUnitUid: String,
        attributeOptionComboUid: String,
    ): List<DataElementOperand> {
        return runBlocking {
            suspendGetMissingMandatoryDataElementOperands(
                dataSetUid, periodId, organisationUnitUid, attributeOptionComboUid,
            )
        }
    }

    override suspend fun suspendGetMissingMandatoryDataElementOperands(
        dataSetUid: String,
        periodId: String,
        organisationUnitUid: String,
        attributeOptionComboUid: String,
    ): List<DataElementOperand> {
        val dataSet = dataSetCollectionRepository.withCompulsoryDataElementOperands()
            .uid(dataSetUid).getInternal()

        return dataSet?.compulsoryDataElementOperands()?.filter { dataElementOperand ->
            !hasDataValue(dataSetUid, dataElementOperand, periodId, organisationUnitUid, attributeOptionComboUid)
        } ?: emptyList()
    }

    private suspend fun hasDataValue(
        dataSetUid: String,
        dataElementOperand: DataElementOperand,
        periodId: String,
        organisationUnitUid: String,
        attributeOptionComboUid: String,
    ): Boolean {
        return dataElementOperand.dataElement()?.let { dataElement ->
            dataElementOperand.categoryOptionCombo()?.let { categoryOptionCombo ->
                dataValueCollectionRepository.byDeleted().isFalse.value(
                    periodId,
                    organisationUnitUid,
                    dataElement.uid(),
                    categoryOptionCombo.uid(),
                    attributeOptionComboUid,
                    dataSetUid,
                ).existsInternal()
            }
        } ?: false
    }

    override fun getMissingMandatoryFieldsCombination(
        dataSetUid: String,
        periodId: String,
        organisationUnitUid: String,
        attributeOptionComboUid: String,
    ): Single<List<DataElementOperand>> {
        return rxSingle {
            suspendGetMissingMandatoryFieldsCombination(
                dataSetUid, periodId, organisationUnitUid, attributeOptionComboUid,
            )
        }
    }

    override fun blockingGetMissingMandatoryFieldsCombination(
        dataSetUid: String,
        periodId: String,
        organisationUnitUid: String,
        attributeOptionComboUid: String,
    ): List<DataElementOperand> {
        return runBlocking {
            suspendGetMissingMandatoryFieldsCombination(
                dataSetUid, periodId, organisationUnitUid, attributeOptionComboUid,
            )
        }
    }

    @Suppress("MagicNumber")
    override suspend fun suspendGetMissingMandatoryFieldsCombination(
        dataSetUid: String,
        periodId: String,
        organisationUnitUid: String,
        attributeOptionComboUid: String,
    ): List<DataElementOperand> {
        val stringListCache = ExpirableCache<String, List<String>>(TimeUnit.SECONDS.toMillis(120))

        val dataSet = dataSetCollectionRepository.withDataSetElements()
            .uid(dataSetUid).getInternal() ?: return emptyList()

        if (dataSet.fieldCombinationRequired() != true) return emptyList()

        return dataSet.dataSetElements().orEmpty().flatMap { dataSetElement ->
            val categoryComboUid = dataSetElement.categoryCombo()?.uid()
                ?: dataElementCollectionRepository
                    .uid(dataSetElement.dataElement().uid())
                    .getInternal()
                    ?.categoryCombo()
                    ?.uid()

            categoryComboUid?.let { catComboUid ->
                val categoryOptionCombos = getCachedCategoryComboUid(catComboUid, stringListCache) { uid ->
                    categoryOptionComboCollectionRepository.byCategoryComboUid().eq(uid).getUidsInternal()
                }

                val dataValues = dataValueCollectionRepository
                    .byPeriod().eq(periodId)
                    .byOrganisationUnitUid().eq(organisationUnitUid)
                    .byAttributeOptionComboUid().eq(attributeOptionComboUid)
                    .byDeleted().isFalse
                    .byDataElementUid().eq(dataSetElement.dataElement().uid())
                    .byCategoryOptionComboUid()
                    .`in`(categoryOptionCombos)
                    .getInternal()

                dataValues.takeIf { it.isNotEmpty() && it.size != categoryOptionCombos.size }
                    ?.map { dataValue ->
                        DataElementOperand.builder().apply {
                            uid(
                                listOfNotNull(dataValue.dataElement(), dataValue.categoryOptionCombo())
                                    .joinToString("."),
                            )
                            dataValue.dataElement()?.let { dataElement(ObjectWithUid.create(it)) }
                            dataValue.categoryOptionCombo()
                                ?.let { categoryOptionCombo(ObjectWithUid.create(it)) }
                        }.build()
                    } ?: emptyList()
            } ?: emptyList()
        }
    }

    private suspend fun getCachedCategoryComboUid(
        uid: String?,
        categoryComboUidCache: ExpirableCache<String, List<String>>,
        getFromRepository: suspend (String) -> List<String>,
    ): List<String> {
        return if (uid != null) {
            categoryComboUidCache[uid] ?: getFromRepository(uid)
                .also { categoryComboUidCache[uid] = it }
        } else {
            emptyList()
        }
    }

    internal suspend fun suspendIsCategoryOptionHasDataWriteAccess(categoryOptionComboUid: String): Boolean {
        val categoryOptions = suspendGetCategoryOptions(categoryOptionComboUid)
        return categoryOptionComboService.hasWriteAccess(categoryOptions)
    }

    internal suspend fun suspendIsPeriodInCategoryOptionRange(period: Period, categoryOptionComboUid: String): Boolean {
        val categoryOptions = suspendGetCategoryOptions(categoryOptionComboUid)
        val dates = listOf(period.startDate(), period.endDate())
        return dates.all { date ->
            categoryOptionComboService.isInOptionRange(categoryOptions, date)
        }
    }

    internal suspend fun suspendIsOrgUnitInCaptureScope(orgUnitUid: String): Boolean {
        return organisationUnitService.suspendIsInCaptureScope(orgUnitUid)
    }

    internal suspend fun suspendIsAttributeOptionComboAssignToOrgUnit(
        categoryOptionComboUid: String,
        orgUnitUid: String,
    ): Boolean {
        return categoryOptionComboService.suspendIsAssignedToOrgUnit(
            categoryOptionComboUid = categoryOptionComboUid,
            orgUnitUid = orgUnitUid,
        )
    }

    @Suppress("MagicNumber")
    internal fun isExpired(dataSet: DataSet, period: Period): Boolean {
        val expiryDays = dataSet.expiryDays()
        return if (expiryDays == null || expiryDays <= 0.0) {
            false
        } else {
            val expiryDate = period.endDate()?.let { endDate ->
                Date(endDate.time + (expiryDays * 24 * 60 * 60 * 1000).toLong())
            }

            expiryDate?.let { Date().after(it) } ?: false
        }
    }

    internal fun isClosed(dataSet: DataSet, period: Period): Boolean {
        val periodType = dataSet.periodType() ?: return false
        val openFuturePeriods = dataSet.openFuturePeriods() ?: 0
        val latestFuturePeriod = periodGenerator.generatePeriod(
            periodType = periodType,
            date = Date(),
            offset = openFuturePeriods - 1,
        )
        return period.endDate()?.after(latestFuturePeriod?.endDate()) ?: false
    }

    internal suspend fun suspendIsPeriodInOrgUnitRange(period: Period, orgUnitUid: String): Boolean {
        return listOfNotNull(period.startDate(), period.endDate()).all { date ->
            organisationUnitService.suspendIsDateInOrgunitRange(orgUnitUid, date)
        }
    }

    internal fun isInDataInputPeriods(dataSet: DataSet, period: Period): Boolean {
        val dataInputPeriods = dataSet.dataInputPeriods()

        return dataInputPeriods.isNullOrEmpty() || dataInputPeriods
            .filter { it.period().uid() == period.periodId() }
            .takeIf { it.isNotEmpty() }
            ?.let { matchingPeriods ->
                val currentDate = Date()
                matchingPeriods.any { dataInputPeriod ->
                    val openingDate = dataInputPeriod.openingDate()
                    val closingDate = dataInputPeriod.closingDate()

                    (openingDate?.let { !currentDate.before(it) } ?: true) &&
                        (closingDate?.let { !currentDate.after(it) } ?: true)
                }
            } ?: false
    }

    private suspend fun suspendGetCategoryOptions(attributeOptionComboUid: String): List<CategoryOption> {
        return categoryOptionRepository
            .byCategoryOptionComboUid(attributeOptionComboUid)
            .getInternal()
    }
}
