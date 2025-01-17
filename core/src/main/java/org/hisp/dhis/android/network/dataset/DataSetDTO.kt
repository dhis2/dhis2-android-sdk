/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.network.dataset

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.dataset.DataSet
import org.hisp.dhis.android.core.dataset.DataSetInternalAccessor
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.network.common.PayloadJson
import org.hisp.dhis.android.network.common.dto.AccessDTO
import org.hisp.dhis.android.network.common.dto.BaseNameableObjectDTO
import org.hisp.dhis.android.network.common.dto.ObjectWithStyleDTO
import org.hisp.dhis.android.network.common.dto.ObjectWithUidDTO
import org.hisp.dhis.android.network.common.dto.PagerDTO
import org.hisp.dhis.android.network.common.dto.applyBaseNameableFields

@Serializable
internal data class DataSetDTO(
    @SerialName("id") override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
    val periodType: String?,
    val categoryCombo: ObjectWithUidDTO?,
    val mobile: Boolean?,
    val version: Int?,
    val expiryDays: Int?,
    val timelyDays: Int?,
    val notifyCompletingUser: Boolean?,
    val openFuturePeriods: Int?,
    val fieldCombinationRequired: Boolean?,
    val validCompleteOnly: Boolean?,
    val noValueRequiresComment: Boolean?,
    val skipOffline: Boolean?,
    val dataElementDecoration: Boolean?,
    val renderAsTabs: Boolean?,
    val renderHorizontally: Boolean?,
    val workflow: ObjectWithUidDTO?,
    val dataSetElements: List<DataSetElementDTO> = emptyList(),
    val indicators: List<ObjectWithUidDTO> = emptyList(),
    val sections: List<SectionDTO> = emptyList(),
    val compulsoryDataElementOperands: List<DataElementOperandDTO> = emptyList(),
    val dataInputPeriods: List<DataInputPeriodDTO> = emptyList(),
    val access: AccessDTO?,
    val style: ObjectWithStyleDTO?,
) : BaseNameableObjectDTO {
    fun toDomain(): DataSet {
        return DataSet.builder()
            .applyBaseNameableFields(this)
            .apply {
                periodType?.let { periodType(PeriodType.valueOf(periodType)) }
            }
            .categoryCombo(categoryCombo?.toDomain())
            .mobile(mobile)
            .version(version)
            .expiryDays(expiryDays)
            .timelyDays(timelyDays)
            .notifyCompletingUser(notifyCompletingUser)
            .openFuturePeriods(openFuturePeriods)
            .fieldCombinationRequired(fieldCombinationRequired)
            .validCompleteOnly(validCompleteOnly)
            .noValueRequiresComment(noValueRequiresComment)
            .skipOffline(skipOffline)
            .dataElementDecoration(dataElementDecoration)
            .renderAsTabs(renderAsTabs)
            .renderHorizontally(renderHorizontally)
            .workflow(workflow?.toDomain())
            .dataSetElements(dataSetElements.map { it.toDomain() })
            .indicators(indicators.map { Indicator.builder().uid(it.uid).build() })
            .apply {
                DataSetInternalAccessor.insertSections(this, sections.map { it.toDomain() })
            }
            .compulsoryDataElementOperands(compulsoryDataElementOperands.map { it.toDomain() })
            .dataInputPeriods(dataInputPeriods.map { it.toDomain(ObjectWithUidDTO(uid)) })
            .apply {
                access?.let { access(access.toDomain()) }
                style?.let { style(style.toDomain()) }
            }
            .build()
    }
}

@Serializable
internal class DataSetPayload(
    override val pager: PagerDTO?,
    @SerialName("dataSets") override val items: List<DataSetDTO> = emptyList(),
) : PayloadJson<DataSetDTO>(pager, items)
