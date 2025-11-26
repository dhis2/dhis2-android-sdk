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

package org.hisp.dhis.android.core.server.internal

import org.hisp.dhis.android.core.arch.modules.internal.UntypedModuleDownloaderCoroutines
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStore
import org.hisp.dhis.android.core.configuration.internal.DatabaseConfigurationInsecureStore
import org.hisp.dhis.android.core.server.LoginConfig
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.internal.DHISVersionManagerImpl
import org.koin.core.annotation.Singleton

@Singleton
internal class LoginConfigDownloader(
    private val dhisVersionManager: DHISVersionManagerImpl,
    private val networkHandler: LoginConfigNetworkHandler,
    private val credentialsSecureStore: CredentialsSecureStore,
    private val databaseConfigurationSecureStore: DatabaseConfigurationInsecureStore,
) : UntypedModuleDownloaderCoroutines {
    override suspend fun downloadMetadata() {
        val serverUrl =
            credentialsSecureStore.getServerUrl() ?: throw IllegalArgumentException("Credentials are not set")

        val loginConfig =
            if (dhisVersionManager.isGreaterOrEqualThanInternal(DHISVersion.V2_41)) {
                networkHandler.loginConfig()
            } else {
                LoginConfig.createDefault(serverUrl)
            }

        val databasesConfiguration = databaseConfigurationSecureStore.get()
        val updatedAccounts = databasesConfiguration?.accounts()?.map { account ->
            if (account.serverUrl() == serverUrl) {
                account.toBuilder().loginConfig(loginConfig).build()
            } else {
                account
            }
        }
        databasesConfiguration?.toBuilder()
            ?.accounts(updatedAccounts)
            ?.build()
            ?.let {
                databaseConfigurationSecureStore.set(it)
            }
    }
}
