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

import android.util.Log;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.configuration.Configuration;
import org.hisp.dhis.android.core.configuration.ConfigurationManager;
import org.hisp.dhis.android.core.configuration.ConfigurationManagerFactory;
import org.hisp.dhis.android.core.configuration.ServerUrlParser;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.database.SqLiteDatabaseAdapter;

import androidx.annotation.VisibleForTesting;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import okhttp3.HttpUrl;

public final class D2Manager {

    private static String databaseName = "dhis.db";

    private static D2 d2;
    private static D2Configuration d2Configuration;
    static DatabaseAdapter databaseAdapter;
    private static ConfigurationManager configurationManager;
    private static Configuration configuration;

    private D2Manager() {
    }

    public static Completable setUp(@Nullable D2Configuration d2Config) {
        return Completable.fromAction(() -> {
            long startTime = System.currentTimeMillis();
            d2Configuration = d2Config;
            databaseAdapter = newDatabaseAdapter();
            configurationManager = ConfigurationManagerFactory.create(databaseAdapter);
            configuration = configurationManager.get();

            long setUpTime = System.currentTimeMillis() - startTime;
            Log.i(D2Manager.class.getName(), "Set up took " + setUpTime + "ms");
        });
    }

    public static boolean isServerUrlSet() {
        return configuration != null;
    }

    public static Completable setServerUrl(@NonNull String url) {
        return Completable.fromAction(() -> {
            ensureSetUp();
            HttpUrl httpUrl = ServerUrlParser.parse(url);
            configuration = Configuration.forServerUrl(httpUrl);
            configurationManager.configure(configuration);
        });
    }

    public static D2 getD2() throws IllegalStateException {
        if (d2 == null) {
            throw new IllegalStateException("D2 is not instantiated yet");
        } else {
            return d2;
        }
    }

    private static void ensureSetUp() {
        if (d2Configuration == null) {
            throw new IllegalStateException("You have to setUp first");
        }
    }

    private static void ensureServerUrl() {
        if (configuration == null) {
            throw new IllegalStateException("You have to configure server URL first");
        }
    }

    private static boolean isD2Instantiated() {
        return d2 != null;
    }

    public static Single<D2> instantiateD2() {
        return Single.fromCallable(() -> {
            ensureSetUp();
            ensureServerUrl();

            long startTime = System.currentTimeMillis();

            d2 = new D2.Builder()
                    .configuration(configuration)
                    .databaseAdapter(databaseAdapter)
                    .okHttpClient(OkHttpClientFactory.okHttpClient(d2Configuration, databaseAdapter))
                    .context(d2Configuration.context())
                    .build();


            long setUpTime = System.currentTimeMillis() - startTime;
            Log.i(D2Manager.class.getName(), "D2 instantiation took " + setUpTime + "ms");

            return d2;
        });
    }

    private static DatabaseAdapter newDatabaseAdapter() {
        DbOpenHelper dbOpenHelper = new DbOpenHelper(d2Configuration.context(), databaseName);
        return new SqLiteDatabaseAdapter(dbOpenHelper);
    }

    @VisibleForTesting
    static void setDatabaseName(String dbName) {
        databaseName = dbName;
    }

    @VisibleForTesting
    static void clear() {
        d2Configuration = null;
        d2 = null;
        databaseAdapter =  null;
        configurationManager = null;
        configuration = null;
    }
}