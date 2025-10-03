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
package org.hisp.dhis.android.core.data.database

import android.database.Cursor
import com.google.common.truth.Truth

class CursorAssert // set to first row by default
private constructor(private val cursor: Cursor) {
    private var row = 0

    fun hasRow(vararg values: Any): CursorAssert {
        Truth.assertThat(cursor.moveToNext()).isTrue()
        row = row + 1

        Truth.assertThat(cursor.columnCount).isEqualTo(values.size)
        for (index in values.indices) {
            Truth.assertThat(cursor.getString(index))
                .isEqualTo(if (values[index] == null) values[index] else values[index].toString())
        }

        return this
    }

    val isExhausted: Unit
        get() {
            if (cursor.moveToNext()) {
                val data = StringBuilder()
                for (i in 0..<cursor.columnCount) {
                    if (i > 0) {
                        data.append(", ")
                    }

                    data.append(cursor.getString(i))
                }

                throw AssertionError("Expected no more rows but was: $data")
            }

            cursor.close()
        }

    companion object {
        fun assertThatCursor(cursor: Cursor): CursorAssert {
            return CursorAssert(cursor)
        }
    }
}
