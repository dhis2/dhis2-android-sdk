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
package org.hisp.dhis.android.core.user

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadOnlyWithTransformerObjectRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.user.internal.UserRoleChildrenAppender
import org.hisp.dhis.android.core.user.internal.UserStore
import org.hisp.dhis.android.core.user.internal.UserUserCredentialsTransformer
import org.koin.core.annotation.Singleton

@Singleton(binds = [UserCredentialsObjectRepository::class])
class UserCredentialsObjectRepository internal constructor(
    store: UserStore,
    scope: RepositoryScope,
    transformer: UserUserCredentialsTransformer,
) : ReadOnlyObjectRepository<UserCredentials> by ReadOnlyWithTransformerObjectRepositoryImpl(
    store,
    childrenAppenders,
    scope,
    transformer,
) {

    private val cf: FilterConnectorFactory<UserCredentialsObjectRepository> = FilterConnectorFactory(scope) { s ->
        UserCredentialsObjectRepository(store, s, transformer)
    }

    fun withUserRoles(): UserCredentialsObjectRepository {
        return cf.withChild(USER_ROLES)
    }

    internal companion object {
        private const val USER_ROLES = "userRoles"

        val childrenAppenders: ChildrenAppenderGetter<User> = mapOf(
            USER_ROLES to UserRoleChildrenAppender::create,
        )
    }
}
