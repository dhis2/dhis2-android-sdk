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
package org.hisp.dhis.android.core.arch.repositories.filters.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(JUnit4::class)
class UnwrappedEqInFilterConnectorShould {

    @Mock
    private lateinit var baseRepositoryFactory: BaseRepositoryFactory<BaseRepository>

    private val updatedRepositoryScope: KArgumentCaptor<RepositoryScope> = argumentCaptor()

    private val key = "key"

    private lateinit var filterConnector: UnwrappedEqInFilterConnector<BaseRepository>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        filterConnector = UnwrappedEqInFilterConnector(baseRepositoryFactory, RepositoryScope.empty(), key)
    }

    @Test
    fun should_build_in_filter() {
        filterConnector.`in`("uid1", "uid2", "uid3")
        val expectedItem =
            RepositoryScopeFilterItem.builder().key(key).operator(FilterItemOperator.IN).value("(uid1, uid2, uid3)")
                .build()

        verify(baseRepositoryFactory).updated(updatedRepositoryScope.capture())
        val item = updatedRepositoryScope.firstValue.filters()[0]

        assertThat(item).isEqualTo(expectedItem)
    }

    @Test
    fun get_value_should_parse_single_value() {
        val value = "uid"

        val parsedList = UnwrappedEqInFilterConnector.getValueList(value)

        assertThat(parsedList.size).isEqualTo(1)
        assertThat(parsedList[0]).isEqualTo(value)
    }

    @Test
    fun get_value_should_parse_list_value() {
        val value = "(uid1, uid2, uid3)"

        val parsedList = UnwrappedEqInFilterConnector.getValueList(value)

        assertThat(parsedList.size).isEqualTo(3)
        assertThat(parsedList[0]).isEqualTo("uid1")
        assertThat(parsedList[1]).isEqualTo("uid2")
        assertThat(parsedList[2]).isEqualTo("uid3")
    }

    @Test
    fun get_value_should_parse_empty_list() {
        val value = "()"

        val parsedList = UnwrappedEqInFilterConnector.getValueList(value)

        assertThat(parsedList.size).isEqualTo(0)
    }
}
