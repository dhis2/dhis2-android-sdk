/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.tracker.importer.internal

internal enum class ImporterError(val regex: Regex) {
    /* General */
    E1000(Regex("User: `(.*?)`, has no write access to OrganisationUnit: `(.*?)`." )),
    E1001(Regex("User: `{0}`, has no data write access to TrackedEntityType: `{1}`." )),
    E1002(Regex("TrackedEntityInstance: `{0}`, already exists." )),
    E1003(Regex("OrganisationUnit: `{0}` of TrackedEntity is outside search scope of User: `{1}`." )),
    E1005(Regex("Could not find TrackedEntityType: `{0}`." )),
    E1006(Regex("Attribute: `{0}`, does not exist." )),
    E1007(Regex("Error validating attribute value type: `{0}`; Error: `{1}`." )),
    E1008(Regex("Program stage `{0}` has no reference to a program. Check the program stage configuration" )),
    E1009(Regex("File resource: `{0}`, has already been assigned to a different object." )),
    E1010(Regex("Could not find Program: `{0}`, linked to Event." )),
    E1011(Regex("Could not find OrganisationUnit: `{0}`, linked to Event." )),
    E1012(Regex("Geometry does not conform to FeatureType: `{0}`." )),
    E1013(Regex("Could not find ProgramStage: `{0}`, linked to Event." )),
    E1014(Regex("Provided Program: `{0}`, is a Program without registration. " +
            "An Enrollment cannot be created into Program without registration." )),
    E1015(Regex("TrackedEntityInstance: `{0}`, already has an active Enrollment in Program `{1}`." )),
    E1016(Regex("TrackedEntityInstance: `{0}`, already has an enrollment in Program: `{1}`, and this " +
            "program only allows enrolling one time." )),
    E1018(Regex("Attribute: `{0}`, is mandatory in program `{1}` but not declared in enrollment `{2}`." )),
    E1019(Regex("Only Program attributes is allowed for enrollment; Non valid attribute: `{0}`." )),
    E1020(Regex("Enrollment date: `{0}`, cannot be a future date." )),
    E1021(Regex("Incident date: `{0}`, cannot be a future date." )),
    E1022(Regex("TrackedEntityInstance: `{0}`, must have same TrackedEntityType as Program `{1}`." )),
    E1023(Regex("DisplayIncidentDate is true but property occurredAt is null or has an invalid format: `{0}`." )),
    E1025(Regex("Property enrolledAt is null or has an invalid format: `{0}`." )),
    E1029(Regex("Event OrganisationUnit: `{0}`, and Program: `{1}`, don't match." )),
    E1030(Regex("Event: `{0}`, already exists." )),
    E1031(Regex("Event OccurredAt date is missing." )),
    E1032(Regex("Event: `{0}`, do not exist." )),
    E1033(Regex("Event: `{0}`, Enrollment value is null." )),
    E1035(Regex("Event: `{0}`, ProgramStage value is null." )),
    E1039(Regex("ProgramStage: `{0}`, is not repeatable and an event already exists." )),
    E1041(Regex("Enrollment OrganisationUnit: `{0}`, and Program: `{1}`, don't match." )),
    E1042(Regex("Event: `{0}`, needs to have completed date." )),
    E1048(Regex("Object: `{0}`, uid: `{1}`, has an invalid uid format." )),
    E1049(Regex("Could not find OrganisationUnit: `{0}`, linked to Tracked Entity." )),
    E1050(Regex("Event ScheduledAt date is missing." )),
    E1055(Regex("Default AttributeOptionCombo is not allowed since program has non-default CategoryCombo." )),
    E1056(Regex("Event date: `{0}`, is before start date: `{1}`, for AttributeOption: `{2}`." )),
    E1057(Regex("Event date: `{0}`, is after end date: `{1}`, for AttributeOption; `{2}`." )),
    E1063(Regex("TrackedEntityInstance: `{0}`, does not exist." )),
    E1064(Regex("Non-unique attribute value `{0}` for attribute `{1}`" )),
    E1068(Regex("Could not find TrackedEntityInstance: `{0}`, linked to Enrollment." )),
    E1069(Regex("Could not find Program: `{0}`, linked to Enrollment." )),
    E1070(Regex("Could not find OrganisationUnit: `{0}`, linked to Enrollment." )),
    E1074(Regex("FeatureType is missing." )),
    E1075(Regex("Attribute: `{0}`, is missing uid." )),
    E1076(Regex("`{0}` `{1}` is mandatory and cannot be null" )),
    E1077(Regex("Attribute: `{0}`, text value exceed the maximum allowed length: `{0}`." )),
    E1079(Regex("Event: `{0}`, program: `{1}` is different from program defined in enrollment `{2}`." )),
    E1080(Regex("Enrollment: `{0}`, already exists." )),
    E1081(Regex("Enrollment: `{0}`, do not exist." )),
    E1082(Regex("Event: `{0}`, is already deleted and cannot be modified." )),
    E1083(Regex("User: `{0}`, is not authorized to modify completed events." )),
    E1084(Regex("File resource: `{0}`, reference could not be found." )),
    E1085(Regex("Attribute: `{0}`, value does not match value type: `{1}`." )),
    E1086(Regex("Event: `{0}`, has a program: `{1}`, that is a registration but its ProgramStage is not valid or missing." )),
    E1087(Regex("Event: `{0}`, could not find DataElement: `{1}`, linked to a data value." )),
    E1088(Regex("Event: `{0}`, program: `{1}`, and ProgramStage: `{2}`, could not be found." )),
    E1089(Regex("Event: `{0}`, references a Program Stage `{1}` that does not belong to Program `{2}`." )),
    E1090(Regex("Attribute: `{0}`, is mandatory in tracked entity type `{1}` but not declared in tracked entity `{2}`." )),
    E1091(Regex("User: `{0}`, has no data write access to Program: `{1}`." )),
    E1095(Regex("User: `{0}`, has no data write access to ProgramStage: `{1}`." )),
    E1096(Regex("User: `{0}`, has no data read access to Program: `{1}`." )),
    E1099(Regex("User: `{0}`, has no write access to CategoryOption: `{1}`." )),
    E1100(Regex("User: `{0}`, is lacking 'F_TEI_CASCADE_DELETE' authority to delete TrackedEntityInstance: `{1}`." )),
    E1102(Regex("User: `{0}`, does not have access to the tracked entity: `{1}`, Program: `{2}`, combination." )),
    E1103(Regex("User: `{0}`, is lacking 'F_ENROLLMENT_CASCADE_DELETE' authority to delete Enrollment : `{1}`." )),
    E1104(Regex("User: `{0}`, has no data read access to program: `{1}`, TrackedEntityType: `{2}`." )),
    E1112(Regex("Attribute value: `{0}`, is set to confidential but system is not properly configured to encrypt data." )),
    E1113(Regex("Enrollment: `{0}`, is already deleted and cannot be modified." )),
    E1114(Regex("TrackedEntity: `{0}`, is already deleted and cannot be modified." )),
    E1115(Regex("Could not find CategoryOptionCombo: `{0}`." )),
    E1116(Regex("Could not find CategoryOption: `{0}`." )),
    E1117(Regex("CategoryOptionCombo does not exist for given category combo and category options: `{0}`." )),
    E1118(Regex("Assigned user `{0}` is not a valid uid." )),
    E1119(Regex("A Tracker Note with uid `{0}` already exists." )),
    E1120(Regex("ProgramStage `{0}` does not allow user assignment" )),
    E1121(Regex("Missing required tracked entity property: `{0}`." )),
    E1122(Regex("Missing required enrollment property: `{0}`." )),
    E1123(Regex("Missing required event property: `{0}`." )),
    E1124(Regex("Missing required relationship property: `{0}`." )),
    E1125(Regex("Value {0} is not a valid option for {1} {2} in option set {3}" )),
    E1126(Regex("Not allowed to update Tracked Entity property: {0}." )),
    E1127(Regex("Not allowed to update Enrollment property: {0}." )),
    E1128(Regex("Not allowed to update Event property: {0}." )),
    E1094(Regex("Not allowed to update Enrollment: `{0}`, existing Program `{1}`." )),
    E1110(Regex("Not allowed to update Event: `{0}`, existing Program `{1}`." )),
    E1045(Regex("Program: `{0}`, expiry date has passed. It is not possible to make changes to this event." )),
    E1043(Regex("Event: `{0}`, completeness date has expired. Not possible to make changes to this event." )),
    E1044(Regex("Event: `{0}`, needs to have event date." )),
    E1046(Regex("Event: `{0}`, needs to have at least one (event or schedule) date." )),
    E1047(Regex("Event: `{0}`, date belongs to an expired period. It is not possible to create such event." )),
    E1300(Regex("Generated by program rule (`{0}`) - `{1}`" )),
    E1301(Regex("Generated by program rule (`{0}`) - Mandatory DataElement `{1}` is not present" )),
    E1302(Regex("DataElement `{0}` is not valid: `{1}`" )),
    E1303(Regex("Mandatory DataElement `{0}` is not present" )),
    E1304(Regex("DataElement `{0}` is not a valid data element" )),
    E1305(Regex("DataElement `{0}` is not part of `{1}` program stage" )),
    E1306(Regex("Generated by program rule (`{0}`) - Mandatory Attribute `{1}` is not present" )),
    E1307(Regex("Generated by program rule (`{0}`) - Unable to assign value to data element `{1}`. " +
            "The provided value must be empty or match the calculated value `{2}`" )),
    E1308(Regex("Generated by program rule (`{0}`) - DataElement `{1}` is being replaced in event `{2}`" )),
    E1309(Regex("Generated by program rule (`{0}`) - Unable to assign value to attribute `{1}`. " +
            "The provided value must be empty or match the calculated value `{2}`" )),
    E1310(Regex("Generated by program rule (`{0}`) - Attribute `{1}` is being replaced in tei `{2}`" )),

    /* Relationship */
    E4000(Regex("Relationship: `{0}` cannot link to itself" )),
    E4001(Regex("Relationship Item `{0}` for Relationship `{1}` is invalid: an Item can link only one Tracker entity." )),
    E4003(Regex("There are duplicated relationships." )),
    E4004(Regex("Missing required relationship property: 'relationshipType'." )),
    E4005(Regex("RelationShip: `{0}`, do not exist." )),
    E4006(Regex("Could not find relationship Type: `{0}`." )),
    E4007(Regex("Missing required relationship property: 'from'." )),
    E4008(Regex("Missing required relationship property: 'to'." )),
    E4009(Regex("Relationship Type `{0}` is not valid." )),
    E4010(Regex("Relationship Type `{0}` constraint requires a {1} but a {2} was found." )),
    E4011(Regex("Relationship: `{0}` cannot be persisted because {1} {2} referenced by this relationship is not valid." )),
    E4012(Regex("Could not find `{0}`: `{1}`, linked to Relationship." )),
    E4013(Regex("Relationship Type `{0}` constraint is missing {1}." )),
    E4014(Regex("Relationship Type `{0}` constraint requires a Tracked Entity having type `{1}` but `{2}` was found." )),
    E4015(Regex("Relationship: `{0}`, already exists." )),
    E9999(Regex("N/A" )),
}
