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

import android.util.Log;

import androidx.annotation.VisibleForTesting;

import org.hisp.dhis.android.core.arch.api.ssl.internal.SSLContextInitializer;
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory;
import org.hisp.dhis.android.core.arch.storage.internal.AndroidInsecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.AndroidSecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.Credentials;
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStoreImpl;
import org.hisp.dhis.android.core.arch.storage.internal.InsecureStore;
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore;
import org.hisp.dhis.android.core.arch.storage.internal.SecureStore;
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManagerForD2Manager;

import io.reactivex.Single;
import io.reactivex.annotations.NonNull;

/**
 * Helper class that offers static methods to setup and initialize the D2 instance. Also, it ensures that D2 is a
 * singleton across the application.
 */
public final class D2Manager {

    private static D2 d2;
    private static D2Configuration d2Configuration;
    private static DatabaseAdapter databaseAdapter;
    private static boolean isTestMode;
    private static SecureStore testingSecureStore;
    private static InsecureStore testingInsecureStore;
    private static String testingDatabaseName;
    private static String testingUsername;

    private D2Manager() {
    }

    /**
     * Returns the D2 instance, given that it has already been initialized using the
     * {@link D2Manager#instantiateD2(D2Configuration)} method.
     * @return the D2 instance
     * @throws IllegalStateException if the D2 object wasn't instantiated
     */
    public static D2 getD2() throws IllegalStateException {
        if (d2 == null) {
            throw new IllegalStateException("D2 is not instantiated yet");
        } else {
            return d2;
        }
    }

    /**
     * Returns if D2 has already been instantiated using the {@link D2Manager#instantiateD2(D2Configuration)} method.
     * @return if D2 has already been instantiated
     */
    public static boolean isD2Instantiated() {
        return d2 != null;
    }

    /**
     * Instantiates D2 with the provided configuration. If you are not using RxJava,
     * use {@link D2Manager#blockingInstantiateD2(D2Configuration)} instead.
     * @param d2Config the configuration
     * @return the D2 instance wrapped in a RxJava Single
     */
    public static Single<D2> instantiateD2(@NonNull D2Configuration d2Config) {
        return Single.fromCallable(() -> {
            long startTime = System.currentTimeMillis();
            SecureStore secureStore = testingSecureStore == null ? new AndroidSecureStore(d2Config.context())
                    : testingSecureStore;
            InsecureStore insecureStore = testingInsecureStore == null ? new AndroidInsecureStore(d2Config.context())
                    : testingInsecureStore;
            DatabaseAdapterFactory databaseAdapterFactory = DatabaseAdapterFactory.create(d2Config.context(),
                    secureStore);

            d2Configuration = D2ConfigurationValidator.validateAndSetDefaultValues(d2Config);
            databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter();

            if (isTestMode) {
                NotClosedObjectsDetector.enableNotClosedObjectsDetection();
            } else {
                /* SSLContextInitializer, necessary to ensure everything works in Android 4.4 crashes
                 when running the StrictMode above. That's why it's in the else clause */
                SSLContextInitializer.initializeSSLContext(d2Configuration.context());
            }

            ObjectKeyValueStore<Credentials> credentialsSecureStore = new CredentialsSecureStoreImpl(secureStore);
            MultiUserDatabaseManagerForD2Manager multiUserDatabaseManager = MultiUserDatabaseManagerForD2Manager
                    .create(databaseAdapter, d2Config.context(), insecureStore, databaseAdapterFactory);
            if (wantToImportDBForExternalTesting()) {
                multiUserDatabaseManager.loadDbForTesting(testingDatabaseName, false, testingUsername);
            } else {
                multiUserDatabaseManager.loadIfLogged(credentialsSecureStore.get());
            }

            d2 = new D2(
                    RetrofitFactory.retrofit(
                            OkHttpClientFactory.okHttpClient(d2Configuration, credentialsSecureStore)),
                    databaseAdapter,
                    d2Configuration.context(),
                    secureStore,
                    insecureStore,
                    credentialsSecureStore
            );

            long setUpTime = System.currentTimeMillis() - startTime;
            Log.i(D2Manager.class.getName(), "D2 instantiation took " + setUpTime + "ms");

            return d2;
        });
    }

    /**
     * Instantiates D2 with the provided configuration. This is a blocking method. If you are using RxJava,
     * use {@link D2Manager#instantiateD2(D2Configuration)} instead.
     * @param d2Config the configuration
     * @return the D2 instance
     */
    public static D2 blockingInstantiateD2(@NonNull D2Configuration d2Config) {
        return instantiateD2(d2Config).blockingGet();
    }

    @VisibleForTesting
    static void setTestMode(boolean testMode) {
        isTestMode = testMode;
    }

    @VisibleForTesting
    static void setTestingSecureStore(SecureStore secureStore) {
        testingSecureStore = secureStore;
    }

    @VisibleForTesting
    static void setTestingInsecureStore(InsecureStore insecureStore) {
        testingInsecureStore = insecureStore;
    }

    @VisibleForTesting
    public static void setTestingDatabase(String databaseName, String username) {
        testingDatabaseName = databaseName;
        testingUsername = username;
    }

    private static boolean wantToImportDBForExternalTesting() {
        return testingDatabaseName != null && testingUsername != null;
    }

    @VisibleForTesting
    static void clear() {
        d2Configuration = null;
        d2 = null;
        databaseAdapter =  null;
        testingSecureStore = null;
        testingInsecureStore = null;
        testingDatabaseName = null;
        testingUsername = null;
    }
}