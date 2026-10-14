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

package org.hisp.dhis.android.persistence.settings

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.fileresource.internal.UPLOAD_QUALITY_KEY
import org.hisp.dhis.android.core.fileresource.internal.UploadQuality
import org.hisp.dhis.android.core.settings.DataSetSetting
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class ImageSettingsDBShould {

    @Test
    fun serialize_image_settings_as_json() {
        val db = IMAGE_SETTINGS.toImageSettingsDB()

        assertThat(db.value).isEqualTo(
            """{"deId":{"uploadQuality":"ORIGINAL"},"attrId":{"uploadQuality":"DEFAULT"}}""",
        )
    }

    @Test
    fun restore_image_settings_from_json() {
        val db = ImageSettingsDB("""{"deId":{"uploadQuality":"ORIGINAL"}}""")

        assertThat(db.toDomain()).isEqualTo(
            mapOf("deId" to mapOf(UPLOAD_QUALITY_KEY to UploadQuality.ORIGINAL)),
        )
    }

    @Test
    fun fall_back_to_empty_map_when_stored_value_is_not_valid_json() {
        assertThat(ImageSettingsDB("not-json").toDomain()).isEmpty()
    }

    @Test
    fun fall_back_to_default_quality_for_unknown_stored_quality_keeping_the_other_items() {
        val db = ImageSettingsDB(
            """{"deId":{"uploadQuality":"AGGRESSIVE"},"attrId":{"uploadQuality":"ORIGINAL"}}""",
        )

        assertThat(db.toDomain()).isEqualTo(
            mapOf(
                "deId" to mapOf(UPLOAD_QUALITY_KEY to UploadQuality.DEFAULT),
                "attrId" to mapOf(UPLOAD_QUALITY_KEY to UploadQuality.ORIGINAL),
            ),
        )
    }

    @Test
    fun persist_image_settings_through_program_setting_db_round_trip() {
        val programSetting = ProgramSetting.builder()
            .uid("IpHINAT79UW")
            .imageSettings(IMAGE_SETTINGS)
            .build()

        assertThat(programSetting.toDB().toDomain().imageSettings()).isEqualTo(IMAGE_SETTINGS)
    }

    @Test
    fun persist_image_settings_through_dataset_setting_db_round_trip() {
        val dataSetSetting = DataSetSetting.builder()
            .uid("BfMAe6Itzgt")
            .imageSettings(IMAGE_SETTINGS)
            .build()

        assertThat(dataSetSetting.toDB().toDomain().imageSettings()).isEqualTo(IMAGE_SETTINGS)
    }

    @Test
    fun keep_image_settings_null_when_not_configured() {
        val programSettingDB = ProgramSetting.builder().uid("IpHINAT79UW").build().toDB()
        val dataSetSettingDB = DataSetSetting.builder().uid("BfMAe6Itzgt").build().toDB()

        assertThat(programSettingDB.imageSettings).isNull()
        assertThat(dataSetSettingDB.imageSettings).isNull()
        assertThat(programSettingDB.toDomain().imageSettings()).isNull()
        assertThat(dataSetSettingDB.toDomain().imageSettings()).isNull()
    }

    companion object {
        private val IMAGE_SETTINGS = mapOf(
            "deId" to mapOf(UPLOAD_QUALITY_KEY to UploadQuality.ORIGINAL),
            "attrId" to mapOf(UPLOAD_QUALITY_KEY to UploadQuality.DEFAULT),
        )
    }
}
