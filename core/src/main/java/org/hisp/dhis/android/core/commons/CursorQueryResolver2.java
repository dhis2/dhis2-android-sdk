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

import android.database.Cursor;
import android.net.Uri;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import io.reactivex.Observable;
import io.reactivex.Single;

class CursorQueryResolver2 implements ReadQueryResolver<Cursor> {
    private final Executor executor;
    private final ContentResolverWrapper contentResolverWrapper;
    private final Uri contentUri;
    private final Query query;

    public CursorQueryResolver2(Executor executor, ContentResolverWrapper contentResolver,
            Uri contentUri, Query query) {
        this.executor = executor;
        this.contentResolverWrapper = contentResolver;
        this.contentUri = contentUri;
        this.query = query;
    }

    @Override
    public Task<Cursor> asTask() {
        return new TaskImpl<>(executor, new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                return contentResolverWrapper.query(contentUri, query).blockingGet();
            }
        });
    }

    @Override
    public Single<Cursor> asSingle() {
        return contentResolverWrapper.query(contentUri, query);
    }

    @Override
    public Observable<Cursor> asObservable() {
        return contentResolverWrapper.query(contentUri, query, true);
    }
}
