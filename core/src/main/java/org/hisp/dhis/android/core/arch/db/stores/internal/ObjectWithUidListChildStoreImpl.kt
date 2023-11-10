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

package org.hisp.dhis.android.core.arch.db.stores.internal

import android.database.AbstractWindowedCursor
import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.cursors.internal.CursorExecutorImpl
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilder
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.LinkTableChildProjection
import org.hisp.dhis.android.core.attribute.AttributeValue
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ObjectWithUidInterface

internal class ObjectWithUidListChildStoreImpl<P : ObjectWithUidInterface, C>(
    private val linkTableChildProjection: LinkTableChildProjection,
    private val databaseAdapter: DatabaseAdapter,
    private val statementBuilder: SQLStatementBuilder,
) : ObjectWithUidListChildStore<P, C> {

    override fun getChildren(p: P): List<AttributeValue> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(linkTableChildProjection.parentColumn, p.uid())
            .build()
        val selectStatement = statementBuilder.selectWhere(whereClause)

        val cursorExecutor = CursorExecutorImpl { cursor: Cursor ->
//            val valueColumnIndex = cursor.getColumnIndex(linkTableChildProjection.childColumn)
            val idColumnIndex = cursor.getColumnIndex(linkTableChildProjection.childColumn)

            val attributeId = ObjectWithUid.create((cursor as AbstractWindowedCursor).getString(idColumnIndex))

            AttributeValue.builder()
                .attribute(attributeId)
                .value("value")
                .build()
        }
        return cursorExecutor.getObjects(databaseAdapter.rawQuery(selectStatement))
    }
}
