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

package org.hisp.dhis.android.core.utils;

import android.database.sqlite.SQLiteStatement;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;

import java.util.Date;

/**
 * Provides helper functions to handle null checks and type conversions.
 */
public final class StoreUtils {

    private StoreUtils() {
        // no instances
    }

    /**
     * Handle if String argument is null and bind it using .bindNull() if so.
     * A helper function to abstract/clean up boilerplate if/else bloat..
     *
     * @param sqLiteStatement
     * @param index
     * @param arg
     */
    public static void sqLiteBind(SQLiteStatement sqLiteStatement, int index, String arg) {
        if (arg == null) {
            sqLiteStatement.bindNull(index);
        } else {
            sqLiteStatement.bindString(index, arg);
        }
    }

    /**
     * Handle if Boolean argument is null and bind it using .bindNull() if so.
     * A helper function to abstract/clean up boilerplate if/else bloat...
     * Also convet the Boolean to Long...
     *
     * @param sqLiteStatement
     * @param index
     * @param arg
     */
    public static void sqLiteBind(SQLiteStatement sqLiteStatement, int index, Boolean arg) {
        if (arg == null) {
            sqLiteStatement.bindNull(index);
        } else {
            sqLiteStatement.bindLong(index, arg ? 1 : 0);
        }
    }

    /**
     * Handle if Integer argument is null and bind it using .bindNull() if so.
     * A helper function to abstract/clean up boilerplate if/else bloat..
     *
     * @param sqLiteStatement
     * @param index
     * @param arg
     */
    public static void sqLiteBind(SQLiteStatement sqLiteStatement, int index, Integer arg) {
        if (arg == null) {
            sqLiteStatement.bindNull(index);
        } else {
            sqLiteStatement.bindLong(index, arg);
        }
    }

    /**
     * Handle if Date argument is null and bind it using .bindNull() if so.
     * A helper function to abstract/clean up boilerplate if/else bloat..
     *
     * @param sqLiteStatement
     * @param index
     * @param arg
     */
    public static void sqLiteBind(SQLiteStatement sqLiteStatement, int index, Date arg) {
        if (arg == null) {
            sqLiteStatement.bindNull(index);
        } else {
            sqLiteStatement.bindString(index, BaseIdentifiableObject.DATE_FORMAT.format(arg));
        }
    }

    /**
     * Handle if Enum argument is null and bind it using .bindNull() if so.
     * A helper function to abstract/clean up boilerplate if/else bloat..
     *
     * @param sqLiteStatement
     * @param index
     * @param arg
     */
    public static void sqLiteBind(SQLiteStatement sqLiteStatement, int index, Enum arg) {
        if (arg == null) {
            sqLiteStatement.bindNull(index);
        } else {
            sqLiteStatement.bindString(index, arg.toString());
        }
    }

    /**
     * Handle if Double argument is null and bind it using .bindNull() if so.
     * A helper function to abstract/clean up boilerplate if/else bloat..
     *
     * @param sqLiteStatement
     * @param index
     * @param arg
     */
    public static void sqLiteBind(SQLiteStatement sqLiteStatement, int index, Double arg) {
        if (arg == null) {
            sqLiteStatement.bindNull(index);
        } else {
            sqLiteStatement.bindDouble(index, arg);
        }
    }

    /**
     * Handle if Long argument is null and bind it using .bindNull() if so.
     * A helper function to abstract/clean up boilerplate if/else bloat..
     *
     * @param sqLiteStatement
     * @param index
     * @param arg
     */
    public static void sqLiteBind(SQLiteStatement sqLiteStatement, int index, Long arg) {
        if (arg == null) {
            sqLiteStatement.bindNull(index);
        } else {
            sqLiteStatement.bindLong(index, arg);
        }
    }
}
