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
package org.hisp.dhis.android.core.systeminfo.internal

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.systeminfo.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DHISVersionManagerImpl @Inject internal constructor(
    private val systemInfoStore: ObjectWithoutUidStore<SystemInfo>
) : DHISVersionManager {
    private var version: DHISVersion? = null
    private var patchVersion: DHISPatchVersion? = null
    private var smsVersion: SMSVersion? = null

    override fun getVersion(): DHISVersion {
        return version
            ?: systemInfoStore.selectFirst()?.let { systemInfo ->
                systemInfo.version()?.let { DHISVersion.getValue(it) }
                    .also { dhisVersion -> version = dhisVersion }
            }
            ?: throw RuntimeException("Cannot get DHIS2 version")
    }

    override fun getPatchVersion(): DHISPatchVersion? {
        return patchVersion
            ?: systemInfoStore.selectFirst()?.let { systemInfo ->
                systemInfo.version()?.let { DHISPatchVersion.getValue(it) }
                    .also { patch -> patchVersion =  patch}
            }
    }

    override fun getSmsVersion(): SMSVersion? {
        return smsVersion
            ?: systemInfoStore.selectFirst()?.let { systemInfo ->
                systemInfo.version()?.let { SMSVersion.getValue(it) }
                    .also { sms -> smsVersion = sms }
            }
    }

    override fun is2_29(): Boolean {
        return getVersion() === DHISVersion.V2_29
    }

    override fun is2_30(): Boolean {
        return getVersion() === DHISVersion.V2_30
    }

    override fun is2_31(): Boolean {
        return getVersion() === DHISVersion.V2_31
    }

    override fun is2_32(): Boolean {
        return getVersion() === DHISVersion.V2_32
    }

    override fun is2_33(): Boolean {
        return getVersion() === DHISVersion.V2_33
    }

    override fun is2_34(): Boolean {
        return getVersion() === DHISVersion.V2_34
    }

    override fun is2_35(): Boolean {
        return getVersion() === DHISVersion.V2_35
    }

    override fun is2_36(): Boolean {
        return getVersion() === DHISVersion.V2_36
    }

    override fun is2_37(): Boolean {
        return getVersion() === DHISVersion.V2_37
    }

    override fun is2_38(): Boolean {
        return getVersion() === DHISVersion.V2_38
    }

    override fun is2_39(): Boolean {
        return getVersion() === DHISVersion.V2_39
    }

    override fun is2_40(): Boolean {
        return getVersion() === DHISVersion.V2_40
    }

    override fun isGreaterThan(version: DHISVersion): Boolean {
        return version < getVersion()
    }

    override fun isGreaterOrEqualThan(version: DHISVersion): Boolean {
        return version <= getVersion()
    }

    internal fun setVersion(versionStr: String) {
        version = DHISVersion.getValue(versionStr)
        patchVersion = DHISPatchVersion.getValue(versionStr)
        smsVersion = SMSVersion.getValue(versionStr)
    }
}
