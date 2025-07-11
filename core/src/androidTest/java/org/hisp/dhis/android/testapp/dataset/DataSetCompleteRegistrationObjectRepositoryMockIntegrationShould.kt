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
package org.hisp.dhis.android.testapp.dataset

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationObjectRepository
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Assert
import org.junit.Test

class DataSetCompleteRegistrationObjectRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    private val dataSetUid = "lyLU2wR22tC"
    private val orgUnitUid = "DiszpKrYNg8"
    private val cocUid = "bRowv6yZOF2"

    @Test
    fun delete() {
        try {
            objectRepository().blockingDelete()
        } catch (d2Error: D2Error) {
            Assert.fail()
        }

        val dataSetCompleteRegistration = objectRepository().blockingGet()
        assertThat(dataSetCompleteRegistration!!.deleted()).isTrue()
        assertThat(dataSetCompleteRegistration.syncState()).isEqualTo(State.TO_UPDATE)
    }

    @Test
    fun delete_newly_created_record() {
        val newObjectRepository = d2.dataSetModule().dataSetCompleteRegistrations()
            .value("2019", orgUnitUid, dataSetUid, cocUid)

        newObjectRepository.blockingSet()
        newObjectRepository.blockingDelete()

        val newObject = newObjectRepository.blockingGet()
        Assert.assertNull(newObject)

        val existingObject = objectRepository().blockingGet()
        Assert.assertNotNull(existingObject)
    }

    private fun objectRepository(): DataSetCompleteRegistrationObjectRepository {
        return d2.dataSetModule().dataSetCompleteRegistrations()
            .value("2017", orgUnitUid, dataSetUid, cocUid)
    }
}
