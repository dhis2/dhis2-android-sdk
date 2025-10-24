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
package org.hisp.dhis.android.core.systeminfo.internal

import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.systeminfo.DHISPatchVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.systeminfo.SMSVersion
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
internal class DHISVersionManagerImpl internal constructor(
    private val systemInfoStore: SystemInfoStore,
) : DHISVersionManager {
    private var version: DHISVersion? = null
    private var patchVersion: DHISPatchVersion? = null
    private var smsVersion: SMSVersion? = null
    private var bypassDHIS2Version: Boolean? = null

    override fun getVersion(): DHISVersion {
        return runBlocking { getVersionInternal() }
    }

    internal suspend fun getVersionInternal(): DHISVersion {
        return version
            ?: systemInfoStore.selectFirst()?.let { systemInfo ->
                systemInfo.version()?.let { DHISVersion.getValue(it, getBypassVersion()) }
                    .also { dhisVersion -> version = dhisVersion }
            }
            ?: throw D2Error.builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.INVALID_DHIS_VERSION)
                .errorDescription("Invalid DHIS version")
                .build()
    }

    override fun getPatchVersion(): DHISPatchVersion? {
        return runBlocking { getPatchVersionInternal() }
    }

    internal suspend fun getPatchVersionInternal(): DHISPatchVersion? {
        return patchVersion ?: systemInfoStore.selectFirst()?.let { systemInfo ->
            systemInfo.version()?.let { DHISPatchVersion.getValue(it, getBypassVersion()) }
                .also { patch -> patchVersion = patch }
        }
    }

    override fun getSmsVersion(): SMSVersion? {
        return runBlocking { getSmsVersionInternal() }
    }

    internal suspend fun getSmsVersionInternal(): SMSVersion? {
        return smsVersion
            ?: systemInfoStore.selectFirst()?.let { systemInfo ->
                systemInfo.version()?.let { SMSVersion.getValue(it, getBypassVersion()) }
                    .also { sms -> smsVersion = sms }
            }
    }

    override fun getBypassVersion(): Boolean? {
        return bypassDHIS2Version
    }

    override fun isVersion(version: DHISVersion): Boolean {
        return runBlocking { isVersionInternal(version) }
    }

    internal suspend fun isVersionInternal(version: DHISVersion): Boolean {
        return version === getVersionInternal()
    }

    override fun isGreaterThan(version: DHISVersion): Boolean {
        return runBlocking { isGreaterThanInternal(version) }
    }

    internal suspend fun isGreaterThanInternal(version: DHISVersion): Boolean {
        return version < getVersionInternal()
    }

    override fun isGreaterOrEqualThan(version: DHISVersion): Boolean {
        return runBlocking { isGreaterOrEqualThanInternal(version) }
    }

    internal suspend fun isGreaterOrEqualThanInternal(version: DHISVersion): Boolean {
        return version <= getVersionInternal()
    }

    internal fun setVersion(versionStr: String) {
        version = DHISVersion.getValue(versionStr, getBypassVersion())
        patchVersion = DHISPatchVersion.getValue(versionStr, getBypassVersion())
        smsVersion = SMSVersion.getValue(versionStr, getBypassVersion())
    }

    override fun setBypassVersion(bypassDHIS2VersionCheck: Boolean?) {
        bypassDHIS2Version = bypassDHIS2VersionCheck
    }
}
