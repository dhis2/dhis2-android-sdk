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

import com.squareup.sqlbrite.BriteContentResolver;

import org.hisp.dhis.android.core.commons.database.Model;

import java.util.List;
import java.util.concurrent.Executor;

public class StoreImpl<T extends Model> implements Store<T> {
    private final BriteContentResolver briteContentResolver;
    private final ContentResolver contentResolver;
    private final Mapper<T> modelMapper;
    private final Uri uri;

    // executor necessary for asynchronous tasks
    private final Executor executor;

    public StoreImpl(BriteContentResolver briteContentResolver, ContentResolver contentResolver,
            Mapper<T> modelMapper, Uri uri, Executor executor) {
        this.briteContentResolver = briteContentResolver;
        this.contentResolver = contentResolver;
        this.modelMapper = modelMapper;
        this.uri = uri;
        this.executor = executor;
    }

    @NonNull
    @Override
    public TypeResolver<T> query() {
        return query(Query.builder().build());
    }

    @NonNull
    @Override
    public TypeResolver<T> query(@NonNull Query query) {
        return new TypeResolverImpl<>(executor, briteContentResolver, contentResolver, modelMapper,
                uri, query);
    }

    @NonNull
    @Override
    public WriteQueryResolver<Long> insert(@NonNull final T model) {
        return new InsertQueryResolver<>(executor, contentResolver, uri, modelMapper, model);
    }

    @NonNull
    @Override
    public WriteQueryResolver<Long> insert(@NonNull ContentValues contentValues) {
        return new InsertQueryResolver<>(executor, contentResolver, uri, contentValues);
    }

    @NonNull
    @Override
    public WriteQueryResolver<Long> insert(@NonNull List<T> models) {
        // TODO
        return null;
    }

    @NonNull
    @Override
    public WriteQueryResolver<Long> insert(@NonNull ContentValues[] values) {
        // TODO
        return null;
    }

    @NonNull
    @Override
    public WriteQueryResolver<Integer> update(@NonNull T model) {
        return new UpdateQueryResolver<>(executor, contentResolver, uri, null, modelMapper, model);
    }

    @NonNull
    @Override
    public WriteQueryResolver<Integer> update(@NonNull ContentValues contentValues) {
        return new UpdateQueryResolver<>(executor, contentResolver, uri, contentValues);
    }

    @NonNull
    @Override
    public WriteQueryResolver<Integer> update(@NonNull T model, @Nullable Where where) {
        return new UpdateQueryResolver<>(executor, contentResolver, uri, where, modelMapper, model);
    }

    @NonNull
    @Override
    public WriteQueryResolver<Integer> update(@NonNull ContentValues values, @Nullable Where where) {
        return new UpdateQueryResolver<>(executor, contentResolver, uri, where, values);
    }

    @NonNull
    @Override
    public WriteQueryResolver<Integer> update(@NonNull Long id,
            @NonNull ContentValues contentValues) {
        return new UpdateQueryResolver<>(executor, contentResolver, uri, id, contentValues);
    }

    @NonNull
    @Override
    public WriteQueryResolver<Integer> delete() {
        return null;
    }

    @NonNull
    @Override
    public WriteQueryResolver<Integer> delete(@NonNull T model) {
        return null;
    }

    @NonNull
    @Override
    public WriteQueryResolver<Integer> delete(@NonNull Where where) {
        return null;
    }
}