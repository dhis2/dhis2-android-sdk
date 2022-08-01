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
package org.hisp.dhis.android.core.arch.cleaners.internal

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.commaSeparatedUidsWithSingleQuotationMarks
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.mapByParentUid
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.ObjectWithUidInterface

internal class SubCollectionCleanerImpl<P : ObjectWithUidInterface>(
    private val tableName: String,
    private val parentColumn: String,
    private val databaseAdapter: DatabaseAdapter,
    private val keyExtractor: Transformer<P, String>
) : SubCollectionCleaner<P> {

    override fun deleteNotPresent(objects: Collection<P>?): Boolean {
        if (objects == null) {
            return false
        }
        val subLists: Map<String, List<P>> = mapByParentUid(objects, keyExtractor)
        var result = false
        for ((key, value) in subLists) {
            val childrenUids = commaSeparatedUidsWithSingleQuotationMarks(value)
            val clause = (
                parentColumn + "='" + key + "'" +
                    " AND " +
                    IdentifiableColumns.UID + " NOT IN (" + childrenUids + ");"
                )
            result = result || databaseAdapter.delete(tableName, clause, null) > 0
        }
        return result
    }
}
