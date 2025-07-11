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

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.koin.core.annotation.Singleton

@Singleton
internal class TrackerExporterParameterManager(
    private val dhisVersionManager: DHISVersionManager,
) {
    fun getTrackedEntitiesParameter(uids: Collection<String>?): Map<String, String> {
        return if (uids.isNullOrEmpty()) {
            emptyMap()
        } else if (dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_41)) {
            mapOf(TrackerExporterService.TRACKED_ENTITIES to uids.joinToString(","))
        } else {
            mapOf(TrackerExporterService.TRACKED_ENTITY to uids.joinToString(";"))
        }
    }

    fun getEventsParameter(uids: Collection<String>?): Map<String, String> {
        return if (uids.isNullOrEmpty()) {
            emptyMap()
        } else if (dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_41)) {
            mapOf(TrackerExporterService.EVENTS to uids.joinToString(","))
        } else {
            mapOf(TrackerExporterService.EVENT to uids.joinToString(";"))
        }
    }

    fun getOrgunitModeParameter(mode: OrganisationUnitMode?): Map<String, String> {
        return if (mode == null) {
            emptyMap()
        } else if (dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_41)) {
            mapOf(TrackerExporterService.OU_MODE to mode.name)
        } else {
            mapOf(TrackerExporterService.OU_MODE_BELOW_41 to mode.name)
        }
    }

    fun getOrgunitsParameter(uids: Collection<String>?): Map<String, String> {
        return if (uids.isNullOrEmpty()) {
            emptyMap()
        } else if (dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_41)) {
            mapOf(TrackerExporterService.ORG_UNITS to uids.joinToString(","))
        } else {
            mapOf(TrackerExporterService.ORG_UNIT to uids.joinToString(";"))
        }
    }
}
