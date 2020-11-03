package org.hisp.dhis.android.core

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.Single
import okhttp3.OkHttpClient
import org.hisp.dhis.android.core.arch.db.access.internal.DatabaseAdapterFactory
import org.hisp.dhis.android.core.arch.storage.internal.Credentials
import org.hisp.dhis.android.core.arch.storage.internal.CredentialsSecureStoreImpl
import org.hisp.dhis.android.core.arch.storage.internal.ObjectKeyValueStore
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManagerForD2Manager

class D2ManagerTestingInstantiator(val testConfig: D2TestingConfig) : D2ManagerInstantiate {

    var okHttpClient: OkHttpClient? = null
    override var d2: D2? = null

    @SuppressLint("VisibleForTests")
    override fun instantiateD2(d2Config: D2Configuration): Single<D2?>? {
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

            okHttpClient = OkHttpClientFactory.okHttpClient(d2Configuration, credentialsSecureStore)

            d2 = D2(
                RetrofitFactory.retrofit(okHttpClient),
                databaseAdapter,
                d2Configuration.context(),
                testConfig.secureStore,
                testConfig.insecureStore,
                credentialsSecureStore
            )
            val setUpTime = System.currentTimeMillis() - startTime
            Log.i(D2ManagerTestingInstantiator::class.java.name, "D2 instantiation took " + setUpTime + "ms")
            d2
        }
    }

    private fun wantToImportDBForExternalTesting(testConfig: D2TestingConfig): Boolean {
        return testConfig.databaseName != null && testConfig.username != null
    }
}