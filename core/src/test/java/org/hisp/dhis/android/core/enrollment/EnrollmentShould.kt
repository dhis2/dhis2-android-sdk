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

package org.hisp.dhis.android.core.enrollment;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.Coordinates;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class EnrollmentShould extends BaseObjectShould implements ObjectShould {

    public EnrollmentShould() {
        super("enrollment/enrollment.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        Enrollment enrollment = objectMapper.readValue(jsonStream, Enrollment.class);

        assertThat(enrollment.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-03-28T12:27:50.740"));
        assertThat(enrollment.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-03-28T12:27:50.748"));
        assertThat(enrollment.uid()).isEqualTo("BVJQIxoM2o4");
        assertThat(enrollment.organisationUnit()).isEqualTo("Rp268JB6Ne4");
        assertThat(enrollment.program()).isEqualTo("ur1Edk5Oe2n");
        assertThat(enrollment.enrollmentDate()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-08-07T12:27:50.730"));
        assertThat(enrollment.incidentDate()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-07-21T12:27:50.730"));
        assertThat(enrollment.completedDate()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-08-21T12:27:50.730"));
        assertThat(enrollment.followUp()).isFalse();
        assertThat(enrollment.status()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(enrollment.coordinate()).isEqualTo(Coordinates.create(10.03, 11.11));
        assertThat(enrollment.trackedEntityInstance()).isEqualTo("D2dUWKQErfQ");
        assertThat(enrollment.geometry().type()).isEqualTo(FeatureType.POINT);
        assertThat(enrollment.geometry().coordinates()).isEqualTo("[11.11, 10.03]");
        assertThat(enrollment.deleted()).isFalse();

        assertThat(enrollment.notes().get(0).uid()).isEqualTo("enrollmentNote1");
        assertThat(enrollment.notes().get(1).uid()).isEqualTo("enrollmentNote2");

        assertThat(enrollment.relationships().get(0).uid()).isEqualTo("hm6qYjPfnzn");
        assertThat(enrollment.relationships().get(0).from().enrollment().enrollment()).isEqualTo("BVJQIxoM2o4");
    }
}
