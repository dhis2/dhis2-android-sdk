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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class TrackedEntityAttributeReservedValueShould extends BaseObjectShould implements ObjectShould {

    public TrackedEntityAttributeReservedValueShould() {
        super("trackedentity/tracked_entity_attribute_reserved_value.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        TrackedEntityAttributeReservedValue reservedValue =
                objectMapper.readValue(jsonStream, TrackedEntityAttributeReservedValue.class);

        assertThat(reservedValue.ownerObject()).isEqualTo("TRACKEDENTITYATTRIBUTE");
        assertThat(reservedValue.ownerUid()).isEqualTo("xeG4wH2I676");
        assertThat(reservedValue.key()).isEqualTo("RANDOM(###)");
        assertThat(reservedValue.value()).isEqualTo("046");
        assertThat(reservedValue.created()).isEqualTo(
                BaseIdentifiableObject.parseDate("2018-04-26T14:54:53.344"));
        assertThat(reservedValue.expiryDate()).isEqualTo(
                BaseIdentifiableObject.parseDate("2018-06-25T14:54:53.344"));
    }
}