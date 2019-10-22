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

package org.hisp.dhis.android.core;

import android.os.StrictMode;
import android.util.Log;

import org.hisp.dhis.android.BuildConfig;
import org.hisp.dhis.android.core.arch.api.internal.ServerUrlInterceptor;
import org.hisp.dhis.android.core.arch.api.ssl.internal.SSLContextInitializer;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.DbOpenHelper;
import org.hisp.dhis.android.core.arch.db.access.internal.SqLiteDatabaseAdapter;
import org.hisp.dhis.android.core.configuration.Configuration;
import org.hisp.dhis.android.core.configuration.ConfigurationManager;
import org.hisp.dhis.android.core.configuration.ConfigurationManagerFactory;
import org.hisp.dhis.android.core.maintenance.D2Error;

import androidx.annotation.VisibleForTesting;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

public final class D2Manager {

    private static String databaseName = "dhis.db";

    private static D2 d2;
    private static D2Configuration d2Configuration;

    @VisibleForTesting
    static DatabaseAdapter databaseAdapter;

    private D2Manager() {
    }

    public static D2 getD2() throws IllegalStateException {
        if (d2 == null) {
            throw new IllegalStateException("D2 is not instantiated yet");
        } else {
            return d2;
        }
    }

    public static boolean isD2Instantiated() {
        return d2 != null;
    }

    public static Single<D2> instantiateD2(@NonNull D2Configuration d2Config) {
        return Single.fromCallable(() -> {
            setUp(d2Config);

            long startTime = System.currentTimeMillis();

            if (BuildConfig.DEBUG) {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .penaltyLog()
                        .penaltyDeath()
                        .build());
            } else {
            /* SSLContextInitializer, necessary to ensure everything works in Android 4.4 crashes
            when running the StrictMode above. That's why it's in the else clause */
                SSLContextInitializer.initializeSSLContext(d2Configuration.context());
            }

            d2 = new D2(
                    RetrofitFactory.retrofit(OkHttpClientFactory.okHttpClient(d2Configuration, databaseAdapter)),
                    databaseAdapter,
                    d2Configuration.context()
            );

            long setUpTime = System.currentTimeMillis() - startTime;
            Log.i(D2Manager.class.getName(), "D2 instantiation took " + setUpTime + "ms");

            return d2;
        });
    }

    public static D2 blockingInstantiateD2(@NonNull D2Configuration d2Config) {
        return instantiateD2(d2Config).blockingGet();
    }

    private static void setUp(@Nullable D2Configuration d2Config) throws D2Error {
        long startTime = System.currentTimeMillis();
        d2Configuration = D2ConfigurationValidator.validateAndSetDefaultValues(d2Config);
        databaseAdapter = newDatabaseAdapter();

        ConfigurationManager configurationManager = ConfigurationManagerFactory.create(databaseAdapter);
        Configuration configuration = configurationManager.get();

        if (configuration != null) {
            ServerUrlInterceptor.setServerUrl(configuration.serverUrl().toString());
        }

        long setUpTime = System.currentTimeMillis() - startTime;
        Log.i(D2Manager.class.getName(), "Set up took " + setUpTime + "ms");
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
    }
}