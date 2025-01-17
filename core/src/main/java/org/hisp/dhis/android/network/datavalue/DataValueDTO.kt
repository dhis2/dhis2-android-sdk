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

package org.hisp.dhis.android.network.datavalue

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.network.common.dto.BaseDeletableDataObjectDTO

@Serializable
internal data class DataValueDTO(
    override val deleted: Boolean?,
    val dataElement: String,
    val period: String,
    @SerialName(DataValueFields.ORGANISATION_UNIT) val organisationUnit: String,
    val categoryOptionCombo: String,
    val attributeOptionCombo: String,
    val value: String?,
    val storedBy: String?,
    val created: String,
    val lastUpdated: String,
    val comment: String?,
    @SerialName(DataValueFields.FOLLOW_UP) val followUp: Boolean?,
) : BaseDeletableDataObjectDTO {

    fun toDomain(): DataValue {
        return DataValue.builder()
            .deleted(deleted)
            .dataElement(dataElement)
            .period(period)
            .organisationUnit(organisationUnit)
            .categoryOptionCombo(categoryOptionCombo)
            .attributeOptionCombo(attributeOptionCombo)
            .value(value)
            .storedBy(storedBy)
            .created(DateUtils.DATE_FORMAT.parse(created))
            .lastUpdated(DateUtils.DATE_FORMAT.parse(lastUpdated))
            .comment(comment)
            .followUp(followUp)
            .build()
    }

    companion object {
        fun fromDomain(dataValue: DataValue): DataValueDTO {
            return DataValueDTO(
                deleted = dataValue.deleted(),
                dataElement = dataValue.dataElement()!!,
                period = dataValue.period()!!,
                organisationUnit = dataValue.organisationUnit()!!,
                categoryOptionCombo = dataValue.categoryOptionCombo()!!,
                attributeOptionCombo = dataValue.attributeOptionCombo()!!,
                value = dataValue.value(),
                storedBy = dataValue.storedBy(),
                created = dataValue.created().toString(),
                lastUpdated = dataValue.lastUpdated().toString(),
                comment = dataValue.comment(),
                followUp = dataValue.followUp(),
            )
        }
    }
}
