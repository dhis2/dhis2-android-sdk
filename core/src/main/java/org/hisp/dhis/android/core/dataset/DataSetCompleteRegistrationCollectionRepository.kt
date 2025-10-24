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
package org.hisp.dhis.android.core.dataset

import io.reactivex.Observable
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.rx2.asObservable
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUploadCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationHandler
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationPostCall
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationStore
import org.hisp.dhis.android.core.user.UserCredentialsObjectRepository
import org.hisp.dhis.android.persistence.dataset.DataSetCompleteRegistrationTableInfo
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("SpreadOperator", "TooManyFunctions")
class DataSetCompleteRegistrationCollectionRepository internal constructor(
    private val dataSetCompleteRegistrationStore: DataSetCompleteRegistrationStore,
    scope: RepositoryScope,
    handler: DataSetCompleteRegistrationHandler,
    private val postCall: DataSetCompleteRegistrationPostCall,
    private val credentialsRepository: UserCredentialsObjectRepository,
) : ReadOnlyCollectionRepositoryImpl<DataSetCompleteRegistration, DataSetCompleteRegistrationCollectionRepository>(
    dataSetCompleteRegistrationStore,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        DataSetCompleteRegistrationCollectionRepository(
            dataSetCompleteRegistrationStore,
            s,
            handler,
            postCall,
            credentialsRepository,
        )
    },
),
    ReadOnlyWithUploadCollectionRepository<DataSetCompleteRegistration> {
    fun value(
        period: String,
        organisationUnit: String,
        dataSet: String,
        attributeOptionCombo: String,
    ): DataSetCompleteRegistrationObjectRepository {
        val updatedScope = byPeriod().eq(period)
            .byOrganisationUnitUid().eq(organisationUnit)
            .byDataSetUid().eq(dataSet)
            .byAttributeOptionComboUid().eq(attributeOptionCombo).scope
        return DataSetCompleteRegistrationObjectRepository(
            dataSetCompleteRegistrationStore,
            credentialsRepository,
            childrenAppenders,
            updatedScope,
            period,
            organisationUnit,
            dataSet,
            attributeOptionCombo,
        )
    }

    override fun upload(): Observable<D2Progress> = flow {
        val dataSetCompleteRegistrations =
            bySyncState().`in`(*uploadableStatesIncludingError()).getWithoutChildrenInternal()

        emitAll(postCall.uploadDataSetCompleteRegistrations(dataSetCompleteRegistrations))
    }.asObservable()

    override fun blockingUpload() {
        upload().blockingSubscribe()
    }

    fun byPeriod(): StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> {
        return cf.string(DataSetCompleteRegistrationTableInfo.Columns.PERIOD)
    }

    fun byDataSetUid(): StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> {
        return cf.string(DataSetCompleteRegistrationTableInfo.Columns.DATA_SET)
    }

    fun byOrganisationUnitUid(): StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> {
        return cf.string(DataSetCompleteRegistrationTableInfo.Columns.ORGANISATION_UNIT)
    }

    fun byAttributeOptionComboUid(): StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> {
        return cf.string(DataSetCompleteRegistrationTableInfo.Columns.ATTRIBUTE_OPTION_COMBO)
    }

    fun byDate(): DateFilterConnector<DataSetCompleteRegistrationCollectionRepository> {
        return cf.date(DataSetCompleteRegistrationTableInfo.Columns.DATE)
    }

    fun byStoredBy(): StringFilterConnector<DataSetCompleteRegistrationCollectionRepository> {
        return cf.string(DataSetCompleteRegistrationTableInfo.Columns.STORED_BY)
    }

    fun byDeleted(): BooleanFilterConnector<DataSetCompleteRegistrationCollectionRepository> {
        return cf.bool(DataSetCompleteRegistrationTableInfo.Columns.DELETED)
    }

    /**
     * @return
     */
    @Deprecated("Use {@link #bySyncState()} instead.\n" + "     \n" + "      ", ReplaceWith("bySyncState()"))
    fun byState(): EnumFilterConnector<DataSetCompleteRegistrationCollectionRepository, State> {
        return bySyncState()
    }

    fun bySyncState(): EnumFilterConnector<DataSetCompleteRegistrationCollectionRepository, State> {
        return cf.enumC(DataSetCompleteRegistrationTableInfo.Columns.SYNC_STATE)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<DataSetCompleteRegistration> = emptyMap()
    }
}
