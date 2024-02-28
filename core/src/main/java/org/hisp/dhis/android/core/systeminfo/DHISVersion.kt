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
package org.hisp.dhis.android.core.systeminfo

enum class DHISVersion(internal val prefix: String, internal val supported: Boolean = true) {
    V2_29("2.29", false),
    V2_30("2.30"),
    V2_31("2.31"),
    V2_32("2.32"),
    V2_33("2.33"),
    V2_34("2.34"),
    V2_35("2.35"),
    V2_36("2.36"),
    V2_37("2.37"),
    V2_38("2.38"),
    V2_39("2.39"),
    V2_40("2.40"),
    V2_41("2.41"),
    UNKNOWN("UNKNOWN", false),
    ;

    companion object {
        @JvmStatic
        fun getValue(versionStr: String, bypassDHIS2VersionCheck: Boolean?): DHISVersion? {
            return entries.find { versionStr.startsWith(it.prefix).and(it.supported) }
                ?: bypassDHIS2VersionCheck.takeIf { it == true }?.let { UNKNOWN }
        }

        @JvmStatic
        fun isAllowedVersion(versionStr: String, bypassDHIS2VersionCheck: Boolean?): Boolean {
            return getValue(versionStr, bypassDHIS2VersionCheck) != null
        }

        @JvmStatic
        fun allowedVersionsAsStr(): Array<String> {
            return entries.filter { it.supported }
                .map { it.prefix }
                .toTypedArray()
        }
    }
}
