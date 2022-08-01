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

package org.hisp.dhis.android.core.arch.repositories.object.internal;

import android.util.Log;

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.object.ReadOnlyObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.DataObject;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.util.Map;

import io.reactivex.Completable;

public class ReadWriteWithValueObjectRepositoryImpl<M extends CoreObject, R extends ReadOnlyObjectRepository<M>>
        extends ReadOnlyOneObjectRepositoryImpl<M, R> implements ReadWriteObjectRepository<M> {

    private final ObjectWithoutUidStore<M> store;

    public ReadWriteWithValueObjectRepositoryImpl(ObjectWithoutUidStore<M> store,
                                                  Map<String, ChildrenAppender<M>> childrenAppenders,
                                                  RepositoryScope scope,
                                                  ObjectRepositoryFactory<R> repositoryFactory) {
        super(store, childrenAppenders, scope, repositoryFactory);
        this.store = store;
    }

    /**
     * Removes the object in scope in an asynchronous way. It removes the value in the database and propagates
     * the changes to modify the {@link DataObject#syncState()} of the parent, so it's updated in the server in
     * the next upload.
     * It returns a {@code Completable} that completes as soon as the object is deleted in the database.
     * The {@code Completable} fails if the object doesn't exist.
     * @return the {@code Completable} which notifies the completion
     */
    @Override
    public Completable delete() {
        return Completable.fromAction(this::blockingDelete);
    }

    /**
     * Removes the object in scope in a synchronous way. It removes the value in the database and propagates
     * the changes to modify the {@link DataObject#syncState()} of the parent, so it's updated in the server in
     * the next upload.
     * It blocks the thread and finishes as soon as the object is deleted in the database.
     *
     * Important: this is a blocking method and it should not be executed in the main thread. Consider the
     * asynchronous version {@link #delete()}.
     *
     * It throws an exception if the object doesn't exist.
     */
    @Override
    public void blockingDelete() throws D2Error {
        delete(blockingGetWithoutChildren());
    }

    /**
     * Removes the object in scope in an asynchronous way. It removes the value in the database and propagates
     * the changes to modify the {@link DataObject#syncState()} of the parent, so it's updated in the server in
     * the next upload.
     * It returns a {@code Completable} that completes as soon as the object is deleted in the database.
     * Unlike {@link #delete()}, it doesn't throw an exception if the object doesn't exist.
     * It returns a {@code Completable} that completes as soon as the object is deleted in the database.
     * @return the {@code Completable} which notifies the completion
     */
    @Override
    public Completable deleteIfExist() {
        return Completable.fromAction(this::blockingDeleteIfExist);
    }

    /**
     * Removes the object in scope in a synchronous way. It removes the value in the database and propagates
     * the changes to modify the {@link DataObject#syncState()} of the parent, so it's updated in the server in
     * the next upload.
     * Unlike {@link #blockingDelete()}, it doesn't throw an exception if the object doesn't exist.
     * It blocks the thread and finishes as soon as the object is deleted in the database.
     *
     * Important: this is a blocking method and it should not be executed in the main thread. Consider the
     * asynchronous version {@link #deleteIfExist()}.
     */
    @Override
    public void blockingDeleteIfExist() {
        try {
            blockingDelete();
        } catch (D2Error d2Error) {
            Log.v(ReadWriteWithValueObjectRepositoryImpl.class.getCanonicalName(), d2Error.errorDescription());
        }
    }

    @SuppressWarnings({"PMD.PreserveStackTrace"})
    protected void delete(M m) throws D2Error {
        try {
            store.deleteWhere(m);
            propagateState(m);
        } catch (Exception e) {
            throw D2Error
                    .builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.UNEXPECTED)
                    .errorDescription("Unexpected exception on value delete")
                    .originalException(e)
                    .build();
        }
    }

    @SuppressWarnings({"PMD.PreserveStackTrace"})
    protected void setObject(M m) throws D2Error {
        try {
            store.updateOrInsertWhere(m);
            propagateState(m);
        } catch (Exception e) {
            throw D2Error
                    .builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.VALUE_CANT_BE_SET)
                    .errorDescription("Value can't be set")
                    .originalException(e)
                    .build();
        }
    }

    protected void propagateState(M m) {
        // Method is empty because is the default action.
    }
}