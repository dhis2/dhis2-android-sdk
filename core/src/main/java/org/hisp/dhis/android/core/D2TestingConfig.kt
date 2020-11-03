package org.hisp.dhis.android.core

import androidx.annotation.VisibleForTesting
import org.hisp.dhis.android.core.arch.storage.internal.InMemorySecureStore
import org.hisp.dhis.android.core.arch.storage.internal.InMemoryUnsecureStore
import org.hisp.dhis.android.core.arch.storage.internal.InsecureStore
import org.hisp.dhis.android.core.arch.storage.internal.SecureStore

@VisibleForTesting
data class D2TestingConfig(
    val databaseName: String? = null,
    val username: String? = null,
    val secureStore: SecureStore = InMemorySecureStore(),
    val insecureStore: InsecureStore = InMemoryUnsecureStore()
)