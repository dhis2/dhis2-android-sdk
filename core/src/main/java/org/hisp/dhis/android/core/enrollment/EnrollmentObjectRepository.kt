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
package org.hisp.dhis.android.core.enrollment

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ObjectRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.`object`.internal.ReadWriteWithUidDataObjectRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.Unit
import org.hisp.dhis.android.core.common.internal.TrackerDataManager
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore
import org.hisp.dhis.android.core.maintenance.D2Error
import java.util.Date

class EnrollmentObjectRepository internal constructor(
    store: EnrollmentStore,
    uid: String?,
    databaseAdapter: DatabaseAdapter,
    childrenAppenders: ChildrenAppenderGetter<Enrollment>,
    scope: RepositoryScope,
    private val trackerDataManager: TrackerDataManager,
) : ReadWriteWithUidDataObjectRepositoryImpl<Enrollment, EnrollmentObjectRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    ObjectRepositoryFactory { s: RepositoryScope ->
        EnrollmentObjectRepository(
            store,
            uid,
            databaseAdapter,
            childrenAppenders,
            s,
            trackerDataManager,
        )
    },
) {

    @Throws(D2Error::class)
    fun setOrganisationUnitUid(organisationUnitUid: String?): Unit {
        return updateIfChanged(organisationUnitUid, { it.organisationUnit() }) { enrollment: Enrollment, value ->
            updateBuilder(enrollment).organisationUnit(value).build()
        }
    }

    @Throws(D2Error::class)
    fun setEnrollmentDate(enrollmentDate: Date?): Unit {
        return updateIfChanged(enrollmentDate, { it.enrollmentDate() }) { enrollment: Enrollment, value ->
            updateBuilder(enrollment).enrollmentDate(value).build()
        }
    }

    @Throws(D2Error::class)
    fun setIncidentDate(incidentDate: Date?): Unit {
        return updateIfChanged(incidentDate, { it.incidentDate() }) { enrollment: Enrollment, value ->
            updateBuilder(enrollment).incidentDate(value).build()
        }
    }

    @Throws(D2Error::class)
    fun setCompletedDate(completedDate: Date?): Unit {
        return updateIfChanged(completedDate, { it.completedDate() }) { enrollment: Enrollment, value ->
            updateBuilder(enrollment).completedDate(value).build()
        }
    }

    @Throws(D2Error::class)
    fun setFollowUp(followUp: Boolean?): Unit {
        return updateIfChanged(followUp, { it.followUp() }) { enrollment: Enrollment, value ->
            updateBuilder(enrollment).followUp(value).build()
        }
    }

    @Throws(D2Error::class)
    fun setStatus(enrollmentStatus: EnrollmentStatus): Unit {
        return updateIfChanged(enrollmentStatus, { it.status() }) { enrollment: Enrollment, value ->
            val completedDate = if (value == EnrollmentStatus.COMPLETED) Date() else null
            updateBuilder(enrollment).status(value).completedDate(completedDate).build()
        }
    }

    @Throws(D2Error::class)
    fun setGeometry(geometry: Geometry?): Unit {
        GeometryHelper.validateGeometry(geometry)
        return updateIfChanged(geometry, { it.geometry() }) { enrollment: Enrollment, value ->
            updateBuilder(enrollment).geometry(value).build()
        }
    }

    private fun updateBuilder(enrollment: Enrollment): Enrollment.Builder {
        val updateDate = Date()
        var state = enrollment.aggregatedSyncState()
        state = if (state === State.TO_POST) state else State.TO_UPDATE
        return enrollment.toBuilder()
            .syncState(state)
            .aggregatedSyncState(state)
            .lastUpdated(updateDate)
            .lastUpdatedAtClient(updateDate)
    }

    override fun propagateState(m: Enrollment, action: HandleAction) {
        trackerDataManager.propagateEnrollmentUpdate(m, action)
    }

    override fun deleteObject(m: Enrollment) {
        trackerDataManager.deleteEnrollment(m)
    }
}
