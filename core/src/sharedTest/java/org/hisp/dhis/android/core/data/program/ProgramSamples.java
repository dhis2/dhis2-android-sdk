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

package org.hisp.dhis.android.core.data.program;

import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramType;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;

import static org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.fillNameableProperties;

public class ProgramSamples {

    public static Program getProgram() {
        Program.Builder builder = Program.builder();

        fillNameableProperties(builder);
        builder
                .id(1L)
                .version(1)
                .onlyEnrollOnce(true)
                .enrollmentDateLabel("enrollment_date_label")
                .displayIncidentDate(false)
                .incidentDateLabel("incident_date_label")
                .registration(true)
                .selectEnrollmentDatesInFuture(true)
                .dataEntryMethod(false)
                .ignoreOverdueEvents(false)
                .relationshipFromA(true)
                .selectIncidentDatesInFuture(true)
                .captureCoordinates(true)
                .useFirstStageDuringRegistration(true)
                .displayFrontPageList(false)
                .programType(ProgramType.WITH_REGISTRATION)
                .relationshipType(RelationshipType.builder().uid("relationship_type_uid").build())
                .relationshipText("relationship_text")
                .relatedProgram(Program.builder().uid("program_uid").build())
                .trackedEntityType(TrackedEntityType.builder().uid("tracked_entity_type").build())
                .categoryCombo(CategoryCombo.builder().uid("category_combo_uid").build())
                .access(Access.create(null, null, null, null, null,null,
                        DataAccess.create(true, true)))
                .expiryDays(2)
                .completeEventsExpiryDays(3)
                .minAttributesRequiredToSearch(1)
                .maxTeiCountToReturn(2)
                .build();
        return builder.build();
    }
}