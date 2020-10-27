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
package org.hisp.dhis.android.core

import android.util.Log
import androidx.annotation.VisibleForTesting
import io.reactivex.Single
import okhttp3.OkHttpClient
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.storage.internal.*
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManagerForD2Manager

@VisibleForTesting
data class D2TestingConfig(
    val databaseName: String? = null,
    val username: String? = null,
    val okHttpClient: OkHttpClient? = null,
    val secureStore: SecureStore = InMemorySecureStore(),
    val insecureStore: InsecureStore = InMemoryUnsecureStore()
)

/**
 * Helper class that offers static methods to setup and initialize the D2 instance. Testing version of the class
 * [D2Manager]. It ensures that D2 is a singleton across the application.
 */
@VisibleForTesting
object TestingD2Manager {
    private var d2: D2? = null

    /**
     * Returns the D2 instance, given that it has already been initialized using the
     * [TestingD2Manager.instantiateD2] method.
     * @return the D2 instance
     * @throws IllegalStateException if the D2 object wasn't instantiated
     */
    @Throws(IllegalStateException::class)
    @JvmStatic
    fun getD2(): D2 {
        return if (d2 == null) {
            throw IllegalStateException("D2 is not instantiated yet")
        } else {
            d2!!
        }
    }

    /**
     * Returns if D2 has already been instantiated using the [TestingD2Manager.instantiateD2] method.
     * @return if D2 has already been instantiated
     */
    @JvmStatic
    fun isD2Instantiated(): Boolean {
        return d2 != null
    }

    /**
     * Instantiates D2 with the provided configuration. If you are not using RxJava,
     * use [TestingD2Manager.blockingInstantiateD2] instead.
     * @param d2Config the configuration
     * @return the D2 instance wrapped in a RxJava Single
     */
    @JvmStatic
    fun instantiateD2(d2Config: D2Configuration, testConfig: D2TestingConfig): Single<D2> {
        return Single.fromCallable {
            val startTime = System.currentTimeMillis()
            val databaseAdapterFactory = DatabaseAdapterFactory.create(
                d2Config.context(),
                testConfig.secureStore
            )
            val d2Configuration = D2ConfigurationValidator.validateAndSetDefaultValues(d2Config)
            val databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter()
            NotClosedObjectsDetector.enableNotClosedObjectsDetection()
            val credentialsSecureStore: ObjectKeyValueStore<Credentials> =
                CredentialsSecureStoreImpl(testConfig.secureStore)
            val multiUserDatabaseManager = MultiUserDatabaseManagerForD2Manager
                .create(databaseAdapter, d2Config.context(), testConfig.insecureStore, databaseAdapterFactory)
            if (wantToImportDBForExternalTesting(testConfig)) {
                multiUserDatabaseManager.loadDbForTesting(testConfig.databaseName, false, testConfig.username)
            } else {
                multiUserDatabaseManager.loadIfLogged(credentialsSecureStore.get())
            }

            val okHttpClient = testConfig.okHttpClient
                ?: OkHttpClientFactory.okHttpClient(d2Configuration, credentialsSecureStore)
            d2 = D2(
                RetrofitFactory.retrofit(okHttpClient),
                databaseAdapter,
                d2Configuration.context(),
                testConfig.secureStore,
                testConfig.insecureStore,
                credentialsSecureStore
            )
            val setUpTime = System.currentTimeMillis() - startTime
            Log.i(TestingD2Manager::class.java.name, "D2 instantiation took " + setUpTime + "ms")
            d2
        }
    }

    /**
     * Instantiates D2 with the provided configuration. This is a blocking method. If you are using RxJava,
     * use [TestingD2Manager.instantiateD2] instead.
     * @param d2Config the configuration
     * @return the D2 instance
     */
    @JvmStatic
    fun blockingInstantiateD2(d2Config: D2Configuration, testConfig: D2TestingConfig): D2 {
        return instantiateD2(d2Config, testConfig).blockingGet()
    }

    private fun wantToImportDBForExternalTesting(testConfig: D2TestingConfig): Boolean {
        return testConfig.databaseName != null && testConfig.username != null
    }
}
