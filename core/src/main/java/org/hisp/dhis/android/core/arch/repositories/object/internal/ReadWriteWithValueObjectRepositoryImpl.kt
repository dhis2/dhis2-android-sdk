/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.arch.repositories.`object`.internal

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadWriteObjectRepository
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent

open class ReadWriteWithValueObjectRepositoryImpl<M : CoreObject, R : ReadOnlyObjectRepository<M>>
internal constructor(
    private val store: ObjectWithoutUidStore<M>,
    private val databaseAdapter: DatabaseAdapter,
    childrenAppenders: ChildrenAppenderGetter<M>,
    scope: RepositoryScope,
    repositoryFactory: ObjectRepositoryFactory<R>,
) : ReadOnlyOneObjectRepositoryImpl<M, R>(store, childrenAppenders, scope, repositoryFactory),
    ReadWriteObjectRepository<M> {

    /**
     * Removes the object in scope in a suspend way. See the implementation JavaDoc for details on how deletion
     * is performed. It throws an exception if the object doesn't exist.
     * @throws D2Error if any errors occur, including when the object doesn't exist.
     */
    @Throws(D2Error::class)
    override suspend fun suspendDelete() {
        databaseAdapter.withTransaction {
            getWithoutChildrenInternal()?.let { suspendDelete(it) }
        }
    }

    /**
     * Removes the object in scope in a suspend way. See the implementation JavaDoc for details on how deletion
     * is performed. Unlike [.suspendDelete], it doesn't throw an exception if the object doesn't exist.
     */
    override suspend fun suspendDeleteIfExist() {
        try {
            suspendDelete()
        } catch (d2Error: D2Error) {
            Log.v(ReadWriteWithValueObjectRepositoryImpl::class.java.canonicalName, d2Error.errorDescription())
        }
    }

    @Throws(D2Error::class)
    @Suppress("TooGenericExceptionCaught")
    protected open suspend fun suspendDelete(m: M) {
        try {
            databaseAdapter.withTransaction {
                store.deleteWhere(m)
                propagateState(m)
            }
        } catch (e: Exception) {
            throw D2Error
                .builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.UNEXPECTED)
                .errorDescription("Unexpected exception on value delete")
                .originalException(e)
                .build()
        }
    }

    @Throws(D2Error::class)
    protected suspend fun setObject(m: M) {
        inValueTransaction { writeObject(m) }
    }

    private suspend fun writeObject(m: M) {
        store.updateOrInsertWhere(m)
        propagateState(m)
    }

    @Suppress("TooGenericExceptionCaught")
    private suspend fun inValueTransaction(block: suspend () -> Unit) {
        try {
            databaseAdapter.withTransaction { block() }
        } catch (e: D2Error) {
            throw e
        } catch (e: Exception) {
            throw D2Error
                .builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.VALUE_CANT_BE_SET)
                .errorDescription("Value can't be set")
                .originalException(e)
                .build()
        }
    }

    protected fun <V> updateIfChanged(
        newValue: V?,
        propertyGetter: (M?) -> V?,
        updater: (M?, V?) -> M,
    ): org.hisp.dhis.android.core.common.Unit {
        return runBlocking(Dispatchers.IO) { updateIfChangedInternal(newValue, propertyGetter, updater) }
    }

    protected suspend fun <V> updateIfChangedInternal(
        newValue: V?,
        propertyGetter: (M?) -> V?,
        updater: (M?, V?) -> M,
    ): org.hisp.dhis.android.core.common.Unit {
        inValueTransaction {
            val obj = getWithoutChildrenInternal()
            val currentValue = propertyGetter(obj)

            if (currentValue != newValue) {
                writeObject(updater(obj, newValue))
            }
        }
        return org.hisp.dhis.android.core.common.Unit()
    }

    protected open suspend fun propagateState(m: M?) {
        // Method is empty because is the default action.
    }
}
