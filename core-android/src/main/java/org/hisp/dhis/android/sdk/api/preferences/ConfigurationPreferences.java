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

import org.hisp.dhis.java.sdk.common.network.Configuration;
import org.hisp.dhis.java.sdk.common.preferences.IConfigurationPreferences;

import static org.hisp.dhis.java.sdk.utils.Preconditions.isNull;

public class ConfigurationPreferences implements IConfigurationPreferences {
    private static final String CONFIGURATION_PREFERENCES = "preferences:configuration";
    private static final String SERVER_URL_KEY = "key:serverUrl";

    private final SharedPreferences mPrefs;

    public ConfigurationPreferences(Context context) {
        mPrefs = context.getSharedPreferences(CONFIGURATION_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public boolean save(Configuration configuration) {
        isNull(configuration, "configuration must not be null");
        return putString(SERVER_URL_KEY, configuration.getServerUrl());
    }

    @Override
    public Configuration get() {
        String serverUrl = getString(SERVER_URL_KEY);
        return new Configuration(serverUrl);
    }

    @Override
    public boolean clear() {
        return mPrefs.edit().clear().commit();
    }

    private boolean putString(String key, String value) {
        return mPrefs.edit().putString(key, value).commit();
    }

    private String getString(String key) {
        return mPrefs.getString(key, null);
    }
}
