package org.hisp.dhis.android.core

import io.reactivex.Single

interface D2ManagerInstantiate {
    var d2: D2?
    fun instantiateD2(d2Config: D2Configuration): Single<D2?>?

    companion object {
        fun createFromType(type: D2ManagerInstantiatorType,
                           testingConfig: D2TestingConfig) =
            when (type) {
                D2ManagerInstantiatorType.PROD -> D2ManagerInstantiator()
                D2ManagerInstantiatorType.TESTING -> D2ManagerTestingInstantiator(testingConfig)
            }
    }
}