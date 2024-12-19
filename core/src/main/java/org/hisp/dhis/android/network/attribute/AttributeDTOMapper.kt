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

import org.hisp.dhis.android.core.attribute.Attribute
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.network.common.dto.applyBaseNameableFields

internal fun attributeDtoToDomainMapper(item: AttributeDTO): Attribute {
    return Attribute.builder()
        .applyBaseNameableFields(item)
        .valueType(item.valueType?.let { ValueType.valueOf(item.valueType) })
        .unique(item.unique)
        .mandatory(item.mandatory)
        .indicatorAttribute(item.indicatorAttribute)
        .indicatorGroupAttribute(item.indicatorGroupAttribute)
        .userGroupAttribute(item.userGroupAttribute)
        .dataElementAttribute(item.dataElementAttribute)
        .constantAttribute(item.constantAttribute)
        .categoryOptionAttribute(item.categoryOptionAttribute)
        .optionSetAttribute(item.optionSetAttribute)
        .sqlViewAttribute(item.sqlViewAttribute)
        .legendSetAttribute(item.legendSetAttribute)
        .trackedEntityAttributeAttribute(item.trackedEntityAttributeAttribute)
        .organisationUnitAttribute(item.organisationUnitAttribute)
        .dataSetAttribute(item.dataSetAttribute)
        .documentAttribute(item.documentAttribute)
        .validationRuleGroupAttribute(item.validationRuleGroupAttribute)
        .dataElementGroupAttribute(item.dataElementGroupAttribute)
        .sectionAttribute(item.sectionAttribute)
        .trackedEntityTypeAttribute(item.trackedEntityTypeAttribute)
        .userAttribute(item.userAttribute)
        .categoryOptionGroupAttribute(item.categoryOptionGroupAttribute)
        .programStageAttribute(item.programStageAttribute)
        .programAttribute(item.programAttribute)
        .categoryAttribute(item.categoryAttribute)
        .categoryOptionComboAttribute(item.categoryOptionComboAttribute)
        .categoryOptionGroupSetAttribute(item.categoryOptionGroupSetAttribute)
        .validationRuleAttribute(item.validationRuleAttribute)
        .programIndicatorAttribute(item.programIndicatorAttribute)
        .organisationUnitGroupAttribute(item.organisationUnitGroupAttribute)
        .dataElementGroupSetAttribute(item.dataElementGroupSetAttribute)
        .organisationUnitGroupSetAttribute(item.organisationUnitGroupSetAttribute)
        .optionAttribute(item.optionAttribute)
        .build()
}
