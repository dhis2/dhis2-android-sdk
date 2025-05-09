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

package org.hisp.dhis.android.persistence.attribute

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.attribute.Attribute
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseNameableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseNameableFields

@Entity(
    tableName = "Attribute",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class AttributeDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
    val valueType: String?,
    val uniqueProperty: Boolean?,
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
    val trackedEntityAttributeAttribute: Boolean?,
    val organisationUnitAttribute: Boolean?,
    val dataSetAttribute: Boolean?,
    val documentAttribute: Boolean?,
    val validationRuleGroupAttribute: Boolean?,
    val dataElementGroupAttribute: Boolean?,
    val sectionAttribute: Boolean?,
    val trackedEntityTypeAttribute: Boolean?,
    val userAttribute: Boolean?,
    val categoryOptionGroupAttribute: Boolean?,
    val programStageAttribute: Boolean?,
    val programAttribute: Boolean?,
    val categoryAttribute: Boolean?,
    val categoryOptionComboAttribute: Boolean?,
    val categoryOptionGroupSetAttribute: Boolean?,
    val validationRuleAttribute: Boolean?,
    val programIndicatorAttribute: Boolean?,
    val organisationUnitGroupAttribute: Boolean?,
    val dataElementGroupSetAttribute: Boolean?,
    val organisationUnitGroupSetAttribute: Boolean?,
    val optionAttribute: Boolean?,
) : EntityDB<Attribute>, BaseNameableObjectDB {

    override fun toDomain(): Attribute {
        return Attribute.builder().apply {
            applyBaseNameableFields(this@AttributeDB)
            id(id.toLong())
            valueType?.let { valueType(ValueType.valueOf(it)) }
            unique(uniqueProperty)
            mandatory(mandatory)
            indicatorAttribute(indicatorAttribute)
            indicatorGroupAttribute(indicatorGroupAttribute)
            userGroupAttribute(userGroupAttribute)
            dataElementAttribute(dataElementAttribute)
            constantAttribute(constantAttribute)
            categoryOptionAttribute(categoryOptionAttribute)
            optionSetAttribute(optionSetAttribute)
            sqlViewAttribute(sqlViewAttribute)
            legendSetAttribute(legendSetAttribute)
            trackedEntityAttributeAttribute(trackedEntityAttributeAttribute)
            organisationUnitAttribute(organisationUnitAttribute)
            dataSetAttribute(dataSetAttribute)
            documentAttribute(documentAttribute)
            validationRuleGroupAttribute(validationRuleGroupAttribute)
            dataElementGroupAttribute(dataElementGroupAttribute)
            sectionAttribute(sectionAttribute)
            trackedEntityTypeAttribute(trackedEntityTypeAttribute)
            userAttribute(userAttribute)
            categoryOptionGroupAttribute(categoryOptionGroupAttribute)
            programStageAttribute(programStageAttribute)
            programAttribute(programAttribute)
            categoryAttribute(categoryAttribute)
            categoryOptionComboAttribute(categoryOptionComboAttribute)
            categoryOptionGroupSetAttribute(categoryOptionGroupSetAttribute)
            validationRuleAttribute(validationRuleAttribute)
            programIndicatorAttribute(programIndicatorAttribute)
            organisationUnitGroupAttribute(organisationUnitGroupAttribute)
            dataElementGroupSetAttribute(dataElementGroupSetAttribute)
            organisationUnitGroupSetAttribute(organisationUnitGroupSetAttribute)
            optionAttribute(optionAttribute)
        }.build()
    }

    companion object {
        fun Attribute.toDB(): AttributeDB {
            return AttributeDB(
                uid = uid(),
                code = code(),
                name = name(),
                displayName = displayName(),
                created = created().dateFormat(),
                lastUpdated = lastUpdated().dateFormat(),
                shortName = shortName(),
                displayShortName = displayShortName(),
                description = description(),
                displayDescription = displayDescription(),
                valueType = valueType()?.name,
                uniqueProperty = unique(),
                mandatory = mandatory(),
                indicatorAttribute = indicatorAttribute(),
                indicatorGroupAttribute = indicatorGroupAttribute(),
                userGroupAttribute = userGroupAttribute(),
                dataElementAttribute = dataElementAttribute(),
                constantAttribute = constantAttribute(),
                categoryOptionAttribute = categoryOptionAttribute(),
                optionSetAttribute = optionSetAttribute(),
                sqlViewAttribute = sqlViewAttribute(),
                legendSetAttribute = legendSetAttribute(),
                trackedEntityAttributeAttribute = trackedEntityAttributeAttribute(),
                organisationUnitAttribute = organisationUnitAttribute(),
                dataSetAttribute = dataSetAttribute(),
                documentAttribute = documentAttribute(),
                validationRuleGroupAttribute = validationRuleGroupAttribute(),
                dataElementGroupAttribute = dataElementGroupAttribute(),
                sectionAttribute = sectionAttribute(),
                trackedEntityTypeAttribute = trackedEntityTypeAttribute(),
                userAttribute = userAttribute(),
                categoryOptionGroupAttribute = categoryOptionGroupAttribute(),
                programStageAttribute = programStageAttribute(),
                programAttribute = programAttribute(),
                categoryAttribute = categoryAttribute(),
                categoryOptionComboAttribute = categoryOptionComboAttribute(),
                categoryOptionGroupSetAttribute = categoryOptionGroupSetAttribute(),
                validationRuleAttribute = validationRuleAttribute(),
                programIndicatorAttribute = programIndicatorAttribute(),
                organisationUnitGroupAttribute = organisationUnitGroupAttribute(),
                dataElementGroupSetAttribute = dataElementGroupSetAttribute(),
                organisationUnitGroupSetAttribute = organisationUnitGroupSetAttribute(),
                optionAttribute = optionAttribute(),
            )
        }
    }
}
