/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.persistence.dataset

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationStore
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.stores.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.persistence.organisationunit.OrganisationUnitTableInfo
import org.koin.core.annotation.Singleton

@Singleton
internal class DataSetCompleteRegistrationStoreImpl(
    private val databaseAdapter: DatabaseAdapter
) : DataSetCompleteRegistrationStore,
    ObjectWithoutUidStoreImpl<DataSetCompleteRegistration, DataSetCompleteRegistrationDB>(
        { databaseAdapter.getCurrentDatabase()?.dataSetCompleteRegistrationDao()!! },
        DataSetCompleteRegistration::toDB,
        SQLStatementBuilderImpl(DataSetCompleteRegistrationTableInfo.TABLE_INFO),
    ) {

    /**
     * @param dataSetCompleteRegistration DataSetCompleteRegistration element you want to update
     * @param newState                    The new state to be set for the DataValue
     */
    override suspend fun setState(dataSetCompleteRegistration: DataSetCompleteRegistration, newState: State?) {
        val updatedDataSetCompleteRegistration = dataSetCompleteRegistration.toBuilder().syncState(newState).build()
        updateWhere(updatedDataSetCompleteRegistration)
    }

    override suspend fun setDeleted(dataSetCompleteRegistration: DataSetCompleteRegistration) {
        val updatedDataSetCompleteRegistration = dataSetCompleteRegistration.toBuilder().deleted(true).build()
        updateWhere(updatedDataSetCompleteRegistration)
    }

    override suspend fun removeNotPresentAndSynced(
        dataSetUids: Collection<String>,
        periodIds: Collection<String>,
        rootOrgunitUid: String,
    ): Boolean {
        val whereClause = WhereClauseBuilder()
        whereClause.appendInKeyStringValues(DataSetCompleteRegistrationTableInfo.Columns.DATA_SET, dataSetUids)
        whereClause.appendInKeyStringValues(DataSetCompleteRegistrationTableInfo.Columns.PERIOD, periodIds)
        val subQuery = String.format(
            "SELECT %s FROM %s WHERE %s LIKE '%s'",
            OrganisationUnitTableInfo.Columns.UID,
            OrganisationUnitTableInfo.TABLE_INFO.name(),
            OrganisationUnitTableInfo.Columns.PATH,
            "%$rootOrgunitUid%",
        )
        whereClause.appendInSubQuery(DataSetCompleteRegistrationTableInfo.Columns.ORGANISATION_UNIT, subQuery)
        whereClause.appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.SYNC_STATE, State.SYNCED)
        return deleteWhere(whereClause.build())
    }

    override suspend fun isBeingUpload(dscr: DataSetCompleteRegistration): Boolean {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.PERIOD, dscr.period())
            .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.DATA_SET, dscr.dataSet())
            .appendKeyStringValue(
                DataSetCompleteRegistrationTableInfo.Columns.ORGANISATION_UNIT,
                dscr.organisationUnit(),
            )
            .appendKeyStringValue(
                DataSetCompleteRegistrationTableInfo.Columns.ATTRIBUTE_OPTION_COMBO,
                dscr.attributeOptionCombo(),
            )
            .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.SYNC_STATE, State.UPLOADING)
            .build()

        return selectWhere(whereClause).isNotEmpty()
    }
}
