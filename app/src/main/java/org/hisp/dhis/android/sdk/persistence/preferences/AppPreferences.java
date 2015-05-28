/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.persistence.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

/**
 * Created by araz on 27.04.2015.
 */
public final class AppPreferences {
    private static final String APP_PREFERENCES = "preferences:Application";

    private static final String SERVER_URL = "key:serverUrl";
    private static final String USERNAME = "key:userName";

    private final SharedPreferences mPrefs;

    public AppPreferences(Context context) {
        isNull(context, "Context object must not be null");
        mPrefs = context.getSharedPreferences(
                APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void putServerUrl(String url) {
        put(SERVER_URL, url);
    }

    public String getServerUrl() {
        return get(SERVER_URL);
    }

    public void putUserName(String username) {
        put(USERNAME, username);
    }

    public String getUsername() {
        return get(USERNAME);
    }

    public void clear() {
        delete();
    }

    private void put(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
    }

    private String get(String key) {
        return mPrefs.getString(key, null);
    }

    private void delete() {
        mPrefs.edit().clear().apply();
    }
}
