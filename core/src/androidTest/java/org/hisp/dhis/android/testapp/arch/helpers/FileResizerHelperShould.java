/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.testapp.arch.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.InstrumentationRegistry;

import org.hisp.dhis.android.core.arch.helpers.FileResizerHelper;
import org.hisp.dhis.android.core.fileresource.internal.FileResourceUtil;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.runner.RunWith;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(D2JunitRunner.class)
public class FileResizerHelperShould {

    //@Test
    public void resize_to_small_file() throws D2Error {
        File file = getFile();

        assertThat(file.length(), is(198137L));

        File resizedFile = FileResizerHelper.resizeFile(file, FileResizerHelper.Dimension.SMALL);

        Bitmap resizedBitmap = BitmapFactory.decodeFile(resizedFile.getAbsolutePath());
        assertThat(resizedBitmap.getHeight(), is(256));
    }

    private File getFile() {
        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        return new File(FileResourceUtil.getFileResourceDirectory(context), "ShRULyY1Ggy.png");
    }
}
