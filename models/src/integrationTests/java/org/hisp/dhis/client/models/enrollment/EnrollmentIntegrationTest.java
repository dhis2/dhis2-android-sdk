package org.hisp.dhis.client.models.enrollment;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.sdk.models.Inject;
import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class EnrollmentIntegrationTest {
    @Test
    public void enrollment_shouldMapFromJsonString() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        Enrollment enrollment = objectMapper.readValue("{\n" +
                "\n" +
                "    \"enrollment\": \"BVJQIxoM2o4\",\n" +
                "    \"created\": \"2015-03-28T12:27:50.740\",\n" +
                "    \"lastUpdated\": \"2015-03-28T12:27:50.748\",\n" +
                "    \"trackedEntity\": \"nEenWmSyUEp\",\n" +
                "    \"trackedEntityInstance\": \"D2dUWKQErfQ\",\n" +
                "    \"program\": \"ur1Edk5Oe2n\",\n" +
                "    \"status\": \"ACTIVE\",\n" +
                "    \"orgUnit\": \"Rp268JB6Ne4\",\n" +
                "    \"orgUnitName\": \"Adonkia CHP\",\n" +
                "    \"enrollmentDate\": \"2014-08-07T12:27:50.730\",\n" +
                "    \"incidentDate\": \"2014-07-21T12:27:50.730\",\n" +
                "    \"followup\": false\n" +
                "\n" +
                "}", Enrollment.class);

        assertThat(enrollment.lastUpdated())
                .isEqualTo(BaseIdentifiableObject.DATE_FORMAT.parse("2015-03-28T12:27:50.748"));
        assertThat(enrollment.created())
                .isEqualTo(BaseIdentifiableObject.DATE_FORMAT.parse("2015-03-28T12:27:50.740"));
        assertThat(enrollment.dateOfEnrollment())
                .isEqualTo(BaseIdentifiableObject.DATE_FORMAT.parse("2014-08-07T12:27:50.730"));
        assertThat(enrollment.dateOfIncident())
                .isEqualTo(BaseIdentifiableObject.DATE_FORMAT.parse("2014-07-21T12:27:50.730"));
        assertThat(enrollment.uid()).isEqualTo("BVJQIxoM2o4");
        assertThat(enrollment.enrollmentStatus().toString()).isEqualTo("ACTIVE");
        assertThat(enrollment.organisationUnit()).isEqualTo("Rp268JB6Ne4");
        assertThat(enrollment.program()).isEqualTo("ur1Edk5Oe2n");
        assertThat(enrollment.followUp()).isFalse();
        assertThat(enrollment.trackedEntityInstance()).isEqualTo("D2dUWKQErfQ");

    }
}
