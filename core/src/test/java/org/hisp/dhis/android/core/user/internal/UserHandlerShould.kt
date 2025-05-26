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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.user.User
import org.hisp.dhis.android.core.user.UserGroup
import org.hisp.dhis.android.core.user.UserRole
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class UserHandlerShould {
    private val userStore: UserStore = mock()
    private val userRoleHandler: UserRoleHandler = mock()
    private val userRoleCollectionCleaner: UserRoleCollectionCleaner = mock()
    private val userGroupHandler: UserGroupHandler = mock()
    private val userGroupCollectionCleaner: UserGroupCollectionCleaner = mock()

    private val userRoles: List<UserRole> = mock()
    private val userGroups: List<UserGroup> = mock()

    private lateinit var user: User

    // object to test
    private lateinit var userHandler: UserHandler

    @Before
    fun setUp() {
        userHandler = UserHandler(
            userStore, userRoleHandler, userRoleCollectionCleaner, userGroupHandler,
            userGroupCollectionCleaner,
        )

        user = User.builder()
            .uid("userUid")
            .username("username")
            .userRoles(userRoles)
            .userGroups(userGroups)
            .build()

        whenever(userStore.updateOrInsert(any())).thenReturn(HandleAction.Insert)
    }

    @Test
    fun extend_identifiable_sync_handler_impl() {
        val genericHandler: IdentifiableHandlerImpl<User> =
            UserHandler(
                userStore,
                userRoleHandler,
                userRoleCollectionCleaner,
                userGroupHandler,
                userGroupCollectionCleaner,
            )

        assertThat(genericHandler).isNotNull()
    }

    @Test
    fun add_username_groups_and_roles_from_credentials() {
        userHandler.handle(user)

        verify(userRoleCollectionCleaner, times(1)).deleteNotPresent(eq(userRoles))
        verify(userRoleHandler, times(1)).handleMany(eq(userRoles))

        verify(userGroupCollectionCleaner, times(1)).deleteNotPresent(eq(userGroups))
        verify(userGroupHandler, times(1)).handleMany(eq(userGroups))
    }
}
