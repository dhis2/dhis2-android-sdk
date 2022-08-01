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

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.BaseObjectShould
import org.hisp.dhis.android.core.common.ObjectShould
import org.junit.Test

class VisualizationShould : BaseObjectShould("visualization/visualization.json"), ObjectShould {

    @Test
    override fun map_from_json_string() {
        val visualization = objectMapper.readValue(jsonStream, Visualization::class.java)

        assertThat(visualization.uid()).isEqualTo("PYBH8ZaAQnC")
        assertThat(visualization.type()).isEqualTo(VisualizationType.PIVOT_TABLE)
        assertThat(visualization.digitGroupSeparator()).isEqualTo(DigitGroupSeparator.COMMA)
        assertThat(visualization.aggregationType()).isEqualTo(AggregationType.SUM)
        assertThat(visualization.dataDimensionItems()!![0].indicator()!!.uid()).isEqualTo("Uvn6LCg7dVU")
        assertThat(visualization.dataDimensionItems()!![1].dataElement()!!.uid()).isEqualTo("cYeuwXTCPkU")
        assertThat(visualization.dataDimensionItems()!![2].dataElementOperand()!!.uid())
            .isEqualTo("Jtf34kNZhzP.pq2XI5kz2BY")
        assertThat(visualization.dataDimensionItems()!![3].programIndicator()!!.uid()).isEqualTo("p2Zxg0wcPQ3")
        assertThat(visualization.dataDimensionItems()!![4].programDataElement()!!.uid())
            .isEqualTo("lxAQ7Zs9VYR.sWoqcoByYmD")
        assertThat(visualization.dataDimensionItems()!![4].programDataElement()!!.program()!!.uid())
            .isEqualTo("lxAQ7Zs9VYR")
        assertThat(visualization.dataDimensionItems()!![4].programDataElement()!!.dataElement()!!.uid())
            .isEqualTo("sWoqcoByYmD")
        assertThat(visualization.dataDimensionItems()!![5].programAttribute()!!.uid())
            .isEqualTo("U5KybNCtA3E.iggSfNDnsCw")
        assertThat(visualization.dataDimensionItems()!![5].programAttribute()!!.program()!!.uid())
            .isEqualTo("U5KybNCtA3E")
        assertThat(visualization.dataDimensionItems()!![5].programAttribute()!!.attribute()!!.uid())
            .isEqualTo("iggSfNDnsCw")

        assertThat(visualization.categoryDimensions()!![0].category()!!.uid()).isEqualTo("fMZEcRHuamy")
        assertThat(visualization.categoryDimensions()!![0].categoryOptions()!!.size).isEqualTo(2)
        assertThat(visualization.categoryDimensions()!![1].category()!!.uid()).isEqualTo("fkAkrdC7eJF")
        assertThat(visualization.categoryDimensions()!![1].categoryOptions()!!.size).isEqualTo(0)
    }
}
