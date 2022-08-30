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

import com.fasterxml.jackson.annotation.JsonProperty

internal data class JobImportCount(
    val created: Int,
    val updated: Int,
    val deleted: Int,
    val ignored: Int,
    val total: Int
)

internal data class JobValidationError(
    val uid: String,
    val trackerType: TrackerImporterObjectType,
    val errorCode: String,
    val message: String
)

internal data class JobValidationReport(
    val errorReports: List<JobValidationError>
)

internal data class JobObjectReport(
    val errorReports: List<String>,
    val index: Int,
    val trackerType: TrackerImporterObjectType,
    val uid: String
)

internal data class JobTypeReport(
    val trackerType: String,
    val stats: JobImportCount,
    val objectReports: List<JobObjectReport>
)

internal data class JobTypeReportMap(
    @JsonProperty("TRACKED_ENTITY") val trackedEntity: JobTypeReport,
    @JsonProperty("EVENT") val event: JobTypeReport,
    @JsonProperty("RELATIONSHIP") val relationship: JobTypeReport,
    @JsonProperty("ENROLLMENT") val enrollment: JobTypeReport
)

internal data class JobBundleReport(
    val status: String,
    val typeReportMap: JobTypeReportMap,
    val stats: JobImportCount
) // TODO whats the difference with father

internal data class JobReport(
    val status: String,
    val validationReport: JobValidationReport,
    val stats: JobImportCount?,
    val bundleReport: JobBundleReport?
)
