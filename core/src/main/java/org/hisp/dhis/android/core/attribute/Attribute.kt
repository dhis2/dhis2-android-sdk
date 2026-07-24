/*
 *  Copyright (c) 2004-2026, University of Oslo
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

package org.hisp.dhis.android.core.attribute

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.BaseNameableObject
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ValueType
import java.util.Date

@ModelBuilder
@Suppress("TooManyFunctions")
data class Attribute(
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: Date?,
    override val lastUpdated: Date?,
    override val deleted: Boolean?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
    val valueType: ValueType?,
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
) : BaseNameableObject, CoreObject {

    fun valueType(): ValueType? = valueType
    fun unique(): Boolean? = unique
    fun mandatory(): Boolean? = mandatory
    fun indicatorAttribute(): Boolean? = indicatorAttribute
    fun indicatorGroupAttribute(): Boolean? = indicatorGroupAttribute
    fun userGroupAttribute(): Boolean? = userGroupAttribute
    fun dataElementAttribute(): Boolean? = dataElementAttribute
    fun constantAttribute(): Boolean? = constantAttribute
    fun categoryOptionAttribute(): Boolean? = categoryOptionAttribute
    fun optionSetAttribute(): Boolean? = optionSetAttribute
    fun sqlViewAttribute(): Boolean? = sqlViewAttribute
    fun legendSetAttribute(): Boolean? = legendSetAttribute
    fun trackedEntityAttributeAttribute(): Boolean? = trackedEntityAttributeAttribute
    fun organisationUnitAttribute(): Boolean? = organisationUnitAttribute
    fun dataSetAttribute(): Boolean? = dataSetAttribute
    fun documentAttribute(): Boolean? = documentAttribute
    fun validationRuleGroupAttribute(): Boolean? = validationRuleGroupAttribute
    fun dataElementGroupAttribute(): Boolean? = dataElementGroupAttribute
    fun sectionAttribute(): Boolean? = sectionAttribute
    fun trackedEntityTypeAttribute(): Boolean? = trackedEntityTypeAttribute
    fun userAttribute(): Boolean? = userAttribute
    fun categoryOptionGroupAttribute(): Boolean? = categoryOptionGroupAttribute
    fun programStageAttribute(): Boolean? = programStageAttribute
    fun programAttribute(): Boolean? = programAttribute
    fun categoryAttribute(): Boolean? = categoryAttribute
    fun categoryOptionComboAttribute(): Boolean? = categoryOptionComboAttribute
    fun categoryOptionGroupSetAttribute(): Boolean? = categoryOptionGroupSetAttribute
    fun validationRuleAttribute(): Boolean? = validationRuleAttribute
    fun programIndicatorAttribute(): Boolean? = programIndicatorAttribute
    fun organisationUnitGroupAttribute(): Boolean? = organisationUnitGroupAttribute
    fun dataElementGroupSetAttribute(): Boolean? = dataElementGroupSetAttribute
    fun organisationUnitGroupSetAttribute(): Boolean? = organisationUnitGroupSetAttribute
    fun optionAttribute(): Boolean? = optionAttribute

    fun toBuilder(): Builder = AttributeBuilder.from(this)

    class Builder : AttributeBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
