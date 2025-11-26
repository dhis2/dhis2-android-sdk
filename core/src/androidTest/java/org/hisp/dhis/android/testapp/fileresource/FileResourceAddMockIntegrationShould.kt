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
package org.hisp.dhis.android.testapp.fileresource

import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper.getFileResourceDirectory
import org.hisp.dhis.android.core.data.fileresource.RandomGeneratedInputStream
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStore
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil.writeInputStream
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.InputStream

@RunWith(D2JunitRunner::class)
class FileResourceAddMockIntegrationShould : BaseMockIntegrationTestEmptyDispatcher() {

    @Test
    fun add_fileResources_to_the_repository() = runTest {
        val file = storeFile()
        assertThat(file.exists()).isTrue()

        val initialFileResources = d2.fileResourceModule().fileResources().blockingGet()

        val fileResourceUid = d2.fileResourceModule().fileResources().blockingAdd(file)
        val finalFileResources = d2.fileResourceModule().fileResources().blockingGet()
        assertThat(finalFileResources.size).isEqualTo(initialFileResources.size + 1)

        val fileResource = d2.fileResourceModule().fileResources()
            .uid(fileResourceUid)
            .blockingGet()!!

        assertThat(fileResource.uid()).isEqualTo(fileResourceUid)

        val savedFile = File(fileResource.path())
        assertThat(savedFile.exists()).isTrue()

        savedFile.delete()
        koin.get<FileResourceStore>().delete(fileResource.uid()!!)
    }

    private fun storeFile(): File {
        val inputStream: InputStream = RandomGeneratedInputStream(1024)
        val context = InstrumentationRegistry.getInstrumentation().context
        val destinationFile = File(getFileResourceDirectory(context), "file1.png")
        return writeInputStream(inputStream, destinationFile, 1024)
    }
}
