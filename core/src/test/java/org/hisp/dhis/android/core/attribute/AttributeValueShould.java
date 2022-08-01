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

package org.hisp.dhis.android.core.attribute;

import static com.google.common.truth.Truth.assertThat;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.common.ValueType;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

public class AttributeValueShould extends BaseObjectShould implements ObjectShould {

    public AttributeValueShould() {
        super("attribute/attributeValue.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        AttributeValue attributeValue = objectMapper.readValue(jsonStream, AttributeValue.class);

        assertThat(attributeValue.value()).isEqualTo("value_test");
        assertThat(attributeValue.attribute().uid()).isEqualTo("r6KOit2qCGw");
        assertThat(attributeValue.attribute().uid()).isEqualTo("r6KOit2qCGw");
        assertThat(attributeValue.attribute().name()).isEqualTo("Name Pattern");
        assertThat(attributeValue.attribute().displayName()).isEqualTo("Name Pattern");
        assertThat(attributeValue.attribute().lastUpdated()).isEqualTo(
                BaseIdentifiableObject.parseDate("2020-07-06T06:27:19.164"));
        assertThat(attributeValue.attribute().created()).isEqualTo(
                BaseIdentifiableObject.parseDate("2020-07-06T06:27:19.164"));
        assertThat(attributeValue.attribute().valueType()).isEqualTo(ValueType.TEXT);
        assertThat(attributeValue.attribute().unique()).isEqualTo(false);
        assertThat(attributeValue.attribute().mandatory()).isEqualTo(false);
        assertThat(attributeValue.attribute().programStageAttribute()).isEqualTo(true);
        assertThat(attributeValue.attribute().indicatorAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().indicatorGroupAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().userGroupAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().dataElementAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().constantAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().categoryOptionAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().optionSetAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().sqlViewAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().legendSetAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().trackedEntityAttributeAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().organisationUnitAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().dataSetAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().documentAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().validationRuleGroupAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().dataElementGroupAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().sectionAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().trackedEntityTypeAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().userAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().categoryOptionGroupAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().programAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().categoryAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().categoryOptionComboAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().categoryOptionGroupSetAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().validationRuleAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().programIndicatorAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().organisationUnitGroupAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().dataElementGroupSetAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().organisationUnitGroupSetAttribute()).isEqualTo(false);
        assertThat(attributeValue.attribute().optionAttribute()).isEqualTo(false);
    }
}