/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.arch.fields.FieldsHelper;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.dataelement.DataElementOperandFields;

public final class SectionFields {

    final static String DESCRIPTION = "description";
    final static String SORT_ORDER = "sortOrder";
    final static String DATA_SET = "dataSet";
    final static String SHOW_ROW_TOTALS = "showRowTotals";
    final static String SHOW_COLUMN_TOTALS = "showColumnTotals";
    private final static String DATA_ELEMENTS = "dataElements";
    private final static String GREYED_FIELDS = "greyedFields";

    private static final FieldsHelper<Section> fh = new FieldsHelper<>();

    public static final Fields<Section> allFields = Fields.<Section>builder()
            .fields(fh.getIdentifiableFields())
            .fields(
                    fh.<String>field(DESCRIPTION),
                    fh.<Integer>field(SORT_ORDER),
                    fh.nestedFieldWithUid(DATA_SET),
                    fh.<Boolean>field(SHOW_ROW_TOTALS),
                    fh.<Boolean>field(SHOW_COLUMN_TOTALS),
                    fh.nestedFieldWithUid(DATA_ELEMENTS),
                    fh.<DataElementOperand>nestedField(GREYED_FIELDS)
                            .with(DataElementOperandFields.allFields)
            ).build();

    private SectionFields() {
    }
}