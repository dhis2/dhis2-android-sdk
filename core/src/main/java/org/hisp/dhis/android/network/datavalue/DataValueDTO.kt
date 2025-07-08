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

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.network.common.dto.BaseDeletableDataObjectDTO

@Serializable
internal data class DataValueDTO(
    override val deleted: Boolean?,
    val dataElement: String,
    val period: String,
    val orgUnit: String,
    val categoryOptionCombo: String,
    val attributeOptionCombo: String,
    val value: String?,
    val storedBy: String?,
    val created: String?,
    val lastUpdated: String?,
    val comment: String?,
    val followup: Boolean?,
) : BaseDeletableDataObjectDTO {

    fun toDomain(): DataValue {
        return DataValue.builder().apply {
            deleted?.let { deleted(it) }
            dataElement.let { dataElement(it) }
            period.let { period(it) }
            orgUnit.let { organisationUnit(it) }
            categoryOptionCombo.let { categoryOptionCombo(it) }
            attributeOptionCombo.let { attributeOptionCombo(it) }
            value?.let { value(it) }
            storedBy?.let { storedBy(it) }
            created?.let { created(DateUtils.DATE_FORMAT.parse(it)) }
            lastUpdated?.let { lastUpdated(DateUtils.DATE_FORMAT.parse(it)) }
            comment?.let { comment(it) }
            followup?.let { followUp(it) }
        }.build()
    }
}

internal fun DataValue.toDto(): DataValueDTO {
    return DataValueDTO(
        deleted = this.deleted(),
        dataElement = this.dataElement()!!,
        period = this.period()!!,
        orgUnit = this.organisationUnit()!!,
        categoryOptionCombo = this.categoryOptionCombo()!!,
        attributeOptionCombo = this.attributeOptionCombo()!!,
        value = this.value(),
        storedBy = this.storedBy(),
        created = this.created()?.let { DateUtils.DATE_FORMAT.format(it) },
        lastUpdated = this.lastUpdated()?.let { DateUtils.DATE_FORMAT.format(it) },
        comment = this.comment(),
        followup = this.followUp(),
    )
}
