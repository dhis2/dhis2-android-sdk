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

public class AttributeShould extends BaseObjectShould implements ObjectShould {

    public AttributeShould() {
        super("attribute/attribute.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        Attribute attribute = objectMapper.readValue(jsonStream, Attribute.class);

        assertThat(attribute.uid()).isEqualTo("r6KOit2qCGw");
        assertThat(attribute.name()).isEqualTo("Name Pattern");
        assertThat(attribute.displayName()).isEqualTo("Name Pattern");
        assertThat(attribute.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.parseDate("2020-07-06T06:27:19.164"));
        assertThat(attribute.created()).isEqualTo(
                BaseIdentifiableObject.parseDate("2020-07-06T06:27:19.164"));
        assertThat(attribute.valueType()).isEqualTo(ValueType.TEXT);
        assertThat(attribute.unique()).isEqualTo(false);
        assertThat(attribute.mandatory()).isEqualTo(false);
        assertThat(attribute.programStageAttribute()).isEqualTo(true);
        assertThat(attribute.indicatorAttribute()).isEqualTo(false);
        assertThat(attribute.indicatorGroupAttribute()).isEqualTo(false);
        assertThat(attribute.userGroupAttribute()).isEqualTo(false);
        assertThat(attribute.dataElementAttribute()).isEqualTo(false);
        assertThat(attribute.constantAttribute()).isEqualTo(false);
        assertThat(attribute.categoryOptionAttribute()).isEqualTo(false);
        assertThat(attribute.optionSetAttribute()).isEqualTo(false);
        assertThat(attribute.sqlViewAttribute()).isEqualTo(false);
        assertThat(attribute.legendSetAttribute()).isEqualTo(false);
        assertThat(attribute.trackedEntityAttributeAttribute()).isEqualTo(false);
        assertThat(attribute.organisationUnitAttribute()).isEqualTo(false);
        assertThat(attribute.dataSetAttribute()).isEqualTo(false);
        assertThat(attribute.documentAttribute()).isEqualTo(false);
        assertThat(attribute.validationRuleGroupAttribute()).isEqualTo(false);
        assertThat(attribute.dataElementGroupAttribute()).isEqualTo(false);
        assertThat(attribute.sectionAttribute()).isEqualTo(false);
        assertThat(attribute.trackedEntityTypeAttribute()).isEqualTo(false);
        assertThat(attribute.userAttribute()).isEqualTo(false);
        assertThat(attribute.categoryOptionGroupAttribute()).isEqualTo(false);
        assertThat(attribute.programAttribute()).isEqualTo(false);
        assertThat(attribute.categoryAttribute()).isEqualTo(false);
        assertThat(attribute.categoryOptionComboAttribute()).isEqualTo(false);
        assertThat(attribute.categoryOptionGroupSetAttribute()).isEqualTo(false);
        assertThat(attribute.validationRuleAttribute()).isEqualTo(false);
        assertThat(attribute.programIndicatorAttribute()).isEqualTo(false);
        assertThat(attribute.organisationUnitGroupAttribute()).isEqualTo(false);
        assertThat(attribute.dataElementGroupSetAttribute()).isEqualTo(false);
        assertThat(attribute.organisationUnitGroupSetAttribute()).isEqualTo(false);
        assertThat(attribute.optionAttribute()).isEqualTo(false);
    }
}