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

package org.hisp.dhis.android.network.trackedentityattribute

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.AccessDTO
import org.hisp.dhis.android.network.common.dto.BaseNameableObjectDTO
import org.hisp.dhis.android.network.common.dto.ObjectWithStyleDTO
import org.hisp.dhis.android.network.common.dto.ObjectWithUidDTO
import org.hisp.dhis.android.network.common.dto.PagerDTO
import org.hisp.dhis.android.network.common.dto.applyBaseNameableFields

@Serializable
internal data class TrackedEntityAttributeDTO(
    override val id: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
    val pattern: String?,
    val sortOrderInListNoProgram: Int?,
    val optionSet: ObjectWithUidDTO?,
    val valueType: String?,
    val expression: String?,
    val aggregationType: String?,
    val programScope: Boolean?,
    val displayInListNoProgram: Boolean?,
    val generated: Boolean?,
    val displayOnVisitSchedule: Boolean?,
    val confidential: Boolean?,
    val orgunitScope: Boolean?,
    val unique: Boolean?,
    val inherit: Boolean?,
    val fieldMask: String?,
    val style: ObjectWithStyleDTO?,
    val access: AccessDTO?,
    val legendSets: List<ObjectWithUidDTO>? = emptyList(),
    val formName: String?,
    val displayFormName: String?,
) : BaseNameableObjectDTO {
    @Suppress("ComplexMethod")
    fun toDomain(): TrackedEntityAttribute {
        return TrackedEntityAttribute.builder().apply {
            applyBaseNameableFields(this@TrackedEntityAttributeDTO)
            pattern?.let { pattern(it) }
            sortOrderInListNoProgram?.let { sortOrderInListNoProgram(it) }
            optionSet?.let { optionSet(it.toDomain()) }
            valueType?.let { valueType(ValueType.valueOf(it)) }
            expression?.let { expression(it) }
            aggregationType?.let { aggregationType(AggregationType.valueOf(it)) }
            programScope?.let { programScope(it) }
            displayInListNoProgram?.let { displayInListNoProgram(it) }
            generated?.let { generated(it) }
            displayOnVisitSchedule?.let { displayOnVisitSchedule(it) }
            confidential?.let { confidential(it) }
            orgunitScope?.let { orgUnitScope(it) }
            unique?.let { unique(it) }
            inherit?.let { inherit(it) }
            fieldMask?.let { fieldMask(it) }
            style?.let { style(it.toDomain()) }
            access?.let { access(it.toDomain()) }
            legendSets?.let { legendSets(it.map { it.toDomain() }) }
            formName?.let { formName(it) }
            displayFormName?.let { displayFormName(it) }
        }.build()
    }
}

@Serializable
internal class TrackedEntityAttributePayload(
    override val pager: PagerDTO?,
    @SerialName("trackedEntityAttributes") override val items: List<TrackedEntityAttributeDTO> = emptyList(),
) : PayloadJson<TrackedEntityAttributeDTO>(pager, items)
