/*
 *  Copyright (c) 2004-2026, University of Oslo
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

package org.hisp.dhis.android.network.settings

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.hisp.dhis.android.core.fileresource.internal.UPLOAD_QUALITY_KEY
import org.hisp.dhis.android.core.fileresource.internal.UploadQuality
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * The upload quality is deserialized as a plain String and only then mapped to [UploadQuality]. Deserializing it
 * straight into the enum makes an unknown quality abort the whole settings payload, which silently leaves every
 * program/dataSet setting unstored.
 */
@RunWith(JUnit4::class)
internal class ImageSettingsDTOShould {

    @Test
    fun map_unknown_program_upload_quality_to_default_without_failing() {
        val json = """
            {
              "globalSettings": {},
              "specificSettings": {
                "IpHINAT79UW": {
                  "id": "IpHINAT79UW",
                  "imageSettings": {
                    "deId": { "uploadQuality": "AGGRESSIVE" },
                    "attrId": { "uploadQuality": "ORIGINAL" }
                  }
                }
              }
            }
        """.trimIndent()

        val settings = KotlinxJsonParser.instance
            .decodeFromString(ProgramSettingsDTO.serializer(), json)
            .toDomain()

        assertThat(settings.specificSettings()["IpHINAT79UW"]?.imageSettings()).isEqualTo(
            mapOf(
                "deId" to mapOf(UPLOAD_QUALITY_KEY to UploadQuality.DEFAULT),
                "attrId" to mapOf(UPLOAD_QUALITY_KEY to UploadQuality.ORIGINAL),
            ),
        )
    }

    @Test
    fun map_unknown_dataset_upload_quality_to_default_without_failing() {
        val json = """
            {
              "globalSettings": {},
              "specificSettings": {
                "BfMAe6Itzgt": {
                  "id": "BfMAe6Itzgt",
                  "imageSettings": {
                    "deId": { "uploadQuality": "AGGRESSIVE" }
                  }
                }
              }
            }
        """.trimIndent()

        val settings = KotlinxJsonParser.instance
            .decodeFromString(DataSetSettingsDTO.serializer(), json)
            .toDomain()

        assertThat(settings.specificSettings()["BfMAe6Itzgt"]?.imageSettings()).isEqualTo(
            mapOf("deId" to mapOf(UPLOAD_QUALITY_KEY to UploadQuality.DEFAULT)),
        )
    }
}
