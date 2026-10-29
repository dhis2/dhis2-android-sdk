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

import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import org.hisp.dhis.android.core.common.internal.NativeLibraryLoader
import org.koin.core.annotation.Singleton
import java.nio.charset.StandardCharsets

/**
 * Produces the [SQLiteDriver] backing a Room database.
 *
 * Room 3 is driver-only: there is no SupportSQLite compatibility mode, so encryption has to be
 * expressed as a driver too. Plaintext databases use the bundled SQLite build (the same engine on
 * every device and, later, every platform); encrypted ones use SQLCipher through
 * [CompiledWriteSQLCipherDriver].
 *
 * Keeping the choice behind this one interface is also what a future KMP split needs -- this
 * becomes the `actual` factory for Android, while other targets supply their own.
 */
internal fun interface SqliteDriverFactory {
    fun create(password: String?): SQLiteDriver
}

@Singleton(binds = [SqliteDriverFactory::class])
internal class SqliteDriverFactoryImpl : SqliteDriverFactory {

    override fun create(password: String?): SQLiteDriver {
        return if (password == null) {
            BundledSQLiteDriver()
        } else {
            NativeLibraryLoader.loadSQLCipher()
            CompiledWriteSQLCipherDriver(
                password.toByteArray(StandardCharsets.UTF_8),
                SqlCipherEncryptionHook,
                LoggingErrorHandler(SQLCIPHER_TAG),
            )
        }
    }

    private companion object {
        const val SQLCIPHER_TAG = "SQLCipherDriver"
    }
}
