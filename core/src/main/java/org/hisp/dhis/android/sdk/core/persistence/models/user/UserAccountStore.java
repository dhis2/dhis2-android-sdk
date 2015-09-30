/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.sdk.core.persistence.models.user;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.core.persistence.models.flow.UserAccount$Flow;
import org.hisp.dhis.android.sdk.core.persistence.models.flow.UserAccount$Flow$Table;
import org.hisp.dhis.android.sdk.models.user.IUserAccountStore;
import org.hisp.dhis.android.sdk.models.user.UserAccount;

import java.util.List;

public final class UserAccountStore implements IUserAccountStore {

    public UserAccountStore() {
        // empty constructor
    }

    @Override
    public void insert(UserAccount object) {
        UserAccount$Flow userAccountFlow = UserAccount$Flow.fromModel(object);
        userAccountFlow.insert();

        object.setId(userAccountFlow.getId());
    }

    @Override
    public void update(UserAccount object) {
        UserAccount$Flow.fromModel(object).update();
    }

    @Override
    public void save(UserAccount object) {
        UserAccount$Flow accountFlow =
                UserAccount$Flow.fromModel(object);
        accountFlow.save();

        object.setId(accountFlow.getId());
    }

    @Override
    public void delete(UserAccount object) {
        UserAccount$Flow.fromModel(object).delete();
    }

    @Override
    public List<UserAccount> queryAll() {
        List<UserAccount$Flow> userAccounts = new Select()
                .from(UserAccount$Flow.class)
                .queryList();
        return UserAccount$Flow.toModels(userAccounts);
    }

    @Override
    public UserAccount queryById(long id) {
        UserAccount$Flow userAccount = new Select()
                .from(UserAccount$Flow.class)
                .where(Condition.column(UserAccount$Flow$Table.ID).is(id))
                .querySingle();
        return UserAccount$Flow.toModel(userAccount);
    }

    @Override
    public UserAccount queryByUid(String uid) {
        UserAccount$Flow userAccount = new Select()
                .from(UserAccount$Flow.class)
                .where(Condition.column(UserAccount$Flow$Table.UID).is(uid))
                .querySingle();
        return UserAccount$Flow.toModel(userAccount);
    }
}
