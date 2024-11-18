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
package org.hisp.dhis.android.core.utils.integration.mock

import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.D2Factory
import org.hisp.dhis.android.core.MockIntegrationTestObjects
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory
import org.hisp.dhis.android.core.period.clock.internal.setFixed

internal object MockIntegrationTestObjectsFactory {
    private val instances: MutableMap<MockIntegrationTestDatabaseContent, MockIntegrationTestObjects> = HashMap()

    private val d2: D2

    init {
        ClockProviderFactory.setFixed()
        d2 = D2Factory.forNewDatabase()
        d2.userModule().accountManager().setMaxAccounts(MultiUserDatabaseManager.DefaultTestMaxAccounts)
    }

    fun getObjects(content: MockIntegrationTestDatabaseContent, port: Int): IntegrationTestObjectsWithIsNewInstance {
        val instance = instances[content]
        return if (instance != null) {
            IntegrationTestObjectsWithIsNewInstance(instance, false)
        } else {
            val newInstance = MockIntegrationTestObjects(d2, content, port)
            instances[content] = newInstance
            IntegrationTestObjectsWithIsNewInstance(newInstance, true)
        }
    }

    fun removeObjects(content: MockIntegrationTestDatabaseContent) {
        val instance = instances[content]
        if (instance != null) {
            instance.tearDown()
            instances.remove(content)
        }
    }

    @JvmStatic
    fun tearDown() {
        if (instances.isNotEmpty()) {
            for (objects in instances.values) {
                objects.tearDown()
            }
            instances.clear()
        }
    }

    internal class IntegrationTestObjectsWithIsNewInstance(
        val objects: MockIntegrationTestObjects,
        val isNewInstance: Boolean,
    )
}
