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
package org.hisp.dhis.android.core.trackedentity.internal

import com.fasterxml.jackson.databind.ObjectMapper
import org.hisp.dhis.android.core.arch.file.ResourcesFileReader
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.tracker.TrackerExporterVersion
import org.hisp.dhis.android.core.tracker.TrackerImporterVersion
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class TrackedEntityInstanceCallOldMockIntegrationShould : TrackedEntityInstanceCallBaseMockIntegrationShould() {

    override val importerVersion = TrackerImporterVersion.V1
    override val exporterVersion = TrackerExporterVersion.V1
    override val teiFile = "trackedentity/tracked_entity_instance.json"
    override val teiCollectionFile = "trackedentity/tracked_entity_instance_collection.json"
    override val teiSingleFile = "trackedentity/tracked_entity_instance_single.json"
    override val teiWithRemovedDataFile = "trackedentity/tracked_entity_instance_with_removed_data_single.json"
    override val teiWithRelationshipFile = "trackedentity/tracked_entity_instances_with_relationship.json"

    override fun parseTrackedEntityInstance(file: String): TrackedEntityInstance {
        val expectedEventsResponseJson = ResourcesFileReader().getStringFromFile(file)
        val objectMapper = ObjectMapper().setDateFormat(DateUtils.DATE_FORMAT.raw())
        return objectMapper.readValue(expectedEventsResponseJson, TrackedEntityInstance::class.java)
    }
}
