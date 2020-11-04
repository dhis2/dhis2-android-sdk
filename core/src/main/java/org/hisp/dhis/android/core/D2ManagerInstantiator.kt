package org.hisp.dhis.android.core

import android.util.Log
import io.reactivex.Single
import org.hisp.dhis.android.core.D2ManagerInstantiate.Companion.d2
import org.hisp.dhis.android.core.arch.api.ssl.internal.SSLContextInitializer
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.storage.internal.AndroidInsecureStore
import org.hisp.dhis.android.core.arch.storage.internal.AndroidSecureStore
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStoreImpl
import org.hisp.dhis.android.core.arch.storage.internal.InsecureStore
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import org.hisp.dhis.android.core.arch.storage.internal.SecureStore
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManagerForD2Manager

class D2ManagerInstantiator() : D2ManagerInstantiate {

    override fun instantiateD2(d2Config: D2Configuration): Single<D2?>? {
        return Single.fromCallable {
            val startTime = System.currentTimeMillis()
            val secureStore: SecureStore = AndroidSecureStore(d2Config.context())
            val insecureStore: InsecureStore = AndroidInsecureStore(d2Config.context())
            val databaseAdapterFactory = DatabaseAdapterFactory.create(
                d2Config.context(),
                secureStore
            )
            val d2Configuration = D2ConfigurationValidator.validateAndSetDefaultValues(d2Config)
            val databaseAdapter = databaseAdapterFactory.newParentDatabaseAdapter()

            /* SSLContextInitializer, necessary to ensure everything works in Android 4.4 crashes
            when running the StrictMode above. That's why it's in the else clause */
            SSLContextInitializer.initializeSSLContext(d2Configuration.context())

            val credentialsSecureStore: ObjectKeyValueStore<Credentials> =
                CredentialsSecureStoreImpl(secureStore)
            val multiUserDatabaseManager = MultiUserDatabaseManagerForD2Manager
                .create(databaseAdapter, d2Config.context(), insecureStore, databaseAdapterFactory)
            multiUserDatabaseManager.loadIfLogged(credentialsSecureStore.get())

            d2 = D2(
                RetrofitFactory.retrofit(
                    OkHttpClientFactory.okHttpClient(d2Config, credentialsSecureStore)
                ),
                databaseAdapter,
                d2Configuration.context(),
                secureStore,
                insecureStore,
                credentialsSecureStore
            )
            val setUpTime = System.currentTimeMillis() - startTime
            Log.i(D2Manager::class.java.name, "D2 instantiation took " + setUpTime + "ms")

            d2
        }
    }
}