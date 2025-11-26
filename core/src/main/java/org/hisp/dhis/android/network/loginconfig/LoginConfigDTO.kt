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

package org.hisp.dhis.android.network.loginconfig

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.server.LoginConfig
import org.hisp.dhis.android.core.server.LoginPageLayout

@Serializable
internal data class LoginConfigDTO(
    val apiVersion: String?,
    val applicationTitle: String?,
    val applicationDescription: String?,
    val applicationNotification: String?,
    val applicationLeftSideFooter: String?,
    val applicationRightSideFooter: String?,
    val countryFlag: String?,
    val uiLocale: String?,
    val loginPageLogo: String?,
    val loginPopup: String?,
    val loginPageLayout: String?,
    val recaptchaSite: String?,
    val minPasswordLength: String?,
    val maxPasswordLength: String?,
    val emailConfigured: Boolean?,
    val selfRegistrationEnabled: Boolean?,
    val selfRegistrationNoRecaptcha: Boolean?,
    val allowAccountRecovery: Boolean?,
    val useCustomLogoFront: Boolean?,
    val oidcProviders: List<LoginOidcProviderDTO>?,
) {
    fun toDomain(): LoginConfig {
        return LoginConfig(
            apiVersion = apiVersion,
            applicationTitle = applicationTitle,
            applicationDescription = applicationDescription,
            applicationNotification = applicationNotification,
            applicationLeftSideFooter = applicationLeftSideFooter,
            applicationRightSideFooter = applicationRightSideFooter,
            countryFlag = countryFlag,
            uiLocale = uiLocale,
            loginPageLogo = loginPageLogo,
            loginPopup = loginPopup,
            loginPageLayout = loginPageLayout?.let { LoginPageLayout.valueOf(loginPageLayout) },
            recaptchaSite = recaptchaSite,
            minPasswordLength = minPasswordLength?.toIntOrNull(),
            maxPasswordLength = maxPasswordLength?.toIntOrNull(),
            emailConfigured = emailConfigured ?: false,
            selfRegistrationEnabled = selfRegistrationEnabled ?: false,
            selfRegistrationNoRecaptcha = selfRegistrationNoRecaptcha ?: false,
            allowAccountRecovery = allowAccountRecovery ?: false,
            useCustomLogoFront = useCustomLogoFront ?: false,
            oidcProviders = oidcProviders?.map { it.toDomain() } ?: emptyList(),
        )
    }
}
