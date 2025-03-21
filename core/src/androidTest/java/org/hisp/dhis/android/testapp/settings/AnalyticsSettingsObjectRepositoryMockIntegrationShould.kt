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
package org.hisp.dhis.android.testapp.settings

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class AnalyticsSettingsObjectRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_analytics_settings() {
        val analyticsSettings = d2.settingModule().analyticsSetting().blockingGet()
        assertThat(analyticsSettings!!.tei().size).isEqualTo(3)

        for (teiSetting in analyticsSettings.tei()) {
            if ("fqEx2avRp1L" == teiSetting.uid()) {
                assertThat(teiSetting.data()!!.dataElements().size).isEqualTo(2)
                assertThat(teiSetting.data()!!.indicators().size).isEqualTo(0)
                assertThat(teiSetting.data()!!.attributes().size).isEqualTo(0)
                assertThat(teiSetting.whoNutritionData()).isNull()
            } else if ("XQUhloISaQJ" == teiSetting.uid()) {
                assertThat(teiSetting.data()!!.dataElements().size).isEqualTo(0)
                assertThat(teiSetting.data()!!.indicators().size).isEqualTo(1)
                assertThat(teiSetting.data()!!.attributes().size).isEqualTo(1)
                assertThat(teiSetting.whoNutritionData()).isNull()
            } else if ("yEdtdG7ql9K" == teiSetting.uid()) {
                assertThat(teiSetting.data()).isNull()
                assertThat(teiSetting.whoNutritionData()!!.x().dataElements().size).isEqualTo(1)
                assertThat(teiSetting.whoNutritionData()!!.x().indicators().size).isEqualTo(0)
                assertThat(teiSetting.whoNutritionData()!!.y().dataElements().size).isEqualTo(0)
                assertThat(teiSetting.whoNutritionData()!!.y().indicators().size).isEqualTo(1)
            }
        }

        for (analyticsDhisVisualizationsGroup in analyticsSettings.dhisVisualizations().home()) {
            if (analyticsDhisVisualizationsGroup.id() == "12345678910") {
                assertThat(analyticsDhisVisualizationsGroup.visualizations().size).isEqualTo(3)
            } else if (analyticsDhisVisualizationsGroup.id() == "12345678911") {
                assertThat(analyticsDhisVisualizationsGroup.visualizations().size).isEqualTo(1)
            }
        }
    }
}
