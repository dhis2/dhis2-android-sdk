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
import io.reactivex.Completable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.rxCompletable
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadWriteObjectRepository
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent

@Suppress("TooManyFunctions")
open class ReadWriteWithValueObjectRepositoryImpl<M : CoreObject, R : ReadOnlyObjectRepository<M>>
internal constructor(
    private val store: ObjectWithoutUidStore<M>,
    childrenAppenders: ChildrenAppenderGetter<M>,
    scope: RepositoryScope,
    repositoryFactory: ObjectRepositoryFactory<R>,
) : ReadOnlyOneObjectRepositoryImpl<M, R>(store, childrenAppenders, scope, repositoryFactory),
    ReadWriteObjectRepository<M> {

    /**
     * Removes the object in scope in an asynchronous way. It removes the value in the database and propagates
     * the changes to modify the [DataObject.syncState] of the parent, so it's updated in the server in
     * the next upload.
     * It returns a `Completable` that completes as soon as the object is deleted in the database.
     * The `Completable` fails if the object doesn't exist.
     * @return the `Completable` which notifies the completion
     */
    override fun delete(): Completable {
        return rxCompletable { deleteInternal() }
    }

    /**
     * Removes the object in scope in a synchronous way. It removes the value in the database and propagates
     * the changes to modify the [DataObject.syncState] of the parent, so it's updated in the server in
     * the next upload.
     * It blocks the thread and finishes as soon as the object is deleted in the database.
     *
     * Important: this is a blocking method and it should not be executed in the main thread. Consider the
     * asynchronous version [.delete].
     *
     * It throws an exception if the object doesn't exist.
     */
    @Throws(D2Error::class)
    override fun blockingDelete() {
        runBlocking { deleteInternal() }
    }

    @Throws(D2Error::class)
    protected open suspend fun deleteInternal() {
        getWithoutChildrenInternal()?.let { delete(it) }
//        getWithoutChildrenInternal()?.let { deleteInternal(it) }
    }

    /**
     * Removes the object in scope in an asynchronous way. It removes the value in the database and propagates
     * the changes to modify the [DataObject.syncState] of the parent, so it's updated in the server in
     * the next upload.
     * It returns a `Completable` that completes as soon as the object is deleted in the database.
     * Unlike [.delete], it doesn't throw an exception if the object doesn't exist.
     * It returns a `Completable` that completes as soon as the object is deleted in the database.
     * @return the `Completable` which notifies the completion
     */
    override fun deleteIfExist(): Completable {
        return rxCompletable { deleteIfExistInternal() }
    }

    /**
     * Removes the object in scope in a synchronous way. It removes the value in the database and propagates
     * the changes to modify the [DataObject.syncState] of the parent, so it's updated in the server in
     * the next upload.
     * Unlike [.blockingDelete], it doesn't throw an exception if the object doesn't exist.
     * It blocks the thread and finishes as soon as the object is deleted in the database.
     *
     * Important: this is a blocking method and it should not be executed in the main thread. Consider the
     * asynchronous version [.deleteIfExist].
     */
    override fun blockingDeleteIfExist() {
        runBlocking { deleteIfExistInternal() }
    }

    protected suspend fun deleteIfExistInternal() {
        try {
            deleteInternal()
        } catch (d2Error: D2Error) {
            Log.v(ReadWriteWithValueObjectRepositoryImpl::class.java.canonicalName, d2Error.errorDescription())
        }
    }

    @Throws(D2Error::class)
    @Suppress("TooGenericExceptionCaught")
    protected open fun delete(m: M) {
        runBlocking { deleteInternal(m) }
    }

    @Throws(D2Error::class)
    @Suppress("TooGenericExceptionCaught")
    protected open suspend fun deleteInternal(m: M) { // sdfsdf
        try {
            store.deleteWhere(m)
            propagateState(m)
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
    @Suppress("TooGenericExceptionCaught")
    protected suspend fun setObject(m: M) {
        try {
            store.updateOrInsertWhere(m)
            propagateState(m)
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

    protected inline fun <V> updateIfChanged(
        newValue: V?,
        crossinline propertyGetter: (M?) -> V?,
        crossinline updater: (M?, V?) -> M,
    ): org.hisp.dhis.android.core.common.Unit {
        return runBlocking { updateIfChangedInternal(newValue, propertyGetter, updater) }
    }

    protected suspend inline fun <V> updateIfChangedInternal(
        newValue: V?,
        propertyGetter: (M?) -> V?,
        crossinline updater: (M?, V?) -> M,
    ): org.hisp.dhis.android.core.common.Unit {
        val obj = getWithoutChildrenInternal()
        val currentValue = propertyGetter(obj)

        if (currentValue != newValue) {
            setObject(updater(obj, newValue))
        }
        return org.hisp.dhis.android.core.common.Unit()
    }

    protected open suspend fun propagateState(m: M?) {
        // Method is empty because is the default action.
    }
}
