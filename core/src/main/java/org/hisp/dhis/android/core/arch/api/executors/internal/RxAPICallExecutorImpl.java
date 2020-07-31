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

package org.hisp.dhis.android.core.arch.api.executors.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyCleaner;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@Reusable
final class RxAPICallExecutorImpl implements RxAPICallExecutor {

    private final DatabaseAdapter databaseAdapter;
    private final ObjectStore<D2Error> errorStore;
    private final APIErrorMapper errorMapper;
    private final ForeignKeyCleaner foreignKeyCleaner;

    @Inject
    RxAPICallExecutorImpl(DatabaseAdapter databaseAdapter,
                          ObjectStore<D2Error> errorStore,
                          APIErrorMapper errorMapper,
                          ForeignKeyCleaner foreignKeyCleaner) {
        this.databaseAdapter = databaseAdapter;
        this.errorStore = errorStore;
        this.errorMapper = errorMapper;
        this.foreignKeyCleaner = foreignKeyCleaner;
    }

    @Override
    public <P> Single<P> wrapSingle(Single<P> single, boolean storeError) {
        return single.onErrorResumeNext(throwable -> Single.error(mapAndStore(throwable, storeError)));
    }

    @Override
    public <P> Observable<P> wrapObservableTransactionally(Observable<P> observable, boolean cleanForeignKeys) {
        return Observable.fromCallable(databaseAdapter::beginNewTransaction).flatMap(transaction -> observable
                .doOnComplete(() -> {
                    if (cleanForeignKeys) {
                        foreignKeyCleaner.cleanForeignKeyErrors();
                    }
                    transaction.setSuccessful();
                    transaction.end();
                }).onErrorResumeNext(throwable -> {
                    transaction.end();
                    return Observable.error(mapAndStore(throwable, true));
                }));
    }

    @Override
    public Completable wrapCompletableTransactionally(Completable completable, boolean cleanForeignKeys) {
        return Single.fromCallable(databaseAdapter::beginNewTransaction).flatMapCompletable(transaction -> completable
                .doOnComplete(() -> {
                    if (cleanForeignKeys) {
                        foreignKeyCleaner.cleanForeignKeyErrors();
                    }
                    transaction.setSuccessful();
                    transaction.end();
                }).onErrorResumeNext(throwable -> {
                    transaction.end();
                    return Completable.error(mapAndStore(throwable, true));
                }));
    }

    private D2Error mapAndStore(Throwable throwable, boolean storeError) {
        D2Error d2Error = throwable instanceof D2Error ? (D2Error) throwable
                : errorMapper.mapRetrofitException(throwable, errorMapper.getRxObjectErrorBuilder());
        if (storeError) {
            errorStore.insert(d2Error);
        }
        return d2Error;
    }
}