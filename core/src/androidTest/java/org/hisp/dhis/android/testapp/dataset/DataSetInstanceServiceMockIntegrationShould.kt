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

package org.hisp.dhis.android.testapp.dataset

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.dataset.DataSetEditableStatus
import org.hisp.dhis.android.core.dataset.DataSetNonEditableReason
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test
import java.util.Date

class DataSetInstanceServiceMockIntegrationShould :
    BaseMockIntegrationTestFullDispatcher() {

    @Test
    fun do_not_allow_edit_expired_periods() {
        val status = d2.dataSetModule().dataSetInstanceService()
            .blockingGetEditableStatus(
                "lyLU2wR22tC",
                "201206",
                "DiszpKrYNg8",
                "bRowv6yZOF2",
            )

        assertThat(status).isInstanceOf(
            DataSetEditableStatus.NonEditable(DataSetNonEditableReason.EXPIRED)::class.java,
        )
    }

    @Test
    fun do_not_allow_edit_closed_periods() {
        val fivePeriodsInFuture = d2.periodModule().periodHelper().blockingGetPeriodForPeriodTypeAndDate(
            PeriodType.Monthly,
            Date(),
            5,
        )
        val status = d2.dataSetModule().dataSetInstanceService()
            .blockingGetEditableStatus(
                "lyLU2wR22tC",
                fivePeriodsInFuture.periodId()!!,
                "DiszpKrYNg8",
                "bRowv6yZOF2",
            )

        assertThat(status).isInstanceOf(
            DataSetEditableStatus.NonEditable(DataSetNonEditableReason.CLOSED)::class.java,
        )
    }

    @Test
    fun return_missing_mandatory_data_element_operands() {
        val dataElementOperands = d2.dataSetModule().dataSetInstanceService()
            .blockingGetMissingMandatoryDataElementOperands(
                "lyLU2wR22tC",
                "201908",
                "DiszpKrYNg8",
                "Gmbgme7z9BF",
            )

        assertThat(dataElementOperands.size).isEqualTo(2)
        assertThat(dataElementOperands[0].dataElement()?.uid()).isEqualTo("bx6fsa0t90x")
        assertThat(dataElementOperands[0].categoryOptionCombo()).isNull()
    }

    @Test
    fun return_missing_mandatory_fields_combinations() {
        val dataElementOperands = d2.dataSetModule().dataSetInstanceService()
            .blockingGetMissingMandatoryFieldsCombination(
                "BfMAe6Itzgt",
                "201908",
                "DiszpKrYNg8",
                "Gmbgme7z9BF",
            )

        assertThat(dataElementOperands.size).isEqualTo(1)
        assertThat(dataElementOperands[0].dataElement()?.uid()).isEqualTo("g9eOBujte1U")
        assertThat(dataElementOperands[0].categoryOptionCombo()?.uid()).isEqualTo("Gmbgme7z9BF")
    }
}
