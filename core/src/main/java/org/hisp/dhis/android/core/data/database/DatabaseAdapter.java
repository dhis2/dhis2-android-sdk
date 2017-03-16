/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.data.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

@SuppressWarnings("PMD.UseVarargs")
public interface DatabaseAdapter {

    /**
     * Compiles an SQL statement into a reusable pre-compiled statement object.
     * You may put ?s in the
     * statement and fill in those values with SQLiteProgram.bindString
     * and SQLiteProgram.bindLong each time you want to run the
     * statement. Statements may not return result sets larger than 1x1.
     * <p>
     * No two threads should be using the same {@link SQLiteStatement} at the same time.
     *
     * @param sql The raw SQL statement, may contain ? for unknown values to be
     *            bound later.
     * @return A pre-compiled {@link SQLiteStatement} object. Note that
     * {@link SQLiteStatement}s are not synchronized, see the documentation for more details.
     */
    SQLiteStatement compileStatement(String sql);

    /**
     * Runs the provided SQL and returns a {@link Cursor} over the result set.
     *
     * @param sql           the SQL query. The SQL string must not be ; terminated
     * @param selectionArgs You may include ?s in where clause in the query,
     *                      which will be replaced by the values from selectionArgs. The
     *                      values will be bound as Strings.
     * @return A {@link Cursor} object, which is positioned before the first entry. Note that
     * {@link Cursor}s are not synchronized, see the documentation for more details.
     */
    Cursor query(String sql, String... selectionArgs);

    /**
     * Execute {@code statement} and return the ID of the row inserted due to this call.
     * The SQL statement should be an INSERT for this to be a useful call.
     *
     * @param table           The affected table
     * @param sqLiteStatement The SQL statement to execute
     * @return the row ID of the last row inserted, if this insert is successful. -1 otherwise.
     * @throws android.database.SQLException If the SQL string is invalid
     * @see SQLiteStatement#executeInsert()
     */
    long executeInsert(String table, SQLiteStatement sqLiteStatement);

    /**
     * Execute this SQL statement, if the the number of rows affected by execution of this SQL
     * statement is of any importance to the caller - for example, UPDATE / DELETE SQL statements.
     *
     * @param table           The affected table
     * @param sqLiteStatement The SQL statement to execute
     * @return the number of rows affected by this SQL statement execution.
     * @throws android.database.SQLException If the SQL string is invalid for
     *                                       some reason
     */
    int executeUpdateDelete(String table, SQLiteStatement sqLiteStatement);

    /**
     * Convenience method for deleting rows in the database.
     *
     * @param table       The affected table
     * @param whereClause the optional WHERE clause to apply when deleting.
     *                    Passing null will delete all rows.
     * @param whereArgs   You may include ?s in the where clause, which
     *                    will be replaced by the values from whereArgs. The values
     *                    will be bound as Strings.
     * @return the number of rows affected if a whereClause is passed in, 0
     * otherwise. To remove all rows and get a count pass "1" as the
     * whereClause.
     */
    int delete(String table, String whereClause, String[] whereArgs);


    /**
     * Convenience method for deleting all rows in a table.
     *
     * @param table The affected table
     */
    int delete(String table);


    /**
     * @return A newly started {@link org.hisp.dhis.android.core.data.database.Transaction}
     */
    Transaction beginNewTransaction();

}