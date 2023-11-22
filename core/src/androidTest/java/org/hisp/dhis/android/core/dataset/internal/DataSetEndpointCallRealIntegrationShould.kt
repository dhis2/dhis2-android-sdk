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
package org.hisp.dhis.android.core.dataset.internal

import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.dataset.DataSet
import org.junit.Before

class DataSetEndpointCallRealIntegrationShould : BaseRealIntegrationTest() {

    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to the
     * metadataSyncCall. It works against the demo server.
     */
    private lateinit var dataSetCall: suspend () -> List<DataSet>

    @Before
    override fun setUp() {
        super.setUp()
        dataSetCall = { createCall() }
    }

    private suspend fun createCall(): List<DataSet> {
        return getD2DIComponent(d2).dataSetCallFactory.create(setOf("lyLU2wR22tC", "BfMAe6Itzgt"))
    }

    // @Test
    @Throws(Exception::class)
    suspend fun download_data_sets() {
        if (!d2.userModule().isLogged().blockingGet()) {
            d2.userModule().logIn(username, password, url).blockingGet()
        }

        /*  This test won't pass independently of DataElementEndpointCallFactory and
            CategoryComboEndpointCallFactory, as the foreign keys constraints won't be satisfied.
            To run the test, you will need to disable foreign key support in database in
            DbOpenHelper.java replacing 'foreign_keys = ON' with 'foreign_keys = OFF' and
            uncomment the @Test tag */
        dataSetCall.invoke()
    }
}
