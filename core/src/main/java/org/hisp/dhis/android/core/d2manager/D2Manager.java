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

package org.hisp.dhis.android.core.d2manager;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.configuration.Configuration;
import org.hisp.dhis.android.core.configuration.ConfigurationManager;
import org.hisp.dhis.android.core.configuration.ConfigurationManagerFactory;
import org.hisp.dhis.android.core.configuration.ServerUrlParser;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.database.SqLiteDatabaseAdapter;

import androidx.annotation.VisibleForTesting;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import okhttp3.HttpUrl;

public final class D2Manager {

    private static String databaseName = "dhis.db";

    private static D2 d2;
    private static D2Configuration d2Configuration;
    static DatabaseAdapter databaseAdapter;
    private static ConfigurationManager configurationManager;

    private D2Manager() {
    }

    public static void setD2Configuration(@Nullable D2Configuration d2Config) {
        if (d2Configuration != null) {
            throw new IllegalArgumentException("D2 Configuration already set");
        }
        d2Configuration = d2Config;
        databaseAdapter = newDatabaseAdapter();
        configurationManager = ConfigurationManagerFactory.create(databaseAdapter);
    }

    @VisibleForTesting
    static void setDatabaseName(String dbName) {
        databaseName = dbName;
    }

    public static boolean isServerUrlSet() {
        return configurationManager != null && configurationManager.get() != null;
    }

    public static void setServerUrl(@NonNull String url) {
        HttpUrl httpUrl = ServerUrlParser.parse(url);
        Configuration configuration = Configuration.forServerUrl(httpUrl);
        configurationManager.configure(configuration);
        instantiateD2(configuration);
    }

    public static D2 getD2() throws IllegalStateException {
        if (d2 == null) {
            if (d2Configuration == null) {
                throw new IllegalArgumentException("D2 Configuration is not yet set");
            } else {
                Configuration configuration = configurationManager.get();
                if (configuration == null) {
                    throw new IllegalStateException("Server URL is not configured");
                } else {
                    instantiateD2(configuration);
                    return d2;
                }
            }
        } else {
            return d2;
        }
    }

    private static void instantiateD2(Configuration configuration) {
        d2 = new D2.Builder()
                .configuration(configuration)
                .databaseAdapter(databaseAdapter)
                .okHttpClient(OkHttpClientFactory.okHttpClient(d2Configuration, databaseAdapter))
                .context(d2Configuration.context())
                .build();
    }

    private static DatabaseAdapter newDatabaseAdapter() {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(d2Configuration.context(), databaseName);
        return new SqLiteDatabaseAdapter(dbOpenHelper);
    }

    @VisibleForTesting
    static void clear()  {
        d2Configuration = null;
        d2 = null;
        databaseAdapter =  null;
        configurationManager = null;
    }
}