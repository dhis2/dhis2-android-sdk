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
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.internal.CategoryCategoryOptionLinkStore
import org.hisp.dhis.android.core.category.internal.CategoryStore
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStore
import org.hisp.dhis.android.core.visualization.DimensionItemType
import org.hisp.dhis.android.core.visualization.VisualizationDimension
import org.hisp.dhis.android.core.visualization.VisualizationDimensionItem
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyString

@RunWith(JUnit4::class)
class AnalyticsVisualizationsServiceDimensionHelperShould {

    private val categoryStore: CategoryStore = mock()
    private val categoryOptionLinkStore: CategoryCategoryOptionLinkStore = mock()
    private val organisationUnitLevelStore: OrganisationUnitLevelStore = mock()
    private val category: Category = mock()
    private val orgUnitLevel: OrganisationUnitLevel = mock()

    private val uid1 = "GMpWZUg2QUf"
    private val uid2 = "AC6H8zCDb3B"
    private val uid3 = "eEIN8RQWxWp"

    private val helper = AnalyticsVisualizationsServiceDimensionHelper(
        categoryStore,
        categoryOptionLinkStore,
        organisationUnitLevelStore,
    )

    @Test
    fun `Should parse dataElement dimension items`() {
        val dataDimensions = listOf(
            VisualizationDimension.builder()
                .id("dx")
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid1)
                            .dimensionItemType(DimensionItemType.DATA_ELEMENT.name)
                            .build(),
                    ),
                )
                .build(),
        )

        val dimensionItems = helper.getDimensionItems(dataDimensions)

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
        val dataDimensions = listOf(
            VisualizationDimension.builder()
                .id("dx")
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem("$uid1.$uid2")
                            .dimensionItemType(DimensionItemType.DATA_ELEMENT_OPERAND.name)
                            .build(),
                    ),
                )
                .build(),
        )

        val dimensionItems = helper.getDimensionItems(dataDimensions)

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
        val dataDimensions = listOf(
            VisualizationDimension.builder()
                .id("dx")
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid1)
                            .dimensionItemType(DimensionItemType.INDICATOR.name)
                            .build(),
                    ),
                )
                .build(),
        )

        val dimensionItems = helper.getDimensionItems(dataDimensions)

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
        val dataDimensions = listOf(
            VisualizationDimension.builder()
                .id("dx")
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid1)
                            .dimensionItemType(DimensionItemType.PROGRAM_INDICATOR.name)
                            .build(),
                    ),
                )
                .build(),
        )

        val dimensionItems = helper.getDimensionItems(dataDimensions)

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
        val dataDimensions = listOf(
            VisualizationDimension.builder()
                .id("dx")
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem("$uid1.$uid2")
                            .dimensionItemType(DimensionItemType.PROGRAM_DATA_ELEMENT.name)
                            .build(),
                    ),
                )
                .build(),
        )

        val dimensionItems = helper.getDimensionItems(dataDimensions)

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
        val dataDimensions = listOf(
            VisualizationDimension.builder()
                .id("dx")
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem("$uid1.$uid2")
                            .dimensionItemType(DimensionItemType.PROGRAM_ATTRIBUTE.name)
                            .build(),
                    ),
                )
                .build(),
        )

        val dimensionItems = helper.getDimensionItems(dataDimensions)

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
    fun `Should parse event dataElements with options dimension items`() {
        val dataDimensions = listOf(
            VisualizationDimension.builder().id("dx").items(
                listOf(
                    VisualizationDimensionItem.builder().dimensionItem("$uid1.$uid2.$uid3")
                        .dimensionItemType(DimensionItemType.PROGRAM_DATA_ELEMENT_OPTION.name).build(),
                ),
            ).build(),
        )

        val dimensionItem = helper.getDimensionItems(dataDimensions)

        assertThat(dimensionItem).hasSize(1)
        when (val item = dimensionItem.first()) {
            is DimensionItem.DataItem.EventDataItem.DataElementOption -> {
                assertThat(item.program).isEqualTo(uid1)
                assertThat(item.dataElement).isEqualTo(uid2)
                assertThat(item.option).isEqualTo(uid3)
            }

            else -> {
                fail("Unexpected dimension item type")
            }
        }
    }

    @Test
    fun `Should parse event attributes with options dimension items`() {
        val dataDimensions = listOf(
            VisualizationDimension.builder().id("dx").items(
                listOf(
                    VisualizationDimensionItem.builder().dimensionItem("$uid1.$uid2.$uid3")
                        .dimensionItemType(DimensionItemType.PROGRAM_ATTRIBUTE_OPTION.name).build(),
                ),
            ).build(),
        )

        val dimensionItem = helper.getDimensionItems(dataDimensions)

        assertThat(dimensionItem).hasSize(1)
        when (val item = dimensionItem.first()) {
            is DimensionItem.DataItem.EventDataItem.AttributeOption -> {
                assertThat(item.program).isEqualTo(uid1)
                assertThat(item.attribute).isEqualTo(uid2)
                assertThat(item.option).isEqualTo(uid3)
            }

            else -> {
                fail("Unexpected dimension item type")
            }
        }
    }

    @Test
    fun `Should parse expression dimension items`() {
        val dataDimensions = listOf(
            VisualizationDimension.builder()
                .id("dx")
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid1)
                            .dimensionItemType(DimensionItemType.EXPRESSION_DIMENSION_ITEM.name)
                            .build(),
                    ),
                )
                .build(),
        )

        val dimensionItems = helper.getDimensionItems(dataDimensions)

        assertThat(dimensionItems).hasSize(1)
        when (val item = dimensionItems.first()) {
            is DimensionItem.DataItem.ExpressionDimensionItem -> {
                assertThat(item.uid).isEqualTo(uid1)
            }

            else ->
                fail("Unexpected dimension item type")
        }
    }

    @Test
    fun `Should parse organisation unit uids and levels`() {
        val orgunitDimensions = listOf(
            VisualizationDimension.builder()
                .id("ou")
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid1)
                            .dimensionItemType(DimensionItemType.ORGANISATION_UNIT.name)
                            .build(),
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid2)
                            .dimensionItemType(DimensionItemType.ORGANISATION_UNIT.name)
                            .build(),
                        VisualizationDimensionItem.builder()
                            .dimensionItem("LEVEL-1")
                            .build(),
                    ),
                )
                .build(),
        )

        whenever(organisationUnitLevelStore.selectOneWhere(anyString())) doReturn orgUnitLevel
        whenever(orgUnitLevel.uid()) doReturn uid3

        val dimensionItems = helper.getDimensionItems(orgunitDimensions)

        assertThat(dimensionItems).hasSize(3)

        val absolute = dimensionItems.filterIsInstance<DimensionItem.OrganisationUnitItem.Absolute>()
        assertThat(absolute.map { it.uid }).containsExactly(uid1, uid2)

        val levels = dimensionItems.filterIsInstance<DimensionItem.OrganisationUnitItem.Level>()
        assertThat(levels.map { it.uid }).containsExactly(uid3)
    }

    @Test
    fun `Should parse relative organisation unit`() {
        val orgunitDimensions = listOf(
            VisualizationDimension.builder()
                .id("ou")
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem(RelativeOrganisationUnit.USER_ORGUNIT.name)
                            .build(),
                        VisualizationDimensionItem.builder()
                            .dimensionItem(RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN.name)
                            .build(),
                        VisualizationDimensionItem.builder()
                            .dimensionItem(RelativeOrganisationUnit.USER_ORGUNIT_GRANDCHILDREN.name)
                            .build(),
                    ),
                )
                .build(),
        )

        val dimensionItems = helper.getDimensionItems(orgunitDimensions)

        assertThat(dimensionItems).hasSize(3)

        val relative = dimensionItems.filterIsInstance<DimensionItem.OrganisationUnitItem.Relative>()
        assertThat(relative.map { it.relative }).containsExactly(
            RelativeOrganisationUnit.USER_ORGUNIT,
            RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN,
            RelativeOrganisationUnit.USER_ORGUNIT_GRANDCHILDREN,
        )
    }

    @Test
    fun `Should parse period dimension items`() {
        val periodDimensions = listOf(
            VisualizationDimension.builder()
                .id("pe")
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid1)
                            .dimensionItemType(DimensionItemType.PERIOD.name)
                            .build(),
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid2)
                            .dimensionItemType(DimensionItemType.PERIOD.name)
                            .build(),
                        VisualizationDimensionItem.builder()
                            .dimensionItem(RelativePeriod.THIS_MONTH.name)
                            .dimensionItemType(DimensionItemType.PERIOD.name)
                            .build(),
                        VisualizationDimensionItem.builder()
                            .dimensionItem(RelativePeriod.LAST_MONTH.name)
                            .dimensionItemType(DimensionItemType.PERIOD.name)
                            .build(),
                    ),
                )
                .build(),
        )

        val dimensionItems = helper.getDimensionItems(periodDimensions)

        assertThat(dimensionItems).hasSize(4)

        val absolute = dimensionItems.filterIsInstance<DimensionItem.PeriodItem.Absolute>()
        assertThat(absolute.map { it.periodId }).containsExactly(uid1, uid2)

        val relative = dimensionItems.filterIsInstance<DimensionItem.PeriodItem.Relative>()
        assertThat(relative.map { it.relative }).containsExactly(RelativePeriod.THIS_MONTH, RelativePeriod.LAST_MONTH)
    }

    @Test
    fun `Should parse category dimension items`() {
        val categoryDimensions = listOf(
            VisualizationDimension.builder()
                .id(uid1)
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid2)
                            .dimensionItemType(DimensionItemType.CATEGORY_OPTION.name)
                            .build(),
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid3)
                            .dimensionItemType(DimensionItemType.CATEGORY_OPTION.name)
                            .build(),
                    ),
                )
                .build(),
        )

        whenever(categoryStore.selectByUid(uid1)) doReturn category
        whenever(category.uid()) doReturn uid1

        val dimensionItems = helper.getDimensionItems(categoryDimensions)

        assertThat(dimensionItems).hasSize(2)

        val catDimension = dimensionItems.filterIsInstance<DimensionItem.CategoryItem>()
        catDimension.forEach { assertThat(it.uid).isEqualTo(uid1) }
        assertThat(catDimension.map { it.categoryOption }).containsExactly(uid2, uid3)
    }

    @Test
    fun `Should combine dimensions items`() {
        val dimensions = listOf(
            VisualizationDimension.builder()
                .id("pe")
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid1)
                            .dimensionItemType(DimensionItemType.PERIOD.name)
                            .build(),
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid2)
                            .dimensionItemType(DimensionItemType.PERIOD.name)
                            .build(),
                    ),
                )
                .build(),
            VisualizationDimension.builder()
                .id("ou")
                .items(
                    listOf(
                        VisualizationDimensionItem.builder()
                            .dimensionItem(uid3)
                            .dimensionItemType(DimensionItemType.ORGANISATION_UNIT.name)
                            .build(),
                    ),
                )
                .build(),
        )

        val dimensionItems = helper.getDimensionItems(dimensions)

        assertThat(dimensionItems).hasSize(3)

        val periodItems = dimensionItems.filterIsInstance<DimensionItem.PeriodItem.Absolute>()
        assertThat(periodItems).hasSize(2)

        val orgunitItems = dimensionItems.filterIsInstance<DimensionItem.OrganisationUnitItem.Absolute>()
        assertThat(orgunitItems).hasSize(1)
    }
}
