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
package org.hisp.dhis.android.core.program.internal

import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.internal.DataElementFields
import org.hisp.dhis.android.core.program.ProgramStageDataElement
import org.hisp.dhis.android.core.program.ProgramStageDataElementTableInfo.Columns
import org.hisp.dhis.android.network.common.fields.BaseFields
import org.hisp.dhis.android.network.common.fields.Fields

internal object ProgramStageDataElementFields : BaseFields<ProgramStageDataElement>() {
    const val RENDER_TYPE = "renderType"

    val allFields = Fields.from(
        fh.getIdentifiableFields(),
        fh.field(Columns.DISPLAY_IN_REPORTS),
        fh.field(Columns.COMPULSORY),
        fh.field(Columns.ALLOW_PROVIDED_ELSEWHERE),
        fh.field(Columns.SORT_ORDER),
        fh.field(Columns.ALLOW_FUTURE_DATE),
        fh.field(RENDER_TYPE),
        fh.nestedField<DataElement>(Columns.DATA_ELEMENT).with(DataElementFields.allFields),
        fh.nestedField<ObjectWithUid>(Columns.PROGRAM_STAGE).with(ObjectWithUid.uid),
    )
}
