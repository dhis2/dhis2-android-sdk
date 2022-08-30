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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.hisp.dhis.android.core.period.PeriodType;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class ProgramStageShould  extends BaseObjectShould implements ObjectShould {

    public ProgramStageShould() {
        super("program/program_stage.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        ProgramStage programStage = objectMapper.readValue(jsonStream, ProgramStage.class);

        assertThat(programStage.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2013-04-10T12:15:02.041"));
        assertThat(programStage.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2013-03-04T11:41:07.541"));

        assertThat(programStage.uid()).isEqualTo("eaDHS084uMp");
        assertThat(programStage.name()).isEqualTo("ANC 1st visit");
        assertThat(programStage.displayName()).isEqualTo("ANC 1st visit");
        assertThat(programStage.description()).isEqualTo("ANC 1st visit");
        assertThat(programStage.displayDescription()).isEqualTo("ANC 1st visit");
        assertThat(programStage.sortOrder()).isEqualTo(1);
        assertThat(programStage.allowGenerateNextVisit()).isFalse();
        assertThat(programStage.autoGenerateEvent()).isTrue();
        assertThat(programStage.blockEntryForm()).isFalse();
        assertThat(programStage.captureCoordinates()).isTrue();
        assertThat(programStage.displayGenerateEventBox()).isFalse();
        assertThat(programStage.executionDateLabel()).isNull();
        assertThat(programStage.dueDateLabel()).isEqualTo("Due date");
        assertThat(programStage.formType()).isEqualTo(FormType.DEFAULT);
        assertThat(programStage.generatedByEnrollmentDate()).isFalse();
        assertThat(programStage.hideDueDate()).isFalse();
        assertThat(programStage.minDaysFromStart()).isEqualTo(0);
        assertThat(programStage.openAfterEnrollment()).isFalse();
        assertThat(programStage.repeatable()).isFalse();
        assertThat(programStage.reportDateToUse()).isEqualTo("false");
        assertThat(programStage.standardInterval()).isNull();
        assertThat(ProgramStageInternalAccessor.accessProgramStageSections(programStage)).isEmpty();
        assertThat(programStage.periodType()).isEqualTo(PeriodType.Monthly);
        assertThat(programStage.remindCompleted()).isFalse();
        assertThat(programStage.featureType()).isEqualTo(FeatureType.POINT);
        assertThat(programStage.enableUserAssignment()).isTrue();

        List<ProgramStageDataElement> dataElements =
                ProgramStageInternalAccessor.accessProgramStageDataElements(programStage);
        assertThat(dataElements.get(0).uid()).isEqualTo("EQCf1l2Mdr8");
        assertThat(dataElements.get(1).uid()).isEqualTo("muxw4SGzUwJ");
        assertThat(dataElements.get(2).uid()).isEqualTo("KWybjio9UZT");
        assertThat(dataElements.get(3).uid()).isEqualTo("ejm0g2hwHHc");
        assertThat(dataElements.get(4).uid()).isEqualTo("yvV3txhSCyc");
        assertThat(dataElements.get(5).uid()).isEqualTo("fzQrjBpbwQD");
        assertThat(dataElements.get(6).uid()).isEqualTo("BbvkNf9PCxX");
        assertThat(dataElements.get(7).uid()).isEqualTo("MbdCfd4HaMQ");
        assertThat(dataElements.get(8).uid()).isEqualTo("SBn4XCFRbyT");
        assertThat(dataElements.get(9).uid()).isEqualTo("F0PZ4nZ86vo");
        assertThat(dataElements.get(10).uid()).isEqualTo("gFBRqVFh60H");
        assertThat(dataElements.get(11).uid()).isEqualTo("ljAoyjH4GYA");
        assertThat(dataElements.get(12).uid()).isEqualTo("MAdsNY2gOlv");
        assertThat(dataElements.get(13).uid()).isEqualTo("IpVUTCDdlGW");
        assertThat(dataElements.get(14).uid()).isEqualTo("psBtdqepNVM");
        assertThat(dataElements.get(15).uid()).isEqualTo("UzB6pZxZ2Rb");
        assertThat(dataElements.get(16).uid()).isEqualTo("FQZEMbBVabW");
    }
}
