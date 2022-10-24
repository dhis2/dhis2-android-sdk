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

package org.hisp.dhis.android.testapp.user;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

import org.hisp.dhis.android.core.configuration.internal.DatabaseAccount;
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(D2JunitRunner.class)
public class AccountManagerMockIntegrationShould extends BaseMockIntegrationTestEmptyEnqueable {

    @BeforeClass
    public static void setUpTestClass() {
        BaseMockIntegrationTestEmptyEnqueable.setUpClass();
    }

    @Test
    public void find_accounts_after_login() {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut();
        }

        dhis2MockServer.enqueueLoginResponses();
        d2.userModule().blockingLogIn("u1", "pass1", dhis2MockServer.getBaseEndpoint());

        List<DatabaseAccount> accountList = d2.userModule().accountManager().getAccounts();

        assertThat(accountList.size()).isEqualTo(1);
        assertThat(accountList.get(0).username()).isEqualTo("u1");
    }

    @Test
    public void can_change_max_accounts() {
        d2.userModule().accountManager().setMaxAccounts(5);
        assertThat(d2.userModule().accountManager().getMaxAccounts()).isEqualTo(5);

        int defaultMaxAccounts = MultiUserDatabaseManager.DefaultMaxAccounts;
        d2.userModule().accountManager().setMaxAccounts(defaultMaxAccounts);
        assertThat(d2.userModule().accountManager().getMaxAccounts()).isEqualTo(defaultMaxAccounts);
    }

    @Test
    public void can_delete_current_logged_account() {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut();
        }

        dhis2MockServer.enqueueLoginResponses();
        d2.userModule().blockingLogIn("u1", "pass1", dhis2MockServer.getBaseEndpoint());

        try {
            d2.userModule().accountManager().deleteCurrentAccount();

            List<DatabaseAccount> accountList = d2.userModule().accountManager().getAccounts();
            assertThat(accountList.size()).isEqualTo(0);
        } catch (D2Error e) {
            fail("Should not throw a D2Error");
        }
    }

    @Test
    public void cannot_delete_not_logged_account() {
        if (d2.userModule().blockingIsLogged()) {
            d2.userModule().blockingLogOut();
        }

        dhis2MockServer.enqueueLoginResponses();
        d2.userModule().blockingLogIn("u1", "pass1", dhis2MockServer.getBaseEndpoint());

        d2.userModule().blockingLogOut();

        try {
            d2.userModule().accountManager().deleteCurrentAccount();
            fail("Should throw a D2Error");
        } catch (D2Error e) {
            assertThat(e.errorCode()).isEqualTo(D2ErrorCode.NO_AUTHENTICATED_USER);

            List<DatabaseAccount> accountList = d2.userModule().accountManager().getAccounts();
            assertThat(accountList.size()).isEqualTo(1);
        } catch (Exception e) {
            fail("Should throw a D2Error");
        }
    }
}