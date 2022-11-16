/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core

import android.util.Log
import androidx.annotation.VisibleForTesting
import io.reactivex.Single
import org.hisp.dhis.android.core.D2ConfigurationValidator.validateAndSetDefaultValues
import org.hisp.dhis.android.core.NotClosedObjectsDetector.enableNotClosedObjectsDetection
import org.hisp.dhis.android.core.arch.api.ssl.internal.SSLContextInitializer
import org.hisp.dhis.android.core.arch.d2.internal.D2DIComponent
import org.hisp.dhis.android.core.arch.storage.internal.AndroidInsecureStore
import org.hisp.dhis.android.core.arch.storage.internal.AndroidSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.InsecureStore
import org.hisp.dhis.android.core.arch.storage.internal.SecureStore

/**
 * Helper class that offers static methods to setup and initialize the D2 instance. Also, it ensures that D2 is a
 * singleton across the application.
 */
@Suppress("TooManyFunctions")
object D2Manager {
    private var d2: D2? = null
    private var isTestMode = false
    private var testingSecureStore: SecureStore? = null
    private var testingInsecureStore: InsecureStore? = null
    private var testingServerUrl: String? = null
    private var testingDatabaseName: String? = null
    private var testingUsername: String? = null
    private lateinit var d2DIComponent: D2DIComponent

    /**
     * Returns the D2 instance, given that it has already been initialized using the
     * [D2Manager.instantiateD2] method.
     * @return the D2 instance
     * @throws IllegalStateException if the D2 object wasn't instantiated
     */
    @Throws(IllegalStateException::class)
    @JvmStatic
    fun getD2(): D2 {
        return d2 ?: throw IllegalStateException("D2 is not instantiated yet")
    }

    /**
     * Returns if D2 has already been instantiated using the [D2Manager.instantiateD2] method.
     * @return if D2 has already been instantiated
     */
    @JvmStatic
    fun isD2Instantiated(): Boolean {
        return d2 != null
    }

    /**
     * Instantiates D2 with the provided configuration. If you are not using RxJava,
     * use [D2Manager.blockingInstantiateD2] instead.
     * @param d2Config the configuration
     * @return the D2 instance wrapped in a RxJava Single
     */
    @JvmStatic
    fun instantiateD2(d2Config: D2Configuration): Single<D2> {
        return Single.fromCallable {
            val startTime = System.currentTimeMillis()
            val context = d2Config.context()

            val secureStore = testingSecureStore ?: AndroidSecureStore(context)
            val insecureStore = testingInsecureStore ?: AndroidInsecureStore(context)

            val d2Configuration = validateAndSetDefaultValues(d2Config)
            d2DIComponent = D2DIComponent.create(d2Configuration, secureStore, insecureStore)

            if (isTestMode) {
                enableNotClosedObjectsDetection()
            } else {
                /* SSLContextInitializer, necessary to ensure everything works in Android 4.4 crashes
                 when running the StrictMode above. That's why it's in the else clause */
                SSLContextInitializer.initializeSSLContext()
            }

            val multiUserDatabaseManager = d2DIComponent.multiUserDatabaseManagerForD2Manager()
            multiUserDatabaseManager.applyMigration()

            val credentials = d2DIComponent.credentialsSecureStore().get()

            if (wantToImportDBForExternalTesting()) {
                multiUserDatabaseManager.loadDbForTesting(
                    testingServerUrl,
                    testingDatabaseName,
                    false,
                    testingUsername
                )
            } else {
                multiUserDatabaseManager.loadIfLogged(credentials)
            }

            d2 = D2(d2DIComponent)

            if (credentials != null) {
                val uid = d2!!.userModule().user().blockingGet().uid()
                d2DIComponent.userIdInMemoryStore().set(uid)
            }

            val setUpTime = System.currentTimeMillis() - startTime
            Log.i(D2Manager::class.java.name, "D2 instantiation took " + setUpTime + "ms")

            d2
        }
    }

    /**
     * Instantiates D2 with the provided configuration. This is a blocking method. If you are using RxJava,
     * use [D2Manager.instantiateD2] instead.
     * @param d2Config the configuration
     * @return the D2 instance
     */
    @JvmStatic
    fun blockingInstantiateD2(d2Config: D2Configuration): D2? {
        return instantiateD2(d2Config).blockingGet()
    }

    @JvmStatic
    @VisibleForTesting
    fun setTestMode(testMode: Boolean) {
        isTestMode = testMode
    }

    @JvmStatic
    @VisibleForTesting
    fun setTestingSecureStore(secureStore: SecureStore?) {
        testingSecureStore = secureStore
    }

    @JvmStatic
    @VisibleForTesting
    fun setTestingInsecureStore(insecureStore: InsecureStore?) {
        testingInsecureStore = insecureStore
    }

    @VisibleForTesting
    @JvmStatic
    fun setTestingDatabase(serverUrl: String, databaseName: String, username: String) {
        testingServerUrl = serverUrl
        testingDatabaseName = databaseName
        testingUsername = username
    }

    @VisibleForTesting
    @JvmStatic
    fun removeCredentials() {
        d2DIComponent.credentialsSecureStore().remove()
    }

    @VisibleForTesting
    @JvmStatic
    fun setCredentials(username: String, password: String) {
        if (testingServerUrl.isNullOrEmpty()) {
            throw NoSuchFieldException("No testing Server Url")
        }
        d2DIComponent.credentialsSecureStore().set(Credentials(username, testingServerUrl!!, password, null))
    }

    private fun wantToImportDBForExternalTesting(): Boolean {
        return testingDatabaseName != null && testingUsername != null
    }

    @JvmStatic
    @VisibleForTesting
    fun clear() {
        d2 = null
        testingSecureStore = null
        testingInsecureStore = null
        testingDatabaseName = null
        testingUsername = null
    }

    @JvmStatic
    @VisibleForTesting
    internal fun setD2(d2: D2) {
        this.d2 = d2
    }
}
