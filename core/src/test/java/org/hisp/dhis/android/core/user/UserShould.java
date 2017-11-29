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

package org.hisp.dhis.android.core.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class UserShould {

    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        User user = objectMapper.readValue("{\n" +
                "\n" +
                "    \"lastUpdated\": \"2016-04-06T00:05:57.495\",\n" +
                "    \"id\": \"DXyJmlo9rge\",\n" +
                "    \"created\": \"2015-03-31T13:31:09.324\",\n" +
                "    \"name\": \"John Barnes\",\n" +
                "    \"displayName\": \"John Barnes\",\n" +
                "    \"externalAccess\": false,\n" +
                "    \"surname\": \"Barnes\",\n" +
                "    \"email\": \"john@hmail.com\",\n" +
                "    \"firstName\": \"John\",\n" +
                "    \"userCredentials\": {\n" +
                "        \"code\": \"android\",\n" +
                "        \"lastUpdated\": \"2016-10-24T13:44:39.346\",\n" +
                "        \"id\": \"M0fCOxtkURr\",\n" +
                "        \"created\": \"2015-03-31T13:31:09.206\",\n" +
                "        \"name\": \"John Traore\",\n" +
                "        \"lastLogin\": \"2016-10-24T13:44:39.342\",\n" +
                "        \"displayName\": \"John Traore\",\n" +
                "        \"externalAuth\": false,\n" +
                "        \"externalAccess\": false,\n" +
                "        \"disabled\": false,\n" +
                "        \"passwordLastUpdated\": \"2015-03-31T13:31:09.206\",\n" +
                "        \"invitation\": false,\n" +
                "        \"selfRegistered\": false,\n" +
                "        \"username\": \"android\",\n" +
                "        \"userInfo\": {\n" +
                "            \"id\": \"DXyJmlo9rge\"\n" +
                "        },\n" +
                "        \"user\": {\n" +
                "            \"id\": \"xE7jOejl9FI\"\n" +
                "        },\n" +
                "        \"cogsDimensionConstraints\": [ ],\n" +
                "        \"catDimensionConstraints\": [ ],\n" +
                "        \"translations\": [ ],\n" +
                "        \"userGroupAccesses\": [ ],\n" +
                "        \"attributeValues\": [ ],\n" +
                "        \"userRoles\": [\n" +
                "            {\n" +
                "                \"id\": \"Ufph3mGRmMo\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"Euq3XfEIEbx\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"DRdaVRtwmG5\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"cUlTcejWree\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"jRWSNIHdKww\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"id\": \"txB7vu1w2Pr\"\n" +
                "            }\n" +
                "        ]\n" +
                "    },\n" +
                "    \"teiSearchOrganisationUnits\": [ ],\n" +
                "    \"translations\": [ ],\n" +
                "    \"organisationUnits\": [\n" +
                "        {\n" +
                "            \"id\": \"DiszpKrYNg8\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"dataViewOrganisationUnits\": [\n" +
                "        {\n" +
                "            \"id\": \"YuQRtpLP10I\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"userGroupAccesses\": [ ],\n" +
                "    \"attributeValues\": [ ],\n" +
                "    \"userGroups\": [ ]\n" +
                "\n" +
                "}", User.class);

        assertThat(user.name()).isEqualTo("John Barnes");
        assertThat(user.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-06T00:05:57.495"));
        assertThat(user.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-03-31T13:31:09.324"));
        assertThat(user.uid()).isEqualTo("DXyJmlo9rge");
        assertThat(user.surname()).isEqualTo("Barnes");
        assertThat(user.firstName()).isEqualTo("John");
        assertThat(user.email()).isEqualTo("john@hmail.com");
        assertThat(user.displayName()).isEqualTo("John Barnes");
        assertThat(user.userCredentials().uid()).isEqualTo("M0fCOxtkURr");
        assertThat(user.userCredentials().username()).isEqualTo("android");

        assertThat(user.userCredentials().userRoles().get(0).uid()).isEqualTo("Ufph3mGRmMo");
        assertThat(user.userCredentials().userRoles().get(1).uid()).isEqualTo("Euq3XfEIEbx");
        assertThat(user.userCredentials().userRoles().get(2).uid()).isEqualTo("DRdaVRtwmG5");
        assertThat(user.userCredentials().userRoles().get(3).uid()).isEqualTo("cUlTcejWree");
        assertThat(user.userCredentials().userRoles().get(4).uid()).isEqualTo("jRWSNIHdKww");
        assertThat(user.userCredentials().userRoles().get(5).uid()).isEqualTo("txB7vu1w2Pr");

        assertThat(user.organisationUnits().get(0).uid()).isEqualTo("DiszpKrYNg8");

        assertThat(user.dataViewOrganisationUnits().get(0).uid()).isEqualTo("YuQRtpLP10I");
    }
}