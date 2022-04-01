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

package org.hisp.dhis.android.core.arch.api.executors.internal;

import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.internal.D2ErrorStore;
import org.hisp.dhis.android.core.user.internal.UserAccountDisabledErrorCatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;
import retrofit2.Call;
import retrofit2.Response;

@Reusable
public final class APICallExecutorImpl implements APICallExecutor {

    private final ObjectStore<D2Error> errorStore;
    private final UserAccountDisabledErrorCatcher userAccountDisabledErrorCatcher;
    private final APIErrorMapper errorMapper = new APIErrorMapper();

    @Inject
    public APICallExecutorImpl(ObjectStore<D2Error> errorStore,
                               UserAccountDisabledErrorCatcher userAccountDisabledErrorCatcher) {
        this.errorStore = errorStore;
        this.userAccountDisabledErrorCatcher = userAccountDisabledErrorCatcher;
    }

    @Override
    public <P> List<P> executePayloadCall(Call<Payload<P>> call) throws D2Error {
        return executeObjectCallInternal(call, new ArrayList<>(), null, null, false).items();
    }

    @Override
    public <P> P executeObjectCall(Call<P> call) throws D2Error {
        return executeObjectCallInternal(call, new ArrayList<>(), null, null, false);
    }

    @Override
    public <P> P executeObjectCallWithAcceptedErrorCodes(Call<P> call, List<Integer> acceptedErrorCodes,
                                                         Class<P> errorClass) throws D2Error {
        return executeObjectCallInternal(call, acceptedErrorCodes, errorClass, null, false);
    }

    @Override
    public <P> P executeObjectCallWithErrorCatcher(Call<P> call, APICallErrorCatcher errorCatcher)
            throws D2Error {
        return executeObjectCallInternal(call, new ArrayList<>(), null, errorCatcher, false);
    }

    @Override
    public Unit executeObjectCallWithEmptyResponse(Call<Unit> call) throws D2Error {
        return executeObjectCallInternal(call, new ArrayList<>(), null, null, true);
    }

    private <P> P executeObjectCallInternal(Call<P> call,
                                            List<Integer> acceptedErrorCodes,
                                            Class<P> errorClass,
                                            APICallErrorCatcher errorCatcher,
                                            boolean emptyBodyExpected) throws D2Error {
        try {
            Response<P> response = call.execute();
            if (response.isSuccessful()) {
                return processSuccessfulResponse(errorBuilder(call), response, emptyBodyExpected);
            } else {
                String errorBody = errorMapper.getErrorBody(response);
                if (userAccountDisabledErrorCatcher.isUserAccountLocked(response, errorBody)) {
                    this.catchAndThrow(userAccountDisabledErrorCatcher, errorBuilder(call), response, errorBody);
                } else if (errorClass != null && acceptedErrorCodes.contains(response.code())) {
                    return ObjectMapperFactory.objectMapper().readValue(errorBody, errorClass);
                } else if (errorCatcher != null) {
                    this.catchAndThrow(errorCatcher, errorBuilder(call), response, errorBody);
                }
                throw storeAndReturn(errorMapper.responseException(errorBuilder(call), response, errorBody));
            }
        } catch (D2Error d2Error) {
            throw d2Error;
        } catch (Throwable t) {
            throw storeAndReturn(errorMapper.mapRetrofitException(t, errorBuilder(call)));
        }
    }

    private <P> void catchAndThrow(APICallErrorCatcher errorCatcher, D2Error.Builder errorBuilder,
                                   Response<P> response, String errorBody) throws IOException, D2Error {
        D2ErrorCode d2ErrorCode = errorCatcher.catchError(response, errorBody);

        if (d2ErrorCode != null) {
            D2Error d2error = errorMapper.responseException(errorBuilder, response, d2ErrorCode, errorBody);

            if (errorCatcher.mustBeStored()) {
                throw storeAndReturn(d2error);
            } else {
                throw d2error;
            }
        }
    }

    private <P> P processSuccessfulResponse(D2Error.Builder errorBuilder, Response<P> response,
                                            boolean emptyBodyExpected) throws D2Error {
        if (emptyBodyExpected) {
            return null;
        } else if (response.body() == null) {
            throw storeAndReturn(errorMapper.responseException(errorBuilder, response, null));
        } else {
            return response.body();
        }
    }

    private D2Error storeAndReturn(D2Error error) {
        if (errorStore.isReady()) {
            errorStore.insert(error);
        }
        return error;
    }

    private <P> D2Error.Builder errorBuilder(Call<P> call) {
        return errorMapper.getBaseErrorBuilder(call);
    }

    public static APICallExecutor create(DatabaseAdapter databaseAdapter,
                                         UserAccountDisabledErrorCatcher userAccountDisabledErrorCatcher) {
        return new APICallExecutorImpl(D2ErrorStore.create(databaseAdapter), userAccountDisabledErrorCatcher);
    }
}