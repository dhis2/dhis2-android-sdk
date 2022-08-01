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

package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class UserModuleMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void allow_access_to_authenticated_user() {
        AuthenticatedUser authenticatedUser = d2.userModule().authenticatedUser().blockingGet();
        assertThat(authenticatedUser.user()).isEqualTo("DXyJmlo9rge");
    }

    @Test
    public void allow_access_to_user_credentials() {
        UserCredentials credentials = d2.userModule().userCredentials().blockingGet();
        assertThat(credentials.username()).isEqualTo("android");
        assertThat(credentials.code()).isEqualTo("android");
        assertThat(credentials.name()).isEqualTo("John Barnes");
    }

    @Test
    public void allow_access_to_user_role() {
        List<UserRole> userRole = d2.userModule().userRoles().blockingGet();
        assertThat(userRole.get(0).uid()).isEqualTo("Ufph3mGRmMo");
        assertThat(userRole.get(0).name()).isEqualTo("Superuser");
        assertThat(userRole.get(0).displayName()).isEqualTo("Superuser");
    }

    @Test
    public void allow_access_to_user() {
        User user = d2.userModule().user().blockingGet();
        assertThat(user.uid()).isEqualTo("DXyJmlo9rge");
        assertThat(user.firstName()).isEqualTo("John");
        assertThat(user.email()).isEqualTo("john@hmail.com");
    }
}