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

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValueTypeRendering
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.dataelement.internal.DataElementFields
import org.hisp.dhis.android.core.program.ProgramStageDataElement
import org.hisp.dhis.android.core.program.ProgramStageDataElementTableInfo

internal object ProgramStageDataElementFields {
    const val RENDER_TYPE = "renderType"

    private val fh = FieldsHelper<ProgramStageDataElement>()

    val allFields: Fields<ProgramStageDataElement> = Fields.builder<ProgramStageDataElement>()
        .fields(fh.getIdentifiableFields())
        .fields(
            fh.field<String>(ProgramStageDataElementTableInfo.Columns.DISPLAY_IN_REPORTS),
            fh.nestedField<DataElement>(ProgramStageDataElementTableInfo.Columns.DATA_ELEMENT)
                .with(DataElementFields.allFields),
            fh.field<Boolean>(ProgramStageDataElementTableInfo.Columns.COMPULSORY),
            fh.field<Boolean>(ProgramStageDataElementTableInfo.Columns.ALLOW_PROVIDED_ELSEWHERE),
            fh.field<Int>(ProgramStageDataElementTableInfo.Columns.SORT_ORDER),
            fh.field<Boolean>(ProgramStageDataElementTableInfo.Columns.ALLOW_FUTURE_DATE),
            fh.field<ValueTypeRendering>(RENDER_TYPE),
            fh.nestedField<ObjectWithUid>(ProgramStageDataElementTableInfo.Columns.PROGRAM_STAGE)
                .with(ObjectWithUid.uid),
        ).build()
}
