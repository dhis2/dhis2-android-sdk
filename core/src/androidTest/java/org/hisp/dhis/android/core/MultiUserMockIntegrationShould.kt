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
package org.hisp.dhis.android.core

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.category.CategoryOptionTableInfo
import org.hisp.dhis.android.core.data.category.CategoryOptionSamples
import org.hisp.dhis.android.core.mockwebserver.Dhis2MockServer
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test

class MultiUserMockIntegrationShould : BaseMockIntegrationTestEmptyEnqueable() {

    @Test
    fun connect_to_server_with_2_different_users() {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }

        dhis2MockServer.enqueueLoginResponses()
        d2.userModule().blockingLogIn("u1", "p1", dhis2MockServer.baseEndpoint)
        d2.databaseAdapter().insert(
            CategoryOptionTableInfo.TABLE_INFO.name(), null,
            CategoryOptionSamples.getCategoryOption().toContentValues()
        )
        assertThat(d2.categoryModule().categoryOptions().blockingCount()).isEqualTo(1)
        d2.userModule().blockingLogOut()

        dhis2MockServer.enqueueLoginResponses()
        d2.userModule().blockingLogIn("u2", "p2", dhis2MockServer.baseEndpoint)
        assertThat(d2.categoryModule().categoryOptions().blockingCount()).isEqualTo(0)
        d2.userModule().blockingLogOut()

        dhis2MockServer.enqueueLoginResponses()
        d2.userModule().blockingLogIn("u1", "p1", dhis2MockServer.baseEndpoint)
        assertThat(d2.categoryModule().categoryOptions().blockingCount()).isEqualTo(1)
    }

    @Test
    fun connect_to_two_servers() {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }
        dhis2MockServer.enqueueLoginResponses()
        d2.userModule().blockingLogIn("u1", "p1", dhis2MockServer.baseEndpoint)
        d2.databaseAdapter().insert(
            CategoryOptionTableInfo.TABLE_INFO.name(), null,
            CategoryOptionSamples.getCategoryOption().toContentValues()
        )
        assertThat(d2.categoryModule().categoryOptions().blockingCount()).isEqualTo(1)
        d2.userModule().blockingLogOut()

        val server2 = Dhis2MockServer(0)
        server2.enqueueLoginResponses()
        d2.userModule().blockingLogIn("u2", "p2", server2.baseEndpoint)
        assertThat(d2.categoryModule().categoryOptions().blockingCount()).isEqualTo(0)
        d2.userModule().blockingLogOut()

        dhis2MockServer.enqueueLoginResponses()
        d2.userModule().blockingLogIn("u1", "p1", dhis2MockServer.baseEndpoint)
        assertThat(d2.categoryModule().categoryOptions().blockingCount()).isEqualTo(1)
    }

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpTestClass() {
            setUpClass()
            d2.userModule().accountManager().setMaxAccounts(5)
        }

        @AfterClass
        @JvmStatic
        fun tearDownTestClass() {
            d2.userModule().accountManager().setMaxAccounts(1)
        }
    }
}
