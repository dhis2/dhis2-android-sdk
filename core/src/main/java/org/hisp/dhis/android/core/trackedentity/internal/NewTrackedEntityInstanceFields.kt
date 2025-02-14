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
package org.hisp.dhis.android.core.trackedentity.internal

import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationship
import org.hisp.dhis.android.core.relationship.internal.NewRelationshipFields
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntity
import org.hisp.dhis.android.core.trackedentity.NewTrackerImporterTrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.hisp.dhis.android.network.common.fields.BaseFields
import org.hisp.dhis.android.network.common.fields.Fields
import org.hisp.dhis.android.network.tracker.NewEnrollmentFields

internal object NewTrackedEntityInstanceFields : BaseFields<NewTrackerImporterTrackedEntity>() {
    private const val UID = "trackedEntity"
    private const val CREATED_AT = "createdAt"
    private const val UPDATED_AT = "updatedAt"
    private const val CREATED_AT_CLIENT = "createdAtClient"
    private const val UPDATED_AT_CLIENT = "updatedAtClient"
    private const val ORGANISATION_UNIT = "orgUnit"
    private const val TRACKED_ENTITY_TYPE = "trackedEntityType"
    private const val TRACKED_ENTITY_ATTRIBUTE_VALUES = "attributes"
    private const val RELATIONSHIPS = "relationships"
    private const val DELETED = "deleted"
    private const val ENROLLMENTS = "enrollments"
    private const val PROGRAM_OWNERS = "programOwners"
    private const val GEOMETRY = "geometry"

    val asRelationshipFields = Fields.from(commonFields())

    val allFields = Fields.from(
        commonFields(),
        fh.nestedField<NewTrackerImporterRelationship>(RELATIONSHIPS).with(NewRelationshipFields.allFields),
        fh.nestedField<NewTrackerImporterEnrollment>(ENROLLMENTS).with(NewEnrollmentFields.allFields),
        fh.nestedField<ProgramOwner>(PROGRAM_OWNERS),
    )

    private fun commonFields() = listOf(
        fh.field(UID),
        fh.field(CREATED_AT),
        fh.field(UPDATED_AT),
        fh.field(CREATED_AT_CLIENT),
        fh.field(UPDATED_AT_CLIENT),
        fh.field(ORGANISATION_UNIT),
        fh.field(TRACKED_ENTITY_TYPE),
        fh.field(GEOMETRY),
        fh.field(DELETED),
        fh.nestedField<NewTrackerImporterTrackedEntityAttributeValue>(TRACKED_ENTITY_ATTRIBUTE_VALUES)
            .with(NewTrackedEntityAttributeValueFields.allFields),
    )
}
