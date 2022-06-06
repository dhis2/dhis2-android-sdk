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
package org.hisp.dhis.android.core.imports.internal.conflicts

import org.hisp.dhis.android.core.imports.internal.ImportConflict

object TrackedImportConflictSamples {

    fun missingMandatoryAttribute(attributeUid: String): ImportConflict {
        return ImportConflict.create(
            "Attribute.attribute",
            "Missing mandatory attribute $attributeUid"
        )
    }

    fun badAttributePattern(): ImportConflict {
        return ImportConflict.create(
            "Attribute.value",
            "Value does not match the attribute pattern"
        )
    }

    fun invalidNumericAttribute(attributeUid: String, value: String): ImportConflict {
        return ImportConflict.create(
            "Attribute.value",
            "Value '$value' is not a valid numeric type for attribute $attributeUid"
        )
    }

    fun invalidBooleanAttribute(attributeUid: String, value: String): ImportConflict {
        return ImportConflict.create(
            "Attribute.value",
            "Value '$value' is not a valid boolean type for attribute $attributeUid"
        )
    }

    fun invalidTrueOnlyAttribute(attributeUid: String, value: String): ImportConflict {
        return ImportConflict.create(
            "Attribute.value",
            "Value '$value' is not true (true-only type) for attribute $attributeUid"
        )
    }

    fun invalidDateAttribute(attributeUid: String, value: String): ImportConflict {
        return ImportConflict.create(
            "Attribute.value",
            "Value '$value' is not a valid date for attribute $attributeUid"
        )
    }

    fun invalidDatetimeAttribute(attributeUid: String, value: String): ImportConflict {
        return ImportConflict.create(
            "Attribute.value",
            "Value '$value' is not a valid datetime for attribute $attributeUid"
        )
    }

    fun invalidUsernameAttribute(attributeUid: String, value: String): ImportConflict {
        return ImportConflict.create(
            "Attribute.value",
            "Value '$value' is not a valid username for attribute $attributeUid"
        )
    }

    fun invalidFileAttribute(value: String): ImportConflict {
        return ImportConflict.create(
            "Attribute.value",
            "Value '$value' is not the uid of a file"
        )
    }

    fun invalidAttributeOption(attributeUid: String, value: String, optionSetUid: String): ImportConflict {
        return ImportConflict.create(
            "Attribute.value",
            "Value '$value' is not a valid option for attribute $attributeUid and option set $optionSetUid"
        )
    }

    fun nonUniqueAttribute(attributeUid: String, value: String): ImportConflict {
        return ImportConflict.create(
            "Attribute.value",
            "Non-unique attribute value '$value' for attribute $attributeUid"
        )
    }

    fun userIsLackingTEICascadeDeleteAuthority(teiUid: String): ImportConflict {
        return ImportConflict.create(
            teiUid,
            "Tracked entity instance $teiUid cannot be deleted as it has associated enrollments and user does" +
                " not have authority F_TEI_CASCADE_DELETE"
        )
    }

    fun userIsLackingEnrollmentCascadeDeleteAuthority(enrollmentUid: String): ImportConflict {
        return ImportConflict.create(
            enrollmentUid,
            "Enrollment $enrollmentUid cannot be deleted as it has associated events and user does not" +
                " have authority: F_ENROLLMENT_CASCADE_DELETE"
        )
    }

    fun fileResourceAlreadyAssigned(fileResourceUid: String): ImportConflict {
        return ImportConflict.create(
            "Attribute.value",
            "File resource with uid '$fileResourceUid' has already been assigned to a different object"
        )
    }

    fun fileResourceReferenceNotFound(fileResourceUid: String): ImportConflict {
        return ImportConflict.create("Attribute.value", "Value '$fileResourceUid' is not the uid of a file")
    }

    fun eventNotFound(eventUid: String, relationshipUid: String): ImportConflict {
        return ImportConflict.create(relationshipUid, "ProgramStageInstance '$eventUid' not found.")
    }

    fun eventHasInvalidProgram(eventUid: String, relationshipUid: String): ImportConflict {
        return ImportConflict.create(relationshipUid, "ProgramStageInstance '$eventUid' has invalid Program.")
    }

    fun eventHasInvalidProgramStage(eventUid: String, relationshipUid: String): ImportConflict {
        return ImportConflict.create(relationshipUid, "ProgramStageInstance '$eventUid' has invalid ProgramStage.")
    }

    fun enrollmentNotFound(enrollmentUid: String, relationshipUid: String): ImportConflict {
        return ImportConflict.create(relationshipUid, "ProgramInstance '$enrollmentUid' not found.")
    }

    fun enrollmentHasInvalidProgram(enrollmentUid: String, relationshipUid: String): ImportConflict {
        return ImportConflict.create(relationshipUid, "ProgramInstance '$enrollmentUid' has invalid Program.")
    }

    fun teiNotFound(teiUid: String, relationshipUid: String): ImportConflict {
        return ImportConflict.create(relationshipUid, "TrackedEntityInstance '$teiUid' not found.")
    }

    fun teiHasInvalidType(teiUid: String, relationshipUid: String): ImportConflict {
        return ImportConflict.create(
            relationshipUid,
            "TrackedEntityInstance '$teiUid' has invalid TrackedEntityType."
        )
    }

    // Data value types

    fun valueNotNumeric(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_numeric")
    }

    fun valueNotUnitInterval(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_unit_interval")
    }

    fun valueNotPercentage(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_percentage")
    }

    fun valueNotInteger(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_integer")
    }

    fun valueNotPositiveInteger(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_positive_integer")
    }

    fun valueNotNegativeInteger(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_negative_integer")
    }

    fun valueNotZeroOrPositiveInteger(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_zero_or_positive_integer")
    }

    fun valueNotBoolean(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_bool")
    }

    fun valueNotTrueOnly(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_true_only")
    }

    fun valueNotValidDate(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_valid_date")
    }

    fun valueNotValidDatetime(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_valid_datetime")
    }

    fun valueNotCoordinate(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_coordinate")
    }

    fun valueNotUrl(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_url")
    }

    fun valueNotFileResourceUid(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_not_valid_file_resource_uid")
    }

    //

    fun missingRequiredDataElement(dataElementId: String): ImportConflict {
        return ImportConflict.create(dataElementId, "value_required_but_not_provided")
    }
}
