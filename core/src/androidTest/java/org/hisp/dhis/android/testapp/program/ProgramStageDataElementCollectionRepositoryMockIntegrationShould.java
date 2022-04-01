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

package org.hisp.dhis.android.testapp.program;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.ValueTypeRenderingType;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class ProgramStageDataElementCollectionRepositoryMockIntegrationShould 
        extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<ProgramStageDataElement> programStageDataElements =
                d2.programModule().programStageDataElements()
                        .blockingGet();

        assertThat(programStageDataElements.size()).isEqualTo(6);
    }

    @Test
    public void filter_by_display_in_reports() {
        List<ProgramStageDataElement> programStageDataElements =
                d2.programModule().programStageDataElements()
                        .byDisplayInReports()
                        .isFalse()
                        .blockingGet();

        assertThat(programStageDataElements.size()).isEqualTo(5);
    }

    @Test
    public void filter_by_compulsory() {
        List<ProgramStageDataElement> programStageDataElements =
                d2.programModule().programStageDataElements()
                        .byCompulsory()
                        .isTrue()
                        .blockingGet();

        assertThat(programStageDataElements.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_allow_provided_elsewhere() {
        List<ProgramStageDataElement> programStageDataElements =
                d2.programModule().programStageDataElements()
                        .byAllowProvidedElsewhere()
                        .isTrue()
                        .blockingGet();

        assertThat(programStageDataElements.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_sort_order() {
        List<ProgramStageDataElement> programStageDataElements =
                d2.programModule().programStageDataElements()
                        .bySortOrder()
                        .biggerThan(1)
                        .blockingGet();

        assertThat(programStageDataElements.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_allow_future_date() {
        List<ProgramStageDataElement> programStageDataElements =
                d2.programModule().programStageDataElements()
                        .byAllowFutureDate()
                        .isTrue()
                        .blockingGet();

        assertThat(programStageDataElements.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_data_element() {
        List<ProgramStageDataElement> programStageDataElements =
                d2.programModule().programStageDataElements()
                        .byDataElement()
                        .eq("Ok9OQpitjQr")
                        .blockingGet();

        assertThat(programStageDataElements.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_program_stage() {
        List<ProgramStageDataElement> programStageDataElements =
                d2.programModule().programStageDataElements()
                        .byProgramStage()
                        .eq("dBwrot7S420")
                        .blockingGet();

        assertThat(programStageDataElements.size()).isEqualTo(3);
    }

    @Test
    public void include_render_type_as_children() {
        ProgramStageDataElement programStageDataElement =
                d2.programModule().programStageDataElements()
                        .byUid().eq("QgGD234oA8C")
                        .withRenderType()
                        .one().blockingGet();

        assertThat(programStageDataElement.renderType().mobile().type())
                .isEqualTo(ValueTypeRenderingType.SHARED_HEADER_RADIOBUTTONS);
        assertThat(programStageDataElement.renderType().desktop().type())
                .isEqualTo(ValueTypeRenderingType.VERTICAL_RADIOBUTTONS);
    }

    @Test
    public void order_by_sort_order() {
        List<ProgramStageDataElement> programStageDataElements =
                d2.programModule().programStageDataElements()
                        .byProgramStage().eq("dBwrot7S420")
                        .orderBySortOrder(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();

        assertThat(programStageDataElements.get(0).uid()).isEqualTo("QgGD234oA8i");
        assertThat(programStageDataElements.get(0).sortOrder()).isEqualTo(2);
        assertThat(programStageDataElements.get(1).uid()).isEqualTo("ZD8pd21Dt4i");
        assertThat(programStageDataElements.get(1).sortOrder()).isEqualTo(1);
        assertThat(programStageDataElements.get(2).uid()).isEqualTo("eM6beRIqUnM");
        assertThat(programStageDataElements.get(2).sortOrder()).isEqualTo(0);
    }

}