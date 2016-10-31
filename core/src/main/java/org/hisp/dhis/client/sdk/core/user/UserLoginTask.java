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

import org.hisp.dhis.client.sdk.core.commons.AbsTask;
import org.hisp.dhis.client.sdk.models.user.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Response;

class UserLoginTask extends AbsTask<User> {
    private final String username;
    private final String password;

    private final UsersApi usersApi;
    private final UserStore userStore;
    private final UserPreferences userPreferences;

    // retrofit call
    private Call<User> userCall;

    UserLoginTask(Executor executor, Executor callbackExecutor, String username, String password,
            UsersApi usersApi, UserStore userStore, UserPreferences preferences) {
        super(executor, callbackExecutor);

        this.username = username;
        this.password = password;
        this.usersApi = usersApi;
        this.userStore = userStore;
        this.userPreferences = preferences;
    }

    @Override
    public User executeTask() throws IOException {
        if (isCanceled()) {
            return null;
        }

        if (userPreferences.isUserConfirmed()) {
            throw new IllegalArgumentException("User is already signed in");
        }

        User user = null;
        if (!isCanceled()) {
            // new username and password will be indirectly
            // used by authentication interceptor
            userPreferences.save(username, password);

            // query parameters
            Map<String, String> query = new HashMap<>();
//            query.put("fields", "id,displayName");
            query.put("fields", "*,userCredentials[*,userRoles[*]]");

            // call /api/me/ endpoint in order to make
            // sure that credentials are correct
            userCall = usersApi.me(query);
            Response<User> userResponse = userCall.execute();

            if (userResponse != null && userResponse.isSuccessful()) {
                user = userResponse.body();
            }
        }

        userStore.insert(user);

        return user;
    }

    @Override
    public void cancelTask() {
        if (userCall != null) {
            userCall.cancel();
        }
    }
}
