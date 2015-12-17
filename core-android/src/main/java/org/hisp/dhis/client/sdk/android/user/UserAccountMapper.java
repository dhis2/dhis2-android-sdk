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

package org.hisp.dhis.client.sdk.android.user;

import org.hisp.dhis.client.sdk.android.common.base.AbsMapper;
import org.hisp.dhis.client.sdk.android.flow.UserAccount$Flow;
import org.hisp.dhis.client.sdk.models.user.UserAccount;


public class UserAccountMapper extends AbsMapper<UserAccount,
        UserAccount$Flow> {

    public UserAccountMapper() {
        // empty constructor
    }

    @Override
    public UserAccount$Flow mapToDatabaseEntity(UserAccount userAccount) {
        if (userAccount == null) {
            return null;
        }

        // flowAccount.setId(userAccount.getId());

        UserAccount$Flow flowAccount = new UserAccount$Flow();
        flowAccount.setUId(userAccount.getUId());
        flowAccount.setCreated(userAccount.getCreated());
        flowAccount.setLastUpdated(userAccount.getLastUpdated());
        flowAccount.setName(userAccount.getName());
        flowAccount.setDisplayName(userAccount.getDisplayName());
        flowAccount.setAccess(userAccount.getAccess());
        flowAccount.setAction(userAccount.getAction());
        flowAccount.setFirstName(userAccount.getFirstName());
        flowAccount.setSurname(userAccount.getSurname());
        flowAccount.setGender(userAccount.getGender());
        flowAccount.setBirthday(userAccount.getBirthday());
        flowAccount.setIntroduction(userAccount.getIntroduction());
        flowAccount.setEducation(userAccount.getEducation());
        flowAccount.setEmployer(userAccount.getEmployer());
        flowAccount.setInterests(userAccount.getInterests());
        flowAccount.setJobTitle(userAccount.getJobTitle());
        flowAccount.setLanguages(userAccount.getLanguages());
        flowAccount.setEmail(userAccount.getEmail());
        flowAccount.setPhoneNumber(userAccount.getPhoneNumber());

        return flowAccount;
    }

    @Override
    public UserAccount mapToModel(UserAccount$Flow flowAccount) {
        if (flowAccount == null) {
            return null;
        }

        UserAccount account = new UserAccount();
        account.setId(flowAccount.getId());
        account.setUId(flowAccount.getUId());
        account.setCreated(flowAccount.getCreated());
        account.setLastUpdated(flowAccount.getLastUpdated());
        account.setName(flowAccount.getName());
        account.setDisplayName(flowAccount.getDisplayName());
        account.setAccess(flowAccount.getAccess());
        account.setAction(flowAccount.getAction());
        account.setFirstName(flowAccount.getFirstName());
        account.setSurname(flowAccount.getSurname());
        account.setGender(flowAccount.getGender());
        account.setBirthday(flowAccount.getBirthday());
        account.setIntroduction(flowAccount.getIntroduction());
        account.setEducation(flowAccount.getEducation());
        account.setEmployer(flowAccount.getEmployer());
        account.setInterests(flowAccount.getInterests());
        account.setJobTitle(flowAccount.getJobTitle());
        account.setLanguages(flowAccount.getLanguages());
        account.setEmail(flowAccount.getEmail());
        account.setPhoneNumber(flowAccount.getPhoneNumber());

        return account;
    }

    @Override
    public Class<UserAccount> getModelTypeClass() {
        return UserAccount.class;
    }

    @Override
    public Class<UserAccount$Flow> getDatabaseEntityTypeClass() {
        return UserAccount$Flow.class;
    }
}
