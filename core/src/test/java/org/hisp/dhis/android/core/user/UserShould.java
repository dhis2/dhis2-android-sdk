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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseObjectShould;
import org.hisp.dhis.android.core.common.ObjectShould;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static com.google.common.truth.Truth.assertThat;

public class UserShould extends BaseObjectShould implements ObjectShould {

    public UserShould() {
        super("user/user.json");
    }

    @Override
    @Test
    public void map_from_json_string() throws IOException, ParseException {
        User user = objectMapper.readValue(jsonStream, User.class);

        assertThat(user.name()).isEqualTo("John Barnes");
        assertThat(user.lastUpdated()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2016-04-06T00:05:57.495"));
        assertThat(user.created()).isEqualTo(
                BaseIdentifiableObject.DATE_FORMAT.parse("2015-03-31T13:31:09.324"));
        assertThat(user.uid()).isEqualTo("DXyJmlo9rge");
        assertThat(user.surname()).isEqualTo("Barnes");
        assertThat(user.firstName()).isEqualTo("John");
        assertThat(user.email()).isEqualTo("john@hmail.com");
        assertThat(user.displayName()).isEqualTo("John Barnes");
        assertThat(UserInternalAccessor.accessUserCredentials(user).uid()).isEqualTo("M0fCOxtkURr");
        assertThat(UserInternalAccessor.accessUserCredentials(user).username()).isEqualTo("android");

        assertThat(UserInternalAccessor.accessUserCredentials(user).userRoles().get(0).uid()).isEqualTo("Ufph3mGRmMo");

        assertThat(user.organisationUnits().get(0).uid()).isEqualTo("YuQRtpLP10I");
    }
}