/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.common;

import android.util.Log;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;

import java.util.concurrent.Callable;

@SuppressWarnings({"PMD.PreserveStackTrace"})
public final class D2CallExecutor {

    private final D2CallException.Builder exceptionBuilder = D2CallException
            .builder()
            .isHttpError(false);

    public <C> C executeD2Call(Callable<C> call) throws D2CallException {
        try {
            return call.call();
        } catch (D2CallException d2e) {
            throw d2e;

        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.toString());
            throw exceptionBuilder.errorDescription("Unexpected error calling " + call).build();
        }
    }

    public <C> C executeD2CallTransactionally(DatabaseAdapter databaseAdapter, Callable<C> call)
            throws D2CallException {
        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            C response = call.call();
            transaction.setSuccessful();
            return response;
        } catch (D2CallException d2E) {
            throw d2E;
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.toString());
            throw exceptionBuilder
                    .errorCode(D2ErrorCode.UNEXPECTED)
                    .errorDescription("Unexpected error calling " + call).build();
        } finally {
            transaction.end();
        }
    }
}