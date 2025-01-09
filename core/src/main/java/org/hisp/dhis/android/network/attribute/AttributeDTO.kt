/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.network.attribute

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.attribute.Attribute
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.BaseNameableObjectDTO
import org.hisp.dhis.android.network.common.dto.PagerDTO
import org.hisp.dhis.android.network.common.dto.applyBaseNameableFields

@Serializable
internal data class AttributeDTO(
    @SerialName("id") override val uid: String,
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
    val valueType: String?,
    val unique: Boolean?,
    val mandatory: Boolean?,
    val indicatorAttribute: Boolean?,
    val indicatorGroupAttribute: Boolean?,
    val userGroupAttribute: Boolean?,
    val dataElementAttribute: Boolean?,
    val constantAttribute: Boolean?,
    val categoryOptionAttribute: Boolean?,
    val optionSetAttribute: Boolean?,
    val sqlViewAttribute: Boolean?,
    val legendSetAttribute: Boolean?,
    val trackedEntityAttributeAttribute: Boolean? = null,
    val organisationUnitAttribute: Boolean? = null,
    val dataSetAttribute: Boolean? = null,
    val documentAttribute: Boolean? = null,
    val validationRuleGroupAttribute: Boolean? = null,
    val dataElementGroupAttribute: Boolean? = null,
    val sectionAttribute: Boolean? = null,
    val trackedEntityTypeAttribute: Boolean? = null,
    val userAttribute: Boolean? = null,
    val categoryOptionGroupAttribute: Boolean? = null,
    val programStageAttribute: Boolean? = null,
    val programAttribute: Boolean? = null,
    val categoryAttribute: Boolean? = null,
    val categoryOptionComboAttribute: Boolean? = null,
    val categoryOptionGroupSetAttribute: Boolean? = null,
    val validationRuleAttribute: Boolean? = null,
    val programIndicatorAttribute: Boolean? = null,
    val organisationUnitGroupAttribute: Boolean? = null,
    val dataElementGroupSetAttribute: Boolean? = null,
    val organisationUnitGroupSetAttribute: Boolean? = null,
    val optionAttribute: Boolean? = null,
) : BaseNameableObjectDTO {
    fun toDomain(): Attribute {
        return Attribute.builder()
            .applyBaseNameableFields(this)
            .valueType(valueType?.let { ValueType.valueOf(valueType) })
            .unique(unique)
            .mandatory(mandatory)
            .indicatorAttribute(indicatorAttribute)
            .indicatorGroupAttribute(indicatorGroupAttribute)
            .userGroupAttribute(userGroupAttribute)
            .dataElementAttribute(dataElementAttribute)
            .constantAttribute(constantAttribute)
            .categoryOptionAttribute(categoryOptionAttribute)
            .optionSetAttribute(optionSetAttribute)
            .sqlViewAttribute(sqlViewAttribute)
            .legendSetAttribute(legendSetAttribute)
            .trackedEntityAttributeAttribute(trackedEntityAttributeAttribute)
            .organisationUnitAttribute(organisationUnitAttribute)
            .dataSetAttribute(dataSetAttribute)
            .documentAttribute(documentAttribute)
            .validationRuleGroupAttribute(validationRuleGroupAttribute)
            .dataElementGroupAttribute(dataElementGroupAttribute)
            .sectionAttribute(sectionAttribute)
            .trackedEntityTypeAttribute(trackedEntityTypeAttribute)
            .userAttribute(userAttribute)
            .categoryOptionGroupAttribute(categoryOptionGroupAttribute)
            .programStageAttribute(programStageAttribute)
            .programAttribute(programAttribute)
            .categoryAttribute(categoryAttribute)
            .categoryOptionComboAttribute(categoryOptionComboAttribute)
            .categoryOptionGroupSetAttribute(categoryOptionGroupSetAttribute)
            .validationRuleAttribute(validationRuleAttribute)
            .programIndicatorAttribute(programIndicatorAttribute)
            .organisationUnitGroupAttribute(organisationUnitGroupAttribute)
            .dataElementGroupSetAttribute(dataElementGroupSetAttribute)
            .organisationUnitGroupSetAttribute(organisationUnitGroupSetAttribute)
            .optionAttribute(optionAttribute)
            .build()
    }
}

@Serializable
internal class AttributePayload(
    override val pager: PagerDTO? = null,
    @SerialName("attributes") override val items: List<AttributeDTO> = emptyList(),
) : PayloadJson<AttributeDTO>(pager, items)
