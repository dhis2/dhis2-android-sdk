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

package org.hisp.dhis.android.core.arch.db.access.internal.migrations

import android.util.Log
import java.util.ArrayList
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.valuetype.validation.validators.NumberValidatorBase

internal class DatabaseCodeMigration133(private val databaseAdapter: DatabaseAdapter) : DatabaseCodeMigration {
    override fun migrate() {
        val valueTypes = "'NUMBER','INTEGER','INTEGER_POSITIVE','INTEGER_NEGATIVE','INTEGER_ZERO_OR_POSITIVE'"
        val whereClause = WhereClauseBuilder()
            .appendNotKeyStringValue("syncState", "SYNCED")
            .appendInSubQuery(
                "dataElement",
                "SELECT uid FROM DataElement WHERE valueType IN ($valueTypes)"
            )
            .build()

        val query = "SELECT _id, value FROM DataValue WHERE $whereClause"

        val cursor = databaseAdapter.rawQuery(query)

        val dataValues: MutableList<DatabaseCodeMigration133DataValue> = ArrayList(cursor.count)
        cursor.use { c ->
            if (c.count > 0) {
                c.moveToFirst()
                do {
                    val value = DatabaseCodeMigration133DataValue(c.getLong(0), c.getString(1))
                    dataValues.add(value)
                } while (c.moveToNext())
            }
        }

        dataValues.forEach { dataValue ->
            if (dataValue.value?.matches(NumberValidatorBase.HAS_LEADING_ZERO_REGEX) == true) {
                val trimmedValue = DatabaseCodeMigration133Helper.removeLeadingZeros(dataValue.value)
                if (trimmedValue != null) {
                    val updateQuery = "UPDATE DataValue SET value = '$trimmedValue', syncState = 'TO_UPDATE' " +
                        "WHERE _id = ${dataValue.id}"

                    databaseAdapter.execSQL(updateQuery)
                    Log.i("Migration 133:", "Value migrated from ${dataValue.value} to $trimmedValue")
                }
            }
        }
    }
}
