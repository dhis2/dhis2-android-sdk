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
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import org.hisp.dhis.android.models.common.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import rx.functions.Func1;

class ListQueryResolver<T extends Model> implements ReadQueryResolver<List<T>> {
    private final Executor executor;
    private final BriteContentResolver briteContentResolver;
    private final ContentResolver contentResolver;
    private final Mapper<T> contentMapper;
    private final Uri contentUri;
    private final Query query;

    ListQueryResolver(Executor executor, BriteContentResolver briteContentResolver,
            ContentResolver contentResolver, Mapper<T> contentMapper, Uri contentUri, Query query) {
        this.executor = executor;
        this.briteContentResolver = briteContentResolver;
        this.contentResolver = contentResolver;
        this.contentMapper = contentMapper;
        this.contentUri = contentUri;
        this.query = query;
    }

    @Override
    public Task<List<T>> asTask() {
        return new TaskImpl<>(executor, new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                Cursor queryCursor = contentResolver.query(contentUri, query.projection(),
                        query.selection(), query.selectionArgs(), query.sortOrder());

                return map(queryCursor);
            }
        });
    }

    @Override
    public Single<List<T>> asSingle() {
        return Single.defer(new Callable<SingleSource<? extends List<T>>>() {
            @Override
            public SingleSource<? extends List<T>> call() throws Exception {
                Cursor queryCursor = contentResolver.query(contentUri, query.projection(),
                        query.selection(), query.selectionArgs(), query.sortOrder());

                return Single.just(map(queryCursor));
            }
        });
    }

    @Override
    public Observable<List<T>> asObservable() {
        return RxJavaInterop.toV2Observable(briteContentResolver.createQuery(contentUri,
                query.projection(), query.selection(), query.selectionArgs(), query.sortOrder(), true)
                .map(new Func1<SqlBrite.Query, List<T>>() {
                    @Override
                    public List<T> call(SqlBrite.Query query) {
                        return map(query.run());
                    }
                })
        );
    }

    @NonNull
    private List<T> map(Cursor cursor) {
        List<T> models = new ArrayList<>();

        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    models.add(contentMapper.toModel(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return models;
    }
}
