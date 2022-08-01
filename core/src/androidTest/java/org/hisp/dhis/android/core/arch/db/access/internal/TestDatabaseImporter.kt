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
package org.hisp.dhis.android.core.arch.db.access.internal

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import java.io.*

class TestDatabaseImporter {

    fun copyDatabaseFromAssets(filename: String = FILESYSTEM_DB_NAME) {
        val context = InstrumentationRegistry.getInstrumentation().context
        val databasePath = context.applicationInfo?.dataDir + "/databases"
        val outputFile = databaseFile(context, filename)
        if (outputFile.exists()) {
            return
        }
        val inputStream = context.assets.open("databases/$ASSETS_DB_NAME")
        val outputStream = FileOutputStream("$databasePath/$filename")
        writeExtractedFileToDisk(inputStream, outputStream)
    }

    @Throws(IOException::class)
    private fun writeExtractedFileToDisk(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var length: Int
        length = input.read(buffer)
        while (length > 0) {
            output.write(buffer, 0, length)
            length = input.read(buffer)
        }
        output.flush()
        output.close()
        input.close()
    }

    fun databaseFile(context: Context, filename: String = FILESYSTEM_DB_NAME): File {
        val databasePath = context.applicationInfo?.dataDir + "/databases"
        return File("$databasePath/$filename")
    }

    companion object {
        const val ASSETS_DB_NAME = "test-database.db"
        const val FILESYSTEM_DB_NAME = "test-database.db"
    }
}
