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
package org.hisp.dhis.android.core.sms.data.localdbrepository.internal

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory

@Suppress("MagicNumber")
internal object SMSOngoingSubmissionStore {

    private val BINDER: StatementBinder<SMSOngoingSubmission> = StatementBinder { o, w ->
        w.bind(1, o.submissionId())
        w.bind(2, o.type())
    }

    private val WHERE_UPDATE_BINDER =
        WhereStatementBinder<SMSOngoingSubmission> { o: SMSOngoingSubmission, w: StatementWrapper ->
            w.bind(3, o.submissionId())
        }

    private val WHERE_DELETE_BINDER =
        WhereStatementBinder<SMSOngoingSubmission> { o: SMSOngoingSubmission, w: StatementWrapper ->
            w.bind(1, o.submissionId())
        }

    @JvmStatic
    fun create(databaseAdapter: DatabaseAdapter): ObjectWithoutUidStore<SMSOngoingSubmission> {
        return StoreFactory.objectWithoutUidStore(
            databaseAdapter,
            SMSOngoingSubmissionTableInfo.TABLE_INFO,
            BINDER,
            WHERE_UPDATE_BINDER,
            WHERE_DELETE_BINDER
        ) { cursor -> SMSOngoingSubmission.create(cursor) }
    }
}
