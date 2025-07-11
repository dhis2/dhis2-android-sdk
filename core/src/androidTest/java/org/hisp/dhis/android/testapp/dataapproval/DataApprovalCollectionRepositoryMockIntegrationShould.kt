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
package org.hisp.dhis.android.testapp.dataapproval

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.dataapproval.DataApprovalState
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class DataApprovalCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val dataApprovals = d2.dataSetModule().dataApprovals()
            .blockingGet()

        assertThat(dataApprovals.size).isEqualTo(1)
    }

    @Test
    fun filter_by_workflow() {
        val dataApprovals = d2.dataSetModule().dataApprovals()
            .byWorkflowUid().eq("rIUL3hYOjJc")
            .blockingGet()

        assertThat(dataApprovals.size).isEqualTo(1)
    }

    @Test
    fun filter_by_organisation_unit() {
        val dataApprovals = d2.dataSetModule().dataApprovals()
            .byOrganisationUnitUid().eq("DiszpKrYNg8")
            .blockingGet()

        assertThat(dataApprovals.size).isEqualTo(1)
    }

    @Test
    fun filter_by_period() {
        val dataApprovals = d2.dataSetModule().dataApprovals()
            .byPeriodId().eq("2018")
            .blockingGet()

        assertThat(dataApprovals.size).isEqualTo(1)
    }

    @Test
    fun filter_by_attribute_option_combo() {
        val dataApprovals = d2.dataSetModule().dataApprovals()
            .byAttributeOptionComboUid().eq("Gmbgme7z9BF")
            .blockingGet()

        assertThat(dataApprovals.size).isEqualTo(1)
    }

    @Test
    fun filter_by_state() {
        val dataApprovals = d2.dataSetModule().dataApprovals()
            .byState().eq(DataApprovalState.UNAPPROVED_ABOVE)
            .blockingGet()

        assertThat(dataApprovals.size).isEqualTo(1)
    }
}
