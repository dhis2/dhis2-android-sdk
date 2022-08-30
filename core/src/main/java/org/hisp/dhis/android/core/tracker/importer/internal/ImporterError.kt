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
package org.hisp.dhis.android.core.tracker.importer.internal

internal enum class ImporterError(val regex: Regex) {
    /* General */
    E1000(Regex("User: `(.*)`, has no write access to OrganisationUnit: `(.*)`.")),
    E1001(Regex("User: `(.*)`, has no data write access to TrackedEntityType: `(.*)`.")),
    E1002(Regex("TrackedEntityInstance: `(.*)`, already exists.")),
    E1003(Regex("OrganisationUnit: `(.*)` of TrackedEntity is outside search scope of User: `(.*)`.")),
    E1005(Regex("Could not find TrackedEntityType: `(.*)`.")),
    E1006(Regex("Attribute: `(.*)`, does not exist.")),
    E1007(Regex("Error validating attribute value type: `(.*)`; Error: `(.*)`.")),
    E1008(Regex("Program stage `(.*)` has no reference to a program. Check the program stage configuration")),
    E1009(Regex("File resource: `(.*)`, has already been assigned to a different object.")),
    E1010(Regex("Could not find Program: `(.*)`, linked to Event.")),
    E1011(Regex("Could not find OrganisationUnit: `(.*)`, linked to Event.")),
    E1012(Regex("Geometry does not conform to FeatureType: `(.*)`.")),
    E1013(Regex("Could not find ProgramStage: `(.*)`, linked to Event.")),
    E1014(
        Regex(
            "Provided Program: `(.*)`, is a Program without registration. " +
                "An Enrollment cannot be created into Program without registration."
        )
    ),
    E1015(Regex("TrackedEntityInstance: `(.*)`, already has an active Enrollment in Program `(.*)`.")),
    E1016(
        Regex(
            "TrackedEntityInstance: `(.*)`, already has an enrollment in Program: `(.*)`, and this " +
                "program only allows enrolling one time."
        )
    ),
    E1018(Regex("Attribute: `(.*)`, is mandatory in program `(.*)` but not declared in enrollment `(.*)`.")),
    E1019(Regex("Only Program attributes is allowed for enrollment; Non valid attribute: `(.*)`.")),
    E1020(Regex("Enrollment date: `(.*)`, cannot be a future date.")),
    E1021(Regex("Incident date: `(.*)`, cannot be a future date.")),
    E1022(Regex("TrackedEntityInstance: `(.*)`, must have same TrackedEntityType as Program `(.*)`.")),
    E1023(Regex("DisplayIncidentDate is true but property occurredAt is null or has an invalid format: `(.*)`.")),
    E1025(Regex("Property enrolledAt is null or has an invalid format: `(.*)`.")),
    E1029(Regex("Event OrganisationUnit: `(.*)`, and Program: `(.*)`, don't match.")),
    E1030(Regex("Event: `(.*)`, already exists.")),
    E1031(Regex("Event OccurredAt date is missing.")),
    E1032(Regex("Event: `(.*)`, do not exist.")),
    E1033(Regex("Event: `(.*)`, Enrollment value is null.")),
    E1035(Regex("Event: `(.*)`, ProgramStage value is null.")),
    E1039(Regex("ProgramStage: `(.*)`, is not repeatable and an event already exists.")),
    E1041(Regex("Enrollment OrganisationUnit: `(.*)`, and Program: `(.*)`, don't match.")),
    E1042(Regex("Event: `(.*)`, needs to have completed date.")),
    E1048(Regex("Object: `(.*)`, uid: `(.*)`, has an invalid uid format.")),
    E1049(Regex("Could not find OrganisationUnit: `(.*)`, linked to Tracked Entity.")),
    E1050(Regex("Event ScheduledAt date is missing.")),
    E1055(Regex("Default AttributeOptionCombo is not allowed since program has non-default CategoryCombo.")),
    E1056(Regex("Event date: `(.*)`, is before start date: `(.*)`, for AttributeOption: `(.*)`.")),
    E1057(Regex("Event date: `(.*)`, is after end date: `(.*)`, for AttributeOption; `(.*)`.")),
    E1063(Regex("TrackedEntityInstance: `(.*)`, does not exist.")),
    E1064(Regex("Non-unique attribute value `(.*)` for attribute `(.*)`")),
    E1068(Regex("Could not find TrackedEntityInstance: `(.*)`, linked to Enrollment.")),
    E1069(Regex("Could not find Program: `(.*)`, linked to Enrollment.")),
    E1070(Regex("Could not find OrganisationUnit: `(.*)`, linked to Enrollment.")),
    E1074(Regex("FeatureType is missing.")),
    E1075(Regex("Attribute: `(.*)`, is missing uid.")),
    E1076(Regex("`(.*)` `(.*)` is mandatory and cannot be null")),
    E1077(Regex("Attribute: `(.*)`, text value exceed the maximum allowed length: `(.*)`.")),
    E1079(Regex("Event: `(.*)`, program: `(.*)` is different from program defined in enrollment `(.*)`.")),
    E1080(Regex("Enrollment: `(.*)`, already exists.")),
    E1081(Regex("Enrollment: `(.*)`, do not exist.")),
    E1082(Regex("Event: `(.*)`, is already deleted and cannot be modified.")),
    E1083(Regex("User: `(.*)`, is not authorized to modify completed events.")),
    E1084(Regex("File resource: `(.*)`, reference could not be found.")),
    E1085(Regex("Attribute: `(.*)`, value does not match value type: `(.*)`.")),
    E1086(
        Regex(
            "Event: `(.*)`, has a program: `(.*)`, that is a registration but its ProgramStage is not " +
                "valid or missing."
        )
    ),
    E1087(Regex("Event: `(.*)`, could not find DataElement: `(.*)`, linked to a data value.")),
    E1088(Regex("Event: `(.*)`, program: `(.*)`, and ProgramStage: `(.*)`, could not be found.")),
    E1089(Regex("Event: `(.*)`, references a Program Stage `(.*)` that does not belong to Program `(.*)`.")),
    E1090(
        Regex(
            "Attribute: `(.*)`, is mandatory in tracked entity type `(.*)` but not declared in tracked entity `(.*)`."
        )
    ),
    E1091(Regex("User: `(.*)`, has no data write access to Program: `(.*)`.")),
    E1095(Regex("User: `(.*)`, has no data write access to ProgramStage: `(.*)`.")),
    E1096(Regex("User: `(.*)`, has no data read access to Program: `(.*)`.")),
    E1099(Regex("User: `(.*)`, has no write access to CategoryOption: `(.*)`.")),
    E1100(Regex("User: `(.*)`, is lacking 'F_TEI_CASCADE_DELETE' authority to delete TrackedEntityInstance: `(.*)`.")),
    E1102(Regex("User: `(.*)`, does not have access to the tracked entity: `(.*)`, Program: `(.*)`, combination.")),
    E1103(Regex("User: `(.*)`, is lacking 'F_ENROLLMENT_CASCADE_DELETE' authority to delete Enrollment : `(.*)`.")),
    E1104(Regex("User: `(.*)`, has no data read access to program: `(.*)`, TrackedEntityType: `(.*)`.")),
    E1112(
        Regex(
            "Attribute value: `(.*)`, is set to confidential but system is not properly configured to encrypt data."
        )
    ),
    E1113(Regex("Enrollment: `(.*)`, is already deleted and cannot be modified.")),
    E1114(Regex("TrackedEntity: `(.*)`, is already deleted and cannot be modified.")),
    E1115(Regex("Could not find CategoryOptionCombo: `(.*)`.")),
    E1116(Regex("Could not find CategoryOption: `(.*)`.")),
    E1117(Regex("CategoryOptionCombo does not exist for given category combo and category options: `(.*)`.")),
    E1118(Regex("Assigned user `(.*)` is not a valid uid.")),
    E1119(Regex("A Tracker Note with uid `(.*)` already exists.")),
    E1120(Regex("ProgramStage `(.*)` does not allow user assignment")),
    E1121(Regex("Missing required tracked entity property: `(.*)`.")),
    E1122(Regex("Missing required enrollment property: `(.*)`.")),
    E1123(Regex("Missing required event property: `(.*)`.")),
    E1124(Regex("Missing required relationship property: `(.*)`.")),
    E1125(Regex("Value (.*) is not a valid option for (.*) (.*) in option set (.*)")),
    E1126(Regex("Not allowed to update Tracked Entity property: (.*).")),
    E1127(Regex("Not allowed to update Enrollment property: (.*).")),
    E1128(Regex("Not allowed to update Event property: (.*).")),
    E1094(Regex("Not allowed to update Enrollment: `(.*)`, existing Program `(.*)`.")),
    E1110(Regex("Not allowed to update Event: `(.*)`, existing Program `(.*)`.")),
    E1045(Regex("Program: `(.*)`, expiry date has passed. It is not possible to make changes to this event.")),
    E1043(Regex("Event: `(.*)`, completeness date has expired. Not possible to make changes to this event.")),
    E1044(Regex("Event: `(.*)`, needs to have event date.")),
    E1046(Regex("Event: `(.*)`, needs to have at least one (event or schedule) date.")),
    E1047(Regex("Event: `(.*)`, date belongs to an expired period. It is not possible to create such event.")),
    E1300(Regex("Generated by program rule (`(.*)`) - `(.*)`")),
    E1301(Regex("Generated by program rule (`(.*)`) - Mandatory DataElement `(.*)` is not present")),
    E1302(Regex("DataElement `(.*)` is not valid: `(.*)`")),
    E1303(Regex("Mandatory DataElement `(.*)` is not present")),
    E1304(Regex("DataElement `(.*)` is not a valid data element")),
    E1305(Regex("DataElement `(.*)` is not part of `(.*)` program stage")),
    E1306(Regex("Generated by program rule (`(.*)`) - Mandatory Attribute `(.*)` is not present")),
    E1307(
        Regex(
            "Generated by program rule (`(.*)`) - Unable to assign value to data element `(.*)`. " +
                "The provided value must be empty or match the calculated value `(.*)`"
        )
    ),
    E1308(Regex("Generated by program rule (`(.*)`) - DataElement `(.*)` is being replaced in event `(.*)`")),
    E1309(
        Regex(
            "Generated by program rule (`(.*)`) - Unable to assign value to attribute `(.*)`. " +
                "The provided value must be empty or match the calculated value `(.*)`"
        )
    ),
    E1310(Regex("Generated by program rule (`(.*)`) - Attribute `(.*)` is being replaced in tei `(.*)`")),

    /* Relationship */
    E4000(Regex("Relationship: `(.*)` cannot link to itself")),
    E4001(
        Regex(
            "Relationship Item `(.*)` for Relationship `(.*)` is invalid: an Item can link only one Tracker entity."
        )
    ),
    E4003(Regex("There are duplicated relationships.")),
    E4004(Regex("Missing required relationship property: 'relationshipType'.")),
    E4005(Regex("RelationShip: `(.*)`, do not exist.")),
    E4006(Regex("Could not find relationship Type: `(.*)`.")),
    E4007(Regex("Missing required relationship property: 'from'.")),
    E4008(Regex("Missing required relationship property: 'to'.")),
    E4009(Regex("Relationship Type `(.*)` is not valid.")),
    E4010(Regex("Relationship Type `(.*)` constraint requires a (.*) but a (.*) was found.")),
    E4011(
        Regex(
            "Relationship: `(.*)` cannot be persisted because (.*) (.*) referenced by this relationship is not valid."
        )
    ),
    E4012(Regex("Could not find `(.*)`: `(.*)`, linked to Relationship.")),
    E4013(Regex("Relationship Type `(.*)` constraint is missing (.*).")),
    E4014(
        Regex(
            "Relationship Type `(.*)` constraint requires a Tracked Entity having type `(.*)` but `(.*)` was found."
        )
    ),
    E4015(Regex("Relationship: `(.*)`, already exists.")),
    E9999(Regex("N/A"))
}
