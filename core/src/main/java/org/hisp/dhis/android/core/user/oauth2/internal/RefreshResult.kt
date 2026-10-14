/*
 *  Copyright (c) 2004-2026, University of Oslo
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
package org.hisp.dhis.android.core.user.oauth2.internal

import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.user.oauth2.OAuth2State

/**
 * Outcome of [OAuth2TokenRefresher.rotate]. The distinction between [Retryable] and [Invalid] is
 * what keeps a network outage from closing the session: only [Invalid] means the session is
 * unrecoverable.
 */
internal sealed class RefreshResult {
    /**
     * A usable state is available, either freshly rotated or already rotated by a concurrent call.
     */
    data class Success(val state: OAuth2State) : RefreshResult()

    /**
     * The refresh could not be completed for a transient reason (offline, timeout, server error).
     * The stored credentials are left untouched and the caller may keep using the current token.
     */
    data class Retryable(val error: D2Error?) : RefreshResult()

    /**
     * There is no usable token and there will not be one without authorizing again. The session has
     * already been closed and [error] is meant to be propagated to the app as-is.
     */
    data class Invalid(val error: D2Error) : RefreshResult()
}
