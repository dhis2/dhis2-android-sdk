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
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDeletableDataObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadWriteObjectRepository
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.DeletableDataObject
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.Unit
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent

abstract class ReadWriteWithUidDataObjectRepositoryImpl<M, R : ReadOnlyObjectRepository<M>> internal constructor(
    store: IdentifiableDeletableDataObjectStore<M>,
    childrenAppenders: Map<String, ChildrenAppender<M>>,
    scope: RepositoryScope,
    repositoryFactory: ObjectRepositoryFactory<R>,
) : ReadWriteWithUidObjectRepositoryImpl<M, R>(store, childrenAppenders, scope, repositoryFactory),
    ReadWriteObjectRepository<M> where M : CoreObject, M : ObjectWithUidInterface, M : DeletableDataObject {
    /**
     * Removes the object in scope in an asynchronous way. Field [DataObject.syncState] is marked as
     * [State.TO_UPDATE] and [DeletableDataObject.deleted] as true. In the next upload, it will be deleted
     * in the server. It returns a `Completable` that completes as soon as the object is deleted in the database.
     * The `Completable` fails if the object doesn't exist.
     * @return the `Completable` which notifies the completion
     */
    override fun delete(): Completable {
        return Completable.fromAction { blockingDelete() }
    }

    /**
     * Removes the object in scope in a synchronous way. Field [DataObject.syncState] is marked as
     * [State.TO_UPDATE] and [DeletableDataObject.deleted] as true. In the next upload, it will be deleted
     * in the server. It blocks the thread and finishes as soon as the object is deleted in the database.
     * It throws an exception if the object doesn't exist.
     *
     * Important: this is a blocking method and it should not be executed in the main thread. Consider the
     * asynchronous version [.delete].
     *
     * @throws D2Error if any errors occur, including when the object doesn't exist.
     */
    @Throws(D2Error::class)
    override fun blockingDelete() {
        val obj = blockingGet()
        if (obj === null) {
            throw D2Error
                .builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.CANT_DELETE_NON_EXISTING_OBJECT)
                .errorDescription("Tried to delete non existing object")
                .build()
        } else {
            deleteObject(obj)
        }
    }

    /**
     * Removes the object in scope in a synchronous way. Field [DataObject.syncState] is marked as
     * [State.TO_POST] and [DeletableDataObject.deleted] as true. Unlike [.delete],
     * it doesn't throw an exception if the object doesn't exist.
     * It returns a `Completable` that completes as soon as the object is deleted in the database.
     * @return the `Completable` which notifies the completion
     */
    override fun deleteIfExist(): Completable {
        return Completable.fromAction { blockingDeleteIfExist() }
    }

    /**
     * Removes the object in scope in an asynchronous way. Field [DataObject.syncState] is marked as
     * [State.TO_POST] and [DeletableDataObject.deleted] as true.
     * Unlike [.blockingDelete], it doesn't throw an exception if the object doesn't exist.
     * It blocks the thread and finishes as soon as the object is deleted in the database.
     *
     * Important: this is a blocking method and it should not be executed in the main thread. Consider the
     * asynchronous version [.delete].
     */
    override fun blockingDeleteIfExist() {
        try {
            blockingDelete()
        } catch (d2Error: D2Error) {
            Log.v(ReadWriteWithUidDataObjectRepositoryImpl::class.java.canonicalName, d2Error.errorDescription())
        }
    }

    @Throws(D2Error::class)
    override fun updateObject(m: M) {
        super.updateObject(m)
        propagateState(m, HandleAction.Update)
        return Unit()
    }

    protected abstract fun propagateState(m: M, action: HandleAction)
    protected abstract fun deleteObject(m: M)
}
