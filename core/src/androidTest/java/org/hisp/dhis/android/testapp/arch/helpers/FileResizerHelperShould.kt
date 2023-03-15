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
package org.hisp.dhis.android.testapp.arch.helpers

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class FileResizerHelperShould {

    @Test
    fun resize_to_small_file() {
        val file = getFile(CompressFormat.PNG, getBitmap(2048, 1024))
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        assertThat(bitmap.height).isEqualTo(1024)
        assertThat(bitmap.width).isEqualTo(2048)

        val resizedFile = FileResizerHelper.resizeFile(file, FileResizerHelper.Dimension.SMALL)
        val resizedBitmap = BitmapFactory.decodeFile(resizedFile.absolutePath)
        assertThat(resizedBitmap.height).isEqualTo(128)
        assertThat(resizedBitmap.width).isEqualTo(256)
    }

    @Test
    fun resize_to_medium_file() {
        val file = getFile(CompressFormat.PNG, getBitmap(2048, 1024))
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        assertThat(bitmap.height).isEqualTo(1024)
        assertThat(bitmap.width).isEqualTo(2048)

        val resizedFile = FileResizerHelper.resizeFile(file, FileResizerHelper.Dimension.MEDIUM)
        val resizedBitmap = BitmapFactory.decodeFile(resizedFile.absolutePath)
        assertThat(resizedBitmap.height).isEqualTo(256)
        assertThat(resizedBitmap.width).isEqualTo(512)
    }

    @Test
    fun resize_to_large_file() {
        val file = getFile(CompressFormat.PNG, getBitmap(2048, 1024))
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        assertThat(bitmap.height).isEqualTo(1024)
        assertThat(bitmap.width).isEqualTo(2048)

        val resizedFile = FileResizerHelper.resizeFile(file, FileResizerHelper.Dimension.LARGE)
        val resizedBitmap = BitmapFactory.decodeFile(resizedFile.absolutePath)
        assertThat(resizedBitmap.height).isEqualTo(512)
        assertThat(resizedBitmap.width).isEqualTo(1024)
    }

    @Test
    fun resize_jpeg() {
        val file = getFile(CompressFormat.JPEG, getBitmap(2048, 1024))
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        assertThat(bitmap.height).isEqualTo(1024)
        assertThat(bitmap.width).isEqualTo(2048)

        val resizedFile = FileResizerHelper.resizeFile(file, FileResizerHelper.Dimension.SMALL)
        val resizedBitmap = BitmapFactory.decodeFile(resizedFile.absolutePath)
        assertThat(resizedBitmap.height).isEqualTo(128)
        assertThat(resizedBitmap.width).isEqualTo(256)
    }

    @Test
    fun do_not_resize_small_to_large_file() {
        val file = getFile(CompressFormat.PNG, getBitmap(100, 125))
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        assertThat(bitmap.height).isEqualTo(125)
        assertThat(bitmap.width).isEqualTo(100)

        val resizedFile = FileResizerHelper.resizeFile(file, FileResizerHelper.Dimension.LARGE)
        val resizedBitmap = BitmapFactory.decodeFile(resizedFile.absolutePath)
        assertThat(resizedBitmap.height).isEqualTo(125)
        assertThat(resizedBitmap.width).isEqualTo(100)
    }

    companion object {
        private fun getFile(compressFormat: CompressFormat, bitmap: Bitmap): File {
            val context = InstrumentationRegistry.getInstrumentation().context
            val imageFile = File(
                FileResourceDirectoryHelper.getRootFileResourceDirectory(context),
                "image." +
                    compressFormat.name.lowercase(Locale.getDefault())
            )
            val os: OutputStream
            try {
                os = FileOutputStream(imageFile)
                bitmap.compress(compressFormat, 100, os)
                os.flush()
                os.close()
            } catch (e: Exception) {
                Log.e(FileResizerHelperShould::class.java.simpleName, "Error writing bitmap", e)
            }
            return imageFile
        }

        private fun getBitmap(width: Int, height: Int): Bitmap {
            return Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        }
    }
}
