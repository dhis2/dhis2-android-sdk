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

package org.hisp.dhis.android.core.organisationunit;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.FeatureType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class OrganisationUnitShould {

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        InputStream jsonStream = this.getClass().getClassLoader()
                .getResourceAsStream("organisationunit/organisation_unit.json");

        OrganisationUnit organisationUnit = objectMapper.readValue(jsonStream, OrganisationUnit.class);


        assertThat(organisationUnit.uid()).isEqualTo("FLjwMPWLrL2");

        assertThat(organisationUnit.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2014-11-25T09:37:54.154"));
        assertThat(organisationUnit.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2012-02-17T15:54:39.987"));
        assertThat(organisationUnit.openingDate()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("1970-01-01T00:00:00.000"));
        assertThat(organisationUnit.closedDate()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("1971-01-01T00:00:00.000"));

        assertThat(organisationUnit.code()).isEqualTo("OU_1126");

        // names
        assertThat(organisationUnit.level()).isEqualTo(4);
        assertThat(organisationUnit.name()).isEqualTo("Baomahun CHC");
        assertThat(organisationUnit.shortName()).isEqualTo("Baomahun CHC");
        assertThat(organisationUnit.displayName()).isEqualTo("Baomahun CHC");
        assertThat(organisationUnit.displayShortName()).isEqualTo("Baomahun CHC");
        assertThat(organisationUnit.parent().uid()).isEqualTo("npWGUj37qDe");

        // geometry
        assertThat(organisationUnit.geometry().type()).isEqualTo(FeatureType.POINT);
        assertThat(organisationUnit.geometry().coordinates()).isEqualTo("[9.0, 9.0]");

        // checking programs
        assertThat(organisationUnit.programs().get(0).uid()).isEqualTo("uy2gU8kT1jF");
        assertThat(organisationUnit.programs().get(1).uid()).isEqualTo("q04UBOqq3rp");
        assertThat(organisationUnit.programs().get(2).uid()).isEqualTo("VBqh0ynB2wv");
        assertThat(organisationUnit.programs().get(3).uid()).isEqualTo("eBAyeGv0exc");

        // checking dataSets
        assertThat(organisationUnit.dataSets().get(0).uid()).isEqualTo("EDzMBk0RRji");
        assertThat(organisationUnit.dataSets().get(1).uid()).isEqualTo("VTdjfLXXmoi");
        assertThat(organisationUnit.dataSets().get(2).uid()).isEqualTo("aLpVgfXiz0f");
        assertThat(organisationUnit.dataSets().get(3).uid()).isEqualTo("N4fIX1HL3TQ");

        // checking ancestors
        assertThat(organisationUnit.ancestors().get(0).uid()).isEqualTo("ImspTQPwCqd");
        assertThat(organisationUnit.ancestors().get(1).uid()).isEqualTo("O6uvpzGd5pu");
        assertThat(organisationUnit.ancestors().get(2).uid()).isEqualTo("npWGUj37qDe");
        assertThat(organisationUnit.ancestors().get(0).displayName()).isEqualTo("Sierra Leone");
        assertThat(organisationUnit.ancestors().get(1).displayName()).isEqualTo("Bo");
        assertThat(organisationUnit.ancestors().get(2).displayName()).isEqualTo("Valunia");
    }

    @Test
    public void map_coordinates_and_feature_type_to_geometry() throws IOException {
        ObjectMapper objectMapper = Inject.objectMapper();
        InputStream jsonStream = this.getClass().getClassLoader()
                .getResourceAsStream("organisationunit/organisation_unit_with_feature_type_and_coordinates.json");

        OrganisationUnit organisationUnit = objectMapper.readValue(jsonStream, OrganisationUnit.class);

        assertThat(organisationUnit.uid()).isEqualTo("FLjwMPWLrL2");

        // geometry
        assertThat(organisationUnit.geometry().type()).isEqualTo(FeatureType.POINT);
        assertThat(organisationUnit.geometry().coordinates()).isEqualTo("[-11.6677,8.4165]");
    }
}
