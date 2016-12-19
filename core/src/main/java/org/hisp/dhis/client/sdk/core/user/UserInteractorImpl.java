/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.core.user;

import org.hisp.dhis.client.sdk.core.commons.Task;
import org.hisp.dhis.client.sdk.models.user.User;

import java.util.concurrent.Executors;

class UserInteractorImpl implements UserInteractor {
    private final UserStore userStore;
    private final UsersApi usersApi;
    private final UserPreferences userPreferences;

    public UserInteractorImpl(UsersApi usersApi,
            UserStore userStore, UserPreferences userPreferences) {
        this.userStore = userStore;
        this.usersApi = usersApi;
        this.userPreferences = userPreferences;
    }

    @Override
    public UserStore store() {
        return userStore;
    }

    @Override
    public UsersApi api() {
        return usersApi;
    }

    @Override
    public String username() {
        return userPreferences.getUsername();
    }

    @Override
    public String password() {
        return userPreferences.getPassword();
    }

    @Override
    public Task<User> logIn(String username, String password) {
        return new UserLoginTask(Executors.newCachedThreadPool(),
                null, username, password, usersApi, userStore, userPreferences);
    }

    @Override
    public Object logOut() {
        userPreferences.invalidateUser();
        //todo delete all data
        return null;
    }

    @Override
    public boolean isLoggedIn() {
        return userPreferences.isUserConfirmed();
    }
}
