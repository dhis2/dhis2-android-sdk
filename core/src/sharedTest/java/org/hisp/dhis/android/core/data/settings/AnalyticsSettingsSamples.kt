/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.data.settings

import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.settings.*

object AnalyticsSettingsSamples {

    val analyticsTeiDataElementSample: AnalyticsTeiDataElement =
        AnalyticsTeiDataElement.builder()
            .id(1L)
            .teiSetting("tei_setting")
            .whoComponent(WHONutritionComponent.X)
            .programStage("programStage")
            .dataElement("dataElement")
            .build()

    val analyticsTeiIndicator: AnalyticsTeiIndicator =
        AnalyticsTeiIndicator.builder()
            .id(1L)
            .whoComponent(WHONutritionComponent.X)
            .teiSetting("tei_setting")
            .indicator("indicator")
            .build()

    val analyticsTeiAttribute: AnalyticsTeiAttribute =
        AnalyticsTeiAttribute.builder()
            .id(1L)
            .whoComponent(WHONutritionComponent.X)
            .teiSetting("tei_setting")
            .attribute("attribute")
            .build()

    val analyticsTeiSetting: AnalyticsTeiSetting =
        AnalyticsTeiSetting.builder()
            .id(1L)
            .uid("uid")
            .name("name")
            .shortName("short_name")
            .program("program")
            .programStage("program_stage")
            .period(PeriodType.Monthly)
            .type(ChartType.LINE)
            .data(AnalyticsTeiData.builder().build())
            .build()

    val analyticsTeiWHONutritionData: AnalyticsTeiWHONutritionData =
        AnalyticsTeiWHONutritionData.builder()
            .id(1L)
            .teiSetting("tei_setting")
            .chartType(WHONutritionChartType.WFH)
            .gender(
                AnalyticsTeiWHONutritionGender.builder()
                    .attribute("gender_attribute")
                    .values(
                        AnalyticsTeiWHONutritionGenderValues.builder()
                            .female("female")
                            .male("male")
                            .build()
                    )
                    .build()
            )
            .x(AnalyticsTeiWHONutritionItem.builder().build())
            .y(AnalyticsTeiWHONutritionItem.builder().build())
            .build()
}
