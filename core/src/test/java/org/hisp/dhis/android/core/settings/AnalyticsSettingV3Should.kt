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
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.network.settings.AnalyticsSettingsDTO
import org.junit.Test

class AnalyticsSettingV3Should : BaseObjectKotlinxShould("settings/analytics_settings_v3.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val analyticsSettingsDTO = deserialize(AnalyticsSettingsDTO.serializer())
        val analyticsSettings = analyticsSettingsDTO.toDomain()

        AnalyticsSettingAsserts.assertTeiAnalytics(analyticsSettings.tei())

        AnalyticsSettingAsserts.assertDhisVisualizations(analyticsSettings.dhisVisualizations())

        analyticsSettings.dhisVisualizations().home().forEach {
            it.visualizations().forEach {
                when (it.uid()) {
                    "FAFa11yFeFe" ->
                        assertThat(it.type()).isEqualTo(AnalyticsDhisVisualizationType.VISUALIZATION)

                    "PYBH8ZaAQnC" ->
                        assertThat(it.type()).isEqualTo(AnalyticsDhisVisualizationType.VISUALIZATION)

                    "s85urBIkN0z" ->
                        assertThat(it.type()).isEqualTo(AnalyticsDhisVisualizationType.TRACKER_VISUALIZATION)
                }
            }
        }
    }
}
