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
package org.hisp.dhis.android.core.configuration.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.D2Factory
import org.junit.Test

/**
 * Integration test to verify that HTTP and HTTPS URLs for the same server
 * are treated as the same account (single database, no duplicates).
 */
class HttpHttpsProtocolSwitchRealIntegrationShould : BaseRealIntegrationTest() {

    private val httpsUrl = "https://play.im.dhis2.org/stable-2-42-3-1/"
    private val httpUrl = "http://play.im.dhis2.org/stable-2-42-3-1/"
    private val testUsername = "android"
    private val testPassword = "Android123"

    // @Test
    fun login_with_https_then_http_should_use_same_account() {
        d2.userModule().blockingLogIn(testUsername, testPassword, httpsUrl)

        val accountsAfterHttps = d2.userModule().accountManager().getAccounts()
        assertThat(accountsAfterHttps).hasSize(1)
        val httpsAccount = accountsAfterHttps[0]
        val httpsDbName = httpsAccount.databaseName()
        assertThat(httpsAccount.serverUrl()).isEqualTo(httpsUrl.trimEnd('/'))

        d2.userModule().blockingLogOut()
        d2.userModule().blockingLogIn(testUsername, testPassword, httpUrl)

        val accountsAfterHttp = d2.userModule().accountManager().getAccounts()

        assertThat(accountsAfterHttp).hasSize(1)

        val httpAccount = accountsAfterHttp[0]

        assertThat(httpAccount.databaseName()).isEqualTo(httpsDbName)
        assertThat(httpAccount.serverUrl()).isEqualTo(httpUrl.trimEnd('/'))

        // Cleanup
        d2.userModule().accountManager().deleteCurrentAccount()
    }

    // @Test
    fun login_with_http_then_https_should_use_same_account() {
        d2.userModule().blockingLogIn(testUsername, testPassword, httpUrl)

        val accountsAfterHttp = d2.userModule().accountManager().getAccounts()
        assertThat(accountsAfterHttp).hasSize(1)
        val httpAccount = accountsAfterHttp[0]
        val httpDbName = httpAccount.databaseName()

        d2.userModule().blockingLogOut()
        d2.userModule().blockingLogIn(testUsername, testPassword, httpsUrl)
        val accountsAfterHttps = d2.userModule().accountManager().getAccounts()

        assertThat(accountsAfterHttps).hasSize(1)

        val httpsAccount = accountsAfterHttps[0]

        assertThat(httpsAccount.databaseName()).isEqualTo(httpDbName)
        assertThat(httpsAccount.serverUrl()).isEqualTo(httpsUrl.trimEnd('/'))

        // Cleanup
        d2.userModule().accountManager().deleteCurrentAccount()
    }

    // @Test
    fun login_with_different_case_should_use_same_account() {
        val lowercaseUrl = "https://play.im.dhis2.org/stable-2-42-3-1/"
        val uppercaseUrl = "https://PLAY.IM.DHIS2.ORG/stable-2-42-3-1/"

        d2.userModule().blockingLogIn(testUsername, testPassword, lowercaseUrl)

        val accountsFirst = d2.userModule().accountManager().getAccounts()
        assertThat(accountsFirst).hasSize(1)
        val firstDbName = accountsFirst[0].databaseName()

        d2.userModule().blockingLogOut()
        D2Factory.clear()
        d2 = D2Factory.forNewDatabase(isRealIntegration = true)
        d2.userModule().blockingLogIn(testUsername, testPassword, uppercaseUrl)

        val accountsSecond = d2.userModule().accountManager().getAccounts()

        assertThat(accountsSecond).hasSize(1)
        assertThat(accountsSecond[0].databaseName()).isEqualTo(firstDbName)

        // Cleanup
        d2.userModule().accountManager().deleteCurrentAccount()
    }

    // @Test
    fun database_hash_should_be_same_for_http_and_https() {
        val generator = DatabaseNameGenerator()

        val httpsDbName = generator.getDatabaseName(httpsUrl, testUsername, false)
        val httpDbName = generator.getDatabaseName(httpUrl, testUsername, false)

        assertThat(httpsDbName).isEqualTo(httpDbName)
    }
}
