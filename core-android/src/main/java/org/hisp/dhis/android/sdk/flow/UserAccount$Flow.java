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

package org.hisp.dhis.android.sdk.flow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.common.meta.DbDhis;
import org.hisp.dhis.java.sdk.models.common.Access;
import org.hisp.dhis.java.sdk.models.common.base.IdentifiableObject;
import org.hisp.dhis.java.sdk.models.user.UserAccount;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class UserAccount$Flow extends BaseModel implements IdentifiableObject {

    // As we have only one user account, the id will be constant
    private static final int LOCAL_ID = 1;

    @Column(name = "id")
    @PrimaryKey(autoincrement = false)
    long id = LOCAL_ID;

    @Column(name = "uId")
    String uId;

    @Column(name = "name")
    String name;

    @Column(name = "displayName")
    String displayName;

    @Column(name = "created")
    DateTime created;

    @Column(name = "lastUpdated")
    DateTime lastUpdated;

    @Column(name = "access")
    Access access;

    @Column(name = "action")
    org.hisp.dhis.java.sdk.models.common.state.Action action;

    @Column(name = "firstName")
    String firstName;

    @Column(name = "surname")
    String surname;

    @Column(name = "gender")
    String gender;

    @Column(name = "birthday")
    String birthday;

    @Column(name = "introduction")
    String introduction;

    @Column(name = "education")
    String education;

    @Column(name = "employer")
    String employer;

    @Column(name = "interests")
    String interests;

    @Column(name = "jobTitle")
    String jobTitle;

    @Column(name = "languages")
    String languages;

    @Column(name = "email")
    String email;

    @Column(name = "phoneNumber")
    String phoneNumber;

    public UserAccount$Flow() {
        action = org.hisp.dhis.java.sdk.models.common.state.Action.SYNCED;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        throw new UnsupportedOperationException("You cannot set id on UserAccount");
    }

    @Override
    public String getUId() {
        return uId;
    }

    @Override
    public void setUId(String uId) {
        this.uId = uId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public DateTime getCreated() {
        return created;
    }

    @Override
    public void setCreated(DateTime created) {
        this.created = created;
    }

    @Override
    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    @Override
    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public Access getAccess() {
        return access;
    }

    @Override
    public void setAccess(Access access) {
        this.access = access;
    }

    public org.hisp.dhis.java.sdk.models.common.state.Action getAction() {
        return action;
    }

    public void setAction(org.hisp.dhis.java.sdk.models.common.state.Action action) {
        this.action = action;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public static UserAccount$Flow fromModel(UserAccount userAccount) {
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

    public static UserAccount toModel(UserAccount$Flow flowAccount) {
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

    public static List<UserAccount> toModels(List<UserAccount$Flow> flows) {
        List<UserAccount> userAccounts = new ArrayList<>();

        if (flows != null && !flows.isEmpty()) {
            for (UserAccount$Flow flow : flows) {
                userAccounts.add(toModel(flow));
            }
        }

        return userAccounts;
    }
}