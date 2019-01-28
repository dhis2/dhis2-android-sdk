/*
 * Copyright (c) 2017, University of Oslo
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
package org.hisp.dhis.android.core.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.program.CreateProgramUtils;
import org.hisp.dhis.android.core.program.ProgramModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Callable;

import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class UserCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private Callable<User> userCall;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());

        dhis2MockServer.enqueueMockResponse("user/user.json");
        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        // ToDo: consider moving this out
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        userCall = getD2DIComponent(d2).internalModules().user.userCall;

        ContentValues program1 = CreateProgramUtils.create(1L, "eBAyeGv0exc", null, null, null);
        ContentValues program2 = CreateProgramUtils.create(2L, "ur1Edk5Oe2n", null, null, null);
        ContentValues program3 = CreateProgramUtils.create(3L, "fDd25txQckK", null, null, null);
        ContentValues program4 = CreateProgramUtils.create(4L, "WSGAb5XwJ3Y", null, null, null);
        ContentValues program5 = CreateProgramUtils.create(5L, "IpHINAT79UW", null, null, null);

        database().insert(ProgramModel.TABLE, null, program1);
        database().insert(ProgramModel.TABLE, null, program2);
        database().insert(ProgramModel.TABLE, null, program3);
        database().insert(ProgramModel.TABLE, null, program4);
        database().insert(ProgramModel.TABLE, null, program5);
    }

    @Test
    public void persist_user_in_data_base_when_call() throws Exception {
        userCall.call();

        Cursor userCursor = database().query(UserTableInfo.TABLE_INFO.name(), UserTableInfo.TABLE_INFO.columns().all(), null, null, null, null, null);

        assertThatCursor(userCursor).hasRow(
                "DXyJmlo9rge",
                null,
                "John Barnes",
                "John Barnes",
                "2015-03-31T13:31:09.324",
                "2016-04-06T00:05:57.495",
                null,
                null,
                null,
                null,
                "Barnes",
                "John",
                null,
                null,
                null,
                null,
                "john@hmail.com",
                null,
                null
        ).isExhausted();
    }

    @Test
    public void persist_user_credentials_in_data_base_when_call() throws Exception {
        userCall.call();

        String[] projection = {
                UserCredentialsModel.Columns.UID,
                UserCredentialsModel.Columns.CODE,
                UserCredentialsModel.Columns.NAME,
                UserCredentialsModel.Columns.DISPLAY_NAME,
                UserCredentialsModel.Columns.CREATED,
                UserCredentialsModel.Columns.LAST_UPDATED,
                UserCredentialsModel.Columns.USERNAME,
                UserCredentialsModel.Columns.USER,
        };


        Cursor userCredentialsCursor = database().query(UserCredentialsModel.TABLE, projection,
                null, null, null, null, null);

        assertThatCursor(userCredentialsCursor).hasRow(
                "M0fCOxtkURr",
                "android",
                "John Barnes",
                "John Barnes",
                "2015-03-31T13:31:09.206",
                "2017-11-29T11:45:37.250",
                "android",
                "DXyJmlo9rge"
        ).isExhausted();
    }

    @After
    @Override
    public void tearDown() throws IOException {
        super.tearDown();
        dhis2MockServer.shutdown();
    }
}
