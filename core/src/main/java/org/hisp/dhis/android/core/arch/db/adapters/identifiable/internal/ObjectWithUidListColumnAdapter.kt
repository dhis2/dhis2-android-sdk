/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal

import android.content.ContentValues
import android.database.Cursor
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.gabrielittner.auto.value.cursor.ColumnTypeAdapter
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory
import org.hisp.dhis.android.core.common.ObjectWithUid

internal class ObjectWithUidListColumnAdapter : ColumnTypeAdapter<List<ObjectWithUid>> {

    override fun fromCursor(cursor: Cursor, columnName: String): List<ObjectWithUid> {
        val columnIndex = cursor.getColumnIndex(columnName)
        val str = cursor.getString(columnIndex)
        return try {
            val idList = ObjectMapperFactory.objectMapper().readValue(str, ArrayList<String>().javaClass)
            idList.map { ObjectWithUid.create(it) }
        } catch (e: JsonProcessingException) {
            listOf()
        } catch (e: JsonMappingException) {
            listOf()
        } catch (e: IllegalArgumentException) {
            listOf()
        } catch (e: IllegalStateException) {
            listOf()
        }
    }

    override fun toContentValues(values: ContentValues, columnName: String, value: List<ObjectWithUid>?) {
        try {
            values.put(columnName, serialize(value))
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
    }

    companion object {
        fun serialize(o: List<ObjectWithUid>?): String? {
            return o?.map { it.uid() }.let {
                ObjectMapperFactory.objectMapper().writeValueAsString(it)
            }
        }
    }
}
