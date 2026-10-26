/*
 *  Copyright (c) 2004-2026, University of Oslo
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

import android.util.Log
import okio.Buffer
import okio.FileSystem
import okio.IOException
import okio.Path
import okio.Path.Companion.toPath
import okio.Source
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil.generateFileName
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil.getExtension
import org.koin.core.annotation.Singleton

/**
 * Operations on the files associated with the file resources.
 */
@Singleton
internal class FileResourceFiles(
    private val fileSystem: FileSystem,
    private val fileResourceDirectories: FileResourceDirectories,
) {

    fun getFile(fileResource: FileResource): Path? {
        return fileResource.path()?.toPath()?.takeIf { fileSystem.exists(it) }
    }

    fun readBytes(file: Path): ByteArray {
        return fileSystem.read(file) { readByteArray() }
    }

    fun renameFile(file: Path, newFileName: String): Path {
        val generatedName = generateFileName(newFileName, getExtension(file.name))
        val parent = file.parent ?: fileResourceDirectories.fileResourceDirectory()
        val newFile = parent / generatedName

        try {
            fileSystem.atomicMove(file, newFile)
        } catch (e: IOException) {
            Log.d(TAG, "Fail renaming ${file.name} to $generatedName", e)
        }

        return newFile
    }

    @Throws(IOException::class)
    fun saveFile(sourceFile: Path, fileResourceUid: String): Path {
        val source = fileSystem.source(sourceFile)
        val generatedName = generateFileName(fileResourceUid, getExtension(sourceFile.name))
        val destinationFile = fileResourceDirectories.fileResourceDirectory() / generatedName

        return writeSource(source, destinationFile)
    }

    fun saveFileFromResponse(body: ByteArray, fileResource: FileResource): Path {
        val generatedName = generateFileName(fileResource.uid(), getExtension(fileResource.name()!!))
        val destinationFile = fileResourceDirectories.fileResourceDirectory() / generatedName

        return writeSource(Buffer().write(body), destinationFile)
    }

    fun writeSource(source: Source, file: Path): Path {
        try {
            source.use { fileSystem.write(file) { writeAll(it) } }
        } catch (e: IOException) {
            e.message?.let { Log.v(TAG, it) }
        }
        return file
    }

    private companion object {
        const val TAG = "FileResourceFiles"
    }
}
