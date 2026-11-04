/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.persistence.db.access

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.SQLiteStatement
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@RunWith(JUnit4::class)
class ColumnNameCachingSQLiteDriverShould {

    private val statement: SQLiteStatement = mock {
        on { getColumnNames() } doReturn listOf("uid", "name")
        on { getColumnName(any()) } doReturn "fromDelegate"
        on { getText(1) } doReturn "value"
    }
    private val connection: SQLiteConnection = mock {
        on { prepare(any()) } doReturn statement
        on { inTransaction() } doReturn true
    }
    private val delegate: SQLiteDriver = mock {
        on { open(any()) } doReturn connection
        on { hasConnectionPool } doReturn true
    }

    private val driver = ColumnNameCachingSQLiteDriver(delegate)

    @Test
    fun read_column_names_from_the_delegate_once_per_statement() {
        val prepared = driver.open("db").prepare("SELECT uid, name FROM T")

        repeat(3) {
            assertThat(prepared.getColumnCount()).isEqualTo(2)
            assertThat(prepared.getColumnName(0)).isEqualTo("uid")
            assertThat(prepared.getColumnName(1)).isEqualTo("name")
            assertThat(prepared.getColumnNames()).containsExactly("uid", "name").inOrder()
        }

        verify(statement, times(1)).getColumnNames()
        verify(statement, never()).getColumnCount()
    }

    @Test
    fun let_the_delegate_handle_an_out_of_range_column() {
        val prepared = driver.open("db").prepare("SELECT uid, name FROM T")

        assertThat(prepared.getColumnName(2)).isEqualTo("fromDelegate")
    }

    @Test
    fun delegate_everything_else() {
        val opened = driver.open("db")
        val prepared = opened.prepare("SELECT uid, name FROM T")

        assertThat(driver.hasConnectionPool).isTrue()
        assertThat(opened.inTransaction()).isTrue()
        assertThat(prepared.getText(1)).isEqualTo("value")

        prepared.close()
        opened.close()

        verify(statement).close()
        verify(connection).close()
    }
}
