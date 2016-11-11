/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.android.core.commons;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import rx.functions.Func1;

class CursorQueryResolver implements ReadQueryResolver<Cursor> {
    private final Executor executor;
    private final BriteContentResolver briteContentResolver;
    private final ContentResolver contentResolver;
    private final Uri contentUri;
    private final Query query;

    CursorQueryResolver(Executor executor, BriteContentResolver briteContentResolver,
            ContentResolver contentResolver, Uri contentUri, Query query) {
        this.executor = executor;
        this.briteContentResolver = briteContentResolver;
        this.contentResolver = contentResolver;
        this.contentUri = contentUri;
        this.query = query;
    }

    @Override
    public Task<Cursor> asTask() {
        return new TaskImpl<>(executor, new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                return contentResolver.query(contentUri, query.projection(), query.selection(),
                        query.selectionArgs(), query.sortOrder());
            }
        });
    }

    @Override
    public Single<Cursor> asSingle() {
        return Single.defer(new Callable<SingleSource<? extends Cursor>>() {
            @Override
            public SingleSource<? extends Cursor> call() throws Exception {
                Cursor queryCursor = contentResolver.query(contentUri, query.projection(),
                        query.selection(), query.selectionArgs(), query.sortOrder());

                if (queryCursor != null) {
                    return Single.just(queryCursor);
                }

                return null;
            }
        });
    }

    @Override
    public Observable<Cursor> asObservable() {
        return RxJavaInterop.toV2Observable(briteContentResolver.createQuery(contentUri,
                query.projection(), query.selection(), query.selectionArgs(), query.sortOrder(), true)
                .switchMap(new Func1<SqlBrite.Query, rx.Observable<Cursor>>() {
                    @Override
                    public rx.Observable<Cursor> call(SqlBrite.Query query) {
                        return rx.Observable.just(query.run());
                    }
                }));
    }
}
