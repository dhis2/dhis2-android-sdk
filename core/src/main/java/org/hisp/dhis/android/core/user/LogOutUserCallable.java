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

package org.hisp.dhis.android.core.user;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.concurrent.Callable;

public class LogOutUserCallable implements Callable<Unit> {

    @NonNull
    private final ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore;

    LogOutUserCallable(@NonNull ObjectWithoutUidStore<AuthenticatedUserModel> authenticatedUserStore) {
        this.authenticatedUserStore = authenticatedUserStore;
    }

    @Override
    public Unit call() throws Exception {
        AuthenticatedUserModel existingUser = this.authenticatedUserStore.selectFirst();

        if (existingUser == null) {
            throw D2CallException.builder()
                    .errorCode(D2ErrorCode.NO_AUTHENTICATED_USER)
                    .errorDescription("There is not any authenticated user")
                    .isHttpError(false)
                    .build();
        }

        AuthenticatedUserModel loggedOutUser =
                AuthenticatedUserModel.builder()
                .user(existingUser.user())
                .hash(existingUser.hash())
                .build();

        authenticatedUserStore.updateOrInsertWhere(loggedOutUser);
        return new Unit();
    }

    public static LogOutUserCallable create(DatabaseAdapter databaseAdapter) {
        return new LogOutUserCallable(
                AuthenticatedUserStore.create(databaseAdapter)
        );
    }
}
