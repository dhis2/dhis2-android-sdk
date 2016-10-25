package org.hisp.dhis.client.models.user;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.client.models.Inject;
import org.hisp.dhis.client.models.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class UserCredentialIntegrationTest {

    @Test
    public void userCredentials_shouldMapFromJsonString() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();

        UserCredentials userCredentials = objectMapper.readValue("{\n" +
                "\n" +
                "    \"code\": \"admin\",\n" +
                "    \"created\": \"2013-04-18T17:15:08.401\",\n" +
                "    \"lastUpdated\": \"2016-10-25T09:21:33.884\",\n" +
                "    \"id\": \"ZyjSDLHGPv4\",\n" +
                "    \"lastLogin\": \"2016-10-25T09:21:33.881\",\n" +
                "    \"passwordLastUpdated\": \"2014-12-18T20:56:05.264\",\n" +
                "    \"invitation\": false,\n" +
                "    \"externalAuth\": false,\n" +
                "    \"selfRegistered\": false,\n" +
                "    \"disabled\": false,\n" +
                "    \"username\": \"admin\",\n" +
                "    \"userInfo\": {\n" +
                "        \"id\": \"xE7jOejl9FI\"\n" +
                "    },\n" +
                "    \"user\": {\n" +
                "        \"id\": \"xE7jOejl9FI\"\n" +
                "    },\n" +
                "    \"cogsDimensionConstraints\": [ ],\n" +
                "    \"catDimensionConstraints\": [ ],\n" +
                "    \"userRoles\": [\n" +
                "        {\n" +
                "            \"id\": \"Ufph3mGRmMo\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"UYXOT4A7JMI\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"aNk5AyC7ydy\"\n" +
                "        }\n" +
                "    ]\n" +
                "\n" +
                "},", UserCredentials.class);


        assertThat(userCredentials.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-10-25T09:21:33.884"));
        assertThat(userCredentials.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2013-04-18T17:15:08.401"));
        assertThat(userCredentials.uid()).isEqualTo("ZyjSDLHGPv4");
        assertThat(userCredentials.username()).isEqualTo("admin");
        assertThat(userCredentials.code()).isEqualTo("admin");

        assertThat(userCredentials.userRoles().get(0).uid()).isEqualTo("Ufph3mGRmMo");
        assertThat(userCredentials.userRoles().get(1).uid()).isEqualTo("UYXOT4A7JMI");
        assertThat(userCredentials.userRoles().get(2).uid()).isEqualTo("aNk5AyC7ydy");

    }

}
