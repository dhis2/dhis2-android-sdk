/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.fileresource.internal;

import android.content.Context;
import android.util.Log;

import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper;
import org.hisp.dhis.android.core.fileresource.FileResource;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

import okhttp3.MediaType;
import okhttp3.ResponseBody;

public final class FileResourceUtil {

    private FileResourceUtil() {
    }

    static File getFile(Context context, FileResource fileResource) throws D2Error {
        File file = new File(FileResourceDirectoryHelper.getFileResourceDirectory(context),
                generateFileName(MediaType.get(fileResource.contentType()), fileResource.uid()));

        if (file.exists()) {
            return file;
        } else {
            throw D2Error.builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.FILE_NOT_FOUND)
                    .errorDescription("File not found for this file resource uid")
                    .build();
        }
    }

    static File renameFile(File file, String newFileName, Context context) {
        String contentType = URLConnection.guessContentTypeFromName(file.getName());
        String generatedName = generateFileName(MediaType.get(contentType), newFileName);
        File newFile = new File(context.getFilesDir(), "sdk_resources/" + generatedName);

        if (!file.renameTo(newFile)) {
            Log.d(FileResourceUtil.class.getCanonicalName(),
                    "Fail renaming " + file.getName() + " to " + generatedName);
        }
        return newFile;
    }

    public static File saveFile(File sourceFile, String fileResourceUid, Context context) throws IOException {
        InputStream inputStream = new FileInputStream(sourceFile);

        String contentType = URLConnection.guessContentTypeFromName(sourceFile.getName());
        String generatedName = generateFileName(MediaType.get(contentType), fileResourceUid);
        File destinationFile = new File(FileResourceDirectoryHelper.getFileResourceDirectory(context), generatedName);

        return writeInputStream(inputStream, destinationFile, sourceFile.length());
    }

    static File saveFileFromResponse(ResponseBody body, String generatedFileName, Context context) {
        File destinationFile = new File(FileResourceDirectoryHelper.getFileResourceDirectory(context),
                generateFileName(body.contentType(), generatedFileName));

        writeInputStream(body.byteStream(), destinationFile, body.contentLength());
        return destinationFile;
    }

    public static File writeInputStream(InputStream inputStream, File file, long fileSize) {
        OutputStream outputStream = null;

        try {
            byte[] fileReader = new byte[1024];
            long fileSizeDownloaded = 0;
            outputStream = new FileOutputStream(file);

            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1) {
                    break;
                }

                outputStream.write(fileReader, 0, read);

                fileSizeDownloaded += read;

                Log.d(FileResourceCallFactory.class.getCanonicalName(),
                        "file download: " + fileSizeDownloaded + " of " + fileSize);
            }

            outputStream.flush();

        } catch (IOException e) {
            Log.v(FileResourceUtil.class.getCanonicalName(), e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.v(FileResourceUtil.class.getCanonicalName(), e.getMessage());
            }
        }

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.v(FileResourceUtil.class.getCanonicalName(), e.getMessage());
            }
        }

        return file;
    }

    static String generateFileName(MediaType mediaType, String fileName) {
        return String.format("%s.%s", fileName, mediaType.subtype());
    }
}
