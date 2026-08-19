/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.core.scopedaccess.internal

import org.hisp.dhis.android.core.arch.repositories.scope.internal.AccessGuard
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.scopedaccess.D2Capability
import org.hisp.dhis.android.core.scopedaccess.D2DataScope
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

/**
 * The [AccessGuard] installed on every repository handed out by
 * [ScopedD2][org.hisp.dhis.android.core.scopedaccess.ScopedD2].
 *
 * Validates the *object being written*, not the query that produced it — a create projection or a
 * value object carries its own organisation unit, program and data element, none of which the read
 * filters constrain.
 *
 * Unknown types are denied. That is deliberate: a model type this guard has not been taught about
 * must not become silently writable when the SDK grows, and every writable type reachable through
 * `ScopedD2` is listed here.
 */
@Suppress("TooManyFunctions")
internal class ScopedAccessGuard(
    private val scope: D2DataScope,
    private val resolver: ScopeResolver,
) : AccessGuard {

    override suspend fun checkWrite(target: Any?) {
        when (target) {
            null -> deny("a null object")
            is TrackedEntityInstance -> checkTrackedEntityInstance(target)
            is Enrollment -> checkEnrollment(target)
            is Event -> checkEvent(target)
            is DataValue -> checkDataValue(target)
            is TrackedEntityDataValue -> checkTrackedEntityDataValue(target)
            is TrackedEntityAttributeValue -> checkTrackedEntityAttributeValue(target)
            else -> deny("${target.javaClass.simpleName}, which no scoped repository may write")
        }
    }

    private fun checkTrackedEntityInstance(tei: TrackedEntityInstance) {
        require(D2Capability.WRITE_TRACKED_ENTITY)
        requireOrgUnit(tei.organisationUnit(), "tracked entity instance ${tei.uid()}")
        if (!scope.trackedEntityTypes.allows(tei.trackedEntityType())) {
            deny("tracked entity type '${tei.trackedEntityType()}'")
        }
    }

    private fun checkEnrollment(enrollment: Enrollment) {
        require(D2Capability.WRITE_ENROLLMENT)
        requireProgram(enrollment.program(), "enrollment ${enrollment.uid()}")
        requireOrgUnit(enrollment.organisationUnit(), "enrollment ${enrollment.uid()}")
    }

    private fun checkEvent(event: Event) {
        require(D2Capability.WRITE_EVENT)
        requireProgram(event.program(), "event ${event.uid()}")
        requireOrgUnit(event.organisationUnit(), "event ${event.uid()}")
    }

    private fun checkDataValue(dataValue: DataValue) {
        require(D2Capability.WRITE_DATA_VALUE)
        val writable = resolver.writableDataElements()
        if (!allowed(writable, dataValue.dataElement())) {
            deny("data element '${dataValue.dataElement()}'")
        }
        requireOrgUnit(dataValue.organisationUnit(), "data value for '${dataValue.dataElement()}'")
    }

    private fun checkTrackedEntityDataValue(value: TrackedEntityDataValue) {
        require(D2Capability.WRITE_EVENT)
        val program = resolver.programOfEvent(value.event())
            ?: deny("a data value for unknown event '${value.event()}'")
        requireProgram(program, "event ${value.event()}")
        requireOrgUnit(resolver.orgUnitOfEvent(value.event()), "event ${value.event()}")
    }

    private fun checkTrackedEntityAttributeValue(value: TrackedEntityAttributeValue) {
        require(D2Capability.WRITE_TRACKED_ENTITY)
        val writablePrograms = scope.writablePrograms().uidsOrNull()
        if (writablePrograms != null) {
            val teiPrograms = resolver.programsOfTrackedEntity(value.trackedEntityInstance())
            if (teiPrograms.none { it in writablePrograms }) {
                deny(
                    "attribute value for tracked entity '${value.trackedEntityInstance()}', " +
                        "which is not enrolled in any writable program",
                )
            }
        }
    }

    private fun require(capability: D2Capability) {
        if (!scope.has(capability)) {
            deny("an operation requiring the $capability capability")
        }
    }

    private fun requireProgram(programUid: String?, what: String) {
        if (!scope.writablePrograms().allows(programUid)) {
            deny("$what in program '$programUid'")
        }
    }

    private fun requireOrgUnit(orgUnitUid: String?, what: String) {
        if (!allowed(resolver.writableOrgUnits(), orgUnitUid)) {
            deny("$what in organisation unit '$orgUnitUid'")
        }
    }

    /** A null [permitted] set means no restriction, so only a null [uid] fails. */
    private fun allowed(permitted: Set<String>?, uid: String?): Boolean =
        uid != null && (permitted == null || uid in permitted)

    private fun deny(what: String): Nothing = throw D2Error
        .builder()
        .errorComponent(D2ErrorComponent.SDK)
        .errorCode(D2ErrorCode.SCOPE_VIOLATION)
        .errorDescription("Write refused: this D2DataScope does not permit writing $what")
        .build()
}
