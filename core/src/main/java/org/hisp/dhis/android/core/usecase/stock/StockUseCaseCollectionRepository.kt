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
package org.hisp.dhis.android.core.usecase.stock

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.TwoWayTransformer
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUidCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyWithUidAndTransformerCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope

@Reusable
class StockUseCaseCollectionRepository @Inject internal constructor(
    store: IdentifiableObjectStore<InternalStockUseCase>,
    childrenAppenders: MutableMap<String, ChildrenAppender<InternalStockUseCase>>,
    scope: RepositoryScope,
    transformer: TwoWayTransformer<InternalStockUseCase, StockUseCase>,
) : ReadOnlyWithUidCollectionRepository<StockUseCase> by
    ReadOnlyWithUidAndTransformerCollectionRepositoryImpl<InternalStockUseCase, StockUseCase,
        StockUseCaseCollectionRepository>(
        store,
        childrenAppenders,
        scope,
        FilterConnectorFactory(scope) { s: RepositoryScope ->
            StockUseCaseCollectionRepository(store, childrenAppenders, s, transformer)
        },
        transformer
    ) {
    private val cf: FilterConnectorFactory<StockUseCaseCollectionRepository> =
        FilterConnectorFactory(scope) { s: RepositoryScope ->
            StockUseCaseCollectionRepository(store, childrenAppenders, s, transformer)
        }

    fun withTransactions(): StockUseCaseCollectionRepository {
        return cf.withChild(InternalStockUseCase.TRANSACTIONS)
    }
}