/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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
package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.internal.SyncHandler;
import org.hisp.dhis.android.core.common.CollectionCleaner;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class UserHandler extends IdentifiableSyncHandlerImpl<User> {
    private final SyncHandler<UserCredentials> userCredentialsHandler;
    private final SyncHandler<UserRole> userRoleHandler;
    private final CollectionCleaner<UserRole> userRoleCollectionCleaner;

    @Inject
    UserHandler(IdentifiableObjectStore<User> userStore,
                SyncHandler<UserCredentials> userCredentialsHandler,
                SyncHandler<UserRole> userRoleHandler,
                CollectionCleaner<UserRole> userRoleCollectionCleaner) {
        super(userStore);
        this.userCredentialsHandler = userCredentialsHandler;
        this.userRoleHandler = userRoleHandler;
        this.userRoleCollectionCleaner = userRoleCollectionCleaner;
    }

    @Override
    protected void afterObjectHandled(User user, HandleAction action) {
        UserCredentials credentials = user.userCredentials();
        if (credentials != null) {
            UserCredentials credentialsWithUser = credentials.toBuilder().user(user).build();
            userCredentialsHandler.handle(credentialsWithUser);

            userRoleCollectionCleaner.deleteNotPresent(credentialsWithUser.userRoles());
            userRoleHandler.handleMany(credentialsWithUser.userRoles());
        }
    }
}