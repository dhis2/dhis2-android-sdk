/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class D2UnitTests {

    @Mock
    private DbOpenHelper dbOpenHelper;

    @Mock
    private OkHttpClient okHttpClient;

    private D2.Builder builder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        HttpUrl dummyUrl = HttpUrl.parse("https://play.dhis2.org/demo");

        this.builder = new D2.Builder()
                .baseUrl(dummyUrl)
                .okHttpClient(okHttpClient)
                .dbOpenHelper(dbOpenHelper);
    }

    @Test
    public void build_shouldThrowIllegalArgumentException_ifNoOkHttpClient() {
        try {
            builder.okHttpClient(null).build();

            fail("IllegalArgumentException was expected, but was not thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    public void build_shouldThrowIllegalArgumentException_ifNoDbOpenHelper() {
        try {
            builder.dbOpenHelper(null).build();

            fail("IllegalArgumentException was expected, but was not thrown");
        } catch (IllegalArgumentException illegalArgumentException) {
            // swallow exception
        }
    }

    @Test
    public void buildShouldThrowExceptionWhenNoBaseUrl() {
        try {
            builder.baseUrl(null).build();

            fail("IllegalStateException was expected, but was not thrown");
        } catch (IllegalStateException illegalStateException) {
            // swallow exception
        }
    }
}
