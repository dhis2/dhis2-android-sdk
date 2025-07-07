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

package org.hisp.dhis.android.core.arch.db.access

/**
 * Interface for managing database lifecycle operations.
 * Designed to be platform-agnostic for future KMP support.
 */
interface DatabaseManager {
    /**
     * Creates or opens a database with the specified name.
     *
     * @param databaseName The name of the database to create or open
     * @return A DatabaseAdapter for the created/opened database
     */
    fun createOrOpenDatabase(databaseName: String): DatabaseAdapter

    /**
     * Creates or opens an encrypted database with the specified name and password.
     *
     * @param databaseName The name of the database to create or open
     * @param password The password to use for encryption
     * @return A DatabaseAdapter for the created/opened encrypted database
     */
    fun createOrOpenEncryptedDatabase(databaseName: String, password: String): DatabaseAdapter

    /**
     * Deletes the database with the specified name.
     *
     * @param databaseName The name of the database to delete
     * @return true if the database was successfully deleted, false otherwise
     */
    fun deleteDatabase(databaseName: String, isEncrypted: Boolean): Boolean

    /**
     * Checks if a database with the specified name exists.
     *
     * @param databaseName The name of the database to check
     * @return true if the database exists, false otherwise
     */
    fun databaseExists(databaseName: String): Boolean

    fun disableDatabase()
}
