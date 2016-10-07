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

package org.hisp.dhis.client.sdk.core.commons;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import org.hisp.dhis.client.sdk.models.common.Model;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

// TODO consider returning URIs instead of booleans
public abstract class AbsStore<T extends Model> implements Store<T> {
    protected final ContentResolver contentResolver;
    protected final Mapper<T> mapper;

    public AbsStore(ContentResolver contentResolver, Mapper<T> mapper) {
        this.contentResolver = isNull(contentResolver, "contentResolver must not be null");
        this.mapper = isNull(mapper, "mapper must not be null");
    }

    @Override
    public boolean insert(T object) {
        contentResolver.insert(mapper.getContentUri(), mapper.toContentValues(object));
        return true;
    }

    @Override
    public boolean update(T object) {
        ContentValues contentValues = mapper.toContentValues(object);
        contentResolver.update(mapper.getContentItemUri(object.getId()), contentValues, null, null);
        return true;
    }

    @Override
    public boolean save(T object) {
        T databaseRow = queryById(object.getId());
        if (databaseRow != null) {
            update(object);
        } else {
            insert(object);
        }

        Store<T> store = this;
        store.deleteAll();

        return true;
    }

    @Override
    public int delete(T object) {
        isNull(object, "object must not be null");
        return contentResolver.delete(mapper.getContentItemUri(object.getId()), null, null);
    }

    @Override
    public int deleteAll() {
        return contentResolver.delete(mapper.getContentUri(), null, null);
    }

    @Override
    public T queryById(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("id must be >= 0");
        }

        Cursor cursor = contentResolver.query(mapper.getContentItemUri(id),
                mapper.getProjection(), null, null, null);
        return toModel(cursor);
    }

    @Override
    public List<T> queryAll() {
        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), null, null, null);
        return toModels(cursor);
    }

    protected T toModel(Cursor cursor) {
        T model = null;

        if (cursor != null && !cursor.isClosed()) {
            try {
                cursor.moveToFirst();
                model = mapper.toModel(cursor);
            } finally {
                cursor.close();
            }
        }

        return model;
    }

    protected List<T> toModels(Cursor cursor) {
        List<T> items = new ArrayList<>();

        if (cursor != null && !cursor.isClosed()) {
            try {
                cursor.moveToFirst();

                do {
                    items.add(mapper.toModel(cursor));
                } while (cursor.moveToNext());
            } finally {
                cursor.close();
            }
        }

        return items;
    }
}
