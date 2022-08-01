/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.core.data.attribute;

import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillNameableProperties;

import org.hisp.dhis.android.core.attribute.Attribute;
import org.hisp.dhis.android.core.common.ValueType;

public class AttributeSamples {

    public static Attribute getAttribute() {
        Attribute.Builder attributeBuilder = Attribute.builder();

        fillNameableProperties(attributeBuilder);
        attributeBuilder
                .id(1L)
                .valueType(ValueType.TEXT)
                .mandatory(false)
                .unique(false)
                .indicatorAttribute(false)
                .indicatorGroupAttribute(false)
                .userGroupAttribute(false)
                .dataElementAttribute(false)
                .constantAttribute(false)
                .categoryOptionAttribute(false)
                .optionSetAttribute(false)
                .sqlViewAttribute(false)
                .legendSetAttribute(false)
                .trackedEntityAttributeAttribute(false)
                .organisationUnitAttribute(false)
                .dataSetAttribute(false)
                .documentAttribute(false)
                .validationRuleGroupAttribute(false)
                .dataElementGroupAttribute(false)
                .sectionAttribute(false)
                .trackedEntityTypeAttribute(false)
                .userAttribute(false)
                .categoryOptionGroupAttribute(false)
                .programStageAttribute(true)
                .programAttribute(false)
                .categoryAttribute(false)
                .categoryOptionComboAttribute(false)
                .categoryOptionGroupSetAttribute(false)
                .validationRuleAttribute(false)
                .programIndicatorAttribute(false)
                .organisationUnitGroupAttribute(false)
                .dataElementGroupSetAttribute(false)
                .organisationUnitGroupSetAttribute(false)
                .optionAttribute(false);

        return attributeBuilder.build();
    }
}