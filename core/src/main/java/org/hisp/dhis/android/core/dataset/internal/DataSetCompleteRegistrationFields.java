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

package org.hisp.dhis.android.core.dataset.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistration;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationTableInfo;

final class DataSetCompleteRegistrationFields {

    private static final String COMPLETED = "completed";

    private static FieldsHelper<DataSetCompleteRegistration> fieldsHelper = new FieldsHelper<>();

    static final Fields<DataSetCompleteRegistration> allFields = Fields.<DataSetCompleteRegistration>builder().fields(
            fieldsHelper.<String>field(DataSetCompleteRegistrationTableInfo.Columns.PERIOD),
            fieldsHelper.<String>field(DataSetCompleteRegistrationTableInfo.Columns.DATA_SET),
            fieldsHelper.<String>field(DataSetCompleteRegistrationTableInfo.Columns.ORGANISATION_UNIT),
            fieldsHelper.<String>field(DataSetCompleteRegistrationTableInfo.Columns.ATTRIBUTE_OPTION_COMBO),
            fieldsHelper.<String>field(DataSetCompleteRegistrationTableInfo.Columns.DATE),
            fieldsHelper.<String>field(DataSetCompleteRegistrationTableInfo.Columns.STORED_BY),
            fieldsHelper.<String>field(COMPLETED)
    ).build();

    private DataSetCompleteRegistrationFields() {}

}
