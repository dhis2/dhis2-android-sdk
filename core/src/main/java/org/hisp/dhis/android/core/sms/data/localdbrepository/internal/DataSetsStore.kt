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
package org.hisp.dhis.android.core.sms.data.localdbrepository.internal

import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.State.Companion.uploadableStatesIncludingError
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationCollectionRepository
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationStore
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueModule
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.koin.core.annotation.Singleton

@Singleton
internal class DataSetsStore(
    private val dataValueModule: DataValueModule,
    private val dataValueStore: DataValueStore,
    private val dataSetStore: DataSetCompleteRegistrationStore,
    private val completeRegistrationRepository: DataSetCompleteRegistrationCollectionRepository,
) {
    suspend fun getDataValues(
        dataSetUid: String?,
        orgUnit: String,
        period: String,
        attributeOptionComboUid: String,
    ): List<DataValue> {
        val baseQuery = dataValueModule.dataValues()
            .byDataSetUid(dataSetUid)
            .byOrganisationUnitUid().eq(orgUnit)
            .byPeriod().eq(period)
            .byAttributeOptionComboUid().eq(attributeOptionComboUid)

        val uploadable = uploadableStatesIncludingError().toList()
        val dataValues = baseQuery
            .bySyncState().`in`(uploadable)
            .getInternal()

        return dataValues.ifEmpty {
            baseQuery
                .getInternal()
                .takeIf { it.isNotEmpty() }
                ?.take(1)
                ?: emptyList()
        }
    }

    /**
     * Updates the sync state for all data values in the given dataset/orgUnit/period/AOC.
     */
    suspend fun updateDataSetValuesState(
        dataSetUid: String?,
        orgUnit: String,
        period: String,
        attributeOptionComboUid: String,
        state: State,
    ) {
        getDataValues(dataSetUid, orgUnit, period, attributeOptionComboUid)
            .forEach { dataValueStore.setState(it, state) }
    }

    /**
     * Updates the sync state for the dataset complete registration entry matching the given parameters.
     */
    suspend fun updateDataSetCompleteRegistrationState(
        dataSetId: String,
        orgUnit: String,
        period: String,
        attributeOptionComboUid: String,
        state: State?,
    ) {
        completeRegistrationRepository
            .byDataSetUid().eq(dataSetId)
            .byOrganisationUnitUid().eq(orgUnit)
            .byPeriod().eq(period)
            .byAttributeOptionComboUid().eq(attributeOptionComboUid)
            .one()
            .getInternal()
            ?.let { dataSetStore.setState(it, state) }
    }
}
