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
package org.hisp.dhis.android.core.user.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStoreImpl
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserTableInfo
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("MagicNumber")
internal class UserStoreImpl(
    databaseAdapter: DatabaseAdapter,
) : UserStore,
    IdentifiableObjectStoreImpl<User>(
        databaseAdapter,
        UserTableInfo.TABLE_INFO,
        BINDER,
        { cursor: Cursor -> User.create(cursor) },
    ) {

    override suspend fun updateIs2faEnabled(twoFactorAuthEnabled: Boolean) {
        selectFirst()?.let { update(it.toBuilder().twoFactorAuthEnabled(twoFactorAuthEnabled).build()) }
    }

    companion object {
        private val BINDER: StatementBinder<User> = object : IdentifiableStatementBinder<User>() {
            override fun bindToStatement(o: User, w: StatementWrapper) {
                super.bindToStatement(o, w)
                w.bind(7, o.birthday())
                w.bind(8, o.education())
                w.bind(9, o.gender())
                w.bind(10, o.jobTitle())
                w.bind(11, o.surname())
                w.bind(12, o.firstName())
                w.bind(13, o.introduction())
                w.bind(14, o.employer())
                w.bind(15, o.interests())
                w.bind(16, o.languages())
                w.bind(17, o.email())
                w.bind(18, o.phoneNumber())
                w.bind(19, o.nationality())
                w.bind(20, o.username())
                w.bind(21, o.twoFactorAuthEnabled())
            }
        }
    }
}
