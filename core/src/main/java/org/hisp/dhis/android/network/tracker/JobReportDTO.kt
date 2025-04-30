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

package org.hisp.dhis.android.network.tracker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.tracker.importer.internal.JobBundleReport
import org.hisp.dhis.android.core.tracker.importer.internal.JobImportCount
import org.hisp.dhis.android.core.tracker.importer.internal.JobObjectReport
import org.hisp.dhis.android.core.tracker.importer.internal.JobReport
import org.hisp.dhis.android.core.tracker.importer.internal.JobTypeReport
import org.hisp.dhis.android.core.tracker.importer.internal.JobTypeReportMap
import org.hisp.dhis.android.core.tracker.importer.internal.JobValidationError
import org.hisp.dhis.android.core.tracker.importer.internal.JobValidationReport
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectType

@Serializable
internal data class JobReportDTO(
    val status: String,
    val validationReport: JobValidationReportDTO,
    val stats: JobImportCountDTO?,
    val bundleReport: JobBundleReportDTO?,
) {
    fun toDomain(): JobReport {
        return JobReport(
            status = status,
            validationReport = validationReport.toDomain(),
            stats = stats?.toDomain(),
            bundleReport = bundleReport?.toDomain(),
        )
    }
}

@Serializable
internal data class JobValidationReportDTO(
    val errorReports: List<JobValidationErrorDTO>,
) {
    fun toDomain(): JobValidationReport {
        return JobValidationReport(
            errorReports = errorReports.map { it.toDomain() },
        )
    }
}

@Serializable
internal data class JobValidationErrorDTO(
    val uid: String,
    val trackerType: String,
    val errorCode: String,
    val message: String,
) {
    fun toDomain(): JobValidationError {
        return JobValidationError(
            uid = uid,
            trackerType = TrackerImporterObjectType.valueOf(trackerType),
            errorCode = errorCode,
            message = message,
        )
    }
}

@Serializable
internal data class JobImportCountDTO(
    val created: Int,
    val updated: Int,
    val deleted: Int,
    val ignored: Int,
    val total: Int,
) {
    fun toDomain(): JobImportCount {
        return JobImportCount(
            created = created,
            updated = updated,
            deleted = deleted,
            ignored = ignored,
            total = total,
        )
    }
}

@Serializable
internal data class JobBundleReportDTO(
    val typeReportMap: JobTypeReportMapDTO,
) {
    fun toDomain(): JobBundleReport {
        return JobBundleReport(
            typeReportMap = typeReportMap.toDomain(),
        )
    }
}

@Serializable
internal data class JobTypeReportMapDTO(
    @SerialName("TRACKED_ENTITY") val trackedEntity: JobTypeReportDTO,
    @SerialName("EVENT") val event: JobTypeReportDTO,
    @SerialName("RELATIONSHIP") val relationship: JobTypeReportDTO,
    @SerialName("ENROLLMENT") val enrollment: JobTypeReportDTO,
) {
    fun toDomain(): JobTypeReportMap {
        return JobTypeReportMap(
            trackedEntity = trackedEntity.toDomain(),
            event = event.toDomain(),
            relationship = relationship.toDomain(),
            enrollment = enrollment.toDomain(),
        )
    }
}

@Serializable
internal data class JobTypeReportDTO(
    val trackerType: String,
    val stats: JobImportCountDTO,
    val objectReports: List<JobObjectReportDTO>,
) {
    fun toDomain(): JobTypeReport {
        return JobTypeReport(
            trackerType = trackerType,
            stats = stats.toDomain(),
            objectReports = objectReports.map { it.toDomain() },
        )
    }
}

@Serializable
internal data class JobObjectReportDTO(
    val errorReports: List<String>,
    val trackerType: String,
    val uid: String,
) {
    fun toDomain(): JobObjectReport {
        return JobObjectReport(
            errorReports = errorReports,
            trackerType = TrackerImporterObjectType.valueOf(trackerType),
            uid = uid,
        )
    }
}
