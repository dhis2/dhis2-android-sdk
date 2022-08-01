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

package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class EventShould extends BaseObjectShould implements ObjectShould {

    public EventShould() {
        super("event/event.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        Event event = objectMapper.readValue(jsonStream, Event.class);

        assertThat(event.uid()).isEqualTo("hnaWBxMw5j3");
        assertThat(event.status()).isEqualTo(EventStatus.COMPLETED);
        assertThat(event.organisationUnit()).isEqualTo("DiszpKrYNg8");
        assertThat(event.program()).isEqualTo("eBAyeGv0exc");
        assertThat(event.programStage()).isEqualTo("Zj7UnCAulEk");
        assertThat(event.enrollment()).isEqualTo("RiLEKhWHlxZ");
        assertThat(event.geometry().type()).isEqualTo(FeatureType.POINT);
        assertThat(event.geometry().coordinates()).isEqualTo("[0.0, 0.0]");
        assertThat(event.deleted()).isFalse();
        assertThat(event.assignedUser()).isEqualTo("aTwqot2S410");

        assertThat(event.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-09-08T21:40:22.000"));
        assertThat(event.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-11-15T14:55:22.995"));
        assertThat(event.eventDate()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-05-01T00:00:00.000"));
        assertThat(event.completedDate()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-11-15T00:00:00.000"));

        assertThat(event.trackedEntityDataValues().get(0).dataElement()).isEqualTo("vV9UWAZohSf");
        assertThat(event.trackedEntityDataValues().get(1).dataElement()).isEqualTo("K6uUAvq500H");
        assertThat(event.trackedEntityDataValues().get(2).dataElement()).isEqualTo("fWIAEtYVEGk");
        assertThat(event.trackedEntityDataValues().get(3).dataElement()).isEqualTo("msodh3rEMJa");
        assertThat(event.trackedEntityDataValues().get(4).dataElement()).isEqualTo("eMyVanycQSC");
        assertThat(event.trackedEntityDataValues().get(5).dataElement()).isEqualTo("oZg33kd9taw");
        assertThat(event.trackedEntityDataValues().get(6).dataElement()).isEqualTo("qrur9Dvnyt5");
        assertThat(event.trackedEntityDataValues().get(7).dataElement()).isEqualTo("GieVkTxp4HH");

        assertThat(event.notes().get(0).uid()).isEqualTo("eventNote1");
        assertThat(event.notes().get(1).uid()).isEqualTo("eventNote2");

        assertThat(event.relationships().get(0).uid()).isEqualTo("ZLrITbZfdnv");
    }
}
