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

package org.hisp.dhis.android.core.fileresource.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.fileresource.FileResourceRoutine
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(D2JunitRunner::class)
internal class FileResourceRoutineShould : BaseFileResourceRoutineIntegrationShould() {

    private val fileResourceRoutine by lazy {
        FileResourceRoutine(
            dataElementCollectionRepository = d2.dataElementModule().dataElements(),
            dataValueCollectionRepository = d2.dataValueModule().dataValues(),
            fileResourceCollectionRepository = d2.fileResourceModule().fileResources(),
            fileResourceStore = fileResourceStore,
            customIconStore = customIconStore,
            trackedEntityAttributeCollectionRepository = d2.trackedEntityModule().trackedEntityAttributes(),
            trackedEntityAttributeValueCollectionRepository = d2.trackedEntityModule().trackedEntityAttributeValues(),
            trackedEntityDataValueCollectionRepository = d2.trackedEntityModule().trackedEntityDataValues(),
            clockProvider = ClockProviderFactory.clockProvider
        )
    }

    @Test
    fun delete_outdated_file_resources_if_present() {
        trackedEntityDataValueStore.delete()
        trackedEntityAttributeValueStore.delete()
        fileResourceRoutine.blockingDeleteOutdatedFileResources()
        val fileResources = d2.fileResourceModule().fileResources().blockingGet()
        assertThat(fileResources.size).isEqualTo(1)
        assertThat(File(FileResourceRoutineSamples.fileResource1.path()!!).exists()).isFalse()
        assertThat(File(FileResourceRoutineSamples.fileResource2.path()!!).exists()).isFalse()
        assertThat(File(FileResourceRoutineSamples.fileResource3.path()!!).exists()).isTrue()
    }
}
