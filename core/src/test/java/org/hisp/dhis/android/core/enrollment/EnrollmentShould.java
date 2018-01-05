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

package org.hisp.dhis.android.core.enrollment;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class EnrollmentShould {

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        Enrollment enrollment = objectMapper.readValue("{\n " +
                "    \"trackedEntity\": \"nEenWmSyUEp\",\n " +
                "    \"created\": \"2015-03-28T12:27:50.740\",\n " +
                "    \"orgUnit\": \"Rp268JB6Ne4\",\n " +
                "    \"program\": \"ur1Edk5Oe2n\",\n " +
                "    \"trackedEntityInstance\": \"D2dUWKQErfQ\",\n " +
                "    \"enrollment\": \"BVJQIxoM2o4\",\n " +
                "    \"lastUpdated\": \"2015-03-28T12:27:50.748\",\n " +
                "    \"orgUnitName\": \"Adonkia CHP\",\n " +
                "    \"enrollmentDate\": \"2014-08-07T12:27:50.730\",\n " +
                "    \"followup\": false,\n " +
                "    \"incidentDate\": \"2014-07-21T12:27:50.730\",\n " +
                "    \"status\": \"ACTIVE\",\n " +
                "    \"notes\": [],\n " +
                "    \"attributes\": []\n " +
                "    }",
                Enrollment.class);

        assertThat(enrollment.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-03-28T12:27:50.740"));
        assertThat(enrollment.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-03-28T12:27:50.748"));
        assertThat(enrollment.uid()).isEqualTo("BVJQIxoM2o4");
        assertThat(enrollment.organisationUnit()).isEqualTo("Rp268JB6Ne4");
        assertThat(enrollment.program()).isEqualTo("ur1Edk5Oe2n");
        assertThat(enrollment.dateOfEnrollment()).isEqualTo("2014-08-07T12:27:50.730");
        assertThat(enrollment.dateOfIncident()).isEqualTo("2014-07-21T12:27:50.730");
        assertThat(enrollment.followUp()).isEqualTo(false);
        assertThat(enrollment.enrollmentStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
        assertThat(enrollment.trackedEntityInstance()).isEqualTo("D2dUWKQErfQ");
    }
}
