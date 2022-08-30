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

import org.hisp.dhis.android.core.program.ProgramSection;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class ProgramSectionCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<ProgramSection> programSections =
                d2.programModule().programSections()
                        .blockingGet();

        assertThat(programSections.size()).isEqualTo(2);
    }

    @Test
    public void include_attributes_children() {
        ProgramSection programSection =
                d2.programModule().programSections()
                        .withAttributes()
                        .one().blockingGet();

        assertThat(programSection.attributes().size()).isEqualTo(1);
    }

    @Test
    public void filter_by_description() {
        List<ProgramSection> programSections =
                d2.programModule().programSections()
                        .byDescription()
                        .eq("Description")
                        .blockingGet();

        assertThat(programSections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_program() {
        List<ProgramSection> programSections =
                d2.programModule().programSections()
                        .byProgramUid()
                        .eq("lxAQ7Zs9VYR")
                        .blockingGet();

        assertThat(programSections.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_sort_order() {
        List<ProgramSection> programSections =
                d2.programModule().programSections()
                        .bySortOrder()
                        .eq(1)
                        .blockingGet();

        assertThat(programSections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_form_name() {
        List<ProgramSection> programSections =
                d2.programModule().programSections()
                        .byFormName()
                        .eq("formName")
                        .blockingGet();

        assertThat(programSections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_field_color() {
        List<ProgramSection> programSections =
                d2.programModule().programSections()
                        .byColor().eq("#555")
                .blockingGet();
        assertThat(programSections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_field_icon() {
        List<ProgramSection> programSections =
                d2.programModule().programSections()
                .byIcon().eq("section-icon")
                .blockingGet();
        assertThat(programSections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_desktop_render_type() {
        List<ProgramSection> programSections =
                d2.programModule().programSections()
                        .byDesktopRenderType()
                        .eq("LISTING")
                        .blockingGet();

        assertThat(programSections.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_mobile_render_type() {
        List<ProgramSection> programSections =
                d2.programModule().programSections()
                        .byMobileRenderType()
                        .eq("LISTING")
                        .blockingGet();

        assertThat(programSections.size()).isEqualTo(1);
    }

}