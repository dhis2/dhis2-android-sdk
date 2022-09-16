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
package org.hisp.dhis.android.core.user.internal

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserCredentials
import org.hisp.dhis.android.core.user.UserInternalAccessor
import org.hisp.dhis.android.core.user.UserRole
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserHandlerShould {
    private val userStore: IdentifiableObjectStore<User> = mock()
    private val userCredentialsHandler: Handler<UserCredentials> = mock()
    private val userRoleHandler: Handler<UserRole> = mock()
    private val userRoleCollectionCleaner: CollectionCleaner<UserRole> = mock()
    private val user: User = mock()

    private lateinit var userCredentials: UserCredentials

    // object to test
    private lateinit var userHandler: UserHandler
    @Before
    fun setUp() {
        userHandler = UserHandler(userStore, userCredentialsHandler, userRoleHandler, userRoleCollectionCleaner)
        userCredentials = UserCredentials.builder().build()
        whenever(UserInternalAccessor.accessUserCredentials(user)).doReturn(userCredentials)
        whenever(user.uid()).doReturn("userUid")
        whenever(userStore.updateOrInsert(user)).doReturn(HandleAction.Insert)
    }

    @Test
    fun extend_identifiable_sync_handler_impl() {
        val genericHandler: IdentifiableHandlerImpl<User> =
            UserHandler(userStore, userCredentialsHandler, userRoleHandler, userRoleCollectionCleaner)
    }

    @Test
    fun call_user_credentials_handler() {
        userHandler.handle(user)
        val credentialsWithUser = userCredentials.toBuilder().user(
            ObjectWithUid.create(
                user.uid()
            )
        ).build()
        verify(userCredentialsHandler).handle(credentialsWithUser)
    }
}