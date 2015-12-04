/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.api.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.hisp.dhis.java.sdk.common.network.UserCredentials;
import org.hisp.dhis.java.sdk.common.preferences.IUserPreferences;

import static org.hisp.dhis.java.sdk.models.utils.Preconditions.isNull;
import static org.hisp.dhis.java.sdk.utils.StringUtils.isEmpty;

public class UserPreferences implements IUserPreferences {
    private static final String USER_CREDENTIALS = "preferences:userCredentials";
    private static final String USERNAME = "key:username";
    private static final String PASSWORD = "key:password";
    private static final String IS_USER_SIGNED_IN = "key:isUserSignedIn";

    private final SharedPreferences mPrefs;

    public UserPreferences(Context context) {
        mPrefs = context.getSharedPreferences(USER_CREDENTIALS, Context.MODE_PRIVATE);
    }

    @Override
    public synchronized boolean save(UserCredentials credentials) {
        isNull(credentials, "credentials must not be null");

        String username = credentials.getUsername();
        String password = credentials.getPassword();

        putString(USERNAME, username);
        putString(PASSWORD, password);

        return true;
    }

    @Override
    public synchronized boolean invalidateUserCredentials() {
        if (!isUserInvalidated()) {
            UserCredentials userCredentials = get();
            UserCredentials invalidatedUserCredentials
                    = new UserCredentials(userCredentials.getUsername(), null);
            save(invalidatedUserCredentials);
            return true;
        }

        return false;
    }

    @Override
    public synchronized boolean isUserSignedIn() {
        UserCredentials credentials = get();
        return !isEmpty(credentials.getUsername()) && !isEmpty(credentials.getPassword());
    }

    @Override
    public synchronized boolean isUserInvalidated() {
        UserCredentials credentials = get();
        return !isEmpty(credentials.getUsername()) && isEmpty(credentials.getPassword());
    }

    @Override
    public synchronized boolean clear() {
        return mPrefs.edit().clear().commit();
    }

    @Override
    public synchronized UserCredentials get() {
        String username = getString(USERNAME);
        String password = getString(PASSWORD);

        return new UserCredentials(username, password);
    }

    private boolean putString(String key, String value) {
        return mPrefs.edit().putString(key, value).commit();
    }

    private String getString(String key) {
        return mPrefs.getString(key, null);
    }
}
