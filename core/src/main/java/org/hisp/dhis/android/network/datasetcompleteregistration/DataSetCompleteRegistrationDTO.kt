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

package org.hisp.dhis.android.network.datasetcompleteregistration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration
import org.hisp.dhis.android.core.util.simpleDateFormat
import org.hisp.dhis.android.core.util.toJavaSimpleDate
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.BaseDeletableDataObjectDTO
import org.hisp.dhis.android.network.common.dto.PagerDTO

@Serializable
internal data class DataSetCompleteRegistrationDTO(
    override val deleted: Boolean?,
    val period: String?,
    val dataSet: String?,
    val organisationUnit: String?,
    val attributeOptionCombo: String?,
    val date: String?,
    val storedBy: String?,
//    val completed: Boolean?,
) : BaseDeletableDataObjectDTO {

    fun toDomain(): DataSetCompleteRegistration {
        return DataSetCompleteRegistration.builder().apply {
            deleted(deleted)
            period?.let { period(it) }
            dataSet?.let { dataSet(it) }
            organisationUnit?.let { organisationUnit(it) }
            attributeOptionCombo?.let { attributeOptionCombo(it) }
            date(date.toJavaSimpleDate())
            storedBy(storedBy)
        }.build()
    }
}

internal fun DataSetCompleteRegistration.toDto(): DataSetCompleteRegistrationDTO {
    return DataSetCompleteRegistrationDTO(
        deleted = this.deleted(),
        period = this.period(),
        dataSet = this.dataSet(),
        organisationUnit = this.organisationUnit(),
        attributeOptionCombo = this.attributeOptionCombo(),
        date = this.date().simpleDateFormat(),
        storedBy = this.storedBy(),
    )
}

@Serializable
internal class DataSetCompleteRegistrationPayload(
    override val pager: PagerDTO?,
    @SerialName("completeDataSetRegistrations") override val items: List<DataSetCompleteRegistrationDTO> = emptyList(),
) : PayloadJson<DataSetCompleteRegistrationDTO>(pager, items)
