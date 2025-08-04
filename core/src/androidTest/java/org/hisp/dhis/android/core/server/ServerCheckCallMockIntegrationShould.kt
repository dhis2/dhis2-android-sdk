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

package org.hisp.dhis.android.core.server

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTest
import org.hisp.dhis.android.core.utils.integration.mock.MockIntegrationTestDatabaseContent
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class ServerCheckCallMockIntegrationShould : BaseMockIntegrationTest() {
    @Before
    fun setUp() {
        setUpClass(MockIntegrationTestDatabaseContent.EmptyEnqueable)
        dhis2MockServer.enqueueMockResponse(LOGIN_CONFIG_JSON)
    }

    @Test
    fun return_correct_login_config_when_call() {
        val loginConfig = getDownload()

        assertThat(loginConfig.apiVersion).isEqualTo("2.41.3")
        assertThat(loginConfig.applicationTitle).isEqualTo("DHIS 2 Demo - Sierra Leone")
        assertThat(loginConfig.applicationDescription).isEqualTo("Welcome to the DHIS 2 demo application")
        assertThat(loginConfig.applicationNotification).isEqualTo(
            "Log in with admin / district and feel free to do changes as system is reset every night." +
                "<br><br><i>Note: In order to maintain access for everyone, changes to the admin user " +
                "password are blocked in the DHIS2 play environment.</i>",
        )
        assertThat(loginConfig.applicationLeftSideFooter)
            .isEqualTo("Learn more at <a href=\"http://www.dhis2.org\">dhis2.org</a>")
        assertThat(loginConfig.countryFlag).isEqualTo("sierra_leone")
        assertThat(loginConfig.uiLocale).isEqualTo("en")
        assertThat(loginConfig.loginPageLogo).isEqualTo("/api/staticContent/logo_front.png")
        assertThat(loginConfig.loginPopup).isEqualTo("")
        assertThat(loginConfig.loginPageLayout).isEqualTo(LoginPageLayout.SIDEBAR)
        assertThat(loginConfig.loginPageTemplate).isEqualTo("login_page_template_test")
        assertThat(loginConfig.recaptchaSite).isEqualTo("6LcVwT0UAAAAAAkO_EGPiYOiymIszZUeHfqWIYX5")
        assertThat(loginConfig.minPasswordLength).isEqualTo(8)
        assertThat(loginConfig.maxPasswordLength).isEqualTo(72)
        assertThat(loginConfig.emailConfigured).isFalse()
        assertThat(loginConfig.selfRegistrationEnabled).isTrue()
        assertThat(loginConfig.selfRegistrationNoRecaptcha).isFalse()
        assertThat(loginConfig.allowAccountRecovery).isTrue()
        assertThat(loginConfig.useCustomLogoFront).isFalse()

        val oidcProviders = loginConfig.oidcProviders
        assertThat(oidcProviders).hasSize(2)
        assertThat(oidcProviders[0].id).isEqualTo("external_dhis2_provider_1")
        assertThat(oidcProviders[0].icon).isEqualTo("/dhis-web-commons/oidc/icon1.svg")
        assertThat(oidcProviders[0].iconPadding).isEqualTo("10px 10px")
        assertThat(oidcProviders[0].loginText).isEqualTo("Login with Provider 1")
        assertThat(oidcProviders[0].url).isEqualTo("oauth2/authorization/provider1")
        assertThat(oidcProviders[1].id).isEqualTo("external_dhis2_provider_2")
        assertThat(oidcProviders[1].icon).isEqualTo("/dhis-web-commons/oidc/icon2.svg")
        assertThat(oidcProviders[1].iconPadding).isEqualTo("15px 15px")
        assertThat(oidcProviders[1].loginText).isEqualTo("Login with Provider 2")
        assertThat(oidcProviders[1].url).isEqualTo("oauth2/authorization/provider2")
    }

    private fun getDownload(): LoginConfig {
        return d2.serverModule().blockingCheckServerUrl(dhis2MockServer.baseEndpoint).getOrThrow()
    }

    companion object {
        private const val LOGIN_CONFIG_JSON = "server/login_config.json"
    }
}
