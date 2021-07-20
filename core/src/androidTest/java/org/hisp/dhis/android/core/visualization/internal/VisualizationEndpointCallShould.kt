/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.visualization.internal

import com.google.common.collect.Lists
import com.google.common.truth.Truth
import io.reactivex.Single
import java.util.*
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyEnqueable
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.hisp.dhis.android.core.visualization.Visualization
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class VisualizationEndpointCallShould : BaseMockIntegrationTestEmptyEnqueable() {

    companion object {
        private lateinit var visualizationsSingle: Single<List<Visualization>>

        @BeforeClass
        @JvmStatic
        @Throws(Exception::class)
        internal fun setUpClass() {
            BaseMockIntegrationTestEmptyEnqueable.setUpClass()
            visualizationsSingle = objects.d2DIComponent.internalModules().visualization.visualizationCall.download(
                HashSet(
                    Lists.newArrayList("PYBH8ZaAQnC", "FAFa11yFeFe")
                )
            )
            dhis2MockServer.enqueueMockResponse("visualization/visualizations.json")
            d2.databaseAdapter().setForeignKeyConstraintsEnabled(false)
        }
    }

    @Test
    fun download_persist_and_get_visualizations_successfully() {
        var visualizations = visualizationsSingle.blockingGet()
        Truth.assertThat(visualizations.isEmpty()).isFalse()
        visualizations = d2.visualizationModule().visualizations().blockingGet()
        Truth.assertThat(visualizations.isEmpty()).isFalse()
    }
}
