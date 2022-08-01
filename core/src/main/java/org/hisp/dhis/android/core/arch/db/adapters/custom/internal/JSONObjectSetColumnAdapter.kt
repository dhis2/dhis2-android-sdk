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
package org.hisp.dhis.android.core.arch.db.adapters.custom.internal

import android.content.ContentValues
import android.database.Cursor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.gabrielittner.auto.value.cursor.ColumnTypeAdapter
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory

internal abstract class JSONObjectSetColumnAdapter<O> : ColumnTypeAdapter<Set<O>> {
    protected abstract fun getObjectClass(): Class<Set<O>>

    override fun fromCursor(cursor: Cursor, columnName: String): Set<O> {
        val columnIndex = cursor.getColumnIndex(columnName)
        val str = cursor.getString(columnIndex)
        return try {
            ObjectMapperFactory.objectMapper().readValue(str, getObjectClass())
        } catch (e: JsonProcessingException) {
            setOf()
        } catch (e: JsonMappingException) {
            setOf()
        } catch (e: IllegalArgumentException) {
            setOf()
        } catch (e: IllegalStateException) {
            setOf()
        }
    }

    override fun toContentValues(contentValues: ContentValues, columnName: String, o: Set<O>?) {
        try {
            contentValues.put(columnName, serialize(o))
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
    }

    abstract fun serialize(o: Set<O>?): String?
}
