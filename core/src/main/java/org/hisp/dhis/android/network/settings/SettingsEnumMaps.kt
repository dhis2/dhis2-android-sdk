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

package org.hisp.dhis.android.network.settings

import org.hisp.dhis.android.core.settings.DataSetFilter
import org.hisp.dhis.android.core.settings.DataSyncPeriod
import org.hisp.dhis.android.core.settings.HomeFilter
import org.hisp.dhis.android.core.settings.MetadataSyncPeriod
import org.hisp.dhis.android.core.settings.ProgramFilter
import org.hisp.dhis.android.core.settings.internal.SettingsAppDataStoreVersion
import org.hisp.dhis.android.core.tracker.TrackerExporterVersion
import org.hisp.dhis.android.core.tracker.TrackerImporterVersion

internal fun DataSyncPeriod.Companion.from(key: String): DataSyncPeriod {
    return when (key) {
        "30m" -> DataSyncPeriod.EVERY_30_MIN
        "1h" -> DataSyncPeriod.EVERY_HOUR
        "6h" -> DataSyncPeriod.EVERY_6_HOURS
        "12h" -> DataSyncPeriod.EVERY_12_HOURS
        "24h" -> DataSyncPeriod.EVERY_24_HOURS
        "manual" -> DataSyncPeriod.MANUAL
        else -> DataSyncPeriod.EVERY_24_HOURS
    }
}

internal fun MetadataSyncPeriod.Companion.from(key: String): MetadataSyncPeriod {
    return when (key) {
        "1h" -> MetadataSyncPeriod.EVERY_HOUR
        "12h" -> MetadataSyncPeriod.EVERY_12_HOURS
        "24h" -> MetadataSyncPeriod.EVERY_24_HOURS
        "7h" -> MetadataSyncPeriod.EVERY_7_DAYS
        "manual" -> MetadataSyncPeriod.MANUAL
        else -> MetadataSyncPeriod.EVERY_24_HOURS
    }
}

internal fun SettingsAppDataStoreVersion.Companion.from(key: String): SettingsAppDataStoreVersion {
    return when (key) {
        "1.1" -> SettingsAppDataStoreVersion.V1_1
        "2.0" -> SettingsAppDataStoreVersion.V2_0
        else -> throw IllegalArgumentException("Invalid version: $key")
    }
}

internal fun TrackerExporterVersion.Companion.from(key: String): TrackerExporterVersion {
    return when (key) {
        "V1" -> TrackerExporterVersion.V1
        "V2" -> TrackerExporterVersion.V2
        else -> TrackerExporterVersion.V1
    }
}

internal fun TrackerImporterVersion.Companion.from(key: String): TrackerImporterVersion {
    return when (key) {
        "V1" -> TrackerImporterVersion.V1
        "V2" -> TrackerImporterVersion.V2
        else -> TrackerImporterVersion.V1
    }
}

internal fun ProgramFilter.Companion.from(key: String): ProgramFilter {
    return when (key) {
        "eventDate" -> ProgramFilter.EVENT_DATE
        "syncStatus" -> ProgramFilter.SYNC_STATUS
        "eventStatus" -> ProgramFilter.EVENT_STATUS
        "assignedToMe" -> ProgramFilter.ASSIGNED_TO_ME
        "enrollmentDate" -> ProgramFilter.ENROLLMENT_DATE
        "enrollmentStatus" -> ProgramFilter.ENROLLMENT_STATUS
        "organisationUnit" -> ProgramFilter.ORG_UNIT
        "categoryCombo" -> ProgramFilter.CAT_COMBO
        "followUp" -> ProgramFilter.FOLLOW_UP
        else -> ProgramFilter.UNKNOWN
    }
}

internal fun HomeFilter.Companion.from(key: String): HomeFilter {
    return when (key) {
        "date" -> HomeFilter.DATE
        "syncStatus" -> HomeFilter.SYNC_STATUS
        "organisationUnit" -> HomeFilter.ORG_UNIT
        "assignedToMe" -> HomeFilter.ASSIGNED_TO_ME
        else -> HomeFilter.UNKNOWN
    }
}

internal fun DataSetFilter.Companion.from(key: String): DataSetFilter {
    return when (key) {
        "syncStatus" -> DataSetFilter.SYNC_STATUS
        "organisationUnit" -> DataSetFilter.ORG_UNIT
        "assignedToMe" -> DataSetFilter.ASSIGNED_TO_ME
        "period" -> DataSetFilter.PERIOD
        "categoryCombo" -> DataSetFilter.CAT_COMBO
        else -> DataSetFilter.UNKNOWN
    }
}
