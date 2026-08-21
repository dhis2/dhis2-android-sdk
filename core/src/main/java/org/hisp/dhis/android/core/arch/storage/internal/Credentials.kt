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
package org.hisp.dhis.android.core.arch.storage.internal

import net.openid.appauth.AuthState
import org.hisp.dhis.android.core.common.AuthorizationType
import org.hisp.dhis.android.core.user.oauth2.OAuth2State

internal data class Credentials(
    val username: String,
    val serverUrl: String,
    val password: String?,
    val pin: String?,
    val openIDConnectState: AuthState?,
    val oauth2State: OAuth2State? = null,
) {

    val authorizationType: AuthorizationType = when {
        openIDConnectState != null -> AuthorizationType.OPEN_ID_CONNECT
        oauth2State != null -> AuthorizationType.OAUTH2
        else -> AuthorizationType.BASIC
    }

    val passwordOrPin: String?
        get() = password ?: pin

    /**
     * Derives a new hash for the stored secret, to be persisted in the AuthenticatedUser table.
     * The result is salted and therefore different on every call, so it must never be compared:
     * use [matches] to verify a secret against an already stored hash.
     */
    fun newPasswordHash(): String? {
        return passwordOrPin?.let { PasswordHasher.hash(it) }
    }

    /**
     * Verifies the stored secret against [storedHash], which may be in either the current or the
     * legacy MD5 format. Accounts without a secret (token based accounts with no PIN) are expected
     * to have no stored hash either.
     */
    fun matches(storedHash: String?): HashVerification {
        val secret = passwordOrPin
        return when {
            secret == null && storedHash == null -> HashVerification.Match(needsUpgrade = false)
            secret == null || storedHash == null -> HashVerification.Mismatch
            else -> PasswordHasher.verify(username, secret, storedHash)
        }
    }

    override fun equals(other: Any?) =
        (other is Credentials) &&
            username == other.username &&
            pin == other.pin &&
            password == other.password &&
            serverUrl == other.serverUrl &&
            openIDConnectState?.jsonSerializeString() == other.openIDConnectState?.jsonSerializeString() &&
            oauth2State?.jsonSerializeString() == other.oauth2State?.jsonSerializeString()

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + serverUrl.hashCode()
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + (pin?.hashCode() ?: 0)
        result = 31 * result + (openIDConnectState?.jsonSerializeString()?.hashCode() ?: 0)
        result = 31 * result + (oauth2State?.jsonSerializeString()?.hashCode() ?: 0)
        return result
    }
}
