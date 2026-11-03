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
package org.hisp.dhis.android.core.arch.api.fields.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.network.attribute.AttributeValueFields
import org.hisp.dhis.android.network.common.fields.AccessFields
import org.hisp.dhis.android.network.common.fields.DataAccessFields
import org.hisp.dhis.android.network.common.fields.Fields
import org.hisp.dhis.android.network.common.fields.ObjectStyleFields
import org.hisp.dhis.android.network.common.fields.Property
import org.hisp.dhis.android.network.dataelement.DataElementFields
import org.hisp.dhis.android.network.enrollment.EnrollmentFields
import org.hisp.dhis.android.network.event.EventFields
import org.hisp.dhis.android.network.event.TrackedEntityDataValueFields
import org.hisp.dhis.android.network.note.NoteFields
import org.hisp.dhis.android.network.organisationunit.OrganisationUnitFields
import org.hisp.dhis.android.network.organisationunit.OrganisationUnitGroupFields
import org.hisp.dhis.android.network.program.ProgramFields
import org.hisp.dhis.android.network.program.ProgramRuleVariableFields
import org.hisp.dhis.android.network.program.ProgramSectionFields
import org.hisp.dhis.android.network.program.ProgramTrackedEntityAttributeFields
import org.hisp.dhis.android.network.programstage.ProgramStageDataElementFields
import org.hisp.dhis.android.network.programstage.ProgramStageFields
import org.hisp.dhis.android.network.programstage.ProgramStageSectionFields
import org.hisp.dhis.android.network.relationship.RelationshipFields
import org.hisp.dhis.android.network.relationship.RelationshipItemEnrollmentFields
import org.hisp.dhis.android.network.relationship.RelationshipItemEventFields
import org.hisp.dhis.android.network.relationship.RelationshipItemFields
import org.hisp.dhis.android.network.relationship.RelationshipItemTrackedEntityInstanceFields
import org.hisp.dhis.android.network.relationshiptype.RelationshipConstraintFields
import org.hisp.dhis.android.network.relationshiptype.RelationshipTypeFields
import org.hisp.dhis.android.network.relationshiptype.TrackerDataViewFields
import org.hisp.dhis.android.network.trackedentityinstance.TrackedEntityAttributeValueFields
import org.hisp.dhis.android.network.trackedentityinstance.TrackedEntityInstanceFields
import org.hisp.dhis.android.network.tracker.NewEnrollmentFields
import org.hisp.dhis.android.network.tracker.NewEventFields
import org.hisp.dhis.android.network.tracker.NewNoteFields
import org.hisp.dhis.android.network.tracker.NewRelationshipFields
import org.hisp.dhis.android.network.tracker.NewRelationshipItemEnrollmentFields
import org.hisp.dhis.android.network.tracker.NewRelationshipItemEventFields
import org.hisp.dhis.android.network.tracker.NewRelationshipItemFields
import org.hisp.dhis.android.network.tracker.NewRelationshipItemTrackedEntityInstanceFields
import org.hisp.dhis.android.network.tracker.NewTrackedEntityAttributeValueFields
import org.hisp.dhis.android.network.tracker.NewTrackedEntityDataValueFields
import org.hisp.dhis.android.network.tracker.NewTrackedEntityInstanceFields
import org.hisp.dhis.android.network.user.UserCredentialsFields
import org.hisp.dhis.android.network.user.UserFields
import org.hisp.dhis.android.network.user.UserGroupFields
import org.hisp.dhis.android.network.user.UserRoleFields
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@Ignore("Test for fields string generation. Only to be executed on demand")
@RunWith(JUnit4::class)
class FieldsStringGenerationShould {

    @Test
    fun generate_not_empty_string() {
        val sortedFieldsClasses = fieldsClasses.sortedBy { it::class.simpleName }

        sortedFieldsClasses.forEach { fieldsClass ->
            fieldsClass::class.members
                .filter { it.returnType.toString().contains("Fields") }
                .forEach { member ->
                    val fieldInstance = try {
                        member.call(fieldsClass) as? Fields<*>
                    } catch (e: Exception) {
                        null
                    }

                    fieldInstance?.let { field ->
                        val sortedFields = field.fields.sortedBy { it.name }

                        @Suppress("UNCHECKED_CAST")
                        val sortedField = Fields(sortedFields as List<Property<Any>>)

                        val generatedString = sortedField.generateString()
                        println("${fieldsClass::class.simpleName}.${member.name}: $generatedString")

                        assertThat(generatedString).isNotEmpty()
                    }
                }
        }
    }

    private val fieldsClasses = listOf(
        AccessFields,
        AttributeValueFields,
        DataAccessFields,
        DataElementFields,
        EnrollmentFields,
        EventFields,
        NewEnrollmentFields,
        NewEventFields,
        NewNoteFields,
        NewRelationshipFields,
        NewRelationshipItemEnrollmentFields,
        NewRelationshipItemEventFields,
        NewRelationshipItemFields,
        NewRelationshipItemTrackedEntityInstanceFields,
        NewTrackedEntityAttributeValueFields,
        NewTrackedEntityDataValueFields,
        NewTrackedEntityInstanceFields,
        NoteFields,
        ObjectStyleFields,
        OrganisationUnitFields,
        OrganisationUnitGroupFields,
        ProgramFields,
        ProgramRuleVariableFields,
        ProgramSectionFields,
        ProgramStageDataElementFields,
        ProgramStageFields,
        ProgramStageSectionFields,
        ProgramTrackedEntityAttributeFields,
        RelationshipConstraintFields,
        RelationshipFields,
        RelationshipItemEnrollmentFields,
        RelationshipItemEventFields,
        RelationshipItemFields,
        RelationshipItemTrackedEntityInstanceFields,
        RelationshipTypeFields,
        TrackedEntityAttributeValueFields,
        TrackedEntityDataValueFields,
        TrackedEntityInstanceFields,
        TrackerDataViewFields,
        UserCredentialsFields,
        UserFields,
        UserGroupFields,
        UserRoleFields,
    )
}
