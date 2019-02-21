/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.ObjectMapperFactory;
import org.hisp.dhis.android.core.arch.api.executors.APICallErrorCatcher;
import org.hisp.dhis.android.core.imports.HttpMessageResponse;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;

import java.io.IOException;

import retrofit2.Response;

final class TrackedEntityInstanceCallErrorCatcher implements APICallErrorCatcher {

    @Override
    public Boolean mustBeStored() {
        return false;
    }

    @Override
    public D2ErrorCode catchError(Response<?> response) throws IOException {
        HttpMessageResponse parsed = ObjectMapperFactory.objectMapper().readValue(response.errorBody().string(),
                HttpMessageResponse.class);

        if (parsed.httpStatusCode() == 401 && parsed.message().equals("OWNERSHIP_ACCESS_DENIED")) {
            return D2ErrorCode.OWNERSHIP_ACCESS_DENIED;
        } else {
            return null;
        }
    }
}