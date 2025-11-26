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

import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataset.DataSetInstance
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB

internal data class DataSetInstanceDB(
    val dataSetUid: String,
    val dataSetDisplayName: String,
    val period: String,
    val periodType: String,
    val organisationUnitUid: String,
    val organisationUnitDisplayName: String,
    val attributeOptionComboUid: String,
    val attributeOptionComboDisplayName: String,
    val valueCount: Int,
    val completionDate: String?,
    val completedBy: String?,
    val lastUpdated: String?,
    val dataValueState: String?,
    val completionState: String?,
    val state: String?,
) : EntityDB<DataSetInstance> {

    override fun toDomain(): DataSetInstance {
        return DataSetInstance.builder()
            .dataSetUid(dataSetUid)
            .dataSetDisplayName(dataSetDisplayName)
            .period(period)
            .periodType(PeriodType.valueOf(periodType))
            .organisationUnitUid(organisationUnitUid)
            .organisationUnitDisplayName(organisationUnitDisplayName)
            .attributeOptionComboUid(attributeOptionComboUid)
            .attributeOptionComboDisplayName(attributeOptionComboDisplayName)
            .valueCount(valueCount)
            .completed(completionDate != null)
            .completionDate(completionDate.toJavaDate())
            .completedBy(completedBy)
            .lastUpdated(lastUpdated.toJavaDate())
            .dataValueState(dataValueState?.let { State.valueOf(it) })
            .completionState(completionState?.let { State.valueOf(it) })
            .state(state?.let { State.valueOf(it) })
            .build()
    }
}
