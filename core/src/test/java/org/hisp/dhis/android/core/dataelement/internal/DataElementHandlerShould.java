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
package org.hisp.dhis.android.core.dataelement.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler;
import org.hisp.dhis.android.core.arch.handlers.internal.OrderedLinkHandler;
import org.hisp.dhis.android.core.attribute.Attribute;
import org.hisp.dhis.android.core.attribute.AttributeValue;
import org.hisp.dhis.android.core.attribute.DataElementAttributeValueLink;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.legendset.DataElementLegendSetLink;
import org.hisp.dhis.android.core.legendset.LegendSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class DataElementHandlerShould {
    @Mock
    private IdentifiableObjectStore<DataElement> dataElementStore;

    @Mock
    private LinkHandler<Attribute, DataElementAttributeValueLink> dataElementAttributeValueLinkHandler;

    @Mock
    private Handler<Attribute> attributeHandler;

    @Mock
    private OrderedLinkHandler<ObjectWithUid, DataElementLegendSetLink> dataElementLegendSetLinkHandler;

    @Mock
    private Handler<ObjectWithUid> legendSetHandler;

    @Mock
    private DataElement dataElement;

    @Mock
    private ObjectWithUid legendSet;

    // object to test
    private Handler<DataElement> dataElementHandler;

    private List<DataElement> dataElements;

    private List<ObjectWithUid> legendSets;

    private List<AttributeValue> attributeValues = new ArrayList<>();

    Attribute attribute;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        dataElementHandler = new DataElementHandler(
                dataElementStore,
                attributeHandler,
                dataElementAttributeValueLinkHandler,
                dataElementLegendSetLinkHandler
        );

        dataElements = new ArrayList<>();
        dataElements.add(dataElement);

        legendSets = new ArrayList<>();
        legendSets.add(legendSet);

        when(dataElement.uid()).thenReturn("test_data_element_uid");

        attribute = Attribute.builder()
                .dataElementAttribute(true)
                .uid("Att_Uid")
                .name("att")
                .code("att")
                .valueType(ValueType.TEXT)
                .build();

        AttributeValue attValue = AttributeValue.builder()
                .value("5")
                .attribute(attribute)
                .build();

        attributeValues.add(attValue);

        when(dataElement.attributeValues()).thenReturn(attributeValues);
        when(dataElement.legendSets()).thenReturn(legendSets);
    }

    @Test
    public void do_nothing_when_passing_null_argument() {
        dataElementHandler.handle(null);

        // verify that program indicator store is never called
        verify(dataElementStore, never()).delete(anyString());

        verify(dataElementStore, never()).update(any(DataElement.class));

        verify(dataElementStore, never()).insert(any(DataElement.class));
    }


    @Test
    public void delete_shouldDeleteDataElement() {
        when(dataElement.deleted()).thenReturn(Boolean.TRUE);

        dataElementHandler.handleMany(dataElements);

        // verify that delete is called once
        verify(dataElementStore, times(1)).deleteIfExists(dataElement.uid());
    }

    @Test
    public void update_shouldUpdateDataElement() {
        dataElementHandler.handleMany(dataElements);

        // verify that update is called once
        verify(dataElementStore, times(1)).updateOrInsert(any(DataElement.class));

        verify(dataElementStore, never()).delete(anyString());
    }

    @Test
    public void call_attribute_handlers() {
        dataElementHandler.handleMany(dataElements);
        verify(attributeHandler).handleMany(eq(Arrays.asList(attribute)));
        verify(dataElementAttributeValueLinkHandler).handleMany(eq(dataElement.uid()), eq(Arrays.asList(attribute)), any());
    }

    public void call_legend_set_handler() {
        dataElementHandler.handleMany(dataElements);
        verify(legendSetHandler).handleMany(eq(legendSets));
    }
}