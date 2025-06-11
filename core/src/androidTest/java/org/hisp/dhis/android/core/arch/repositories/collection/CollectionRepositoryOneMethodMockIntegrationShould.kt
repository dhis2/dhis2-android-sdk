/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.arch.repositories.collection

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Test
import org.junit.runner.RunWith

class CollectionRepositoryOneMethodMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    private val birthUid = "m2jTvAj5kkm"
    private val defaultUid = "p0KPaWEg3cf"

    @Test
    fun get_first_object_without_filters() {
        val combo = d2.categoryModule().categoryCombos()
            .one()
            .blockingGet()

        assertThat(combo!!.uid()).isEqualTo(birthUid)
    }

    @Test
    fun get_first_when_filter_limits_to_one_object() {
        val combo = d2.categoryModule().categoryCombos()
            .byName().eq("Births")
            .one()
            .blockingGet()

        assertThat(combo!!.uid()).isEqualTo(birthUid)
    }

    @Test
    fun get_first_when_filter_limits_to_other_object() {
        val combo = d2.categoryModule().categoryCombos()
            .byIsDefault().isTrue
            .one()
            .blockingGet()

        assertThat(combo!!.uid()).isEqualTo(defaultUid)
    }

    @Test
    fun get_first_when_filter_limits_to_no_objects() {
        val combo = d2.categoryModule().categoryCombos()
            .byName().eq("Wrong name")
            .one()
            .blockingGet()

        assertThat(combo).isNull()
    }

    @Test
    fun get_with_all_children_returns_object_children() {
        val combo = d2.categoryModule().categoryCombos()
            .withCategories()
            .one()
            .blockingGet()

        assertThat(combo!!.uid()).isEqualTo(birthUid)
        assertThat(combo.categories()!!.size).isEqualTo(2)
    }
}
