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
import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.client.models.common.Model;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import io.reactivex.Single;
import io.reactivex.SingleSource;

class UpdateQueryResolver<T extends Model> implements WriteQueryResolver<Integer> {

    @NonNull
    private final Executor executor;

    @NonNull
    private final ContentResolver contentResolver;

    @Nullable
    private final Mapper<T> modelMapper;

    @Nullable
    private final Where where;

    @NonNull
    private final Uri contentUri;

    @Nullable
    private final ContentValues contentValues;

    @Nullable
    private final T model;

    @Nullable
    private final Long id;

    UpdateQueryResolver(@NonNull Executor executor, @NonNull ContentResolver contentResolver,
            @NonNull Uri contentUri, @Nullable Where where, @NonNull ContentValues values) {
        this.executor = executor;
        this.contentResolver = contentResolver;
        this.contentValues = values;
        this.contentUri = contentUri;
        this.where = where;
        this.modelMapper = null;
        this.model = null;
        this.id = null;
    }

    UpdateQueryResolver(@NonNull Executor executor, @NonNull ContentResolver contentResolver,
            @NonNull Uri contentUri, @NonNull ContentValues values) {
        this.executor = executor;
        this.contentResolver = contentResolver;
        this.contentValues = values;
        this.contentUri = contentUri;
        this.modelMapper = null;
        this.where = null;
        this.model = null;
        this.id = null;
    }

    UpdateQueryResolver(@NonNull Executor executor, @NonNull ContentResolver contentResolver,
            @NonNull Uri uri, @Nullable Where where, @NonNull Mapper<T> mapper, @NonNull T model) {
        this.executor = executor;
        this.contentResolver = contentResolver;
        this.modelMapper = mapper;
        this.contentUri = uri;
        this.model = model;
        this.where = where;
        this.contentValues = null;
        this.id = null;
    }

    UpdateQueryResolver(@NonNull Executor executor, @NonNull ContentResolver contentResolver,
            @NonNull Uri uri, @NonNull Long id, @NonNull ContentValues values) {
        this.executor = executor;
        this.contentResolver = contentResolver;
        this.contentUri = uri;
        this.contentValues = values;
        this.id = id;
        this.where = null;
        this.modelMapper = null;
        this.model = null;
    }

    @Override
    public Task<Integer> asTask() {
        return new TaskImpl<>(executor, new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return update();
            }
        });
    }

    @Override
    public Single<Integer> asSingle() {
        return Single.defer(new Callable<SingleSource<? extends Integer>>() {
            @Override
            public SingleSource<? extends Integer> call() throws Exception {
                return Single.just(update());
            }
        });
    }

    private Integer update() {
        ContentValues values = modelMapper != null ?
                modelMapper.toContentValues(model) : contentValues;

        String selection = where != null ? where.where() : null;
        String[] selectionArgs = where != null ? where.arguments() : null;

        Uri uri = contentUri;
//        if (id != null) {
//            uri = ContentUris.withAppendedId(uri, id);
//        }

        return contentResolver.update(uri, values, selection, selectionArgs);
    }
}
