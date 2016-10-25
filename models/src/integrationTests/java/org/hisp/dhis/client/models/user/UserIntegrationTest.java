package org.hisp.dhis.client.models.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class UserIntegrationTest {

    DateFormat dateFormat;
    ObjectMapper objectMapper;

    @Before
    public void setup() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);

        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(dateFormat);
    }

    @Test
    public void user_shouldMapFromJsonString() throws IOException, ParseException {
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
        assertThat(user.lastUpdated())
                .isEqualTo(dateFormat.parse("2016-04-06T00:05:57.495"));
        assertThat(user.created())
                .isEqualTo(dateFormat.parse("2015-03-31T13:31:09.324"));
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