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
package org.hisp.dhis.android.core.settings

import com.google.common.truth.Truth.assertThat
import java.io.IOException
import java.text.ParseException
import org.hisp.dhis.android.core.common.BaseObjectShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.core.period.PeriodType
import org.junit.Assert.fail
import org.junit.Test

class AnalyticsSettingV1Should : BaseObjectShould("settings/analytics_settings.json"), ObjectShould {

    @Test
    @Throws(IOException::class, ParseException::class)
    override fun map_from_json_string() {
        val analyticsSettings = objectMapper.readValue(jsonStream, AnalyticsSettings::class.java)

        assertThat(analyticsSettings.tei().size).isEqualTo(3)

        analyticsSettings.tei().forEach { tei ->
            when (tei.uid()) {
                "fqEx2avRp1L" -> {
                    assertThat(tei.name()).isEqualTo("Height evolution")
                    assertThat(tei.shortName()).isEqualTo("H. evolution")
                    assertThat(tei.program()).isEqualTo("IpHINAT79UW")
                    assertThat(tei.programStage()).isEqualTo("dBwrot7S420")
                    assertThat(tei.period()).isEquivalentAccordingToCompareTo(PeriodType.Monthly)
                    assertThat(tei.type()).isEquivalentAccordingToCompareTo(ChartType.LINE)
                    assertThat(tei.data()?.dataElements()?.size).isEqualTo(2)

                    assertThat(
                        tei.data()?.dataElements()?.any { dataElement ->
                            dataElement.dataElement() == "sWoqcoByYmD" && dataElement.programStage() == "dBwrot7S420"
                        }
                    ).isTrue()

                    assertThat(
                        tei.data()?.dataElements()?.any { dataElement ->
                            dataElement.dataElement() == "Ok9OQpitjQr" && dataElement.programStage() == "dBwrot7S421"
                        }
                    ).isTrue()
                }
                "XQUhloISaQJ" -> {
                    assertThat(tei.name()).isEqualTo("Weight gain")

                    assertThat(tei.data()?.indicators()?.size).isEqualTo(1)
                    assertThat(
                        tei.data()?.indicators()?.first()?.let {
                            it.indicator() == "GSae40Fyppf" && it.programStage() == "dBwrot7S420"
                        }
                    ).isTrue()

                    assertThat(tei.data()?.attributes()?.size).isEqualTo(1)
                    assertThat(tei.data()?.attributes()?.first()?.attribute() == "cejWyOfXge6").isTrue()
                }
                "yEdtdG7ql9K" -> {
                    assertThat(tei.name()).isEqualTo("Who chart")

                    assertThat(tei.whoNutritionData()).isNotNull()
                    assertThat(tei.whoNutritionData()?.chartType())
                        .isEquivalentAccordingToCompareTo(WHONutritionChartType.WFH)

                    assertThat(tei.whoNutritionData()?.gender()?.attribute()).isEqualTo("cejWyOfXge6")
                    assertThat(tei.whoNutritionData()?.gender()?.values()?.male()).isEqualTo("male")
                    assertThat(tei.whoNutritionData()?.gender()?.values()?.female()).isEqualTo("female")

                    assertThat(tei.whoNutritionData()?.x()?.dataElements()?.size).isEqualTo(1)
                    assertThat(tei.whoNutritionData()?.x()?.indicators()?.size).isEqualTo(0)
                    assertThat(tei.whoNutritionData()?.y()?.dataElements()?.size).isEqualTo(0)
                    assertThat(tei.whoNutritionData()?.y()?.indicators()?.size).isEqualTo(1)
                }
                else -> fail("Unexpected tei uid")
            }
        }
    }
}
