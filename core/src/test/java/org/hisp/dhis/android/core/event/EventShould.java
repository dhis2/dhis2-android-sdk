/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.event;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class EventShould {

    @Test(expected = IllegalStateException.class)
    public void throw_illegal_state_exception_on_null_uid_field() {
        EventModel.builder().uid(null).build();
    }

    // ToDo: Consider re-evaluating usage of EqualsVerifier for store models
//    @Test
//    public void have_the_equals_method_conform_to_contract() {
//        EqualsVerifier.forClass(EventModel.builder().uid("a1b2c3d4e5f").build().getClass())
//                .suppress(Warning.NULL_FIELDS)
//                .verify();
//    }

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        Event event = objectMapper.readValue("{\n" +
                        "\n" +
                        "    \"programStage\": \"Zj7UnCAulEk\",\n" +
                        "    \"storedBy\": \"system\",\n" +
                        "    \"orgUnit\": \"DiszpKrYNg8\",\n" +
                        "    \"dueDate\": \"2015-05-01T00:00:00.000\",\n" +
                        "    \"program\": \"eBAyeGv0exc\",\n" +
                        "    \"href\": \"https://play.dhis2.org/demo/api/events/hnaWBxMw5j3\",\n" +
                        "    \"event\": \"hnaWBxMw5j3\",\n" +
                        "    \"status\": \"COMPLETED\",\n" +
                        "    \"eventDate\": \"2015-05-01T00:00:00.000\",\n" +
                        "    \"orgUnitName\": \"Ngelehun CHC\",\n" +
                        "    \"attributeCategoryOptions\": \"as6ygGvUGNg\",\n" +
                        "    \"created\": \"2015-09-08T21:40:22.000\",\n" +
                        "    \"completedDate\": \"2015-11-15T00:00:00.000\",\n" +
                        "    \"lastUpdated\": \"2015-11-15T14:55:22.995\",\n" +
                        "    \"completedBy\": \"system\",\n" +
                        "    \"enrollment\": \"RiLEKhWHlxZ\",\n" +
                        "    \"coordinate\": {\n" +
                        "        \"latitude\": 0.0,\n" +
                        "        \"longitude\": 0.0\n" +
                        "    },\n" +
                        "    \"dataValues\": [\n" +
                        "        {\n" +
                        "            \"lastUpdated\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"storedBy\": \"system\",\n" +
                        "            \"created\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"dataElement\": \"vV9UWAZohSf\",\n" +
                        "            \"value\": \"24\",\n" +
                        "            \"providedElsewhere\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"lastUpdated\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"storedBy\": \"system\",\n" +
                        "            \"created\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"dataElement\": \"K6uUAvq500H\",\n" +
                        "            \"value\": \"A00\",\n" +
                        "            \"providedElsewhere\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"lastUpdated\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"storedBy\": \"system\",\n" +
                        "            \"created\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"dataElement\": \"fWIAEtYVEGk\",\n" +
                        "            \"value\": \"MODDISCH\",\n" +
                        "            \"providedElsewhere\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"lastUpdated\": \"2014-11-15T14:55:23.006\",\n" +
                        "            \"storedBy\": \"system\",\n" +
                        "            \"created\": \"2014-11-15T14:55:23.006\",\n" +
                        "            \"dataElement\": \"msodh3rEMJa\",\n" +
                        "            \"value\": \"2013-05-31\",\n" +
                        "            \"providedElsewhere\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"lastUpdated\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"storedBy\": \"system\",\n" +
                        "            \"created\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"dataElement\": \"eMyVanycQSC\",\n" +
                        "            \"value\": \"2013-05-01\",\n" +
                        "            \"providedElsewhere\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"lastUpdated\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"storedBy\": \"system\",\n" +
                        "            \"created\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"dataElement\": \"oZg33kd9taw\",\n" +
                        "            \"value\": \"Female\",\n" +
                        "            \"providedElsewhere\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"lastUpdated\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"storedBy\": \"system\",\n" +
                        "            \"created\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"dataElement\": \"qrur9Dvnyt5\",\n" +
                        "            \"value\": \"16\",\n" +
                        "            \"providedElsewhere\": false\n" +
                        "        },\n" +
                        "        {\n" +
                        "            \"lastUpdated\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"storedBy\": \"system\",\n" +
                        "            \"created\": \"2014-11-15T14:55:23.007\",\n" +
                        "            \"dataElement\": \"GieVkTxp4HH\",\n" +
                        "            \"value\": \"140\",\n" +
                        "            \"providedElsewhere\": false\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"notes\": [ ]\n" +
                        "\n" +
                        "}",
                Event.class);


        assertThat(event.uid()).isEqualTo("hnaWBxMw5j3");
        assertThat(event.status()).isEqualTo(EventStatus.COMPLETED);
        assertThat(event.organisationUnit()).isEqualTo("DiszpKrYNg8");
        assertThat(event.program()).isEqualTo("eBAyeGv0exc");
        assertThat(event.programStage()).isEqualTo("Zj7UnCAulEk");
        assertThat(event.enrollmentUid()).isEqualTo("RiLEKhWHlxZ");

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
    }
}
