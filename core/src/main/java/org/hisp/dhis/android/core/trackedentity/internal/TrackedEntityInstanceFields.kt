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
package org.hisp.dhis.android.core.trackedentity.internal

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentFields
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipFields
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import java.util.*

internal object TrackedEntityInstanceFields {
    const val UID = "trackedEntityInstance"
    const val ORGANISATION_UNIT = "orgUnit"
    const val TRACKED_ENTITY_ATTRIBUTE_VALUES = "attributes"
    private const val RELATIONSHIPS = "relationships"
    const val COORDINATES = "coordinates"
    const val DELETED = "deleted"
    private const val ENROLLMENTS = "enrollments"
    const val PROGRAM_OWNERS = "programOwners"
    const val GEOMETRY = "geometry"
    private val fh = FieldsHelper<TrackedEntityInstance>()

    val allFields: Fields<TrackedEntityInstance> = commonFields()
        .fields(
            fh.nestedField<Relationship>(RELATIONSHIPS)
                .with(RelationshipFields.allFields),
            fh.nestedField<Enrollment>(ENROLLMENTS)
                .with(EnrollmentFields.allFields),
            fh.nestedField<ProgramOwner>(PROGRAM_OWNERS)
        ).build()

    val asRelationshipFields: Fields<TrackedEntityInstance> = commonFields().build()

    private fun commonFields(): Fields.Builder<TrackedEntityInstance> {
        return Fields.builder<TrackedEntityInstance>().fields(
            fh.field<String>(UID),
            fh.field<Date>(TrackedEntityInstanceTableInfo.Columns.CREATED),
            fh.field<Date>(TrackedEntityInstanceTableInfo.Columns.LAST_UPDATED),
            fh.field<String>(ORGANISATION_UNIT),
            fh.field<String>(TrackedEntityInstanceTableInfo.Columns.TRACKED_ENTITY_TYPE),
            fh.field<String>(COORDINATES),
            fh.field<Geometry>(GEOMETRY),
            fh.field<Boolean>(DELETED),
            fh.nestedField<TrackedEntityAttributeValue>(TRACKED_ENTITY_ATTRIBUTE_VALUES)
                .with(TrackedEntityAttributeValueFields.allFields)
        )
    }
}
