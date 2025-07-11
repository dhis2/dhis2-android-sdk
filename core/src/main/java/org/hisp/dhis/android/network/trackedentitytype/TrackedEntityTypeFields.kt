/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.network.trackedentitytype

import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeTableInfo.Columns
import org.hisp.dhis.android.network.common.fields.AccessFields
import org.hisp.dhis.android.network.common.fields.BaseFields
import org.hisp.dhis.android.network.common.fields.DataAccessFields
import org.hisp.dhis.android.network.common.fields.Fields
import org.hisp.dhis.android.network.common.fields.ObjectStyleFields

internal object TrackedEntityTypeFields : BaseFields<TrackedEntityType>() {
    private const val STYLE = "style"
    const val TRACKED_ENTITY_TYPE_ATTRIBUTES = "trackedEntityTypeAttributes"
    private const val ACCESS = "access"

    val uid = fh.uid()
    val lastUpdated = fh.lastUpdated()

    val allFields = Fields.from(
        fh.getNameableFields(),
        fh.field(Columns.FEATURE_TYPE),
        fh.nestedField<TrackedEntityTypeAttribute>(TRACKED_ENTITY_TYPE_ATTRIBUTES)
            .with(TrackedEntityTypeAttributeFields.allFields),
        fh.nestedField<ObjectStyle>(STYLE).with(ObjectStyleFields.allFields),
        fh.nestedField<Access>(ACCESS).with(AccessFields.data.with(DataAccessFields.allFields)),
    )
}
