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
package org.hisp.dhis.android.core.arch.repositories.collection.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUidCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.util.Map;

import io.reactivex.Single;

public abstract class ReadWriteWithUidCollectionRepositoryImpl
        <M extends CoreObject & ObjectWithUidInterface, P, R extends ReadOnlyCollectionRepository<M>>
        extends BaseReadOnlyWithUidCollectionRepositoryImpl<M, R>
        implements ReadWriteWithUidCollectionRepository<M, P> {

    protected final Transformer<P, M> transformer;

    public ReadWriteWithUidCollectionRepositoryImpl(IdentifiableObjectStore<M> store,
                                                    Map<String, ChildrenAppender<M>> childrenAppenders,
                                                    RepositoryScope scope,
                                                    Transformer<P, M> transformer,
                                                    FilterConnectorFactory<R> cf) {
        super(store, childrenAppenders, scope, cf);
        this.transformer = transformer;
    }

    /**
     * Adds a new object to the given collection in an asynchronous way based on the provided CreateProjection.
     * It returns a {@code Single<String>} with the generated UID, which is completed when the object is added to the
     * database. It adds an object with a {@link State#TO_POST}, which will be uploaded to the server in the next
     * upload.
     * @param projection the CreateProjection of the object to add
     * @return the Single with the UID
     */
    @Override
    public Single<String> add(P projection) {
        return Single.fromCallable(() -> blockingAdd(projection));
    }

    /**
     * Adds a new object to the given collection in a synchronous way based on the provided CreateProjection.
     * It blocks the current thread and returns the generated UID.
     * It adds an object with a {@link State#TO_POST}, which will be uploaded to the server in the next
     * upload. Important: this is a blocking method and it should not be executed in the main thread. Consider the
     * asynchronous version {@link #add}.
     * @param projection the CreateProjection of the object to add
     * @return the UID
     */
    @SuppressWarnings({"PMD.PreserveStackTrace"})
    @Override
    public String blockingAdd(P projection) throws D2Error {
        M object = transformer.transform(projection);
        try {
            store.insert(object);
            propagateState(object, HandleAction.Insert);
            return object.uid();
        } catch (Exception e) {
            throw D2Error
                    .builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.OBJECT_CANT_BE_INSERTED)
                    .errorDescription("Object can't be inserted")
                    .originalException(e)
                    .build();
        }
    }

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void propagateState(M m, HandleAction action) {
        // Method is empty because is the default action.
    }
}