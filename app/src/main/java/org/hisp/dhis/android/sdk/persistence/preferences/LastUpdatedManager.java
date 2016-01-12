/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.persistence.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.sdk.network.Credentials;
import org.hisp.dhis.android.sdk.network.Session;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

public final class LastUpdatedManager {
    private static final String PREFERENCES = "preferences:Session";
    private static final String SERVER_URI = "key:Uri";
    private static final String USERNAME = "key:username";
    private static final String PASSWORD = "key:password";

    private static LastUpdatedManager mLastUpdatedManager;
    private SharedPreferences mPrefs;

    private LastUpdatedManager(Context context) {
        mPrefs = context.getSharedPreferences(PREFERENCES,
                Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        isNull(context, "Context object must not be null");
        mLastUpdatedManager = new LastUpdatedManager(context);
    }

    public static LastUpdatedManager getInstance() {
        return mLastUpdatedManager;
    }

    public Session get() {
        String serverUrlString = getString(SERVER_URI);
        String userNameString = getString(USERNAME);
        String passwordString = getString(PASSWORD);

        HttpUrl serverUrl = null;
        if (serverUrlString != null) {
            serverUrl = HttpUrl.parse(serverUrlString);
        }

        Credentials credentials = null;
        if (userNameString != null && passwordString != null) {
            credentials = new Credentials(
                    userNameString, passwordString
            );
        }
        return new Session(serverUrl, credentials);
    }

    public void put(Session session) {
        isNull(session, "Session object must not be null");
        HttpUrl serverUrl = session.getServerUrl();
        Credentials credentials = session.getCredentials();

        String url = null;
        String username = null;
        String password = null;

        if (serverUrl != null) {
            url = serverUrl.toString();
        }

        if (credentials != null) {
            username = credentials.getUsername();
            password = credentials.getPassword();
        }

        putString(SERVER_URI, url);
        putString(USERNAME, username);
        putString(PASSWORD, password);
    }

    public void delete() {
        mPrefs.edit().clear().apply();
    }

    public void invalidate() {
        putString(USERNAME, null);
        putString(PASSWORD, null);
    }

    public boolean isInvalid() {
        return getString(USERNAME) == null &&
                getString(PASSWORD) == null;
    }

    private void putString(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
    }

    private String getString(String key) {
        return mPrefs.getString(key, null);
    }
}
