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
package org.hisp.dhis.android.core.data.visualization

import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils
import org.hisp.dhis.android.core.visualization.*

internal object VisualizationSamples {

    private const val DATE_STR = "2021-06-16T14:26:50.195"
    private val DATE = FillPropertiesTestUtils.parseDate(DATE_STR)

    @JvmStatic
    fun visualization(): Visualization = Visualization.builder()
        .id(1L)
        .uid("PYBH8ZaAQnC")
        .name("Android SDK Visualization sample")
        .displayName("Android SDK Visualization sample")
        .created(DATE)
        .lastUpdated(DATE)
        .description("Sample visualization for the Android SDK")
        .displayDescription("Sample visualization for the Android SDK")
        .displayFormName("Android SDK Visualization sample")
        .title("Sample title")
        .displayTitle("Sample display title")
        .subtitle("Sample subtitle")
        .displaySubtitle("Sample display subtitle")
        .type(VisualizationType.PIVOT_TABLE)
        .hideTitle(false)
        .hideSubtitle(false)
        .hideEmptyColumns(false)
        .hideEmptyRows(false)
        .hideEmptyRowItems(HideEmptyItemStrategy.NONE)
        .hideLegend(false)
        .showHierarchy(false)
        .rowTotals(true)
        .rowSubTotals(false)
        .colTotals(false)
        .colSubTotals(false)
        .showDimensionLabels(false)
        .percentStackedValues(false)
        .noSpaceBetweenColumns(false)
        .skipRounding(false)
        .legend(
            VisualizationLegend.builder()
                .set(ObjectWithUid.create("Yf6UHoPkd57"))
                .showKey(false)
                .strategy(LegendStrategy.FIXED)
                .style(LegendStyle.FILL)
                .build(),
        )
        .displayDensity(DisplayDensity.NORMAL)
        .digitGroupSeparator(DigitGroupSeparator.COMMA)
        .columns(
            listOf(
                VisualizationDimension.builder()
                    .id("dx")
                    .items(
                        listOf(
                            VisualizationDimensionItem.builder()
                                .dimensionItem("Uvn6LCg7dVU")
                                .dimensionItemType(DataDimensionItemType.INDICATOR.name)
                                .build(),
                            VisualizationDimensionItem.builder()
                                .dimensionItem("cYeuwXTCPkU")
                                .dimensionItemType(DataDimensionItemType.DATA_ELEMENT.name)
                                .build(),
                        ),
                    )
                    .build(),
                VisualizationDimension.builder()
                    .id("fMZEcRHuamy")
                    .items(
                        listOf(
                            VisualizationDimensionItem.builder()
                                .dimensionItem("qkPbeWaFsnU")
                                .dimensionItemType("CATEGORY_OPTION")
                                .build(),
                            VisualizationDimensionItem.builder()
                                .dimensionItem("wbrDrL2aYEc")
                                .dimensionItemType("CATEGORY_OPTION")
                                .build(),
                        ),
                    )
                    .build(),
            ),
        )
        .rows(
            listOf(
                VisualizationDimension.builder()
                    .id("pe")
                    .items(
                        listOf(
                            VisualizationDimensionItem.builder()
                                .dimensionItem("202102")
                                .dimensionItemType("PERIOD")
                                .build(),
                            VisualizationDimensionItem.builder()
                                .dimensionItem("202103")
                                .dimensionItemType("PERIOD")
                                .build(),
                            VisualizationDimensionItem.builder()
                                .dimensionItem("2021S2")
                                .dimensionItemType("PERIOD")
                                .build(),
                            VisualizationDimensionItem.builder()
                                .dimensionItem(RelativePeriod.LAST_12_MONTHS.name)
                                .dimensionItemType("PERIOD")
                                .build(),
                        ),
                    )
                    .build(),
            ),
        )
        .filters(
            listOf(
                VisualizationDimension.builder()
                    .id("ou")
                    .items(
                        listOf(
                            VisualizationDimensionItem.builder()
                                .dimensionItem("YuQRtpLP10I")
                                .dimensionItemType("ORGANISATION_UNIT")
                                .build(),
                            VisualizationDimensionItem.builder()
                                .dimensionItem("USER_ORGUNIT")
                                .build(),
                        ),
                    )
                    .build(),
            ),
        )
        .aggregationType(AggregationType.SUM)
        .build()
}
