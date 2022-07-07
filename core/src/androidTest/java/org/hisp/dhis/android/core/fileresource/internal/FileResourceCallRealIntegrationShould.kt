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
package org.hisp.dhis.android.core.fileresource.internal

import com.google.common.truth.Truth.assertThat
import java.io.File
import java.util.*
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.data.server.RealServerMother
import org.hisp.dhis.android.core.event.EventCreateProjection
import org.hisp.dhis.android.core.fileresource.FileResourceDomainType
import org.hisp.dhis.android.core.fileresource.FileResourceElementType

class FileResourceCallRealIntegrationShould : BaseRealIntegrationTest() {

    // @Test
    fun download_and_write_files_successfully() {
        loginAndSyncMetadata()
        d2.trackedEntityModule().trackedEntityInstanceDownloader()
            .byProgramUid("uy2gU8kT1jF").limit(20).blockingDownload()

        d2.fileResourceModule().fileResourceDownloader().blockingDownload()

        val fileResources = d2.fileResourceModule().fileResources().blockingGet()
        assertThat(fileResources.size).isEqualTo(2)

        val file = File(fileResources[0]!!.path()!!)
        assertThat(file.exists()).isTrue()
    }

    // @Test
    fun write_tracked_entity_attribute_related_files_and_upload() {
        loginAndSyncMetadata()
        d2.trackedEntityModule().trackedEntityInstanceDownloader()
            .byProgramUid("uy2gU8kT1jF").limit(20).blockingDownload()

        d2.fileResourceModule().fileResourceDownloader().blockingDownload()

        val fileResources = d2.fileResourceModule().fileResources().blockingGet()
        val file = File(fileResources[0]!!.path()!!)
        assertThat(file.exists()).isTrue()

        val valueUid = d2.fileResourceModule().fileResources().blockingAdd(file)
        val trackedEntityAttribute =
            d2.trackedEntityModule().trackedEntityAttributes().byValueType().eq(ValueType.IMAGE).one().blockingGet()
        val trackedEntityInstance = d2.trackedEntityModule().trackedEntityInstances().blockingGet()[0]
        d2.trackedEntityModule().trackedEntityAttributeValues()
            .value(trackedEntityAttribute.uid(), trackedEntityInstance.uid()).blockingSet(valueUid)

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        val fileResources2 = d2.fileResourceModule().fileResources().blockingGet()
        val file2 = File(fileResources2[1]!!.path()!!)
        assertThat(file2.exists()).isTrue()

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        val trackedEntityInstance2 = d2.trackedEntityModule().trackedEntityInstances().blockingGet()[0]
        assertThat(trackedEntityInstance2.syncState()).isEqualTo(State.SYNCED)
    }

    // @Test
    fun write_data_element_related_images_and_upload() {
        loginAndSyncMetadata()
        d2.eventModule().eventDownloader()
            .byProgramUid("VBqh0ynB2wv").limit(40).blockingDownload()

        d2.fileResourceModule().fileResourceDownloader().blockingDownload()

        val fileResources = d2.fileResourceModule().fileResources().blockingGet()
        val file = File(fileResources[0]!!.path()!!)
        assertThat(file.exists()).isTrue()

        val valueUid = d2.fileResourceModule().fileResources().blockingAdd(file)
        val dataElement = d2.dataElementModule().dataElements().byValueType().eq(ValueType.IMAGE).one().blockingGet()
        val event = d2.eventModule().events().blockingGet()[0]
        d2.trackedEntityModule().trackedEntityDataValues().value(event.uid(), dataElement.uid()).blockingSet(valueUid)
        d2.eventModule().events().blockingUpload()

        val fileResources2 = d2.fileResourceModule().fileResources().blockingGet()
        val file2 = File(fileResources2[1]!!.path()!!)
        assertThat(file2.exists()).isTrue()
    }

    // @Test
    fun write_data_element_related_files_and_upload() {
        loginAndSyncMetadata()
        d2.eventModule().eventDownloader()
            .byProgramUid("eBAyeGv0exc").limit(5).blockingDownload()

        d2.fileResourceModule().fileResourceDownloader()
            .byDomainType().eq(FileResourceDomainType.TRACKER)
            .byElementType().eq(FileResourceElementType.DATA_ELEMENT)
            .blockingDownload()

        val fileResource = d2.fileResourceModule().fileResources().one().blockingGet()!!
        val file = File(fileResource.path()!!)
        assertThat(file.exists()).isTrue()

        val existingValue = d2.trackedEntityModule().trackedEntityDataValues()
            .byValue().eq(fileResource.uid())
            .one().blockingGet()!!

        val existingEvent = d2.eventModule().events().uid(existingValue.event()).blockingGet()

        val newEventUid = d2.eventModule().events().blockingAdd(
            EventCreateProjection.create(
                existingEvent.enrollment(), existingEvent.program(),
                existingEvent.programStage(), existingEvent.organisationUnit(), existingEvent.attributeOptionCombo()
            )
        )
        d2.eventModule().events().uid(newEventUid).setEventDate(Date())

        val newValueUid = d2.fileResourceModule().fileResources().blockingAdd(file)
        d2.trackedEntityModule().trackedEntityDataValues().value(newEventUid, existingValue.dataElement())
            .blockingSet(newValueUid)

        d2.eventModule().events().blockingUpload()
    }

    // @Test
    fun not_download_existing_resources() {
        loginAndSyncMetadata()
        d2.eventModule().eventDownloader()
            .byProgramUid("VBqh0ynB2wv").limit(40).blockingDownload()

        d2.fileResourceModule().fileResourceDownloader().blockingDownload()

        val fileResources = d2.fileResourceModule().fileResources().blockingGet()

        d2.fileResourceModule().fileResourceDownloader().blockingDownload()

        val fileResources2 = d2.fileResourceModule().fileResources().blockingGet()
        assertThat(fileResources.size).isEqualTo(fileResources2.size)
    }

    // @Test
    fun write_aggregated_value_files() {
        loginAndSyncMetadata()
        d2.aggregatedModule().data().blockingDownload()

        d2.fileResourceModule().fileResourceDownloader().blockingDownload()

        val aggregatedFiles = d2.dataElementModule().dataElements()
            .byDomainType().eq("AGGREGATE")
            .byValueType().eq(ValueType.FILE_RESOURCE)
            .blockingGet()

        val dataValue = d2.dataValueModule().dataValues()
            .byDataElementUid().`in`(aggregatedFiles.map { it.uid() })
            .one()
            .blockingGet()!!

        // Copy to following period
        val period = d2.periodModule().periodHelper().blockingGetPeriodForPeriodId(dataValue.period()!!)
        val nextPeriod = d2.periodModule().periodHelper()
            .blockingGetPeriodForPeriodTypeAndDate(period.periodType()!!, period.startDate()!!, 1)

        val fileResource = d2.fileResourceModule().fileResources().uid(dataValue.value()).blockingGet()
        val file = File(fileResource!!.path()!!)
        assertThat(file.exists()).isTrue()

        val uid = d2.fileResourceModule().fileResources().blockingAdd(file)

        d2.dataValueModule().dataValues()
            .value(
                nextPeriod.periodId()!!, dataValue.organisationUnit(), dataValue.dataElement(),
                dataValue.categoryOptionCombo(), dataValue.attributeOptionCombo()
            )
            .blockingSet(uid)

        d2.dataValueModule().dataValues().blockingUpload()
    }

    private fun loginAndSyncMetadata() {
        d2.userModule().logIn(username, password, RealServerMother.url2_36).blockingGet()
        d2.metadataModule().blockingDownload()
    }
}
