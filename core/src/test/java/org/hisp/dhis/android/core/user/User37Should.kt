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
package org.hisp.dhis.android.core.user

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.BaseObjectKotlinxShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.hisp.dhis.android.network.user.UserDTO
import org.hisp.dhis.android.network.user.userDTOtoDomainMapper
import org.junit.Test

class User37Should : BaseObjectKotlinxShould("user/user37.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val userDTO = deserialize(UserDTO.serializer())
        val user = userDTOtoDomainMapper(userDTO)

        Truth.assertThat(user.name()).isEqualTo("John Barnes")
        Truth.assertThat(user.lastUpdated()).isEqualTo(DateUtils.DATE_FORMAT.parse("2016-04-06T00:05:57.495"))
        Truth.assertThat(user.created()).isEqualTo(DateUtils.DATE_FORMAT.parse("2015-03-31T13:31:09.324"))
        Truth.assertThat(user.uid()).isEqualTo("DXyJmlo9rge")
        Truth.assertThat(user.surname()).isEqualTo("Barnes")
        Truth.assertThat(user.firstName()).isEqualTo("John")
        Truth.assertThat(user.email()).isEqualTo("john@hmail.com")
        Truth.assertThat(user.displayName()).isEqualTo("John Barnes")
        Truth.assertThat(UserInternalAccessor.accessUserCredentials(user).username()).isEqualTo("android")
        Truth.assertThat(UserInternalAccessor.accessUserCredentials(user).userRoles()!![0].uid())
            .isEqualTo("Ufph3mGRmMo")
        Truth.assertThat(user.organisationUnits()!![0].uid()).isEqualTo("YuQRtpLP10I")
    }
}
