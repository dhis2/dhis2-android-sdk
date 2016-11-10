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
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;

import org.hisp.dhis.client.models.common.Model;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import io.reactivex.Single;
import io.reactivex.SingleSource;

class InsertQueryResolver<T extends Model> implements WriteQueryResolver<Long> {
    private final Executor executor;
    private final ContentResolver contentResolver;
    private final ContentValues contentValues;
    private final Mapper<T> modelMapper;
    private final Uri contentUri;
    private final T model;

    InsertQueryResolver(Executor executor, ContentResolver contentResolver, Uri contentUri,
            ContentValues contentValues) {
        this.executor = executor;
        this.contentResolver = contentResolver;
        this.contentValues = contentValues;
        this.contentUri = contentUri;
        this.modelMapper = null;
        this.model = null;
    }

    InsertQueryResolver(Executor executor, ContentResolver contentResolver, Uri contentUri,
            Mapper<T> modelMapper, T model) {
        this.executor = executor;
        this.contentResolver = contentResolver;
        this.modelMapper = modelMapper;
        this.contentUri = contentUri;
        this.model = model;
        this.contentValues = null;
    }

    @Override
    public Task<Long> asTask() {
        return new TaskImpl<>(executor, new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return insert();
            }
        });
    }

    @Override
    public Single<Long> asSingle() {
        return Single.defer(new Callable<SingleSource<? extends Long>>() {
            @Override
            public SingleSource<? extends Long> call() throws Exception {
                return Single.just(insert());
            }
        });
    }

    private Long insert() {
        ContentValues values = modelMapper != null ?
                modelMapper.toContentValues(model) : contentValues;

        return ContentUris.parseId(contentResolver.insert(contentUri, values));
    }
}
