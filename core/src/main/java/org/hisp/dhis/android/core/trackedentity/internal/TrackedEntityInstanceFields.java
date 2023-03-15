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

package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.arch.api.fields.internal.Fields;
import org.hisp.dhis.android.core.arch.api.fields.internal.Property;
import org.hisp.dhis.android.core.arch.fields.internal.FieldsHelper;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentFields;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipFields;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceTableInfo.Columns;
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public final class TrackedEntityInstanceFields {

    public final static String UID = "trackedEntityInstance";
    public final static String ORGANISATION_UNIT = "orgUnit";
    public final static String TRACKED_ENTITY_ATTRIBUTE_VALUES = "attributes";
    private final static String RELATIONSHIPS = "relationships";
    public final static String COORDINATES = "coordinates";
    public final static String DELETED = "deleted";
    private final static String ENROLLMENTS = "enrollments";
    public final static String PROGRAM_OWNERS = "programOwners";
    public final static String GEOMETRY = "geometry";

    private static final FieldsHelper<TrackedEntityInstance> fh = new FieldsHelper<>();

    public static final Fields<TrackedEntityInstance> allFields = Fields.<TrackedEntityInstance>builder()
            .fields(getCommonFields())
            .fields(
                    fh.<Relationship>nestedField(RELATIONSHIPS)
                            .with(RelationshipFields.allFields),
                    fh.<Enrollment>nestedField(ENROLLMENTS)
                            .with(EnrollmentFields.allFields),
                    fh.<ProgramOwner>nestedField(PROGRAM_OWNERS)
            ).build();

    public static final Fields<TrackedEntityInstance> asRelationshipFields = Fields.<TrackedEntityInstance>builder()
            .fields(getCommonFields()).build();
    
    private static List<Property<TrackedEntityInstance, ?>> getCommonFields() {
        return new ArrayList<>(Arrays.asList(
                fh.<String>field(UID),
                fh.<Date>field(Columns.CREATED),
                fh.<Date>field(Columns.LAST_UPDATED),
                fh.<String>field(ORGANISATION_UNIT),
                fh.<String>field(Columns.TRACKED_ENTITY_TYPE),
                fh.<String>field(COORDINATES),
                fh.<Geometry>field(GEOMETRY),
                fh.<Boolean>field(DELETED),
                fh.<TrackedEntityAttributeValue>nestedField(TRACKED_ENTITY_ATTRIBUTE_VALUES)
                        .with(TrackedEntityAttributeValueFields.allFields)
        ));
    }

    private TrackedEntityInstanceFields() {
    }
}