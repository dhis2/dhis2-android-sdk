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

package org.hisp.dhis.android.core.program;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class ProgramShould extends BaseObjectShould implements ObjectShould {

    public ProgramShould() {
        super("program/program.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        Program program = objectMapper.readValue(jsonStream, Program.class);

        assertThat(program.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-10-15T11:32:27.242"));
        assertThat(program.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-06-06T20:44:21.375"));

        assertThat(program.uid()).isEqualTo("WSGAb5XwJ3Y");
        assertThat(program.name()).isEqualTo("WHO RMNCH Tracker");
        assertThat(program.displayName()).isEqualTo("WHO RMNCH Tracker");
        assertThat(program.shortName()).isEqualTo("WHO RMNCH Tracker");
        assertThat(program.displayShortName()).isEqualTo("WHO RMNCH Tracker");
        assertThat(program.ignoreOverdueEvents()).isFalse();
        assertThat(program.dataEntryMethod()).isFalse();
        assertThat(program.captureCoordinates()).isTrue();
        assertThat(program.enrollmentDateLabel()).isEqualTo("Date of first visit");
        assertThat(program.onlyEnrollOnce()).isFalse();
        assertThat(program.version()).isEqualTo(11);
        assertThat(program.selectIncidentDatesInFuture()).isTrue();
        assertThat(program.incidentDateLabel()).isEqualTo("Date of incident");
        assertThat(program.selectEnrollmentDatesInFuture()).isFalse();
        assertThat(program.registration()).isTrue();
        assertThat(program.useFirstStageDuringRegistration()).isFalse();
        assertThat(program.minAttributesRequiredToSearch()).isEqualTo(3);
        assertThat(program.maxTeiCountToReturn()).isEqualTo(2);
        assertThat(program.featureType()).isEqualTo(FeatureType.MULTI_POLYGON);
        assertThat(program.accessLevel()).isEqualTo(AccessLevel.PROTECTED);

        assertThat(program.displayFrontPageList()).isFalse();
        assertThat(program.programType()).isEqualTo(ProgramType.WITH_REGISTRATION);
        assertThat(program.displayIncidentDate()).isFalse();
        assertThat(program.categoryCombo().uid()).isEqualTo("p0KPaWEg3cf");
        assertThat(program.trackedEntityType().uid()).isEqualTo("nEenWmSyUEp");
        assertThat(program.relatedProgram().uid()).isEqualTo("IpHINAT79UW");

        assertThat(program.programRuleVariables().get(0).uid()).isEqualTo("varonrw1032");
        assertThat(program.programRuleVariables().get(1).uid()).isEqualTo("idLCptBEOF9");

        assertThat(program.programTrackedEntityAttributes().get(0).uid()).isEqualTo("YGMlKXYa5xF");
        assertThat(program.programTrackedEntityAttributes().get(1).uid()).isEqualTo("WZWEBrkJSAm");

        assertThat(program.programSections().get(0).uid()).isEqualTo("FdpWnXhl7c1");
    }
}