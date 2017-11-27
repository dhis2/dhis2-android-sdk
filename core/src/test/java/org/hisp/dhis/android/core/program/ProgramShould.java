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

package org.hisp.dhis.android.core.program;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.Inject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ProgramShould {
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ObjectMapper objectMapper = Inject.objectMapper();
        Program program = objectMapper.readValue("{\n" +
                "\n" +
                "    \"lastUpdated\": \"2015-10-15T11:32:27.242\",\n" +
                "    \"id\": \"WSGAb5XwJ3Y\",\n" +
                "    \"created\": \"2014-06-06T20:44:21.375\",\n" +
                "    \"name\": \"WHO RMNCH Tracker\",\n" +
                "    \"shortName\": \"WHO RMNCH Tracker\",\n" +
                "    \"publicAccess\": \"rw------\",\n" +
                "    \"ignoreOverdueEvents\": false,\n" +
                "    \"skipOffline\": false,\n" +
                "    \"dataEntryMethod\": false,\n" +
                "    \"captureCoordinates\": false,\n" +
                "    \"enrollmentDateLabel\": \"Date of first visit\",\n" +
                "    \"onlyEnrollOnce\": false,\n" +
                "    \"version\": 11,\n" +
                "    \"selectIncidentDatesInFuture\": true,\n" +
                "    \"incidentDateLabel\": \"Date of incident\",\n" +
                "    \"selectEnrollmentDatesInFuture\": false,\n" +
                "    \"registration\": true,\n" +
                "    \"useFirstStageDuringRegistration\": false,\n" +
                "    \"displayName\": \"WHO RMNCH Tracker\",\n" +
                "    \"completeEventsExpiryDays\": 0,\n" +
                "    \"displayShortName\": \"WHO RMNCH Tracker\",\n" +
                "    \"externalAccess\": false,\n" +
                "    \"withoutRegistration\": false,\n" +
                "    \"displayFrontPageList\": false,\n" +
                "    \"programType\": \"WITH_REGISTRATION\",\n" +
                "    \"relationshipFromA\": true,\n" +
                "    \"relationshipText\": \"Add child\",\n" +
                "    \"displayIncidentDate\": false,\n" +
                "    \"expiryDays\": 0,\n" +
                "    \"categoryCombo\": {\n" +
                "        \"id\": \"p0KPaWEg3cf\"\n" +
                "    },\n" +
                "    \"trackedEntity\": {\n" +
                "        \"id\": \"nEenWmSyUEp\"\n" +
                "    },\n" +
                "    \"relatedProgram\": {\n" +
                "        \"id\": \"IpHINAT79UW\"\n" +
                "    },\n" +
                "    \"relationshipType\": {\n" +
                "        \"id\": \"V2kkHafqs8G\"\n" +
                "    },\n" +
                "    \"user\": {\n" +
                "        \"id\": \"xE7jOejl9FI\"\n" +
                "    },\n" +
                "    \"programIndicators\": [ ],\n" +
                "    \"translations\": [ ],\n" +
                "    \"userGroupAccesses\": [ ],\n" +
                "    \"attributeValues\": [ ],\n" +
                "    \"validationCriterias\": [ ],\n" +
                "    \"userRoles\": [\n" +
                "        {\n" +
                "            \"id\": \"Ufph3mGRmMo\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"LGWLyWNro4x\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"programRuleVariables\": [\n" +
                "        {\n" +
                "            \"id\": \"varonrw1032\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"idLCptBEOF9\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"iGBDXK1Gbcb\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"varonrw1026\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"pJ79JBmuFT4\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"YnREl4QOKFB\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"varonrw1030\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"yrhN4QM8gqg\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"WS8wNA4Jvev\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"varonrw1027\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"OnXTZg6lPA6\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"Jx93QwiIP1B\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"varonrw1029\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"AU9n351Zwno\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"k8QP8SWkQtA\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"VY8KZyr8d2d\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"varonrw1028\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"varonrw1033\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"varonrw1031\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"WuSvlMvjeu9\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"d1XWbV9mlle\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"XJ3gR8WFYP1\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"programTrackedEntityAttributes\": [\n" +
                "        {\n" +
                "            \"id\": \"YGMlKXYa5xF\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"WZWEBrkJSAm\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"v4GVdtLY8KV\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"pasqxL7MU6h\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"B5ggND5eXU5\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"pCliTGHh5WS\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"ypiK0ehyoCv\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"Z5eRPdPIL49\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"dHThsTGLdC3\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"eNdG4pvswEX\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"notificationTemplates\": [ ],\n" +
                "    \"programStages\": [\n" +
                "        {\n" +
                "            \"id\": \"WZbXY0S00lP\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"PUZaKR0Jh2k\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"edqlbukwRfQ\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"PFDfvmGpsR3\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"bbKtnxRZKEP\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"programRules\": [\n" +
                "        {\n" +
                "            \"id\": \"tO1D62oB0tq\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"ruleonr1065\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"jZ6TKNCRhdt\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"HTKIQDVMu0K\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"ruleonr1066\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"ruleonr1055\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"ruleonr1061\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"RtCIjfyRB9L\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"hpO8g3CRAeC\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"IdrDtQmRGrv\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"NkpU28ZlfVh\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"GTtHkDt4doY\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": \"ruleonr1062\"\n" +
                "        }\n" +
                "    ]\n" +
                "\n" +
                "}", Program.class);

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
        assertThat(program.captureCoordinates()).isFalse();
        assertThat(program.enrollmentDateLabel()).isEqualTo("Date of first visit");
        assertThat(program.onlyEnrollOnce()).isFalse();
        assertThat(program.version()).isEqualTo(11);
        assertThat(program.selectIncidentDatesInFuture()).isTrue();
        assertThat(program.incidentDateLabel()).isEqualTo("Date of incident");
        assertThat(program.selectEnrollmentDatesInFuture()).isFalse();
        assertThat(program.registration()).isTrue();
        assertThat(program.useFirstStageDuringRegistration()).isFalse();

        assertThat(program.displayFrontPageList()).isFalse();
        assertThat(program.programType()).isEqualTo(ProgramType.WITH_REGISTRATION);
        assertThat(program.relationshipFromA()).isTrue();
        assertThat(program.relationshipText()).isEqualTo("Add child");
        assertThat(program.displayIncidentDate()).isFalse();
        assertThat(program.categoryCombo().uid()).isEqualTo("p0KPaWEg3cf");
        assertThat(program.trackedEntity().uid()).isEqualTo("nEenWmSyUEp");
        assertThat(program.relatedProgram().uid()).isEqualTo("IpHINAT79UW");
        assertThat(program.relationshipType().uid()).isEqualTo("V2kkHafqs8G");
        assertThat(program.programIndicators()).isEmpty();

        assertThat(program.programStages().get(0).uid()).isEqualTo("WZbXY0S00lP");
        assertThat(program.programStages().get(1).uid()).isEqualTo("PUZaKR0Jh2k");
        assertThat(program.programStages().get(2).uid()).isEqualTo("edqlbukwRfQ");
        assertThat(program.programStages().get(3).uid()).isEqualTo("PFDfvmGpsR3");
        assertThat(program.programStages().get(4).uid()).isEqualTo("bbKtnxRZKEP");

        assertThat(program.programRules().get(0).uid()).isEqualTo("tO1D62oB0tq");
        assertThat(program.programRules().get(1).uid()).isEqualTo("ruleonr1065");
        assertThat(program.programRules().get(2).uid()).isEqualTo("jZ6TKNCRhdt");
        assertThat(program.programRules().get(3).uid()).isEqualTo("HTKIQDVMu0K");
        assertThat(program.programRules().get(4).uid()).isEqualTo("ruleonr1066");
        assertThat(program.programRules().get(5).uid()).isEqualTo("ruleonr1055");
        assertThat(program.programRules().get(6).uid()).isEqualTo("ruleonr1061");
        assertThat(program.programRules().get(7).uid()).isEqualTo("RtCIjfyRB9L");
        assertThat(program.programRules().get(8).uid()).isEqualTo("hpO8g3CRAeC");
        assertThat(program.programRules().get(9).uid()).isEqualTo("IdrDtQmRGrv");
        assertThat(program.programRules().get(10).uid()).isEqualTo("NkpU28ZlfVh");
        assertThat(program.programRules().get(11).uid()).isEqualTo("GTtHkDt4doY");
        assertThat(program.programRules().get(12).uid()).isEqualTo("ruleonr1062");

        assertThat(program.programRuleVariables().get(0).uid()).isEqualTo("varonrw1032");
        assertThat(program.programRuleVariables().get(1).uid()).isEqualTo("idLCptBEOF9");
        assertThat(program.programRuleVariables().get(2).uid()).isEqualTo("iGBDXK1Gbcb");
        assertThat(program.programRuleVariables().get(3).uid()).isEqualTo("varonrw1026");
        assertThat(program.programRuleVariables().get(4).uid()).isEqualTo("pJ79JBmuFT4");
        assertThat(program.programRuleVariables().get(5).uid()).isEqualTo("YnREl4QOKFB");
        assertThat(program.programRuleVariables().get(6).uid()).isEqualTo("varonrw1030");
        assertThat(program.programRuleVariables().get(7).uid()).isEqualTo("yrhN4QM8gqg");
        assertThat(program.programRuleVariables().get(8).uid()).isEqualTo("WS8wNA4Jvev");
        assertThat(program.programRuleVariables().get(9).uid()).isEqualTo("varonrw1027");
        assertThat(program.programRuleVariables().get(10).uid()).isEqualTo("OnXTZg6lPA6");
        assertThat(program.programRuleVariables().get(11).uid()).isEqualTo("Jx93QwiIP1B");
        assertThat(program.programRuleVariables().get(12).uid()).isEqualTo("varonrw1029");
        assertThat(program.programRuleVariables().get(13).uid()).isEqualTo("AU9n351Zwno");
        assertThat(program.programRuleVariables().get(14).uid()).isEqualTo("k8QP8SWkQtA");
        assertThat(program.programRuleVariables().get(15).uid()).isEqualTo("VY8KZyr8d2d");
        assertThat(program.programRuleVariables().get(16).uid()).isEqualTo("varonrw1028");
        assertThat(program.programRuleVariables().get(17).uid()).isEqualTo("varonrw1033");
        assertThat(program.programRuleVariables().get(18).uid()).isEqualTo("varonrw1031");
        assertThat(program.programRuleVariables().get(19).uid()).isEqualTo("WuSvlMvjeu9");
        assertThat(program.programRuleVariables().get(20).uid()).isEqualTo("d1XWbV9mlle");
        assertThat(program.programRuleVariables().get(21).uid()).isEqualTo("XJ3gR8WFYP1");

        assertThat(program.programTrackedEntityAttributes().get(0).uid()).isEqualTo("YGMlKXYa5xF");
        assertThat(program.programTrackedEntityAttributes().get(1).uid()).isEqualTo("WZWEBrkJSAm");
        assertThat(program.programTrackedEntityAttributes().get(2).uid()).isEqualTo("v4GVdtLY8KV");
        assertThat(program.programTrackedEntityAttributes().get(3).uid()).isEqualTo("pasqxL7MU6h");
        assertThat(program.programTrackedEntityAttributes().get(4).uid()).isEqualTo("B5ggND5eXU5");
        assertThat(program.programTrackedEntityAttributes().get(5).uid()).isEqualTo("pCliTGHh5WS");
        assertThat(program.programTrackedEntityAttributes().get(6).uid()).isEqualTo("ypiK0ehyoCv");
        assertThat(program.programTrackedEntityAttributes().get(7).uid()).isEqualTo("Z5eRPdPIL49");
        assertThat(program.programTrackedEntityAttributes().get(8).uid()).isEqualTo("dHThsTGLdC3");
        assertThat(program.programTrackedEntityAttributes().get(9).uid()).isEqualTo("eNdG4pvswEX");
    }
}
