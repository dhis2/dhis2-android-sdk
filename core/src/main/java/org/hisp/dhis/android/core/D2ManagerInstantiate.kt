package org.hisp.dhis.android.core

import io.reactivex.Single

interface D2ManagerInstantiate {
    var d2: D2?
    fun instantiateD2(d2Config: D2Configuration): Single<D2?>?

    companion object {
        fun createFromType(testingConfig: D2TestingConfig?) =
            when (testingConfig) {
                null -> D2ManagerInstantiator()
                else -> D2ManagerTestingInstantiator(testingConfig)
            }
    }
}