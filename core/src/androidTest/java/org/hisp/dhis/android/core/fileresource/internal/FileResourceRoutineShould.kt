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

import androidx.test.platform.app.InstrumentationRegistry
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.FileResourceDomain
import org.hisp.dhis.android.core.fileresource.FileResourceRoutine
import org.hisp.dhis.android.core.trackedentity.*
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.*
import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl
import org.junit.After


@RunWith(D2JunitRunner::class)
class FileResourceRoutineShould : BaseMockIntegrationTestMetadataDispatcher() {

    private val firstFileResourceUid = "firstFileResourceUid"
    private val secondFileResourceUid = "secondFileResourceUid"
    private val thirdFileResourceUid = "thirdFileResourceUid"

    private val trackedEntityDataValueStore by lazy {
        TrackedEntityDataValueStoreImpl.create(d2.databaseAdapter())
    }
    private val trackedEntityAttributeValueStore by lazy {
        TrackedEntityAttributeValueStoreImpl.create(d2.databaseAdapter())
    }

    private val fileResourceStore by lazy {
        FileResourceStoreImpl.create(d2.databaseAdapter())
    }

    private val fileResourceRoutine by lazy {
        FileResourceRoutine(
            dataElementCollectionRepository = d2.dataElementModule().dataElements(),
            dataValueCollectionRepository = d2.dataValueModule().dataValues(),
            fileResourceCollectionRepository = d2.fileResourceModule().fileResources(),
            fileResourceStore = fileResourceStore,
            trackedEntityAttributeCollectionRepository = d2.trackedEntityModule().trackedEntityAttributes(),
            trackedEntityAttributeValueCollectionRepository = d2.trackedEntityModule().trackedEntityAttributeValues(),
            trackedEntityDataValueCollectionRepository = d2.trackedEntityModule().trackedEntityDataValues()
        )
    }



    // 1. - Create file
    // 2. - Add File FSRepo
    // 3. Get the Id of the FR
    // 4. Create a DATAValue
    // 5. Delete the value
    // 6. Modifier the last update of the FR
    // 7. Call the the routine

    @Before
    fun setup() {
        fileResourceStore.insert(getFileResources())
        val trackedEntityDataValue = TrackedEntityDataValue.builder()
            .value(firstFileResourceUid)
            .event("event")
            .dataElement("data-element")
            .build()

//        val trackedEntityAttributeValue = TrackedEntityAttributeValue.builder()
//            .trackedEntityAttribute("tracked_entity_attribute")
//            .trackedEntityInstance("tracked_entity_instance")
//            .value(secondFileResourceUid)
//            .build()
//        trackedEntityAttributeValueStore.insert(trackedEntityAttributeValue)
        trackedEntityDataValueStore.insert(trackedEntityDataValue)
    }

    @Test
    fun delete_outdated_file_resources_if_present() {
        trackedEntityDataValueStore.delete()
        trackedEntityAttributeValueStore.delete()
        fileResourceRoutine.deleteOutdatedFileResources()
        val fileResources = d2.fileResourceModule().fileResources()
            .blockingGet()

        val (firstFile, secondFile, thirdFile) = getFileResources()
        assertThat(fileResources.size).isEqualTo(1)
        assertThat(File(firstFile.path()!!).exists()).isFalse()
        assertThat(File(secondFile.path()!!).exists()).isFalse()
        assertThat(File(thirdFile.path()!!).exists()).isTrue()
    }

    @After
    fun tearDown() {
        fileResourceStore.delete()
    }

    private fun getFileResources(): List<FileResource> {
        val twoHoursAgo = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, -2)
        }
        val threeHoursAgo = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, -3)
        }
        val firstFileResource = FileResource.builder()
            .uid(firstFileResourceUid)
            .lastUpdated(twoHoursAgo.time)
            .domain(FileResourceDomain.DATA_VALUE)
            .path(getFile("first-file").path)
            .build()

        val secondFileResource = FileResource.builder()
            .uid(secondFileResourceUid)
            .lastUpdated(threeHoursAgo.time)
            .domain(FileResourceDomain.DATA_VALUE)
            .path(getFile("second-file").path)
            .build()

        val thirdFileResource = FileResource.builder()
            .uid(thirdFileResourceUid)
            .lastUpdated(Date())
            .domain(FileResourceDomain.DATA_VALUE)
            .path(getFile("third-file").path)
            .build()
        return listOf(firstFileResource, secondFileResource, thirdFileResource)
    }

    private fun getFile(fileName: String): File {
        val context = InstrumentationRegistry.getInstrumentation().context
        val root = FileResourceDirectoryHelper.getRootFileResourceDirectory(context)
        return File(root, fileName).apply {
            createNewFile()
        }
    }
}