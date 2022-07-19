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
import org.hisp.dhis.android.core.arch.helpers.AccessHelper
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.category.CategoryOptionCollectionRepository
import org.hisp.dhis.android.core.category.CategoryOptionComboService
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.dataset.internal.DataSetInstanceServiceImpl
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitService
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.joda.time.Duration
import org.joda.time.Instant
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class DataSetInstanceServiceShould {

    private val dataSetUid: String = "dataSetUid"
    private val attOptionComboUid: String = "attOptionComboUid"
    private val orgUnitUid: String = "orgUnitUid"
    private val firstPeriodId: String = "firstPeriodId"
    private val secondPeriodId: String = "secondPeriodId"

    private val writeDataAccess = AccessHelper.createForDataWrite(true)

    private val dataSet: DataSet = mock()

    private val firstJanuary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-01-01T00:00:00.000")
    private val thirdJanuary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-01-03T00:00:00.000")
    private val firstFebruary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-02-01T00:00:00.000")
    private val thirdFebruary = BaseIdentifiableObject.DATE_FORMAT.parse("2020-02-03T00:00:00.000")

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

    private val categories: List<CategoryOption> = mock()

    private val dataSetInstanceService = DataSetInstanceServiceImpl(
        dataSetCollectionRepository = dataSetCollectionRepository,
        organisationUnitService = organisationUnitService,
        periodHelper = periodHelper,
        categoryOptionComboService = categoryOptionComboService,
        periodGenerator = periodGenerator,
        categoryOptionRepository = categoryOptionRepository
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
            orgUnitUid = orgUnitUid
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
            dataSetInstanceService.blockingIsPeriodInCategoryOptionRange(firstPeriod, attOptionComboUid)
        ).isTrue()
    }

    @Test
    fun `Should return true if attributeOptionCombo Assign To OrgUnit`() {
        whenever(categoryOptionComboService.blockingIsAssignedToOrgUnit(attOptionComboUid, orgUnitUid)).doReturn(true)
        assertThat(
            dataSetInstanceService.blockingIsAttributeOptionComboAssignToOrgUnit(attOptionComboUid, orgUnitUid)
        ).isTrue()
    }

    @Test
    fun `Should return false if dataSet is not closed`() {
        whenever(dataSet.periodType()) doReturn PeriodType.Monthly
        whenever(periodHelper.getPeriodForPeriodId(firstPeriodId).blockingGet()) doReturn secondPeriod
        whenever(periodGenerator.generatePeriod(any(), any(), any())) doReturn firstPeriod

        assertThat(dataSetInstanceService.blockingIsClosed(dataSet, firstPeriod)).isFalse()
    }

    @Test
    fun `Should return true if dataSet is closed`() {
        whenever(dataSet.periodType()) doReturn PeriodType.Monthly
        whenever(periodHelper.getPeriodForPeriodId(firstPeriodId).blockingGet()) doReturn firstPeriod
        whenever(periodGenerator.generatePeriod(any(), any(), any())) doReturn secondPeriod

        assertThat(dataSetInstanceService.blockingIsClosed(dataSet, firstPeriod)).isTrue()
    }

    @Test
    fun `Should return true if dataSet is expired`() {
        whenever(dataSet.periodType()) doReturn PeriodType.Daily
        whenever(dataSet.openFuturePeriods()) doReturn 20
        whenever(periodHelper.getPeriodForPeriodId(any()).blockingGet()) doReturn firstPeriod
        whenever(periodGenerator.generatePeriod(any(), any(), any())) doReturn firstPeriod

        assertThat(dataSetInstanceService.blockingIsExpired(dataSet, firstPeriod)).isTrue()
    }

    @Test
    fun `Should return false if dataSet is not expired`() {
        val now = Instant.now()
        val monthLater = now.plus(Duration.standardDays(30))

        whenever(periodHelper.getPeriodForPeriodId(secondPeriodId).blockingGet()) doReturn secondPeriod
        whenever(secondPeriod.endDate()) doReturn monthLater.toDate()
        whenever(dataSet.periodType()) doReturn PeriodType.Daily
        whenever(periodGenerator.generatePeriod(any(), any(), any())) doReturn secondPeriod

        assertThat(dataSetInstanceService.blockingIsExpired(dataSet, firstPeriod)).isFalse()
    }
}
