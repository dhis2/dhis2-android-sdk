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
package org.hisp.dhis.android.core.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(JUnit4.class)
public class ObjectStyleHandlerShould {

    @Mock
    private ObjectWithoutUidStore<ObjectStyleModel> store;

    // object to test
    private DictionaryTableHandler<ObjectStyle> styleHandler;

    private static final String UID = "uid";
    private static final String TABLE = "table";
    private static final String COLOR = "red";
    private static final String ICON = "batman";
    private static final ObjectStyle STYLE = ObjectStyle.create(COLOR, ICON);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        styleHandler = new ObjectStyleHandlerImpl(store);
    }

    @Test
    public void call_store_when_style_not_null() throws Exception {
        styleHandler.handle(STYLE, UID, TABLE);
        verify(store).updateOrInsertWhere(ObjectStyleModel.fromPojo(STYLE, UID, TABLE));
        verifyNoMoreInteractions(store);
    }

    @Test
    public void not_call_store_when_style_null() throws Exception {
        styleHandler.handle(null, UID, TABLE);
        verifyNoMoreInteractions(store);
    }
}
