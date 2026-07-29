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

import android.graphics.Bitmap
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.arch.helpers.FileCompressionHelper
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper.getFileResourceDirectory
import org.hisp.dhis.android.core.arch.helpers.ResourceContext
import org.hisp.dhis.android.core.data.fileresource.RandomGeneratedInputStream
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.internal.FileResourceStore
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil.writeInputStream
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Random

/**
 * The image settings driving these tests come from the downloaded `settings/synchronization_settings.json`, so
 * nothing is written to the settings tables. Inserting a setting row for a synthetic program/dataSet uid violates
 * its foreign key, and the failed transaction poisons the shared Room connection for the rest of the class.
 */
@RunWith(D2JunitRunner::class)
class FileResourceAddMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    private val addedFileResources = mutableListOf<FileResource>()
    private val addedSourceFiles = mutableListOf<File>()

    /**
     * Runs the test body and its clean up inside a single coroutine, going through the stores. Room binds the
     * connection, and the transaction open on it, to the coroutine context, so cleaning up from a separate
     * coroutine (an `@After` with its own `runTest`) fails with "cannot start a transaction within a transaction".
     */
    private fun fileResourceTest(block: suspend () -> Unit) = runTest {
        try {
            block()
        } finally {
            removeAddedFileResources()
        }
    }

    private suspend fun removeAddedFileResources() {
        addedFileResources.forEach { fileResource ->
            File(fileResource.path()).delete()
            koin.get<FileResourceStore>().delete(fileResource.uid())
        }
        addedFileResources.clear()

        addedSourceFiles.forEach { sourceFile ->
            File(sourceFile.parent, "compressed-${sourceFile.name}").delete()
            sourceFile.delete()
        }
        addedSourceFiles.clear()
    }

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
        koin.get<FileResourceStore>().delete(fileResource.uid())
    }

    @Test
    fun add_file_untouched_for_a_plain_file_context() = fileResourceTest {
        val file = storeImageFile("plain-file.png")

        val fileResource = processAndAdd(file, ResourceContext.FileContext)

        assertThat(fileResource.contentLength()).isEqualTo(file.length())
    }

    @Test
    fun compress_image_when_program_setting_quality_is_default() = fileResourceTest {
        val file = storeImageFile("program-default.png")

        val fileResource = processAndAdd(file, programImageContext(file, ITEM_B_UID))

        assertCompressed(fileResource, file)
    }

    @Test
    fun keep_original_image_when_program_setting_quality_is_original() = fileResourceTest {
        val file = storeImageFile("program-original.png")

        val fileResource = processAndAdd(file, programImageContext(file, ITEM_A_UID))

        assertThat(fileResource.contentLength()).isEqualTo(file.length())
    }

    @Test
    fun compress_image_when_dataset_setting_quality_is_default() = fileResourceTest {
        val file = storeImageFile("dataset-default.png")

        val fileResource = processAndAdd(file, dataSetImageContext(file, ITEM_A_UID))

        assertCompressed(fileResource, file)
    }

    @Test
    fun keep_original_image_when_dataset_setting_quality_is_original() = fileResourceTest {
        val file = storeImageFile("dataset-original.png")

        val fileResource = processAndAdd(file, dataSetImageContext(file, ITEM_B_UID))

        assertThat(fileResource.contentLength()).isEqualTo(file.length())
    }

    @Test
    fun compress_image_when_the_item_has_no_configured_quality() = fileResourceTest {
        val file = storeImageFile("program-unconfigured-item.png")

        val fileResource = processAndAdd(file, programImageContext(file, "unconfiguredIt"))

        assertCompressed(fileResource, file)
    }

    @Test
    fun compress_image_when_the_program_has_no_settings_at_all() = fileResourceTest {
        val file = storeImageFile("program-unconfigured.png")

        val fileResource = processAndAdd(
            file,
            ResourceContext.ImageContext.ProgramImageContext(
                programUid = "unconfiguredPr",
                resourceUid = ITEM_A_UID,
            ),
        )

        assertCompressed(fileResource, file)
    }

    private fun assertCompressed(fileResource: FileResource, sourceFile: File) {
        assertThat(fileResource.contentLength()).isLessThan(sourceFile.length())
        assertThat(fileResource.contentLength()).isAtMost(FileCompressionHelper.TARGET_SIZE_BYTES)
    }

    private suspend fun processAndAdd(file: File, resourceContext: ResourceContext): FileResource {
        val uid = d2.fileResourceModule().fileResources().suspendProcessAndAdd(file, resourceContext)

        val fileResource = d2.fileResourceModule().fileResources().uid(uid).blockingGet()!!
        addedFileResources.add(fileResource)

        assertThat(File(fileResource.path()).exists()).isTrue()
        return fileResource
    }

    private fun programImageContext(file: File, itemUid: String) =
        ResourceContext.ImageContext.ProgramImageContext(
            programUid = PROGRAM_UID,
            resourceUid = itemUid,
        )

    private fun dataSetImageContext(file: File, itemUid: String) =
        ResourceContext.ImageContext.DatasetImageContext(
            datasetUid = DATA_SET_UID,
            resourceUid = itemUid,
        )

    private fun storeFile(): File {
        val inputStream: InputStream = RandomGeneratedInputStream(1024)
        val context = InstrumentationRegistry.getInstrumentation().context
        val destinationFile = File(getFileResourceDirectory(context), "file1.png")
        return writeInputStream(inputStream, destinationFile, 1024)
    }

    /**
     * Stores a decodable PNG of random pixels, large enough that the default compression has to shrink it. Random
     * noise barely compresses, so the encoded size stays well above [FileCompressionHelper.TARGET_SIZE_BYTES].
     */
    private fun storeImageFile(name: String): File {
        val context = InstrumentationRegistry.getInstrumentation().context
        val destinationFile = File(getFileResourceDirectory(context), name)

        val random = Random(SEED)
        val pixels = IntArray(IMAGE_SIDE_PX * IMAGE_SIDE_PX) { OPAQUE or random.nextInt(RGB_RANGE) }
        val bitmap = Bitmap.createBitmap(IMAGE_SIDE_PX, IMAGE_SIDE_PX, Bitmap.Config.ARGB_8888)
        try {
            bitmap.setPixels(pixels, 0, IMAGE_SIDE_PX, 0, 0, IMAGE_SIDE_PX, IMAGE_SIDE_PX)
            FileOutputStream(destinationFile).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        } finally {
            bitmap.recycle()
        }

        assertThat(destinationFile.length()).isGreaterThan(FileCompressionHelper.TARGET_SIZE_BYTES)
        addedSourceFiles.add(destinationFile)
        return destinationFile
    }

    companion object {
        // Configured in settings/synchronization_settings.json. Each item is given the opposite quality by the
        // program and by the dataSet setting, so a single uid covers both branches depending on the context:
        // item A -> ORIGINAL for the program, DEFAULT for the dataSet; item B -> the other way around.
        private const val PROGRAM_UID = "IpHINAT79UW"
        private const val DATA_SET_UID = "BfMAe6Itzgt"
        private const val ITEM_A_UID = "aJK3Dfn45Ol"
        private const val ITEM_B_UID = "bJK3Dfn45Ol"

        private const val IMAGE_SIDE_PX = 1000
        private const val OPAQUE = 0xFF000000.toInt()
        private const val RGB_RANGE = 0xFFFFFF
        private const val SEED = 42L
    }
}
