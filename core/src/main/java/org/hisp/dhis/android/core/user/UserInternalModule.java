/*
 * Copyright (c) 2004-2018, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.wipe.TableWiper;
import org.hisp.dhis.android.core.wipe.WipeableModule;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.Reusable;

@Reusable
public final class UserInternalModule implements WipeableModule, UserDownloadModule {

    private final TableWiper tableWiper;
    public final UserModule publicModule;

    private final Provider<UserCall> userCallProvider;
    private final AuthorityEndpointCallFactory authorityEndpointCallFactory;

    @Inject
    UserInternalModule(TableWiper tableWiper,
                       UserModule publicModule,
                       Provider<UserCall> userCallProvider,
                       AuthorityEndpointCallFactory authorityEndpointCallFactory) {
        this.tableWiper = tableWiper;
        this.publicModule = publicModule;
        this.userCallProvider = userCallProvider;
        this.authorityEndpointCallFactory = authorityEndpointCallFactory;
    }

    @Override
    public void wipeMetadata() {
        tableWiper.wipeTables(
                UserModel.TABLE,
                UserCredentialsTableInfo.TABLE_INFO.name(),
                UserOrganisationUnitLinkModel.TABLE,
                AuthenticatedUserModel.TABLE,
                AuthorityTableInfo.TABLE_INFO.name(),
                UserRoleModel.TABLE
        );
    }

    @Override
    public void wipeData() {
        // No data to wipe
    }

    @Override
    public Call<User> downloadUser() {
        return userCallProvider.get();
    }

    @Override
    public Call<List<Authority>> downloadAuthority() {
        return authorityEndpointCallFactory.create();
    }
}
