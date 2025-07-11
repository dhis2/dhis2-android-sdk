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
package org.hisp.dhis.android.testapp.maintenance

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class ForeignKeyViolationCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations().blockingGet()
        assertThat(foreignKeyViolations.size).isEqualTo(4)
    }

    @Test
    fun filter_by_from_table() {
        val foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
            .byFromTable().eq("Option").blockingGet()
        assertThat(foreignKeyViolations.size).isEqualTo(1)
    }

    @Test
    fun filter_by_from_column() {
        val foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
            .byFromColumn().eq("optionSet").blockingGet()
        assertThat(foreignKeyViolations.size).isEqualTo(1)
    }

    @Test
    fun filter_by_to_table() {
        val foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
            .byToTable().eq("OptionSet").blockingGet()
        assertThat(foreignKeyViolations.size).isEqualTo(1)
    }

    @Test
    fun filter_by_to_column() {
        val foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
            .byToColumn().eq("uid").blockingGet()
        assertThat(foreignKeyViolations.size).isEqualTo(2)
    }

    @Test
    fun filter_by_not_found_value() {
        val foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
            .byNotFoundValue().eq("non_existent_option_set_uid").blockingGet()
        assertThat(foreignKeyViolations.size).isEqualTo(1)
    }

    @Test
    fun filter_by_from_object_uid() {
        val foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
            .byFromObjectUid().eq("non_existent_option_uid").blockingGet()
        assertThat(foreignKeyViolations.size).isEqualTo(1)
    }

    @Test
    fun filter_by_from_object_row() {
        val foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
            .byFromObjectRow().like("optionSet: non_existent_option_set_uid").blockingGet()
        assertThat(foreignKeyViolations.size).isEqualTo(1)
    }

    @Test
    fun filter_by_created() {
        val foreignKeyViolations = d2.maintenanceModule().foreignKeyViolations()
            .byCreated().after("2019-01-15T08:14:06.767".toJavaDate()!!).blockingGet()
        assertThat(foreignKeyViolations.size).isEqualTo(4)
    }
}
