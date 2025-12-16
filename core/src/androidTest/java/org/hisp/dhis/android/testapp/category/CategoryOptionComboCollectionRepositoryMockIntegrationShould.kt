/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.testapp.category

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class CategoryOptionComboCollectionRepositoryMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val categoryOptionCombos = d2.categoryModule().categoryOptionCombos()
            .withCategoryOptions().blockingGet()

        assertThat(categoryOptionCombos.size).isEqualTo(3)
    }

    @Test
    fun filter_by_category_combo_A() {
        val categoryOptionCombos = d2.categoryModule().categoryOptionCombos()
            .byCategoryComboUid().eq("m2jTvAj5kkm")
            .blockingGet()

        assertThat(categoryOptionCombos.size).isEqualTo(2)
    }

    @Test
    fun filter_by_category_combo_B() {
        val categoryOptionCombos = d2.categoryModule().categoryOptionCombos()
            .byCategoryComboUid().eq("p0KPaWEg3cf")
            .blockingGet()

        assertThat(categoryOptionCombos.size).isEqualTo(1)
    }

    @Test
    fun filter_by_category_option() {
        val categoryOptionCombos = d2.categoryModule().categoryOptionCombos()
            .byCategoryOptions(listOf("as6ygGvUGNg"))
            .blockingGet()

        assertThat(categoryOptionCombos.size).isEqualTo(1)
    }

    @Test
    fun filter_by_category_option_list() {
        val categoryOptionCombos = d2.categoryModule().categoryOptionCombos()
            .byCategoryOptions(listOf("Fp4gVHbRvEV", "uZUnebiT5DI"))
            .blockingGet()

        assertThat(categoryOptionCombos.size).isEqualTo(1)
    }

    @Test
    fun not_find_combos_when_filter_by_less_options_than_they_have() {
        val categoryOptionCombos = d2.categoryModule().categoryOptionCombos()
            .byCategoryOptions(listOf("Fp4gVHbRvEV"))
            .blockingGet()

        assertThat(categoryOptionCombos.size).isEqualTo(0)
    }

    @Test
    fun not_find_combos_when_filter_by_more_options_than_they_have() {
        val categoryOptionCombos = d2.categoryModule().categoryOptionCombos()
            .byCategoryOptions(listOf("as6ygGvUGNg", "Fp4gVHbRvEV", "uZUnebiT5DI"))
            .blockingGet()

        assertThat(categoryOptionCombos.size).isEqualTo(0)
    }

    @Test
    fun not_find_combos_when_no_matching_options() {
        val categoryOptionCombos = d2.categoryModule().categoryOptionCombos()
            .byCategoryOptions(listOf("as6ygGvUGNg", "Fp4gVHbRvEV"))
            .blockingGet()
        assertThat(categoryOptionCombos.size).isEqualTo(0)
    }

    @Test
    fun include_category_options_as_children() {
        val categoryOptionCombo = d2.categoryModule().categoryOptionCombos()
            .withCategoryOptions()
            .one()
            .blockingGet()!!

        assertThat(categoryOptionCombo.categoryOptions()!![0].name()).isEqualTo("At PHU")
    }

    @Test
    fun include_category_options_as_children_in_collection_repository_when_all_selected() {
        val categoryOptionCombo = d2.categoryModule().categoryOptionCombos()
            .withCategoryOptions().blockingGet()[0]
        assertThat(categoryOptionCombo.categoryOptions()!![0].name()).isEqualTo("At PHU")
    }

    @Test
    fun include_category_options_as_children_in_object_repository_when_all_selected() {
        val categoryOptionCombo = d2.categoryModule().categoryOptionCombos()
            .withCategoryOptions()
            .one()
            .blockingGet()!!

        assertThat(categoryOptionCombo.categoryOptions()!![0].name()).isEqualTo("At PHU")
    }

    @Test
    fun include_organisation_units_as_children() {
        val categoryOption = d2.categoryModule().categoryOptions()
            .withOrganisationUnits()
            .uid("as6ygGvUGNg")
            .blockingGet()!!

        assertThat(categoryOption.organisationUnits()!!.size).isEqualTo(1)
        assertThat(categoryOption.organisationUnits()!![0].uid()).isEqualTo("DiszpKrYNg8")
    }

    @Test
    fun include_organisation_units_as_children_no_restrictions() {
        val categoryOption = d2.categoryModule().categoryOptions()
            .withOrganisationUnits()
            .uid("TXGfLxZlInA")
            .blockingGet()!!

        assertThat(categoryOption.organisationUnits()).isNull()
    }

    @Test
    fun include_organisation_units_as_children_restrictions_out_of_scope() {
        val categoryOption = d2.categoryModule().categoryOptions()
            .withOrganisationUnits()
            .uid("apsOixVZlf1")
            .blockingGet()!!
        // temporary fix for hotfix 1.13.0.1
        assertThat(categoryOption.organisationUnits()).isNull()
    }
}
