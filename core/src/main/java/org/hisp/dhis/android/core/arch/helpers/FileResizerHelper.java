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

package org.hisp.dhis.android.core.arch.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;

import okhttp3.MediaType;

public final class FileResizerHelper {

    public enum Dimension {
        SMALL(256),
        MEDIUM(512),
        LARGE(1024);

        private final int dimension;

        Dimension(int dimension) {
            this.dimension = dimension;
        }

        public int getDimension() {
            return dimension;
        }
    }

    private FileResizerHelper() {}

    /**
     * Resize an image file to a given dimension. The possible dimensions are small (256px), medium (512px) and
     * large (1024px). The method will scale the largest between height and width to the given dimension
     * without change the relation between them. In case both height and width are smaller than the given dimension
     * the method will return the given file without modifications.
     *
     * @param fileToResize  Image file to resize.
     * @param dimension     The dimension to resize.
     * @return The resized {@link File}.
     */
    public static File resizeFile(File fileToResize, Dimension dimension) throws D2Error {
        Bitmap bitmap = BitmapFactory.decodeFile(fileToResize.getAbsolutePath());
        Float width = ((Integer) bitmap.getWidth()).floatValue();
        Float height = ((Integer) bitmap.getHeight()).floatValue();

        float scaleFactor = width / height;
        if (scaleFactor > 1) {
            if (width < dimension.getDimension()) {
                return fileToResize;
            }
            return resize(fileToResize, bitmap,
                    dimension.getDimension(), (int) (dimension.getDimension() / scaleFactor), dimension);
        } else {
            if (height < dimension.getDimension()) {
                return fileToResize;
            }
            return resize(fileToResize, bitmap,
                    (int) (scaleFactor * dimension.getDimension()),  dimension.getDimension(), dimension);
        }
    }

    private static File resize(File fileToResize, Bitmap bitmap, int dstWidth, int dstHeight, Dimension dimension)
            throws D2Error {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, false);
        File resizedFile = new File(fileToResize.getParent(), "resized-" + dimension.name() + "-"
                + fileToResize.getName());

        try (FileOutputStream fileOutputStream = new FileOutputStream(resizedFile)){
            scaledBitmap.compress(getCompressFormat(resizedFile), 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            bitmap.recycle();
            scaledBitmap.recycle();
        } catch (IOException e) {
            throw buildD2Error(e);
        }

        return resizedFile;
    }

    private static Bitmap.CompressFormat getCompressFormat(File file) {
        String contentType = URLConnection.guessContentTypeFromName(file.getName());
        MediaType mediaType = MediaType.get(contentType);
        return mediaType.subtype().equals("jpeg") ? Bitmap.CompressFormat.JPEG :
                Bitmap.CompressFormat.PNG;
    }

    private static D2Error buildD2Error(Exception e) {
        return D2Error.builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.FAIL_RESIZING_IMAGE)
                .errorDescription(e.getMessage())
                .build();
    }
}