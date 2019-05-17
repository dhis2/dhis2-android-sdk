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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.fields.FieldsHelper;
import org.hisp.dhis.android.core.common.Property;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentFields;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.relationship.Relationship229Compatible;
import org.hisp.dhis.android.core.relationship.RelationshipFields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public final class TrackedEntityInstanceFields {

    final static String UID = "trackedEntityInstance";
    final static String CREATED = "created";
    final static String LAST_UPDATED = "lastUpdated";
    final static String ORGANISATION_UNIT = "orgUnit";
    public final static String TRACKED_ENTITY_ATTRIBUTE_VALUES = "attributes";
    final static String RELATIONSHIPS = "relationships";
    final static String TRACKED_ENTITY_TYPE = "trackedEntityType";
    final static String COORDINATES = "coordinates";
    final static String FEATURE_TYPE = "featureType";
    private final static String DELETED = "deleted";
    final static String ENROLLMENTS = "enrollments";

    private static final FieldsHelper<TrackedEntityInstance> fh = new FieldsHelper<>();

    private static List<Property<TrackedEntityInstance, ?>> getCommonFields() {
        return new ArrayList<>(Arrays.asList(
                fh.<String>field(UID),
                fh.<Date>field(CREATED),
                fh.<Date>field(LAST_UPDATED),
                fh.<String>field(ORGANISATION_UNIT),
                fh.<String>field(TRACKED_ENTITY_TYPE),
                fh.<String>field(COORDINATES),
                fh.<FeatureType>field(FEATURE_TYPE),
                fh.<Boolean>field(DELETED),
                fh.<TrackedEntityAttributeValue>nestedField(TRACKED_ENTITY_ATTRIBUTE_VALUES)
                        .with(TrackedEntityAttributeValueFields.allFields)
        ));
    }

    public static final Fields<TrackedEntityInstance> allFields = Fields.<TrackedEntityInstance>builder()
            .fields(getCommonFields())
            .fields(
                    fh.<Relationship229Compatible>nestedField(RELATIONSHIPS)
                            .with(RelationshipFields.allFields),
                    fh.<Enrollment>nestedField(ENROLLMENTS)
                            .with(EnrollmentFields.allFields)
            ).build();

    public static final Fields<TrackedEntityInstance> asRelationshipFields = Fields.<TrackedEntityInstance>builder()
            .fields(getCommonFields()).build();

    private TrackedEntityInstanceFields() {
    }
}