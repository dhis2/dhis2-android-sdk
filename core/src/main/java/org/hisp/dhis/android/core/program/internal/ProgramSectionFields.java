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

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.objectstyle.internal.ObjectStyleFields;
import org.hisp.dhis.android.core.program.ProgramSection;
import org.hisp.dhis.android.core.program.ProgramSectionTableInfo.Columns;
import org.hisp.dhis.android.core.program.SectionRendering;

public final class ProgramSectionFields {

    /**
     * @deprecated In version 2.33 and later, use {@link #TRACKED_ENTITY_ATTRIBUTES} instead.
     */
    public static final String ATTRIBUTES = "programTrackedEntityAttribute";
    public static final String TRACKED_ENTITY_ATTRIBUTES = "trackedEntityAttributes";
    private static final String STYLE = "style";
    private static final String RENDER_TYPE = "renderType";

    private static FieldsHelper<ProgramSection> fh = new FieldsHelper<>();

    public static final Fields<ProgramSection> allFields = Fields.<ProgramSection>builder()
            .fields(fh.getIdentifiableFields())
            .fields(
                    fh.<String>field(Columns.DESCRIPTION),
                    fh.nestedFieldWithUid(Columns.PROGRAM),
                    fh.nestedFieldWithUid(ATTRIBUTES),
                    fh.nestedFieldWithUid(TRACKED_ENTITY_ATTRIBUTES),
                    fh.<String>field(Columns.SORT_ORDER),
                    fh.<ObjectStyle>nestedField(STYLE).with(ObjectStyleFields.allFields),
                    fh.<String>field(Columns.FORM_NAME),
                    fh.<SectionRendering>nestedField(RENDER_TYPE)
            ).build();

    private ProgramSectionFields() {
    }
}