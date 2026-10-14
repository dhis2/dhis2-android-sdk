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
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyOneObjectRepositoryFinalImpl
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore
import org.hisp.dhis.android.core.category.internal.CategoryOptionStore
import org.hisp.dhis.android.core.category.internal.CategoryStore
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.expressiondimensionitem.ExpressionDimensionItem
import org.hisp.dhis.android.core.expressiondimensionitem.internal.ExpressionDimensionItemStore
import org.hisp.dhis.android.core.indicator.internal.IndicatorStore
import org.hisp.dhis.android.core.legendset.Legend
import org.hisp.dhis.android.core.legendset.internal.LegendStore
import org.hisp.dhis.android.core.option.Option
import org.hisp.dhis.android.core.option.internal.OptionStore
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitGroup
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitGroupStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitLevelStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.ProgramIndicatorCollectionRepository
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceHelperSamples as s

@RunWith(JUnit4::class)
class AnalyticsServiceMetadataHelperShould {

    private val categoryStore: CategoryStore = mock()
    private val categoryOptionStore: CategoryOptionStore = mock()
    private val dataElementStore: DataElementStore = mock()
    private val categoryOptionComboStore: CategoryOptionComboStore = mock()
    private val indicatorStore: IndicatorStore = mock()
    private val expressionDimensionItemStore: ExpressionDimensionItemStore = mock()
    private val legendStore: LegendStore = mock()
    private val organisationUnitStore: OrganisationUnitStore = mock()
    private val organisationUnitGroupStore: OrganisationUnitGroupStore = mock()
    private val organisationUnitLevelStore: OrganisationUnitLevelStore = mock()
    private val programStore: ProgramStore = mock()
    private val optionStore: OptionStore = mock()
    private val trackedEntityAttributeStore: TrackedEntityAttributeStore = mock()
    private val programIndicatorRepository: ProgramIndicatorCollectionRepository = mock()
    private val analyticsOrganisationUnitHelper: AnalyticsOrganisationUnitHelper = mock()
    private val parentPeriodGenerator: ParentPeriodGenerator = mock()
    private val periodHelper: PeriodHelper = mock()

    private val helper = AnalyticsServiceMetadataHelper(
        categoryStore,
        categoryOptionStore,
        categoryOptionComboStore,
        dataElementStore,
        indicatorStore,
        expressionDimensionItemStore,
        legendStore,
        organisationUnitStore,
        organisationUnitGroupStore,
        organisationUnitLevelStore,
        programStore,
        optionStore,
        trackedEntityAttributeStore,
        programIndicatorRepository,
        analyticsOrganisationUnitHelper,
        parentPeriodGenerator,
        periodHelper,
    )

    private val dataElement = AggregatedSamples.dataElement1
    private val coc = CategoryOptionCombo.builder().uid("coc_uid").displayName("Fixed").build()
    private val program = Program.builder()
        .uid("program_uid")
        .displayName("Child Programme")
        .categoryCombo(ObjectWithUid.create("cc_uid"))
        .enrollmentCategoryCombo(ObjectWithUid.create("cc_uid"))
        .build()
    private val attribute = TrackedEntityAttribute.builder().uid("attribute_uid").displayName("Gender").build()
    private val option = Option.builder()
        .uid("option_uid")
        .displayName("Female")
        .optionSet(ObjectWithUid.create("option_set_uid"))
        .build()
    private val expressionItem = ExpressionDimensionItem.builder().uid("expression_uid").displayName("Expr").build()
    private val orgunitGroup = OrganisationUnitGroup.builder().uid("group_uid").displayName("Group").build()

    @Test
    fun `Should extract category and category option metadata items`() = runTest {
        whenever(categoryStore.selectByUid(s.categoryItem1_1.uid)).thenReturn(AggregatedSamples.cc1)
        whenever(categoryOptionStore.selectByUid(s.categoryItem1_1.categoryOption)).thenReturn(AggregatedSamples.co11)
        whenever(categoryOptionStore.selectByUid(s.categoryItem1_2.categoryOption)).thenReturn(AggregatedSamples.co12)

        val evaluationItems = listOf(
            AnalyticsServiceEvaluationItem(
                dimensionItems = listOf(s.categoryItem1_1, s.categoryItem1_2),
                filters = listOf(),
            ),
        )

        val metadataMap = helper.getMetadata(evaluationItems)

        assertThat(metadataMap.keys).containsExactly(
            s.categoryItem1_1.uid,
            s.categoryItem1_1.categoryOption,
            s.categoryItem1_2.categoryOption,
        )
    }

    @Test
    fun `Should extract data element metadata item`() = runTest {
        whenever(dataElementStore.selectByUid(dataElement.uid())).thenReturn(dataElement)

        val metadata = helper.getMetadata(evaluationItems(DimensionItem.DataItem.DataElementItem(dataElement.uid())))

        assertThat(metadata[dataElement.uid()]).isInstanceOf(MetadataItem.DataElementItem::class.java)
        assertThat(metadata[dataElement.uid()]!!.displayName).isEqualTo("ANC 1st visit")
    }

    @Test
    fun `Should throw if data element does not exist`() = runTest {
        assertThrows<AnalyticsException.InvalidDataElement> {
            helper.getMetadata(evaluationItems(DimensionItem.DataItem.DataElementItem("missing")))
        }
    }

    @Test
    fun `Should extract data element operand metadata item`() = runTest {
        whenever(dataElementStore.selectByUid(dataElement.uid())).thenReturn(dataElement)
        whenever(categoryOptionComboStore.selectByUid(coc.uid())).thenReturn(coc)
        val item = DimensionItem.DataItem.DataElementOperandItem(dataElement.uid(), coc.uid())

        val metadata = helper.getMetadata(evaluationItems(item))

        val metadataItem = metadata[item.id] as MetadataItem.DataElementOperandItem
        assertThat(metadataItem.id).isEqualTo("${dataElement.uid()}.${coc.uid()}")
        assertThat(metadataItem.displayName).isEqualTo("ANC 1st visit Fixed")
        assertThat(metadataItem.item.dataElement()!!.uid()).isEqualTo(dataElement.uid())
        assertThat(metadataItem.item.categoryOptionCombo()!!.uid()).isEqualTo(coc.uid())
    }

    @Test
    fun `Should throw if data element operand has no data element or category option combo`() = runTest {
        whenever(dataElementStore.selectByUid(dataElement.uid())).thenReturn(dataElement)
        whenever(categoryOptionComboStore.selectByUid(coc.uid())).thenReturn(coc)

        assertThrows<AnalyticsException.InvalidDataElementOperand> {
            helper.getMetadata(evaluationItems(DimensionItem.DataItem.DataElementOperandItem("missing", coc.uid())))
        }
        assertThrows<AnalyticsException.InvalidDataElementOperand> {
            helper.getMetadata(
                evaluationItems(DimensionItem.DataItem.DataElementOperandItem(dataElement.uid(), "missing")),
            )
        }
    }

    @Test
    fun `Should extract indicator metadata item`() = runTest {
        whenever(indicatorStore.selectByUid(AggregatedSamples.indicator1.uid()))
            .thenReturn(AggregatedSamples.indicator1)

        val metadata = helper.getMetadata(evaluationItems(s.indicatorItem))

        assertThat(metadata[s.indicatorItem.id]).isInstanceOf(MetadataItem.IndicatorItem::class.java)
    }

    @Test
    fun `Should throw if indicator does not exist`() = runTest {
        assertThrows<AnalyticsException.InvalidIndicator> {
            helper.getMetadata(evaluationItems(s.indicatorItem))
        }
    }

    @Test
    fun `Should extract program indicator metadata item using analytics period boundaries`() = runTest {
        givenProgramIndicator(AggregatedSamples.programIndicator1)

        val metadata = helper.getMetadata(evaluationItems(s.programIndicatorItem))

        assertThat(metadata[s.programIndicatorItem.id]).isInstanceOf(MetadataItem.ProgramIndicatorItem::class.java)
        verify(programIndicatorRepository).withAnalyticsPeriodBoundaries()
    }

    @Test
    fun `Should throw if program indicator does not exist`() = runTest {
        givenProgramIndicator(null)

        assertThrows<AnalyticsException.InvalidProgramIndicator> {
            helper.getMetadata(evaluationItems(s.programIndicatorItem))
        }
    }

    @Test
    fun `Should extract event data element metadata item`() = runTest {
        whenever(dataElementStore.selectByUid(dataElement.uid())).thenReturn(dataElement)
        whenever(programStore.selectByUid(program.uid())).thenReturn(program)
        val item = DimensionItem.DataItem.EventDataItem.DataElement(program.uid(), dataElement.uid())

        val metadata = helper.getMetadata(evaluationItems(item))

        assertThat(metadata[item.id]).isInstanceOf(MetadataItem.EventDataElementItem::class.java)
        assertThat(metadata[item.id]!!.displayName).isEqualTo("Child Programme ANC 1st visit")
    }

    @Test
    fun `Should throw if event data element or its program does not exist`() = runTest {
        whenever(dataElementStore.selectByUid(dataElement.uid())).thenReturn(dataElement)

        assertThrows<AnalyticsException.InvalidDataElement> {
            helper.getMetadata(
                evaluationItems(DimensionItem.DataItem.EventDataItem.DataElement(program.uid(), "missing")),
            )
        }
        assertThrows<AnalyticsException.InvalidProgram> {
            helper.getMetadata(
                evaluationItems(DimensionItem.DataItem.EventDataItem.DataElement("missing", dataElement.uid())),
            )
        }
    }

    @Test
    fun `Should extract event attribute metadata item`() = runTest {
        whenever(trackedEntityAttributeStore.selectByUid(attribute.uid())).thenReturn(attribute)
        whenever(programStore.selectByUid(program.uid())).thenReturn(program)
        val item = DimensionItem.DataItem.EventDataItem.Attribute(program.uid(), attribute.uid())

        val metadata = helper.getMetadata(evaluationItems(item))

        assertThat(metadata[item.id]).isInstanceOf(MetadataItem.EventAttributeItem::class.java)
        assertThat(metadata[item.id]!!.displayName).isEqualTo("Child Programme Gender")
    }

    @Test
    fun `Should throw if event attribute or its program does not exist`() = runTest {
        whenever(trackedEntityAttributeStore.selectByUid(attribute.uid())).thenReturn(attribute)

        assertThrows<AnalyticsException.InvalidTrackedEntityAttribute> {
            helper.getMetadata(
                evaluationItems(DimensionItem.DataItem.EventDataItem.Attribute(program.uid(), "missing")),
            )
        }
        assertThrows<AnalyticsException.InvalidProgram> {
            helper.getMetadata(
                evaluationItems(DimensionItem.DataItem.EventDataItem.Attribute("missing", attribute.uid())),
            )
        }
    }

    @Test
    fun `Should extract event data element option metadata item`() = runTest {
        whenever(dataElementStore.selectByUid(dataElement.uid())).thenReturn(dataElement)
        whenever(programStore.selectByUid(program.uid())).thenReturn(program)
        whenever(optionStore.selectByUid(option.uid())).thenReturn(option)
        val item = DimensionItem.DataItem.EventDataItem.DataElementOption(
            program.uid(),
            dataElement.uid(),
            option.uid(),
        )

        val metadata = helper.getMetadata(evaluationItems(item))

        assertThat(metadata[item.id]).isInstanceOf(MetadataItem.EventDataElementOptionItem::class.java)
        assertThat(metadata[item.id]!!.displayName).isEqualTo("Child Programme ANC 1st visit Female")
    }

    @Test
    fun `Should throw if any part of an event data element option does not exist`() = runTest {
        whenever(dataElementStore.selectByUid(dataElement.uid())).thenReturn(dataElement)
        whenever(programStore.selectByUid(program.uid())).thenReturn(program)

        assertThrows<AnalyticsException.InvalidDataElement> {
            helper.getMetadata(
                evaluationItems(
                    DimensionItem.DataItem.EventDataItem.DataElementOption(program.uid(), "missing", option.uid()),
                ),
            )
        }
        assertThrows<AnalyticsException.InvalidProgram> {
            helper.getMetadata(
                evaluationItems(
                    DimensionItem.DataItem.EventDataItem.DataElementOption("missing", dataElement.uid(), option.uid()),
                ),
            )
        }
        assertThrows<AnalyticsException.InvalidOption> {
            helper.getMetadata(
                evaluationItems(
                    DimensionItem.DataItem.EventDataItem.DataElementOption(program.uid(), dataElement.uid(), "missing"),
                ),
            )
        }
    }

    @Test
    fun `Should extract event attribute option metadata item`() = runTest {
        whenever(trackedEntityAttributeStore.selectByUid(attribute.uid())).thenReturn(attribute)
        whenever(programStore.selectByUid(program.uid())).thenReturn(program)
        whenever(optionStore.selectByUid(option.uid())).thenReturn(option)
        val item = DimensionItem.DataItem.EventDataItem.AttributeOption(program.uid(), attribute.uid(), option.uid())

        val metadata = helper.getMetadata(evaluationItems(item))

        assertThat(metadata[item.id]).isInstanceOf(MetadataItem.EventAttributeOptionItem::class.java)
        assertThat(metadata[item.id]!!.displayName).isEqualTo("Child Programme Gender Female")
    }

    @Test
    fun `Should throw if any part of an event attribute option does not exist`() = runTest {
        whenever(trackedEntityAttributeStore.selectByUid(attribute.uid())).thenReturn(attribute)
        whenever(programStore.selectByUid(program.uid())).thenReturn(program)

        assertThrows<AnalyticsException.InvalidTrackedEntityAttribute> {
            helper.getMetadata(
                evaluationItems(
                    DimensionItem.DataItem.EventDataItem.AttributeOption(program.uid(), "missing", option.uid()),
                ),
            )
        }
        assertThrows<AnalyticsException.InvalidProgram> {
            helper.getMetadata(
                evaluationItems(
                    DimensionItem.DataItem.EventDataItem.AttributeOption("missing", attribute.uid(), option.uid()),
                ),
            )
        }
        assertThrows<AnalyticsException.InvalidOption> {
            helper.getMetadata(
                evaluationItems(
                    DimensionItem.DataItem.EventDataItem.AttributeOption(program.uid(), attribute.uid(), "missing"),
                ),
            )
        }
    }

    @Test
    fun `Should extract expression dimension item metadata item`() = runTest {
        whenever(expressionDimensionItemStore.selectByUid(expressionItem.uid())).thenReturn(expressionItem)
        val item = DimensionItem.DataItem.ExpressionDimensionItem(expressionItem.uid())

        val metadata = helper.getMetadata(evaluationItems(item))

        assertThat(metadata[item.id]).isInstanceOf(MetadataItem.ExpressionDimensionItemItem::class.java)
    }

    @Test
    fun `Should throw if expression dimension item does not exist`() = runTest {
        assertThrows<AnalyticsException.InvalidExpressionDimensionItem> {
            helper.getMetadata(evaluationItems(DimensionItem.DataItem.ExpressionDimensionItem("missing")))
        }
    }

    @Test
    fun `Should extract absolute and relative period metadata items`() = runTest {
        val periods = listOf(s.period1, s.period2, s.period3)
        whenever(periodHelper.suspendGetPeriodForPeriodId(s.periodAbsolute1.periodId))
            .thenReturn(AggregatedSamples.period1)
        whenever(parentPeriodGenerator.generateRelativePeriods(RelativePeriod.LAST_3_DAYS)).thenReturn(periods)

        val metadata = helper.getMetadata(evaluationItems(s.periodAbsolute1, s.periodLast3Days))

        assertThat((metadata[s.periodAbsolute1.id] as MetadataItem.PeriodItem).item)
            .isEqualTo(AggregatedSamples.period1)
        assertThat((metadata[s.periodLast3Days.id] as MetadataItem.RelativePeriodItem).periods)
            .containsExactlyElementsIn(periods)
    }

    @Test
    fun `Should extract absolute organisation unit metadata item`() = runTest {
        whenever(organisationUnitStore.selectByUid(AggregatedSamples.orgunit1.uid()))
            .thenReturn(AggregatedSamples.orgunit1)

        val metadata = helper.getMetadata(evaluationItems(s.orgunitAbsolute))

        assertThat((metadata[s.orgunitAbsolute.id] as MetadataItem.OrganisationUnitItem).item)
            .isEqualTo(AggregatedSamples.orgunit1)
    }

    @Test
    fun `Should throw if organisation unit does not exist`() = runTest {
        assertThrows<AnalyticsException.InvalidOrganisationUnit> {
            helper.getMetadata(evaluationItems(s.orgunitAbsolute))
        }
    }

    @Test
    fun `Should extract relative organisation unit metadata item`() = runTest {
        val item = DimensionItem.OrganisationUnitItem.Relative(RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN)
        whenever(analyticsOrganisationUnitHelper.getRelativeOrganisationUnitUids(item.relative))
            .thenReturn(listOf("ou1", "ou2"))

        val metadata = helper.getMetadata(evaluationItems(item))

        val metadataItem = metadata[item.id] as MetadataItem.OrganisationUnitRelativeItem
        assertThat(metadataItem.item).isEqualTo(RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN)
        assertThat(metadataItem.organisationUnitUids).containsExactly("ou1", "ou2")
    }

    @Test
    fun `Should extract organisation unit level metadata item`() = runTest {
        whenever(organisationUnitLevelStore.selectByUid(AggregatedSamples.level3.uid()))
            .thenReturn(AggregatedSamples.level3)
        whenever(analyticsOrganisationUnitHelper.getOrganisationUnitUidsByLevel(3)).thenReturn(listOf("ou1"))

        val metadata = helper.getMetadata(evaluationItems(s.orgunitLevel3))

        val metadataItem = metadata[s.orgunitLevel3.id] as MetadataItem.OrganisationUnitLevelItem
        assertThat(metadataItem.item).isEqualTo(AggregatedSamples.level3)
        assertThat(metadataItem.organisationUnitUids).containsExactly("ou1")
    }

    @Test
    fun `Should throw if organisation unit level does not exist`() = runTest {
        assertThrows<AnalyticsException.InvalidOrganisationUnitLevel> {
            helper.getMetadata(evaluationItems(s.orgunitLevel3))
        }
        verifyNoInteractions(analyticsOrganisationUnitHelper)
    }

    @Test
    fun `Should extract organisation unit group metadata item`() = runTest {
        val item = DimensionItem.OrganisationUnitItem.Group(orgunitGroup.uid())
        whenever(organisationUnitGroupStore.selectByUid(orgunitGroup.uid())).thenReturn(orgunitGroup)
        whenever(analyticsOrganisationUnitHelper.getOrganisationUnitUidsByGroup(orgunitGroup.uid()))
            .thenReturn(listOf("ou1", "ou2"))

        val metadata = helper.getMetadata(evaluationItems(item))

        val metadataItem = metadata[item.id] as MetadataItem.OrganisationUnitGroupItem
        assertThat(metadataItem.item).isEqualTo(orgunitGroup)
        assertThat(metadataItem.organisationUnitUids).containsExactly("ou1", "ou2")
    }

    @Test
    fun `Should throw if organisation unit group does not exist`() = runTest {
        assertThrows<AnalyticsException.InvalidOrganisationUnitGroup> {
            helper.getMetadata(evaluationItems(DimensionItem.OrganisationUnitItem.Group("missing")))
        }
        verifyNoInteractions(analyticsOrganisationUnitHelper)
    }

    @Test
    fun `Should throw if category or category option does not exist`() = runTest {
        assertThrows<AnalyticsException.InvalidCategory> {
            helper.getMetadata(evaluationItems(s.categoryItem1_1))
        }

        whenever(categoryStore.selectByUid(s.categoryItem1_1.uid)).thenReturn(AggregatedSamples.cc1)
        assertThrows<AnalyticsException.InvalidCategoryOption> {
            helper.getMetadata(evaluationItems(s.categoryItem1_1))
        }
    }

    @Test
    fun `Should include filters and merge metadata of every evaluation item`() = runTest {
        whenever(dataElementStore.selectByUid(dataElement.uid())).thenReturn(dataElement)
        whenever(dataElementStore.selectByUid(AggregatedSamples.dataElement2.uid()))
            .thenReturn(AggregatedSamples.dataElement2)
        whenever(organisationUnitStore.selectByUid(AggregatedSamples.orgunit1.uid()))
            .thenReturn(AggregatedSamples.orgunit1)

        val metadata = helper.getMetadata(
            listOf(
                AnalyticsServiceEvaluationItem(
                    dimensionItems = listOf(s.dataElementItem1),
                    filters = listOf(s.orgunitAbsolute),
                ),
                AnalyticsServiceEvaluationItem(
                    dimensionItems = listOf(s.dataElementItem2),
                    filters = emptyList(),
                ),
            ),
        )

        assertThat(metadata.keys).containsExactly(
            s.dataElementItem1.id,
            s.orgunitAbsolute.id,
            s.dataElementItem2.id,
        )
    }

    @Test
    fun `Should query repeated items of an evaluation item only once`() = runTest {
        whenever(dataElementStore.selectByUid(dataElement.uid())).thenReturn(dataElement)

        val metadata = helper.getMetadata(
            listOf(
                AnalyticsServiceEvaluationItem(
                    dimensionItems = listOf(s.dataElementItem1),
                    filters = listOf(s.dataElementItem1),
                ),
            ),
        )

        assertThat(metadata.keys).containsExactly(s.dataElementItem1.id)
        verify(dataElementStore, times(1)).selectByUid(dataElement.uid())
    }

    @Test
    fun `Should include distinct legends in metadata keeping existing items`() = runTest {
        val legend1 = Legend.builder().uid("legend1").displayName("Low").build()
        val legend2 = Legend.builder().uid("legend2").displayName("High").build()
        whenever(legendStore.selectByUids(listOf("legend1", "legend2"))).thenReturn(listOf(legend1, legend2))
        val existing = mapOf<String, MetadataItem>(
            dataElement.uid() to MetadataItem.DataElementItem(dataElement),
        )

        val metadata = helper.includeLegendsToMetadata(existing, listOf("legend1", "legend2", "legend1"))

        assertThat(metadata.keys).containsExactly(dataElement.uid(), "legend1", "legend2")
        assertThat((metadata["legend1"] as MetadataItem.LegendItem).item).isEqualTo(legend1)
        assertThat(existing.keys).containsExactly(dataElement.uid())
    }

    private fun evaluationItems(vararg items: DimensionItem) = listOf(
        AnalyticsServiceEvaluationItem(dimensionItems = emptyList(), filters = items.toList()),
    )

    private suspend fun givenProgramIndicator(programIndicator: ProgramIndicator?) {
        val objectRepository: ReadOnlyOneObjectRepositoryFinalImpl<ProgramIndicator> = mock()
        whenever(programIndicatorRepository.withAnalyticsPeriodBoundaries()).thenReturn(programIndicatorRepository)
        whenever(programIndicatorRepository.uid(s.programIndicatorItem.uid)).thenReturn(objectRepository)
        whenever(objectRepository.suspendGet()).thenReturn(programIndicator)
    }

    private suspend inline fun <reified T : Throwable> assertThrows(crossinline block: suspend () -> Unit) {
        val error = runCatching { block() }.exceptionOrNull()
        assertThat(error).isInstanceOf(T::class.java)
    }
}
