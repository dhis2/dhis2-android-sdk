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

package org.hisp.dhis.android.core.analytics.aggregated.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLink
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.android.core.visualization.*
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString

@RunWith(JUnit4::class)
class AnalyticsVisualizationsServiceDimensionHelperShould {

    private val categoryStore: IdentifiableObjectStore<Category> = mock()
    private val categoryOptionLinkStore: LinkStore<CategoryCategoryOptionLink> = mock()
    private val organisationUnitLevelStore: IdentifiableObjectStore<OrganisationUnitLevel> = mock()
    private val category: Category = mock()
    private val visualization: Visualization = mock()
    private val orgUnitLevel: OrganisationUnitLevel = mock()

    private val uid1 = "GMpWZUg2QUf"
    private val uid2 = "AC6H8zCDb3B"
    private val uid3 = "eEIN8RQWxWp"

    private val helper = AnalyticsVisualizationsServiceDimensionHelper(
        categoryStore,
        categoryOptionLinkStore,
        organisationUnitLevelStore
    )

    @Test
    fun `Should parse dataElement dimension items`() {
        val dataDimensionItems = listOf(
            DataDimensionItem.builder()
                .dataElement(ObjectWithUid.create(uid1))
                .dataDimensionItemType(DataDimensionItemType.DATA_ELEMENT)
                .build()
        )

        whenever(visualization.dataDimensionItems()) doReturn dataDimensionItems

        val dimensionItems = helper.getDimensionItems(visualization, listOf("dx"))

        assertThat(dimensionItems).hasSize(1)
        when (val item = dimensionItems.first()) {
            is DimensionItem.DataItem.DataElementItem ->
                assertThat(item.uid).isEqualTo(uid1)
            else ->
                fail("Unexpected dimension item type")
        }
    }

    @Test
    fun `Should parse dataElementOperand dimension items`() {
        val dataDimensionItems = listOf(
            DataDimensionItem.builder()
                .dataElementOperand(ObjectWithUid.create("$uid1.$uid2"))
                .dataDimensionItemType(DataDimensionItemType.DATA_ELEMENT_OPERAND)
                .build()
        )

        whenever(visualization.dataDimensionItems()) doReturn dataDimensionItems

        val dimensionItems = helper.getDimensionItems(visualization, listOf("dx"))

        assertThat(dimensionItems).hasSize(1)
        when (val item = dimensionItems.first()) {
            is DimensionItem.DataItem.DataElementOperandItem -> {
                assertThat(item.dataElement).isEqualTo(uid1)
                assertThat(item.categoryOptionCombo).isEqualTo(uid2)
            }
            else ->
                fail("Unexpected dimension item type")
        }
    }

    @Test
    fun `Should parse indicator dimension items`() {
        val dataDimensionItems = listOf(
            DataDimensionItem.builder()
                .indicator(ObjectWithUid.create(uid1))
                .dataDimensionItemType(DataDimensionItemType.INDICATOR)
                .build()
        )

        whenever(visualization.dataDimensionItems()) doReturn dataDimensionItems

        val dimensionItems = helper.getDimensionItems(visualization, listOf("dx"))

        assertThat(dimensionItems).hasSize(1)
        when (val item = dimensionItems.first()) {
            is DimensionItem.DataItem.IndicatorItem ->
                assertThat(item.uid).isEqualTo(uid1)
            else ->
                fail("Unexpected dimension item type")
        }
    }

    @Test
    fun `Should parse programIndicator dimension items`() {
        val dataDimensionItems = listOf(
            DataDimensionItem.builder()
                .programIndicator(ObjectWithUid.create(uid1))
                .dataDimensionItemType(DataDimensionItemType.PROGRAM_INDICATOR)
                .build()
        )

        whenever(visualization.dataDimensionItems()) doReturn dataDimensionItems

        val dimensionItems = helper.getDimensionItems(visualization, listOf("dx"))

        assertThat(dimensionItems).hasSize(1)
        when (val item = dimensionItems.first()) {
            is DimensionItem.DataItem.ProgramIndicatorItem ->
                assertThat(item.uid).isEqualTo(uid1)
            else ->
                fail("Unexpected dimension item type")
        }
    }

    @Test
    fun `Should parse event dataElements dimension items`() {
        val dataDimensionItems = listOf(
            DataDimensionItem.builder()
                .programDataElement(
                    DataDimensionItemProgramDataElement.builder()
                        .uid("$uid1.$uid2")
                        .build()
                )
                .dataDimensionItemType(DataDimensionItemType.PROGRAM_DATA_ELEMENT)
                .build()
        )

        whenever(visualization.dataDimensionItems()) doReturn dataDimensionItems

        val dimensionItems = helper.getDimensionItems(visualization, listOf("dx"))

        assertThat(dimensionItems).hasSize(1)
        when (val item = dimensionItems.first()) {
            is DimensionItem.DataItem.EventDataItem.DataElement -> {
                assertThat(item.program).isEqualTo(uid1)
                assertThat(item.dataElement).isEqualTo(uid2)
            }
            else ->
                fail("Unexpected dimension item type")
        }
    }

    @Test
    fun `Should parse event attribute dimension items`() {
        val dataDimensionItems = listOf(
            DataDimensionItem.builder()
                .programAttribute(
                    DataDimensionItemProgramAttribute.builder()
                        .uid("$uid1.$uid2")
                        .build()
                )
                .dataDimensionItemType(DataDimensionItemType.PROGRAM_ATTRIBUTE)
                .build()
        )

        whenever(visualization.dataDimensionItems()) doReturn dataDimensionItems

        val dimensionItems = helper.getDimensionItems(visualization, listOf("dx"))

        assertThat(dimensionItems).hasSize(1)
        when (val item = dimensionItems.first()) {
            is DimensionItem.DataItem.EventDataItem.Attribute -> {
                assertThat(item.program).isEqualTo(uid1)
                assertThat(item.attribute).isEqualTo(uid2)
            }
            else ->
                fail("Unexpected dimension item type")
        }
    }

    @Test
    fun `Should parse organisation unit uids and levels`() {
        val orgunitItems = listOf(
            ObjectWithUid.create(uid1),
            ObjectWithUid.create(uid2)
        )
        val orgunitLevels = listOf(1)

        whenever(visualization.organisationUnits()) doReturn orgunitItems
        whenever(visualization.organisationUnitLevels()) doReturn orgunitLevels
        whenever(organisationUnitLevelStore.selectOneWhere(anyString())) doReturn orgUnitLevel
        whenever(orgUnitLevel.uid()) doReturn uid3

        val dimensionItems = helper.getDimensionItems(visualization, listOf("ou"))

        assertThat(dimensionItems).hasSize(3)

        val absolute = dimensionItems.filterIsInstance<DimensionItem.OrganisationUnitItem.Absolute>()
        assertThat(absolute.map { it.uid }).containsExactly(uid1, uid2)

        val levels = dimensionItems.filterIsInstance<DimensionItem.OrganisationUnitItem.Level>()
        assertThat(levels.map { it.uid }).containsExactly(uid3)
    }

    @Test
    fun `Should parse relative organisation unit`() {
        whenever(visualization.userOrganisationUnit()) doReturn true
        whenever(visualization.userOrganisationUnitChildren()) doReturn true
        whenever(visualization.userOrganisationUnitGrandChildren()) doReturn true

        val dimensionItems = helper.getDimensionItems(visualization, listOf("ou"))

        assertThat(dimensionItems).hasSize(3)

        val relative = dimensionItems.filterIsInstance<DimensionItem.OrganisationUnitItem.Relative>()
        assertThat(relative.map { it.relative }).containsExactly(
            RelativeOrganisationUnit.USER_ORGUNIT,
            RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN,
            RelativeOrganisationUnit.USER_ORGUNIT_GRANDCHILDREN
        )
    }

    @Test
    fun `Should parse period dimension items`() {
        val periods = listOf(
            ObjectWithUid.create(uid1),
            ObjectWithUid.create(uid2)
        )
        val relativePeriods = mapOf(
            RelativePeriod.THIS_MONTH to true,
            RelativePeriod.LAST_MONTH to true
        )

        whenever(visualization.periods()) doReturn periods
        whenever(visualization.relativePeriods()) doReturn relativePeriods

        val dimensionItems = helper.getDimensionItems(visualization, listOf("pe"))

        assertThat(dimensionItems).hasSize(4)

        val absolute = dimensionItems.filterIsInstance<DimensionItem.PeriodItem.Absolute>()
        assertThat(absolute.map { it.periodId }).containsExactly(uid1, uid2)

        val relative = dimensionItems.filterIsInstance<DimensionItem.PeriodItem.Relative>()
        assertThat(relative.map { it.relative }).containsExactly(RelativePeriod.THIS_MONTH, RelativePeriod.LAST_MONTH)
    }

    @Test
    fun `Should parse category dimension items`() {
        val categoryDimensions = listOf(
            CategoryDimension.builder()
                .category(ObjectWithUid.create(uid1))
                .categoryOptions(listOf(ObjectWithUid.create(uid2), ObjectWithUid.create(uid3)))
                .build()
        )

        whenever(visualization.categoryDimensions()) doReturn categoryDimensions
        whenever(categoryStore.selectByUid(uid1)) doReturn category

        val dimensionItems = helper.getDimensionItems(visualization, listOf(uid1))

        assertThat(dimensionItems).hasSize(2)

        val catDimension = dimensionItems.filterIsInstance<DimensionItem.CategoryItem>()
        catDimension.forEach { assertThat(it.uid).isEqualTo(uid1) }
        assertThat(catDimension.map { it.categoryOption }).containsExactly(uid2, uid3)
    }

    @Test
    fun `Should combine dimensions items`() {
        val periods = listOf(
            ObjectWithUid.create(uid1),
            ObjectWithUid.create(uid2)
        )
        val orgunits = listOf(
            ObjectWithUid.create(uid3)
        )

        whenever(visualization.periods()) doReturn periods
        whenever(visualization.organisationUnits()) doReturn orgunits

        val dimensionItems = helper.getDimensionItems(visualization, listOf("pe", "ou"))

        assertThat(dimensionItems).hasSize(3)

        val periodItems = dimensionItems.filterIsInstance<DimensionItem.PeriodItem.Absolute>()
        assertThat(periodItems).hasSize(2)

        val orgunitItems = dimensionItems.filterIsInstance<DimensionItem.OrganisationUnitItem.Absolute>()
        assertThat(orgunitItems).hasSize(1)
    }
}
