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

package org.hisp.dhis.android.core.arch.call.executors.internal;

import android.util.Log;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.Transaction;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.maintenance.internal.D2ErrorStore;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;

@SuppressWarnings({"PMD.PreserveStackTrace"})
@Reusable
public final class D2CallExecutor {

    private final D2Error.Builder exceptionBuilder = D2Error
            .builder()
            .errorComponent(D2ErrorComponent.SDK);

    private final DatabaseAdapter databaseAdapter;
    private final ObjectStore<D2Error> errorStore;

    @Inject
    public D2CallExecutor(DatabaseAdapter databaseAdapter, ObjectStore<D2Error> errorStore) {
        this.databaseAdapter = databaseAdapter;
        this.errorStore = errorStore;
    }

    public <C> C executeD2Call(Callable<C> call, boolean storeError) throws D2Error {
        try {
            return call.call();
        } catch (D2Error d2E) {
            if (storeError) {
                errorStore.insert(d2E);
            }
            throw d2E;
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.toString());
            throw exceptionBuilder.errorDescription("Unexpected error calling " + call).build();
        }
    }

    public <C> C executeD2CallTransactionally(Callable<C> call) throws D2Error {
        try {
            return innerExecuteD2CallTransactionally(call);
        } catch (D2Error d2E) {
            errorStore.insert(d2E);
            throw d2E;
        }
    }

    private <C> C innerExecuteD2CallTransactionally(Callable<C> call) throws D2Error {
        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {
            C response = call.call();
            transaction.setSuccessful();
            return response;
        } catch (D2Error d2E) {
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

    public static D2CallExecutor create(DatabaseAdapter databaseAdapter) {
        return new D2CallExecutor(databaseAdapter, D2ErrorStore.create(databaseAdapter));
    }
}