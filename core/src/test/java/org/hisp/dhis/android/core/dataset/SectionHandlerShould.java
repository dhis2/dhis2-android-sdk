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
package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class SectionHandlerShould {

    @Mock
    private IdentifiableObjectStore<SectionModel> sectionStore;

    @Mock
    private LinkModelHandler<ObjectWithUid, SectionDataElementLinkModel> sectionDataElementLinkHandler;

    @Mock
    private Section section;

    // object to test
    private SectionHandler sectionHandler;

    List<ObjectWithUid> dataElements;

    @Before
    public void setUp() throws Exception {
        
        MockitoAnnotations.initMocks(this);

        sectionHandler = new SectionHandler(sectionStore, sectionDataElementLinkHandler);

        when(section.uid()).thenReturn("section_uid");

        dataElements = new ArrayList<>();
        dataElements.add(ObjectWithUid.create("dataElement_uid"));
        when(section.dataElements()).thenReturn(dataElements);
    }

    @Test
    public void passingNullArguments_shouldNotPerformAnyAction() {

       sectionHandler.handle(null, null);

        verify(sectionStore, never()).delete(anyString());

        verify(sectionStore, never()).update(any(SectionModel.class));

        verify(sectionStore, never()).insert(any(SectionModel.class));
    }

    @Test
    public void handlingSection_shouldHandleLinkedDataElements() {

        sectionHandler.handle(section, new SectionModelBuilder());
        verify(sectionDataElementLinkHandler).handleMany(eq(section.uid()), eq(dataElements), any(SectionDataElementLinkModelBuilder.class));
    }


}
