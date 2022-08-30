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

package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallErrorCatcher;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;

import java.io.IOException;

import retrofit2.Response;

final class TrackedEntityAttributeReservedValueCallErrorCatcher implements APICallErrorCatcher {

    @Override
    public Boolean mustBeStored() {
        return true;
    }

    @Override
    public D2ErrorCode catchError(Response<?> response, String errorBody) throws IOException {
        if (errorBody.contains("Not enough values left to reserve")) {
            return D2ErrorCode.NOT_ENOUGH_VALUES_LEFT_TO_RESERVE_ON_SERVER;
        } else if (errorBody.contains("Generation and reservation of values took too long")) {
            return D2ErrorCode.VALUES_RESERVATION_TOOK_TOO_LONG;
        } else if (errorBody.contains("You might be running low on available values")) {
            return D2ErrorCode.MIGHT_BE_RUNNING_LOW_ON_AVAILABLE_VALUES;
        } else if (response.code() == 409) {
            return D2ErrorCode.COULD_NOT_RESERVE_VALUE_ON_SERVER;
        }

        return null;
    }
}