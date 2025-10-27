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
package org.hisp.dhis.android.testapp.user

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.mockwebserver.Dhis2MockServer
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(D2JunitRunner::class)
class AccountManagerMockIntegrationShould : BaseMockIntegrationTestEmptyEnqueable() {

    private val user1 = "username1"
    private val pass1 = "password1"
    private val user2 = "username2"
    private val pass2 = "password2"

    @Test
    fun find_accounts_after_login() {
        val initialAccountSize = d2.userModule().accountManager().getAccounts().size

        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }
        dhis2MockServer.enqueueLoginResponses()
        d2.userModule().blockingLogIn(user1, pass1, dhis2MockServer.baseEndpoint)

        val accountList = d2.userModule().accountManager().getAccounts()

        assertThat(accountList.size).isEqualTo(initialAccountSize + 1)
        val account = accountList.find { it.username() == user1 }
        assertThat(account).isNotNull()

        loginAndDeleteAccount(user1, pass1, dhis2MockServer)
    }

    @Test
    fun find_current_account_after_login() {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }
        dhis2MockServer.enqueueLoginResponses()
        d2.userModule().blockingLogIn(user1, pass1, dhis2MockServer.baseEndpoint)

        val currentAccount = d2.userModule().accountManager().getCurrentAccount()
        assertThat(currentAccount?.username()).isEqualTo(user1)
        assertThat(currentAccount?.syncState()).isNotNull()

        loginAndDeleteAccount(user1, pass1, dhis2MockServer)
    }

    @Test
    fun cannot_find_current_account_after_logout() {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }

        val currentAccount = d2.userModule().accountManager().getCurrentAccount()
        assertThat(currentAccount).isNull()
    }

    @Test
    fun can_change_max_accounts() {
        d2.userModule().accountManager().setMaxAccounts(5)
        assertThat(d2.userModule().accountManager().getMaxAccounts()).isEqualTo(5)

        val defaultMaxAccounts = MultiUserDatabaseManager.DefaultTestMaxAccounts
        d2.userModule().accountManager().setMaxAccounts(defaultMaxAccounts)
        assertThat(d2.userModule().accountManager().getMaxAccounts()).isEqualTo(defaultMaxAccounts)
    }

    @Test
    fun can_set_null_max_accounts() {
        d2.userModule().accountManager().setMaxAccounts(null)
        assertThat(d2.userModule().accountManager().getMaxAccounts()).isNull()

        d2.userModule().accountManager().setMaxAccounts(MultiUserDatabaseManager.DefaultTestMaxAccounts)
    }

    @Test
    fun can_delete_current_logged_account() {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }
        dhis2MockServer.enqueueLoginResponses()
        d2.userModule().blockingLogIn(user1, pass1, dhis2MockServer.baseEndpoint)
        try {
            d2.userModule().accountManager().deleteCurrentAccount()
            val accountList = d2.userModule().accountManager().getAccounts()
            val account = accountList.find { it.username() == user1 }
            assertThat(account).isNull()
        } catch (e: D2Error) {
            Assert.fail("Should not throw a D2Error")
        }

        loginAndDeleteAccount(user1, pass1, dhis2MockServer)
    }

    @Test
    fun cannot_delete_not_logged_account() {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }
        dhis2MockServer.enqueueLoginResponses()
        d2.userModule().blockingLogIn(user1, pass1, dhis2MockServer.baseEndpoint)
        d2.userModule().blockingLogOut()
        try {
            d2.userModule().accountManager().deleteCurrentAccount()
            Assert.fail("Should throw a D2Error")
        } catch (e: D2Error) {
            assertThat(e.errorCode()).isEqualTo(D2ErrorCode.NO_AUTHENTICATED_USER)
            val accountList = d2.userModule().accountManager().getAccounts()
            val account = accountList.find { it.username() == user1 }
            assertThat(account).isNotNull()
        } catch (e: Exception) {
            Assert.fail("Should throw a D2Error")
        }

        loginAndDeleteAccount(user1, pass1, dhis2MockServer)
    }

    @Test
    fun evaluate_sync_status() {
        val initialAccountSize = d2.userModule().accountManager().getAccounts().size

        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }

        dhis2MockServer.enqueueLoginResponses()
        d2.userModule().blockingLogIn(user1, pass1, dhis2MockServer.baseEndpoint)

        val accounts = d2.userModule().accountManager().getAccounts()
        assertThat(accounts.size).isEqualTo(initialAccountSize + 1)
        val u1Account = accounts.find { it.username() == user1 }!!
        assertThat(u1Account.syncState()).isEqualTo(State.SYNCED)

        d2.userModule().blockingLogOut()

        val server2 = Dhis2MockServer(0)
        server2.enqueueLoginResponses()
        d2.userModule().blockingLogIn(user2, pass2, server2.baseEndpoint)
        server2.enqueueMetadataResponses()
        d2.metadataModule().blockingDownload()
        addDataValue()

        val accounts2 = d2.userModule().accountManager().getAccounts()
        assertThat(accounts2.size).isEqualTo(initialAccountSize + 2)

        val account1 = accounts2.find { it.username() == user1 }!!
        val account2 = accounts2.find { it.username() == user2 }!!
        assertThat(account1.syncState()).isEqualTo(State.SYNCED)
        assertThat(account2.syncState()).isEqualTo(State.TO_UPDATE)

        loginAndDeleteAccount(user1, pass1, dhis2MockServer)
        loginAndDeleteAccount(user2, pass2, server2)
    }

    @Test
    fun return_login_config() {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }
        dhis2MockServer.enqueueLoginResponses()
        d2.userModule().blockingLogIn(user1, pass1, dhis2MockServer.baseEndpoint)

        dhis2MockServer.enqueueMetadataResponses()
        d2.metadataModule().blockingDownload()

        val loginConfig = d2.userModule().accountManager().getCurrentAccount()?.loginConfig()
        assertThat(loginConfig).isNotNull()
        assertThat(loginConfig?.apiVersion).isEqualTo("2.41.3")
    }

    private fun addDataValue() {
        val period = d2.periodModule().periodHelper().blockingGetPeriodForPeriodTypeAndDate(PeriodType.Yearly, Date())
        val orgunit = d2.organisationUnitModule().organisationUnits().one().blockingGet()!!
        val coc = d2.categoryModule().categoryOptionCombos().one().blockingGet()!!
        val dataElement = d2.dataElementModule().dataElements().one().blockingGet()!!

        d2.dataValueModule().dataValues()
            .value(period.periodId()!!, orgunit.uid(), dataElement.uid(), coc.uid(), coc.uid())
            .blockingSet("45")
    }

    private fun loginAndDeleteAccount(username: String, password: String, server: Dhis2MockServer) {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut()
        }
        server.enqueueLoginResponses()
        d2.userModule().blockingLogIn(username, password, server.baseEndpoint)
        d2.userModule().accountManager().deleteCurrentAccount()
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUpTestClass() {
            setUpClass()
        }
    }
}
