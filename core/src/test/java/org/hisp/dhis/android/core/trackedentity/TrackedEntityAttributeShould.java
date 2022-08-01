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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.AggregationType;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.common.ValueType;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class TrackedEntityAttributeShould extends BaseObjectShould implements ObjectShould {

    public TrackedEntityAttributeShould() {
        super("trackedentity/tracked_entity_attribute.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        TrackedEntityAttribute trackedEntityAttribute = objectMapper.readValue(jsonStream, TrackedEntityAttribute.class);

        assertThat(trackedEntityAttribute.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-08-04T11:48:56.928"));
        assertThat(trackedEntityAttribute.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-01-09T19:12:46.551"));
        assertThat(trackedEntityAttribute.uid()).isEqualTo("ruQQnf6rswq");
        assertThat(trackedEntityAttribute.name()).isEqualTo("TB number");
        assertThat(trackedEntityAttribute.displayName()).isEqualTo("TB number");
        assertThat(trackedEntityAttribute.shortName()).isEqualTo("TB number");
        assertThat(trackedEntityAttribute.displayShortName()).isEqualTo("TB number");
        assertThat(trackedEntityAttribute.description()).isEqualTo("TB number");
        assertThat(trackedEntityAttribute.displayDescription()).isEqualTo("TB number");
        assertThat(trackedEntityAttribute.formName()).isEqualTo("number");
        assertThat(trackedEntityAttribute.displayFormName()).isEqualTo("num");
        assertThat(trackedEntityAttribute.displayInListNoProgram()).isFalse();
        assertThat(trackedEntityAttribute.displayOnVisitSchedule()).isFalse();
        assertThat(trackedEntityAttribute.generated()).isFalse();
        assertThat(trackedEntityAttribute.aggregationType()).isEqualTo(AggregationType.DEFAULT);
        assertThat(trackedEntityAttribute.inherit()).isFalse();
        assertThat(trackedEntityAttribute.fieldMask()).isEqualTo("XXXXX");
        assertThat(trackedEntityAttribute.optionSet().uid()).isEqualTo("xjA5E9MimMU");
        assertThat(trackedEntityAttribute.orgUnitScope()).isFalse();
        assertThat(trackedEntityAttribute.programScope()).isFalse();
        assertThat(trackedEntityAttribute.unique()).isFalse();
        assertThat(trackedEntityAttribute.valueType()).isEqualTo(ValueType.TEXT);
        assertThat(trackedEntityAttribute.access().read()).isTrue();
    }
}