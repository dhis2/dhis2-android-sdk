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

import kotlinx.serialization.SerializationException
import org.hisp.dhis.android.core.arch.json.internal.KotlinxJsonParser
import org.hisp.dhis.android.core.fileresource.internal.UploadQuality
import org.hisp.dhis.android.core.fileresource.internal.toUploadQualityMap

/**
 * Persists the imageSettings map of a program or dataSet setting as a JSON string. The outer key is the item uid
 * (dataElement or trackedEntityAttribute) and the inner map holds the settings for that item, e.g.
 * `{"deId":{"uploadQuality":"ORIGINAL"}}`.
 */
@JvmInline
internal value class ImageSettingsDB(
    val value: String,
) {
    fun toDomain(): Map<String, Map<String, UploadQuality>> {
        return try {
            KotlinxJsonParser.instance
                .decodeFromString<Map<String, Map<String, String>>>(value)
                .toUploadQualityMap()
        } catch (e: SerializationException) {
            emptyMap()
        }
    }
}

internal fun Map<String, Map<String, UploadQuality>>.toImageSettingsDB(): ImageSettingsDB {
    return try {
        val rawSettings = mapValues { (_, itemSettings) ->
            itemSettings.mapValues { (_, quality) -> quality.name }
        }
        ImageSettingsDB(KotlinxJsonParser.instance.encodeToString(rawSettings))
    } catch (e: SerializationException) {
        ImageSettingsDB("{}")
    }
}
