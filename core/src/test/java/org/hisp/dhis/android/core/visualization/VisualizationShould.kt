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
package org.hisp.dhis.android.core.visualization

import org.hisp.dhis.android.core.common.BaseObjectShould
import org.hisp.dhis.android.core.common.ObjectShould
import kotlin.Throws
import org.hisp.dhis.android.core.visualization.Visualization
import com.google.common.truth.Truth
import org.hisp.dhis.android.core.visualization.VisualizationType
import org.hisp.dhis.android.core.visualization.DigitGroupSeparator
import org.junit.Test
import java.io.IOException
import java.text.ParseException

class VisualizationShould : BaseObjectShould("visualization/visualization.json"), ObjectShould {
    @Test
    @Throws(IOException::class, ParseException::class)
    override fun map_from_json_string() {
        val visualization = objectMapper.readValue(jsonStream, Visualization::class.java)
        Truth.assertThat(visualization.uid()).isEqualTo("PYBH8ZaAQnC")
        Truth.assertThat(visualization.type()).isEqualTo(VisualizationType.PIVOT_TABLE)
        Truth.assertThat(visualization.digitGroupSeparator()).isEqualTo(DigitGroupSeparator.COMMA)
        Truth.assertThat(visualization.dataDimensionItems()!![0].indicator()!!.uid()).isEqualTo("Uvn6LCg7dVU")
        Truth.assertThat(visualization.dataDimensionItems()!![1].dataElement()!!.uid()).isEqualTo("cYeuwXTCPkU")
        Truth.assertThat(visualization.dataDimensionItems()!![2].dataElementOperand()!!.uid())
            .isEqualTo("Jtf34kNZhzP.pq2XI5kz2BY")
        Truth.assertThat(visualization.dataDimensionItems()!![3].programIndicator()!!.uid()).isEqualTo("p2Zxg0wcPQ3")
        Truth.assertThat(visualization.dataDimensionItems()!![4].programDataElement()!!.uid())
            .isEqualTo("lxAQ7Zs9VYR.sWoqcoByYmD")
        Truth.assertThat(visualization.dataDimensionItems()!![4].programDataElement()!!.program()!!.uid())
            .isEqualTo("lxAQ7Zs9VYR")
        Truth.assertThat(visualization.dataDimensionItems()!![4].programDataElement()!!.dataElement()!!.uid())
            .isEqualTo("sWoqcoByYmD")
        Truth.assertThat(visualization.dataDimensionItems()!![5].programAttribute()!!.uid())
            .isEqualTo("U5KybNCtA3E.iggSfNDnsCw")
        Truth.assertThat(visualization.dataDimensionItems()!![5].programAttribute()!!.program()!!.uid())
            .isEqualTo("U5KybNCtA3E")
        Truth.assertThat(visualization.dataDimensionItems()!![5].programAttribute()!!.attribute()!!.uid())
            .isEqualTo("iggSfNDnsCw")
    }
}