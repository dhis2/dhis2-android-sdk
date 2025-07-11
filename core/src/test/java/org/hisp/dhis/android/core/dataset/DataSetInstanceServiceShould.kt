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

package org.hisp.dhis.android.core.dataset

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import kotlinx.datetime.Clock
import org.hisp.dhis.android.core.arch.helpers.AccessHelper
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.category.CategoryOptionCollectionRepository
import org.hisp.dhis.android.core.category.CategoryOptionComboCollectionRepository
import org.hisp.dhis.android.core.category.CategoryOptionComboService
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataelement.DataElementCollectionRepository
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.dataset.internal.DataSetInstanceServiceImpl
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueCollectionRepository
import org.hisp.dhis.android.core.datavalue.DataValueObjectRepository
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.Date
import kotlin.time.Duration.Companion.days

class DataSetInstanceServiceShould {

    private val dataSetUid: String = "dataSetUid"
    private val attOptionComboUid: String = "attOptionComboUid"
    private val orgUnitUid: String = "orgUnitUid"
    private val firstPeriodId: String = "firstPeriodId"
    private val secondPeriodId: String = "secondPeriodId"

    private val writeDataAccess = AccessHelper.createForDataWrite(true)

    private val dataSet: DataSet = mock()

    private val firstJanuary = DateUtils.DATE_FORMAT.parse("2020-01-01T00:00:00.000")
    private val thirdJanuary = DateUtils.DATE_FORMAT.parse("2020-01-03T00:00:00.000")
    private val firstFebruary = DateUtils.DATE_FORMAT.parse("2020-02-01T00:00:00.000")
    private val thirdFebruary = DateUtils.DATE_FORMAT.parse("2020-02-03T00:00:00.000")

    private val firstPeriod: Period = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val secondPeriod: Period = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)

    private val organisationUnitService: OrganisationUnitService = mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val categoryOptionComboService: CategoryOptionComboService =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val categoryOptionRepository: CategoryOptionCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val periodHelper: PeriodHelper = mock(verboseLogging = true, defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val periodGenerator: ParentPeriodGenerator = mock(verboseLogging = true)
    private val dataSetCollectionRepository: DataSetCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val dataElementCollectionRepository: DataElementCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val dataValueCollectionRepository: DataValueCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)
    private val categoryOptionComboCollectionRepository: CategoryOptionComboCollectionRepository =
        mock(defaultAnswer = Mockito.RETURNS_DEEP_STUBS)

    private val categories: List<CategoryOption> = mock()

    private val dataSetInstanceService = DataSetInstanceServiceImpl(
        dataSetCollectionRepository = dataSetCollectionRepository,
        organisationUnitService = organisationUnitService,
        dataElementCollectionRepository = dataElementCollectionRepository,
        dataValueCollectionRepository = dataValueCollectionRepository,
        categoryOptionComboCollectionRepository = categoryOptionComboCollectionRepository,
        periodHelper = periodHelper,
        categoryOptionComboService = categoryOptionComboService,
        periodGenerator = periodGenerator,
        categoryOptionRepository = categoryOptionRepository,
    )

    @Before
    fun setUp() {
        whenever(dataSet.uid()) doReturn dataSetUid
        whenever(categoryOptionRepository.byCategoryOptionComboUid(any()).blockingGet()) doReturn categories
        whenever(dataSetCollectionRepository.uid(any()).blockingGet()) doReturn dataSet
        whenever(periodHelper.getPeriodForPeriodId(firstPeriodId).blockingGet()) doReturn firstPeriod
        whenever(firstPeriod.startDate()) doReturn firstJanuary
        whenever(firstPeriod.endDate()) doReturn thirdJanuary
        whenever(secondPeriod.startDate()) doReturn firstFebruary
        whenever(secondPeriod.endDate()) doReturn thirdFebruary
    }

    @Test
    fun `Should return true if dataSet has write access`() {
        whenever(dataSet.access()) doReturn writeDataAccess
        assertThat(dataSetInstanceService.blockingHasDataWriteAccess(dataSet.uid())).isTrue()
    }

    @Test
    fun `Should return true if is in OrgUnit capture scope`() {
        whenever(organisationUnitService.blockingIsInCaptureScope(orgUnitUid)) doReturn true
        assertThat(dataSetInstanceService.blockingIsOrgUnitInCaptureScope(orgUnitUid)).isTrue()
    }

    @Test
    fun `Should return true if period is in OrgUnit`() {
        val end = firstPeriod.endDate()!!
        val start = firstPeriod.startDate()!!
        whenever(organisationUnitService.blockingIsDateInOrgunitRange(orgUnitUid, end)) doReturn true
        whenever(organisationUnitService.blockingIsDateInOrgunitRange(orgUnitUid, start)) doReturn true

        val isPeriodInOrgUnitRange = dataSetInstanceService.blockingIsPeriodInOrgUnitRange(
            period = firstPeriod,
            orgUnitUid = orgUnitUid,
        )
        assertThat(isPeriodInOrgUnitRange).isTrue()
    }

    @Test
    fun `Should return true if CategoryOption Has data write access`() {
        whenever(categoryOptionComboService.blockingHasWriteAccess(categories)) doReturn true
        assertThat(dataSetInstanceService.blockingIsCategoryOptionHasDataWriteAccess(attOptionComboUid)).isTrue()
    }

    @Test
    fun `Should return true if Period is in Category Option Range`() {
        whenever(categoryOptionComboService.isInOptionRange(categories, firstPeriod.startDate())) doReturn true
        whenever(categoryOptionComboService.isInOptionRange(categories, firstPeriod.endDate())) doReturn true
        assertThat(
            dataSetInstanceService.blockingIsPeriodInCategoryOptionRange(firstPeriod, attOptionComboUid),
        ).isTrue()
    }

    @Test
    fun `Should return true if attributeOptionCombo Assign To OrgUnit`() {
        whenever(categoryOptionComboService.blockingIsAssignedToOrgUnit(attOptionComboUid, orgUnitUid)).doReturn(true)
        assertThat(
            dataSetInstanceService.blockingIsAttributeOptionComboAssignToOrgUnit(attOptionComboUid, orgUnitUid),
        ).isTrue()
    }

    @Test
    fun `Should return false if dataSet is not closed`() {
        whenever(dataSet.periodType()) doReturn PeriodType.Monthly
        whenever(periodGenerator.generatePeriod(any(), any(), any())) doReturn firstPeriod

        assertThat(dataSetInstanceService.blockingIsClosed(dataSet, firstPeriod)).isFalse()
    }

    @Test
    fun `Should return true if dataSet is closed`() {
        whenever(dataSet.periodType()) doReturn PeriodType.Monthly
        whenever(periodGenerator.generatePeriod(any(), any(), any())) doReturn firstPeriod

        assertThat(dataSetInstanceService.blockingIsClosed(dataSet, secondPeriod)).isTrue()
    }

    @Test
    fun `Should return true if dataSet is expired`() {
        whenever(dataSet.periodType()) doReturn PeriodType.Daily
        whenever(dataSet.openFuturePeriods()) doReturn 20
        whenever(dataSet.expiryDays()) doReturn 5.0
        whenever(periodHelper.getPeriodForPeriodId(any()).blockingGet()) doReturn firstPeriod
        whenever(periodGenerator.generatePeriod(any(), any(), any())) doReturn firstPeriod

        assertThat(dataSetInstanceService.blockingIsExpired(dataSet, firstPeriod)).isTrue()
    }

    @Test
    fun `Should return false if dataSet is not expired`() {
        val now = Clock.System.now()
        val monthLater = now.plus(30.days)

        whenever(periodHelper.getPeriodForPeriodId(secondPeriodId).blockingGet()) doReturn secondPeriod
        whenever(secondPeriod.endDate()) doReturn Date(monthLater.toEpochMilliseconds())
        whenever(dataSet.periodType()) doReturn PeriodType.Daily
        whenever(periodGenerator.generatePeriod(any(), any(), any())) doReturn secondPeriod

        assertThat(dataSetInstanceService.blockingIsExpired(dataSet, firstPeriod)).isFalse()
    }

    @Test
    fun `Should return false if expiry days is 0 or negative`() {
        whenever(dataSet.expiryDays()) doReturn 0.0
        assertThat(dataSetInstanceService.blockingIsExpired(dataSet, firstPeriod)).isFalse()

        whenever(dataSet.expiryDays()) doReturn -15.0
        assertThat(dataSetInstanceService.blockingIsExpired(dataSet, firstPeriod)).isFalse()
    }

    @Test
    fun `Should return missing mandatory data element operands when data value is missing`() {
        val operand = mock<DataElementOperand> {
            on { dataElement() } doReturn ObjectWithUid.create("de1")
            on { categoryOptionCombo() } doReturn ObjectWithUid.create("coc1")
        }

        val dataSetWithCompulsory = mock<DataSet> {
            on { compulsoryDataElementOperands() } doReturn listOf(operand)
        }

        whenever(dataSetCollectionRepository.withCompulsoryDataElementOperands().uid(dataSetUid).get())
            .thenReturn(Single.just(dataSetWithCompulsory))

        val dataValueQuery = mock<DataValueObjectRepository> {
            on { blockingExists() } doReturn false
        }
        whenever(
            dataValueCollectionRepository.value(
                firstPeriodId,
                orgUnitUid,
                "de1",
                "coc1",
                attOptionComboUid,
            ),
        ).thenReturn(dataValueQuery)

        dataSetInstanceService.getMissingMandatoryDataElementOperands(
            dataSetUid,
            firstPeriodId,
            orgUnitUid,
            attOptionComboUid,
        )
            .test()
            .assertValue { missingOperands ->
                missingOperands.size == 1 && missingOperands.first() == operand
            }
    }

    @Test
    @Suppress("LongMethod")
    fun `Should return missing mandatory fields combination when data values are incomplete`() {
        val dataSetElement = mock<DataSetElement> {
            on { categoryCombo() } doReturn ObjectWithUid.create("ccUid")
            on { dataElement() } doReturn ObjectWithUid.create("de1")
        }
        val dataSetWithElements = mock<DataSet> {
            on { fieldCombinationRequired() } doReturn true
            on { dataSetElements() } doReturn listOf(dataSetElement)
        }
        whenever(dataSetCollectionRepository.withDataSetElements().uid(dataSetUid).get())
            .thenReturn(Single.just(dataSetWithElements))

        val byCatComboUidConnector = mock<StringFilterConnector<CategoryOptionComboCollectionRepository>>()
        val eqRepo = mock<CategoryOptionComboCollectionRepository>()
        whenever(categoryOptionComboCollectionRepository.byCategoryComboUid()).thenReturn(byCatComboUidConnector)
        whenever(byCatComboUidConnector.eq("ccUid")).thenReturn(eqRepo)
        whenever(eqRepo.blockingGetUids()).thenReturn(listOf("coc1", "coc2"))

        val dataValue = mock<DataValue> {
            on { dataElement() } doReturn "de1"
            on { categoryOptionCombo() } doReturn "coc1"
        }

        val periodConnector = mock<StringFilterConnector<DataValueCollectionRepository>>()
        val repoAfterPeriod = mock<DataValueCollectionRepository>()
        whenever(dataValueCollectionRepository.byPeriod()).thenReturn(periodConnector)
        whenever(periodConnector.eq(firstPeriodId)).thenReturn(repoAfterPeriod)

        val orgUnitConnector = mock<StringFilterConnector<DataValueCollectionRepository>>()
        val repoAfterOrgUnit = mock<DataValueCollectionRepository>()
        whenever(repoAfterPeriod.byOrganisationUnitUid()).thenReturn(orgUnitConnector)
        whenever(orgUnitConnector.eq(orgUnitUid)).thenReturn(repoAfterOrgUnit)

        val attrOptionConnector = mock<StringFilterConnector<DataValueCollectionRepository>>()
        val repoAfterAttrOption = mock<DataValueCollectionRepository>()
        whenever(repoAfterOrgUnit.byAttributeOptionComboUid()).thenReturn(attrOptionConnector)
        whenever(attrOptionConnector.eq(attOptionComboUid)).thenReturn(repoAfterAttrOption)

        val deletedConnector = mock<BooleanFilterConnector<DataValueCollectionRepository>>()
        val repoAfterDeleted = mock<DataValueCollectionRepository>()
        whenever(repoAfterAttrOption.byDeleted()).thenReturn(deletedConnector)
        whenever(deletedConnector.isFalse).thenReturn(repoAfterDeleted)

        val deConnector = mock<StringFilterConnector<DataValueCollectionRepository>>()
        val repoAfterDE = mock<DataValueCollectionRepository>()
        whenever(repoAfterDeleted.byDataElementUid()).thenReturn(deConnector)
        whenever(deConnector.eq("de1")).thenReturn(repoAfterDE)

        val cocConnector = mock<StringFilterConnector<DataValueCollectionRepository>>()
        val finalRepo = mock<DataValueCollectionRepository>()
        whenever(repoAfterDE.byCategoryOptionComboUid()).thenReturn(cocConnector)
        whenever(cocConnector.`in`(listOf("coc1", "coc2"))).thenReturn(finalRepo)

        whenever(finalRepo.blockingGet()).thenReturn(listOf(dataValue))

        dataSetInstanceService.getMissingMandatoryFieldsCombination(
            dataSetUid,
            firstPeriodId,
            orgUnitUid,
            attOptionComboUid,
        )
            .test()
            .assertValue { missingFields ->
                missingFields.size == 1 &&
                    missingFields.first().dataElement()?.uid() == "de1" &&
                    missingFields.first().categoryOptionCombo()?.uid() == "coc1" &&
                    missingFields.first().uid() == "de1.coc1"
            }
    }
}
