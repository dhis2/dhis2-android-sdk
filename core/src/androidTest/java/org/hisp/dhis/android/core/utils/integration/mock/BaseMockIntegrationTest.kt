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
package org.hisp.dhis.android.core.utils.integration.mock

import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.D2Manager
import org.hisp.dhis.android.core.MockIntegrationTestObjects
import org.hisp.dhis.android.core.arch.api.internal.ServerURLWrapper.setServerUrl
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.mockwebserver.Dhis2MockServer

abstract class BaseMockIntegrationTest {
    companion object {
        @JvmStatic
        lateinit var objects: MockIntegrationTestObjects
        @JvmStatic
        lateinit var d2: D2
        @JvmStatic
        lateinit var dhis2MockServer: Dhis2MockServer
        @JvmStatic
        lateinit var databaseAdapter: DatabaseAdapter

        @JvmStatic
        fun setUpClass(content: MockIntegrationTestDatabaseContent): Boolean {
            val tuple = MockIntegrationTestObjectsFactory.getObjects(content)
            tuple.objects.let { objs ->
                objects = objs
                d2 = objs.d2
                D2Manager.setD2(objs.d2)
                databaseAdapter = objs.databaseAdapter
                dhis2MockServer = objs.dhis2MockServer
                setServerUrl(dhis2MockServer.baseEndpoint)
            }
            return tuple.isNewInstance
        }
    }
}
