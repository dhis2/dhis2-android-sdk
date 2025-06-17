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
package org.hisp.dhis.android.core.systeminfo

import android.database.MatrixCursor
import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.data.systeminfo.SystemInfoSamples
import org.hisp.dhis.android.core.util.dateFormat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SystemInfoDatabaseMappingShould {
    private val systemInfo: SystemInfo = SystemInfoSamples.get1()

    @Test
    fun map_cursor_to_object() {
        val columnsWithId = SystemInfoTableInfo.TABLE_INFO.columns().all()
        val cursor = MatrixCursor(columnsWithId)

        cursor.addRow(
            arrayOf<Any?>(
                systemInfo.serverDate().dateFormat()!!,
                systemInfo.dateFormat(),
                systemInfo.version(),
                systemInfo.contextPath(),
                systemInfo.systemName(),
            ),
        )

        cursor.moveToFirst()

        val dbSystemInfo = SystemInfo.create(cursor).toBuilder().id(null).build()
        val expectedInfo = systemInfo.toBuilder().id(null).build()
        cursor.close()

        assertThat(dbSystemInfo).isEqualTo(expectedInfo)
    }

    @Test
    fun map_object_to_content_values() {
        val contentValues = systemInfo.toContentValues()

        assertThat(contentValues.getAsString(SystemInfoTableInfo.Columns.SERVER_DATE))
            .isEqualTo(systemInfo.serverDate().dateFormat())
        assertThat(contentValues.getAsString(SystemInfoTableInfo.Columns.DATE_FORMAT))
            .isEqualTo(systemInfo.dateFormat())
        assertThat(contentValues.getAsString(SystemInfoTableInfo.Columns.CONTEXT_PATH))
            .isEqualTo(systemInfo.contextPath())
        assertThat(contentValues.getAsString(SystemInfoTableInfo.Columns.VERSION))
            .isEqualTo(systemInfo.version())
        assertThat(contentValues.getAsString(SystemInfoTableInfo.Columns.SYSTEM_NAME))
            .isEqualTo(systemInfo.systemName())
    }
}
