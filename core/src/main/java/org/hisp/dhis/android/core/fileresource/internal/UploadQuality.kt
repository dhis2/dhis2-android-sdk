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

package org.hisp.dhis.android.core.fileresource.internal

enum class UploadQuality {
    DEFAULT,
    ORIGINAL,
}

/**
 * Key holding the [UploadQuality] within the per-item map of the imageSettings configuration, as sent by the
 * Android Settings web app: `{"itemUid": {"uploadQuality": "ORIGINAL"}}`.
 */
internal const val UPLOAD_QUALITY_KEY = "uploadQuality"

/**
 * Parses [value] into an [UploadQuality], falling back to [UploadQuality.DEFAULT] for any value this SDK version
 * does not know about. The Settings web app may introduce new qualities before the SDK supports them, and an
 * unknown value must not prevent the rest of the settings from being read.
 */
internal fun uploadQualityOf(value: String): UploadQuality {
    return UploadQuality.entries.find { it.name == value } ?: UploadQuality.DEFAULT
}

/**
 * Converts a raw imageSettings map, where the quality is still the string sent by the server or stored in the
 * database, into its domain representation. See [uploadQualityOf] for the handling of unknown qualities.
 */
internal fun Map<String, Map<String, String>>.toUploadQualityMap(): Map<String, Map<String, UploadQuality>> {
    return mapValues { (_, itemSettings) ->
        itemSettings.mapValues { (_, quality) -> uploadQualityOf(quality) }
    }
}
