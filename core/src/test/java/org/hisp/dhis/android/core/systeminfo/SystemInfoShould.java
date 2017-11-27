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

package org.hisp.dhis.android.core.systeminfo;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SystemInfoShould {
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();

        SystemInfo systemInfo = objectMapper.readValue("{\n" +
                        "\"contextPath\": \"https://play.dhis2.org/dev\",\n" +
                        "\"userAgent\": \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.59 Safari/537.36\",\n" +
                        "\"calendar\": \"iso8601\",\n" +
                        "\"dateFormat\": \"yyyy-mm-dd\",\n" +
                        "\"serverDate\": \"2016-10-27T12:59:32.733\",\n" +
                        "\"lastAnalyticsTableSuccess\": \"2016-10-11T17:05:52.897\",\n" +
                        "\"intervalSinceLastAnalyticsTableSuccess\": \"379 h, 53 m, 39 s\",\n" +
                        "\"lastAnalyticsTableRuntime\": \"7 m, 42 s\",\n" +
                        "\"version\": \"2.26-SNAPSHOT\",\n" +
                        "\"revision\": \"954aba7\",\n" +
                        "\"buildTime\": \"2016-10-27T12:37:23.000\",\n" +
                        "\"jasperReportsVersion\": \"6.1.0\",\n" +
                        "\"environmentVariable\": \"DHIS2_HOME\",\n" +
                        "\"fileStoreProvider\": \"aws-s3\",\n" +
                        "\"readOnlyMode\": \"off\",\n" +
                        "\"javaVersion\": \"1.8.0_101\",\n" +
                        "\"javaVendor\": \"Oracle Corporation\",\n" +
                        "\"javaOpts\": \"-Xmx2000m -Xms2000m\",\n" +
                        "\"osName\": \"Linux\",\n" +
                        "\"osArchitecture\": \"amd64\",\n" +
                        "\"osVersion\": \"3.13.0-48-generic\",\n" +
                        "\"externalDirectory\": \"/ebs1/instances/dev/home\",\n" +
                        "\"databaseInfo\": {\n" +
                        "\"type\": \"PostgreSQL\",\n" +
                        "\"spatialSupport\": true\n" +
                        "},\n" +
                        "\"readReplicaCount\": 0,\n" +
                        "\"memoryInfo\": \"Mem Total in JVM: 1883 Free in JVM: 1486 Max Limit: 1883\",\n" +
                        "\"cpuCores\": 4,\n" +
                        "\"encryption\": false,\n" +
                        "\"systemId\": \"eed3d451-4ff5-4193-b951-ffcc68954299\",\n" +
                        "\"isMetadataVersionEnabled\": true,\n" +
                        "\"isMetadataSyncEnabled\": false\n" +
                        "}",
                SystemInfo.class);

        assertThat(systemInfo.serverDate()).isEqualTo("2016-10-27T12:59:32.733");
        assertThat(systemInfo.dateFormat()).isEqualTo("yyyy-mm-dd");
    }
}
