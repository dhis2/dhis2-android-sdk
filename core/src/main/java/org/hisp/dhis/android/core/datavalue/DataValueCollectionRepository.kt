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
package org.hisp.dhis.android.core.datavalue

import dagger.Reusable
import io.reactivex.Observable
import javax.inject.Inject
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.rx2.asObservable
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUploadCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.*
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.datavalue.DataValueByDataSetQueryHelper.dataValueKey
import org.hisp.dhis.android.core.datavalue.DataValueByDataSetQueryHelper.operator
import org.hisp.dhis.android.core.datavalue.DataValueByDataSetQueryHelper.whereClause
import org.hisp.dhis.android.core.datavalue.internal.DataValuePostCall
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore

@Suppress("TooManyFunctions")
@Reusable
class DataValueCollectionRepository @Inject internal constructor(
    private val store: DataValueStore,
    childrenAppenders: MutableMap<String, ChildrenAppender<DataValue>>,
    scope: RepositoryScope,
    private val postCall: DataValuePostCall
) : ReadOnlyCollectionRepositoryImpl<DataValue, DataValueCollectionRepository>(
    store,
    childrenAppenders,
    scope,
    FilterConnectorFactory(scope) { s: RepositoryScope ->
        DataValueCollectionRepository(store, childrenAppenders, s, postCall)
    }
),
    ReadOnlyWithUploadCollectionRepository<DataValue> {

    override fun upload(): Observable<D2Progress> = flow {
        val dataValues =
            bySyncState().`in`(State.uploadableStatesIncludingError().toMutableList()).blockingGetWithoutChildren()
        emitAll(postCall.uploadDataValues(dataValues))
    }.asObservable()

    override fun blockingUpload() {
        upload().blockingSubscribe()
    }

    fun value(
        period: String,
        organisationUnit: String,
        dataElement: String,
        categoryOptionCombo: String,
        attributeOptionCombo: String
    ): DataValueObjectRepository {
        val updatedScope = byPeriod().eq(period)
            .byOrganisationUnitUid().eq(organisationUnit)
            .byDataElementUid().eq(dataElement)
            .byCategoryOptionComboUid().eq(categoryOptionCombo)
            .byAttributeOptionComboUid().eq(attributeOptionCombo).scope
        return DataValueObjectRepository(
            store,
            childrenAppenders,
            updatedScope,
            period,
            organisationUnit,
            dataElement,
            categoryOptionCombo,
            attributeOptionCombo
        )
    }

    fun byDataElementUid(): StringFilterConnector<DataValueCollectionRepository> {
        return cf.string(DataValueTableInfo.Columns.DATA_ELEMENT)
    }

    fun byPeriod(): StringFilterConnector<DataValueCollectionRepository> {
        return cf.string(DataValueTableInfo.Columns.PERIOD)
    }

    fun byOrganisationUnitUid(): StringFilterConnector<DataValueCollectionRepository> {
        return cf.string(DataValueTableInfo.Columns.ORGANISATION_UNIT)
    }

    fun byCategoryOptionComboUid(): StringFilterConnector<DataValueCollectionRepository> {
        return cf.string(DataValueTableInfo.Columns.CATEGORY_OPTION_COMBO)
    }

    fun byAttributeOptionComboUid(): StringFilterConnector<DataValueCollectionRepository> {
        return cf.string(DataValueTableInfo.Columns.ATTRIBUTE_OPTION_COMBO)
    }

    fun byValue(): StringFilterConnector<DataValueCollectionRepository> {
        return cf.string(DataValueTableInfo.Columns.VALUE)
    }

    fun byDataSetUid(dataSetUid: String?): DataValueCollectionRepository {
        return cf.subQuery(dataValueKey)
            .rawSubQuery(
                operator,
                whereClause(dataSetUid!!)
            )!!
    }

    fun byStoredBy(): StringFilterConnector<DataValueCollectionRepository> {
        return cf.string(DataValueTableInfo.Columns.STORED_BY)
    }

    fun byCreated(): DateFilterConnector<DataValueCollectionRepository> {
        return cf.date(DataValueTableInfo.Columns.CREATED)
    }

    fun byLastUpdated(): DateFilterConnector<DataValueCollectionRepository> {
        return cf.date(DataValueTableInfo.Columns.LAST_UPDATED)
    }

    fun byComment(): StringFilterConnector<DataValueCollectionRepository> {
        return cf.string(DataValueTableInfo.Columns.COMMENT)
    }

    fun byFollowUp(): BooleanFilterConnector<DataValueCollectionRepository> {
        return cf.bool(DataValueTableInfo.Columns.FOLLOW_UP)
    }

    /**
     * @return
     */
    @Deprecated("Use {@link #bySyncState()} instead.\n" + "     \n" + "      ")
    fun byState(): EnumFilterConnector<DataValueCollectionRepository, State> {
        return bySyncState()
    }

    fun bySyncState(): EnumFilterConnector<DataValueCollectionRepository, State> {
        return cf.enumC<State>(DataValueTableInfo.Columns.SYNC_STATE)
    }

    fun byDeleted(): BooleanFilterConnector<DataValueCollectionRepository> {
        return cf.bool(DataValueTableInfo.Columns.DELETED)
    }
}
