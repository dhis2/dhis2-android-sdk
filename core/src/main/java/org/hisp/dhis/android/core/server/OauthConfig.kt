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

internal data class OauthConfig(
    val authorizationEndpoint: String? = null,
    val jwksUri: String? = null,
    val codeChallengeMethodsSupported: List<String> = emptyList(),
    val grantTypesSupported: List<String> = emptyList(),
    val responseTypesSupported: List<String> = emptyList(),
) {
    fun supportsRequiredFunctions(): Boolean {
        return codeChallengeMethodsSupported.contains(REQUIRED_CODE_CHALLENGE_METHOD) &&
            grantTypesSupported.containsAll(REQUIRED_GRANT_TYPES) &&
            responseTypesSupported.contains(REQUIRED_RESPONSE_TYPE)
    }

    companion object {
        const val REQUIRED_CODE_CHALLENGE_METHOD = "S256"
        const val REQUIRED_RESPONSE_TYPE = "code"
        val REQUIRED_GRANT_TYPES = listOf("authorization_code", "refresh_token")
    }
}
