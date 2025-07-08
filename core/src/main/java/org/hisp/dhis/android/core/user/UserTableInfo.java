/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import org.hisp.dhis.android.core.arch.db.tableinfos.TableInfo;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.common.IdentifiableColumns;

public final class UserTableInfo {

    private UserTableInfo() {
    }

    public static final TableInfo TABLE_INFO = new TableInfo() {

        @Override
        public String name() {
            return "User";
        }

        @Override
        public Columns columns() {
            return new Columns();
        }
    };

    static class Columns extends IdentifiableColumns {
        private static final String USERNAME = "username";
        private static final String BIRTHDAY = "birthday";
        private static final String EDUCATION = "education";
        private static final String GENDER = "gender";
        private static final String JOB_TITLE = "jobTitle";
        private static final String SURNAME = "surname";
        private static final String FIRST_NAME = "firstName";
        private static final String INTRODUCTION = "introduction";
        private static final String EMPLOYER = "employer";
        private static final String INTERESTS = "interests";
        private static final String LANGUAGES = "languages";
        private static final String EMAIL = "email";
        private static final String PHONE_NUMBER = "phoneNumber";
        private static final String NATIONALITY = "nationality";

        @Override
        public String[] all() {
            return CollectionsHelper.appendInNewArray(super.all(),
                    BIRTHDAY,
                    EDUCATION,
                    GENDER,
                    JOB_TITLE,
                    SURNAME,
                    FIRST_NAME,
                    INTRODUCTION,
                    EMPLOYER,
                    INTERESTS,
                    LANGUAGES,
                    EMAIL,
                    PHONE_NUMBER,
                    NATIONALITY,
                    USERNAME
            );
        }
    }
}
