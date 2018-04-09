/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.persistence.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.persistence.Dhis2Database;
import org.hisp.dhis.android.sdk.persistence.models.meta.State;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Table(databaseName = Dhis2Database.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class UserAccount extends BaseModel implements IdentifiableObject {

    // As we have only one user account, the id will be constant
    private static final int LOCAL_ID = 1;

    @JsonIgnore
    @Column(name = "id")
    @PrimaryKey(autoincrement = false)
    long id = LOCAL_ID;

    @JsonProperty("id")
    @Column(name = "uId")
    String uId;

    @JsonProperty("name")
    @Column(name = "name")
    String name;

    @JsonProperty("displayName")
    @Column(name = "displayName")
    String displayName;

    @JsonProperty("created")
    @Column(name = "created")
    DateTime created;

    @JsonProperty("lastUpdated")
    @Column(name = "lastUpdated")
    DateTime lastUpdated;

    @JsonProperty("access")
    @Column(name = "access")
    Access access;

    @JsonIgnore
    @Column(name = "state")
    State state;

    @JsonProperty("firstName")
    @Column(name = "firstName")
    String firstName;

    @JsonProperty("surname")
    @Column(name = "surname")
    String surname;

    @JsonProperty("gender")
    @Column(name = "gender")
    String gender;

    @JsonProperty("birthday")
    @Column(name = "birthday")
    String birthday;

    @JsonProperty("introduction")
    @Column(name = "introduction")
    String introduction;

    @JsonProperty("education")
    @Column(name = "education")
    String education;

    @JsonProperty("employer")
    @Column(name = "employer")
    String employer;

    @JsonProperty("interests")
    @Column(name = "interests")
    String interests;

    @JsonProperty("jobTitle")
    @Column(name = "jobTitle")
    String jobTitle;

    @JsonProperty("languages")
    @Column(name = "languages")
    String languages;

    @JsonProperty("email")
    @Column(name = "email")
    String email;

    @JsonProperty("phoneNumber")
    @Column(name = "phoneNumber")
    String phoneNumber;

    /* This properties should not be directly
    accessible through model accessors */
    @JsonProperty("userCredentials")
    UserCredentials userCredentials;

    @JsonProperty("organisationUnits")
    List<OrganisationUnit> organisationUnits;

    @JsonProperty("teiSearchOrganisationUnits")
    List<OrganisationUnit> teiSearchOrganisationUnits;

    @JsonProperty("programs")
    List<String> programUIds;

    public UserAccount() {
        state = State.SYNCED;
    }


    @JsonIgnore
    /* Exposing getUserPrograms() instead of making UserCredentials model public */
    public List<Program> getUserPrograms() {
        Map<String, Program> programMap = new HashMap<>();
        if(DhisController.getInstance().isLoggedInServerWithLatestApiVersion()){
            for(String programUid : programUIds){
                Program program = new Program();
                program.setUid(programUid);
                programMap.put(programUid, program);
            }
        }else {
            if (userCredentials != null && userCredentials.getUserRoles() != null) {

            /* go through all UserRoles and extract assigned programs */
                for (UserRole userRole : userCredentials.getUserRoles()) {
                    if (userRole.getPrograms() != null) {
                        for (Program program : userRole.getPrograms()) {
                            programMap.put(program.getUid(), program);
                        }
                    }
                }
            }
        }
        return new ArrayList<>(programMap.values());
    }

    @JsonIgnore
    public static UserAccount getCurrentUserAccountFromDb() {
        return new Select().from(UserAccount.class)
                .where(Condition.column(UserAccount$Table.ID).is(LOCAL_ID))
                .querySingle();
    }

    /* @JsonIgnore
    public static User getCurrentUser() {
        return toUser(getCurrentUserAccountFromDb());
    } */

    @JsonIgnore
    public static User toUser(UserAccount userAccount) {
        User user = new User();
        user.setUid(userAccount.getUId());
        user.setAccess(userAccount.getAccess());
        user.setCreated(user.getCreated());
        //user.setLastUpdated(userAccount.getLastUpdated());
        user.setName(userAccount.getName());
        //user.setDisplayName(userAccount.getDisplayName());
        return user;
    }

    @JsonIgnore
    @Override
    public long getId() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setId(long id) {
        throw new UnsupportedOperationException("You cannot set id on UserAccount");
    }

    @JsonIgnore
    public List<OrganisationUnit> getOrganisationUnits() {
        return organisationUnits;
    }

    @JsonIgnore
    @Override
    public String getUId() {
        return uId;
    }

    @JsonIgnore
    @Override
    public void setUId(String uId) {
        this.uId = uId;
    }

    @JsonIgnore
    @Override
    public String getName() {
        return name;
    }

    @JsonIgnore
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    @Override
    public String getDisplayName() {
        return displayName;
    }

    @JsonIgnore
    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonIgnore
    @Override
    public DateTime getCreated() {
        return created;
    }

    @JsonIgnore
    @Override
    public void setCreated(DateTime created) {
        this.created = created;
    }

    @JsonIgnore
    @Override
    public DateTime getLastUpdated() {
        return lastUpdated;
    }

    @JsonIgnore
    @Override
    public void setLastUpdated(DateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @JsonIgnore
    @Override
    public Access getAccess() {
        return access;
    }

    @JsonIgnore
    @Override
    public void setAccess(Access access) {
        this.access = access;
    }

    @JsonIgnore
    public State getState() {
        return state;
    }

    @JsonIgnore
    public void setState(State state) {
        this.state = state;
    }

    @JsonIgnore
    public String getFirstName() {
        return firstName;
    }

    @JsonIgnore
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonIgnore
    public String getSurname() {
        return surname;
    }

    @JsonIgnore
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @JsonIgnore
    public String getGender() {
        return gender;
    }

    @JsonIgnore
    public void setGender(String gender) {
        this.gender = gender;
    }

    @JsonIgnore
    public String getBirthday() {
        return birthday;
    }

    @JsonIgnore
    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @JsonIgnore
    public String getIntroduction() {
        return introduction;
    }

    @JsonIgnore
    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    @JsonIgnore
    public String getEducation() {
        return education;
    }

    @JsonIgnore
    public void setEducation(String education) {
        this.education = education;
    }

    @JsonIgnore
    public String getEmployer() {
        return employer;
    }

    @JsonIgnore
    public void setEmployer(String employer) {
        this.employer = employer;
    }

    @JsonIgnore
    public String getInterests() {
        return interests;
    }

    @JsonIgnore
    public void setInterests(String interests) {
        this.interests = interests;
    }

    @JsonIgnore
    public String getJobTitle() {
        return jobTitle;
    }

    @JsonIgnore
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    @JsonIgnore
    public String getLanguages() {
        return languages;
    }

    @JsonIgnore
    public void setLanguages(String languages) {
        this.languages = languages;
    }

    @JsonIgnore
    public String getEmail() {
        return email;
    }

    @JsonIgnore
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonIgnore
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setProgramUIds(List<String> programs) {
        this.programUIds = programs;
    }

    public List<String> getProgramUIds() {
        return programUIds;
    }

    public List<OrganisationUnit> getTeiSearchOrganisationUnits() {
        return teiSearchOrganisationUnits;
    }

    public void setTeiSearchOrganisationUnits(List<OrganisationUnit> teiSearchOrganisationUnits) {
        this.teiSearchOrganisationUnits = teiSearchOrganisationUnits;
    }
}