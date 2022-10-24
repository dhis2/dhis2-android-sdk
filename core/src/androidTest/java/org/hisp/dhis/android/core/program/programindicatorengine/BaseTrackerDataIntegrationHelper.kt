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
package org.hisp.dhis.android.core.program.programindicatorengine

import java.util.*
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.event.internal.EventStoreImpl
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.hisp.dhis.android.core.program.internal.ProgramIndicatorStore
import org.hisp.dhis.android.core.relationship.*
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemStoreImpl
import org.hisp.dhis.android.core.relationship.internal.RelationshipStoreImpl
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStoreImpl

open class BaseTrackerDataIntegrationHelper(private val databaseAdapter: DatabaseAdapter) {

    fun createTrackedEntity(teiUid: String, orgunitUid: String, teiTypeUid: String) {
        val teiStore = TrackedEntityInstanceStoreImpl.create(databaseAdapter)
        val trackedEntityInstance = TrackedEntityInstance.builder()
            .uid(teiUid)
            .created(Date())
            .lastUpdated(Date())
            .organisationUnit(orgunitUid)
            .trackedEntityType(teiTypeUid)
            .build()
        teiStore.insert(trackedEntityInstance)
    }

    fun createEnrollment(
        teiUid: String,
        enrollmentUid: String,
        programUid: String,
        orgunitUid: String,
        enrollmentDate: Date? = null,
        incidentDate: Date? = null,
        created: Date? = null,
        lastUpdated: Date? = null,
        status: EnrollmentStatus? = EnrollmentStatus.ACTIVE
    ) {
        val enrollment = Enrollment.builder().uid(enrollmentUid).organisationUnit(orgunitUid).program(programUid)
            .enrollmentDate(enrollmentDate).incidentDate(incidentDate).trackedEntityInstance(teiUid)
            .created(created).lastUpdated(lastUpdated).status(status).build()
        EnrollmentStoreImpl.create(databaseAdapter).insert(enrollment)
    }

    fun createEvent(
        eventUid: String,
        programUid: String,
        programStageUid: String,
        enrollmentUid: String? = null,
        orgunitUid: String,
        deleted: Boolean = false,
        eventDate: Date?,
        created: Date? = null,
        lastUpdated: Date? = null,
        status: EventStatus? = EventStatus.ACTIVE
    ) {
        val event = Event.builder().uid(eventUid).enrollment(enrollmentUid).created(created).lastUpdated(lastUpdated)
            .program(programUid).programStage(programStageUid).organisationUnit(orgunitUid)
            .eventDate(eventDate).deleted(deleted).status(status).build()
        EventStoreImpl.create(databaseAdapter).insert(event)
    }

    fun createTrackerEvent(
        eventUid: String,
        enrollmentUid: String,
        programUid: String,
        programStageUid: String,
        orgunitUid: String,
        deleted: Boolean = false,
        eventDate: Date? = null,
        created: Date? = null,
        lastUpdated: Date? = null,
        status: EventStatus? = EventStatus.ACTIVE
    ) {
        createEvent(
            eventUid = eventUid,
            programUid = programUid,
            programStageUid = programStageUid,
            enrollmentUid = enrollmentUid,
            orgunitUid = orgunitUid,
            deleted = deleted,
            created = created,
            lastUpdated = lastUpdated,
            eventDate = eventDate,
            status = status
        )
    }

    fun createSingleEvent(
        eventUid: String,
        programUid: String,
        programStageUid: String,
        orgunitUid: String,
        deleted: Boolean = false,
        eventDate: Date? = null,
        lastUpdated: Date? = null,
        status: EventStatus? = EventStatus.ACTIVE
    ) {
        createEvent(
            eventUid = eventUid,
            programUid = programUid,
            programStageUid = programStageUid,
            enrollmentUid = null,
            orgunitUid = orgunitUid,
            deleted = deleted,
            eventDate = eventDate,
            lastUpdated = lastUpdated,
            status = status
        )
    }

    fun setProgramIndicatorExpression(
        programIndicatorUid: String,
        programUid: String,
        expression: String
    ) {
        insertProgramIndicator(programIndicatorUid, programUid, expression, AggregationType.AVERAGE)
    }

    fun insertProgramIndicator(
        programIndicatorUid: String,
        programUid: String,
        expression: String,
        aggregationType: AggregationType
    ) {
        val programIndicator = ProgramIndicator.builder().uid(programIndicatorUid)
            .program(ObjectWithUid.create(programUid)).expression(expression).aggregationType(aggregationType).build()
        setProgramIndicator(programIndicator)
    }

    fun setProgramIndicator(programIndicator: ProgramIndicator) {
        ProgramIndicatorStore.create(databaseAdapter).updateOrInsert(programIndicator)
    }

    fun insertTrackedEntityDataValue(eventUid: String, dataElementUid: String, value: String) {
        val trackedEntityDataValue = TrackedEntityDataValue.builder()
            .event(eventUid)
            .dataElement(dataElementUid)
            .value(value).build()
        TrackedEntityDataValueStoreImpl.create(databaseAdapter).updateOrInsertWhere(trackedEntityDataValue)
    }

    fun insertTrackedEntityAttributeValue(teiUid: String, attributeUid: String, value: String) {
        val trackedEntityAttributeValue = TrackedEntityAttributeValue.builder()
            .value(value).trackedEntityAttribute(attributeUid).trackedEntityInstance(teiUid).build()
        TrackedEntityAttributeValueStoreImpl.create(databaseAdapter).updateOrInsertWhere(trackedEntityAttributeValue)
    }

    fun createRelationship(typeUid: String, fromTei: String, toTei: String) {
        val relationship = RelationshipHelper.teiToTeiRelationship(fromTei, toTei, typeUid)

        RelationshipStoreImpl.create(databaseAdapter).insert(relationship)
        RelationshipItemStoreImpl.create(databaseAdapter).let {
            val r = ObjectWithUid.create(relationship.uid())
            it.insert(
                relationship.from()!!.toBuilder()
                    .relationshipItemType(RelationshipConstraintType.FROM)
                    .relationship(r)
                    .build()
            )
            it.insert(
                relationship.to()!!.toBuilder()
                    .relationshipItemType(RelationshipConstraintType.TO)
                    .relationship(r)
                    .build()
            )
        }
    }

    companion object {
        fun cons(constantUid: String): String {
            return "C{$constantUid}"
        }

        fun de(programStageUid: String, dataElementUid: String): String {
            return "#{$programStageUid.$dataElementUid}"
        }

        fun att(attributeUid: String): String {
            return "A{$attributeUid}"
        }

        fun `var`(variable: String): String {
            return "V{$variable}"
        }

        fun psEventDate(programStageUid: String): String {
            return "PS_EVENTDATE:$programStageUid"
        }

        fun today(): Date {
            return Date()
        }

        fun twoDaysBefore(): Date {
            val newTime = Date().time - 2 * 24 * 60 * 60 * 1000
            return Date(newTime)
        }
    }
}
