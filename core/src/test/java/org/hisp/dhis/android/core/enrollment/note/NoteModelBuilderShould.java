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

package org.hisp.dhis.android.core.enrollment.note;

import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

public class NoteModelBuilderShould {

    @Mock
    private DHISVersionManager versionManager;

    @Mock
    private Enrollment enrollment;

    private NoteModelBuilder noteModelBuilder;

    private String VALUE = "value";
    private String STORED_BY = "storedBy";
    private String STORED_DATE_229 = "2018-03-19 15:20:55.058";
    private String STORED_DATE_230 = "2018-03-19T15:20:55.058";

    private String ENROLLMENT_UID = "enrollment_uid";

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        when(enrollment.uid()).thenReturn(ENROLLMENT_UID);

        noteModelBuilder = new NoteModelBuilder(enrollment, versionManager);
    }

    @Test
    public void parse_note_229() {
        when(versionManager.is2_29()).thenReturn(true);

        Note229Compatible note229 = Note229Compatible.create(VALUE, STORED_BY, STORED_DATE_229);
        NoteModel model = noteModelBuilder.buildModel(note229);

        assertThat(model.enrollment()).isEqualTo(ENROLLMENT_UID);
        assertThat(model.value()).isEqualTo(VALUE);
        assertThat(model.storedBy()).isEqualTo(STORED_BY);
        assertThat(model.storedDate()).isEqualTo(STORED_DATE_229);
    }

    @Test
    public void parse_note_230() {
        when(versionManager.is2_29()).thenReturn(false);

        Note229Compatible note230 = Note229Compatible.create(VALUE, STORED_BY, STORED_DATE_230);
        NoteModel model = noteModelBuilder.buildModel(note230);

        assertThat(model.enrollment()).isEqualTo(ENROLLMENT_UID);
        assertThat(model.value()).isEqualTo(VALUE);
        assertThat(model.storedBy()).isEqualTo(STORED_BY);
        assertThat(model.storedDate()).isEqualTo(STORED_DATE_230);
    }
}