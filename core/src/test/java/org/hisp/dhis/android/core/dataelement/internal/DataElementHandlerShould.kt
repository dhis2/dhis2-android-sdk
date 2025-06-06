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
package org.hisp.dhis.android.core.dataelement.internal

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.attribute.AttributeValue
import org.hisp.dhis.android.core.attribute.internal.DataElementAttributeValueLinkHandler
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.legendset.internal.DataElementLegendSetLinkHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class DataElementHandlerShould {
    private val dataElementStore: DataElementStore = mock()
    private val dataElementAttributeValueLinkHandler: DataElementAttributeValueLinkHandler = mock()
    private val dataElementLegendSetLinkHandler: DataElementLegendSetLinkHandler = mock()

    private val dataElement: DataElement = mock()
    private val legendSet: ObjectWithUid = mock()

    // object to test
    private lateinit var dataElementHandler: DataElementHandler
    private lateinit var dataElements: List<DataElement>
    private lateinit var legendSets: List<ObjectWithUid>

    private val attributeValues: MutableList<AttributeValue> = ArrayList()
    private val attributeValue = ObjectWithUid.create("Att_Uid")

    @Before
    fun setUp() = runTest {
        dataElementHandler = DataElementHandler(
            dataElementStore,
            dataElementAttributeValueLinkHandler,
            dataElementLegendSetLinkHandler,
        )
        dataElements = listOf(dataElement)
        legendSets = listOf(legendSet)

        whenever(dataElement.uid()).doReturn("test_data_element_uid")

    /*    attribute = Attribute.builder()
            .dataElementAttribute(true)
            .uid("Att_Uid")
            .name("att")
            .code("att")
            .valueType(ValueType.TEXT)
            .build()
        val attValue = AttributeValue.builder()
            .value("5")
            .attribute(attribute)
            .build()*/

        val attribute = ObjectWithUid.create("Att_Uid")
        val attValue = AttributeValue.builder()
            .value("5")
            .attribute(attribute)
            .build()
        attributeValues.add(attValue)

        whenever(dataElement.attributeValues()).doReturn(attributeValues)
        whenever(dataElement.legendSets()).doReturn(legendSets)

        whenever(dataElementStore.updateOrInsert(any())).doReturn(HandleAction.Insert)
    }

    @Test
    fun do_nothing_when_passing_null_argument() = runTest {
        dataElementHandler.handle(null)

        // verify that program indicator store is never called
        verify(dataElementStore, never()).delete(any())
        verify(dataElementStore, never()).update(any())
        verify(dataElementStore, never()).insert(any<DataElement>())
    }

    @Test
    fun delete_shouldDeleteDataElement() = runTest {
        whenever(dataElement.deleted()).doReturn(true)
        dataElementHandler.handleMany(dataElements)

        // verify that delete is called once
        verify(dataElementStore, times(1)).deleteIfExists(dataElement.uid())
    }

    @Test
    fun update_shouldUpdateDataElement() = runTest {
        dataElementHandler.handleMany(dataElements)

        // verify that update is called once
        verify(dataElementStore, times(1)).updateOrInsert(any())
        verify(dataElementStore, never()).delete(any())
    }

    @Test
    fun call_attribute_handlers() = runTest {
        dataElementHandler.handleMany(dataElements)
        verify(dataElementAttributeValueLinkHandler).handleMany(
            eq(dataElement.uid()),
            eq(listOf(attributeValue) as Collection<ObjectWithUid>),
            any(),
        )
    }

    @Test
    fun call_data_element_legend_set_handler() = runTest {
        dataElementHandler.handleMany(dataElements)
        verify(dataElementLegendSetLinkHandler).handleMany(any(), any(), any())
    }
}
