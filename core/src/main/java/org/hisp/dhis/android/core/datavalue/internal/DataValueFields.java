/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core.datavalue.internal;

import static org.hisp.dhis.android.core.datavalue.DataValueTableInfo.Columns;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.datavalue.DataValue;

public final class DataValueFields {

    public static final String ORGANISATION_UNIT = "orgUnit";
    public static final String FOLLOW_UP = "followup";
    public static final String DELETED = "deleted";

    private static final FieldsHelper<DataValue> fieldsHelper = new FieldsHelper<>();

    static final Fields<DataValue> allFields = Fields.<DataValue>builder().fields(
            fieldsHelper.field(Columns.DATA_ELEMENT),
            fieldsHelper.field(Columns.PERIOD),
            fieldsHelper.field(ORGANISATION_UNIT),
            fieldsHelper.field(Columns.CATEGORY_OPTION_COMBO),
            fieldsHelper.field(Columns.ATTRIBUTE_OPTION_COMBO),
            fieldsHelper.field(Columns.VALUE),
            fieldsHelper.field(Columns.STORED_BY),
            fieldsHelper.field(Columns.CREATED),
            fieldsHelper.field(Columns.LAST_UPDATED),
            fieldsHelper.field(Columns.COMMENT),
            fieldsHelper.field(FOLLOW_UP),
            fieldsHelper.field(DELETED)

    ).build();

    private DataValueFields() {
    }

}
