package org.hisp.dhis.android.core

import io.reactivex.Single

interface D2ManagerInstantiate {
    fun instantiateD2(d2Config: D2Configuration): Single<D2?>?

    companion object {
        var d2: D2? = null
        fun createFrom(testingConfig: D2TestingConfig?) =
            testingConfig?.let { D2ManagerTestingInstantiator(it) } ?: D2ManagerInstantiator()
    }
}