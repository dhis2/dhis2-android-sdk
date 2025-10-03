/*
 *  Copyright (c) 2004-2025, University of Oslo
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

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.CoreObjectShould
import org.hisp.dhis.android.network.settings.GeneralSettingsDTO
import org.junit.Test
import java.io.IOException

class GeneralSettingsV1Should : CoreObjectShould("settings/general_settings_v1.json") {
    @Test
    override fun map_from_json_string() {
        val generalSettingsDTO = deserialize(GeneralSettingsDTO.serializer())
        val generalSettings = generalSettingsDTO.toDomain()

        Truth.assertThat(generalSettings.dataSync()).isEqualTo(DataSyncPeriod.EVERY_24_HOURS)
        Truth.assertThat(generalSettings.encryptDB()).isFalse()
        Truth.assertThat(generalSettings.lastUpdated())
            .isEqualTo(BaseIdentifiableObject.parseDate("2020-01-13T16:52:05.144Z"))
        Truth.assertThat(generalSettings.metadataSync()).isEqualTo(MetadataSyncPeriod.MANUAL)
        Truth.assertThat(generalSettings.reservedValues()).isEqualTo(100)
        Truth.assertThat(generalSettings.smsGateway()).isEqualTo("98456123")
        Truth.assertThat(generalSettings.numberSmsToSend()).isEqualTo("98456123")
        Truth.assertThat(generalSettings.smsResultSender()).isEqualTo("98456122")
        Truth.assertThat(generalSettings.numberSmsConfirmation()).isEqualTo("98456122")
        Truth.assertThat(generalSettings.matomoID()).isNull()
        Truth.assertThat(generalSettings.matomoURL()).isNull()
    }

    @Test
    @Throws(IOException::class)
    fun map_from_json_with_unknown_properties() {
        val jsonPath = "settings/general_settings_with_unknown_options.json"
        val generalSettings = deserializePath(jsonPath, GeneralSettingsDTO.serializer()).toDomain()

        Truth.assertThat(generalSettings.dataSync()).isEqualTo(DataSyncPeriod.EVERY_24_HOURS)
        Truth.assertThat(generalSettings.metadataSync())
            .isEqualTo(MetadataSyncPeriod.EVERY_24_HOURS)
    }
}
