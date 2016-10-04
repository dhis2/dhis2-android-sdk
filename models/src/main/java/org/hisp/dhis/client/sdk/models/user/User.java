/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.models.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

import static org.hisp.dhis.client.sdk.utils.StringUtils.isEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseIdentifiableObject {
    public static final String GENDER_MALE = "gender_male";
    public static final String GENDER_FEMALE = "gender_female";
    public static final String GENDER_OTHER = "gender_other";

    @JsonProperty("birthday")
    String birthday;

    @JsonProperty("education")
    String education;

    @JsonProperty("gender")
    String gender;

    @JsonProperty("jobTitle")
    String jobTitle;

    @JsonProperty("surname")
    String surname;

    @JsonProperty("firstName")
    String firstName;

    @JsonProperty("introduction")
    String introduction;

    @JsonProperty("employer")
    String employer;

    @JsonProperty("interests")
    String interests;

    @JsonProperty("languages")
    String languages;

    @JsonProperty("email")
    String email;

    @JsonProperty("phoneNumber")
    String phoneNumber;

    @JsonProperty("nationality")
    String nationality;

    @JsonProperty("userCredentials")
    UserCredentials userCredentials;

    @JsonProperty("organisationUnits")
    List<OrganisationUnit> organisationUnits;

    @JsonProperty("teiSearchOrganisationUnits")
    List<OrganisationUnit> teiSearchOrganisationUnits;

    @JsonProperty("dataViewOrganisationUnits")
    List<OrganisationUnit> dataViewOrganisationUnits;

    public User() {
        // explicit empty constructor
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public UserCredentials getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(UserCredentials userCredentials) {
        this.userCredentials = userCredentials;
    }

    public List<OrganisationUnit> getOrganisationUnits() {
        return organisationUnits;
    }

    public void setOrganisationUnits(List<OrganisationUnit> organisationUnits) {
        this.organisationUnits = organisationUnits;
    }

    public List<OrganisationUnit> getDataViewOrganisationUnits() {
        return dataViewOrganisationUnits;
    }

    public void setDataViewOrganisationUnits(List<OrganisationUnit> dataViewOrganisationUnits) {
        this.dataViewOrganisationUnits = dataViewOrganisationUnits;
    }

    public List<OrganisationUnit> getTeiSearchOrganisationUnits() {
        return teiSearchOrganisationUnits;
    }

    public void setTeiSearchOrganisationUnits(List<OrganisationUnit> teiSearchOrganisationUnits) {
        this.teiSearchOrganisationUnits = teiSearchOrganisationUnits;
    }

    public String getInitials() {
        if (!isEmpty(getFirstName()) &&
                !isEmpty(getSurname())) {
            return String.valueOf(getFirstName().charAt(0)) +
                    String.valueOf(getSurname().charAt(0));
        } else if (getDisplayName() != null &&
                getDisplayName().length() > 1) {
            return String.valueOf(getDisplayName().charAt(0)) +
                    String.valueOf(getDisplayName().charAt(1));
        }
        return "";
    }
}
