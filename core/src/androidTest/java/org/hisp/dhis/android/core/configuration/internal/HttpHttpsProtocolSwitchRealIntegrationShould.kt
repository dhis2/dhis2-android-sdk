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

class HttpHttpsProtocolSwitchRealIntegrationShould : BaseRealIntegrationTest() {

    private val httpsUrl = "https://play.im.dhis2.org/stable-2-42-3-1/"
    private val testUsername = "android"
    private val testPassword = "Android123"

    /**
     * Verifies that HTTP and HTTPS URLs create DIFFERENT accounts/databases.
     * This is the expected behavior since they are technically different endpoints.
     */
    // @Test
    fun login_with_https_then_http_should_create_different_accounts() {
        val generator = DatabaseNameGenerator()

        val httpsDbName = generator.getDatabaseName(httpsUrl, testUsername, false)
        val httpDbName = generator.getDatabaseName(
            "http://play.im.dhis2.org/stable-2-42-3-1/",
            testUsername,
            false,
        )

        // HTTP and HTTPS should generate DIFFERENT database names
        assertThat(httpsDbName).isNotEqualTo(httpDbName)
        assertThat(httpsDbName).startsWith("https-")
        assertThat(httpDbName).startsWith("http-")
    }

    /**
     * Verifies that different domain case generates the SAME database.
     * Domain is case-insensitive per DNS standards.
     */
    // @Test
    fun login_with_different_domain_case_should_use_same_account() {
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

        // Should be the same account (domain case is normalized)
        assertThat(accountsSecond).hasSize(1)
        assertThat(accountsSecond[0].databaseName()).isEqualTo(firstDbName)

        // Cleanup
        d2.userModule().accountManager().deleteCurrentAccount()
    }

    /**
     * Verifies that different path case generates DIFFERENT databases.
     * Path is case-sensitive per URL standards.
     */
    // @Test
    fun database_name_should_differ_for_different_path_case() {
        val generator = DatabaseNameGenerator()

        val lowercasePath = generator.getDatabaseName(
            "https://play.dhis2.org/demo",
            testUsername,
            false,
        )
        val uppercasePath = generator.getDatabaseName(
            "https://play.dhis2.org/DEMO",
            testUsername,
            false,
        )

        // Different path case should generate DIFFERENT database names
        assertThat(lowercasePath).isNotEqualTo(uppercasePath)
    }

    /**
     * Verifies the database name format includes protocol prefix.
     */
    // @Test
    fun database_name_should_include_protocol_prefix() {
        val generator = DatabaseNameGenerator()

        val httpsDbName = generator.getDatabaseName(httpsUrl, testUsername, false)

        assertThat(httpsDbName).startsWith("https-")
        assertThat(httpsDbName).contains("play-im-dhis2-org")
        assertThat(httpsDbName).contains(testUsername)
        assertThat(httpsDbName).endsWith(".db")
    }
}