/*
 *  Copyright (c) 2004-2026, University of Oslo
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

package org.hisp.dhis.android.core.dataset

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.arch.helpers.AccessHelper.defaultAccess
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.BaseNameableObject
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.ObjectWithStyleKt
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.dataelement.DataElementOperand
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.period.PeriodType
import java.util.Date

@ModelBuilder
@Suppress("TooManyFunctions")
data class DataSet(
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: Date?,
    override val lastUpdated: Date?,
    override val deleted: Boolean?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
    val periodType: PeriodType?,
    val categoryCombo: ObjectWithUid,
    val mobile: Boolean?,
    val version: Int?,
    val expiryDays: Double?,
    val timelyDays: Double?,
    val notifyCompletingUser: Boolean?,
    val openFuturePeriods: Int?,
    val fieldCombinationRequired: Boolean?,
    val validCompleteOnly: Boolean?,
    val noValueRequiresComment: Boolean?,
    val skipOffline: Boolean?,
    val dataElementDecoration: Boolean?,
    val renderAsTabs: Boolean?,
    val renderHorizontally: Boolean?,
    val workflow: ObjectWithUid?,
    val dataSetElements: List<DataSetElement>?,
    val indicators: List<Indicator>?,
    internal val sections: List<Section>?,
    val compulsoryDataElementOperands: List<DataElementOperand>?,
    val dataInputPeriods: List<DataInputPeriod>?,
    val displayOptions: DataSetDisplayOptions?,
    val access: Access,
    override val style: ObjectStyle,
) : BaseNameableObject, CoreObject, ObjectWithStyleKt {

    fun periodType(): PeriodType? = periodType
    fun categoryCombo(): ObjectWithUid = categoryCombo
    fun mobile(): Boolean? = mobile
    fun version(): Int? = version
    fun expiryDays(): Double? = expiryDays
    fun timelyDays(): Double? = timelyDays
    fun notifyCompletingUser(): Boolean? = notifyCompletingUser
    fun openFuturePeriods(): Int? = openFuturePeriods
    fun fieldCombinationRequired(): Boolean? = fieldCombinationRequired
    fun validCompleteOnly(): Boolean? = validCompleteOnly
    fun noValueRequiresComment(): Boolean? = noValueRequiresComment
    fun skipOffline(): Boolean? = skipOffline
    fun dataElementDecoration(): Boolean? = dataElementDecoration
    fun renderAsTabs(): Boolean? = renderAsTabs
    fun renderHorizontally(): Boolean? = renderHorizontally
    fun workflow(): ObjectWithUid? = workflow
    fun dataSetElements(): List<DataSetElement>? = dataSetElements
    fun indicators(): List<Indicator>? = indicators
    internal fun sections(): List<Section>? = sections
    fun compulsoryDataElementOperands(): List<DataElementOperand>? = compulsoryDataElementOperands
    fun dataInputPeriods(): List<DataInputPeriod>? = dataInputPeriods
    fun displayOptions(): DataSetDisplayOptions? = displayOptions
    fun access(): Access = access

    fun toBuilder(): Builder = DataSetBuilder.from(this)

    class Builder : DataSetBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
            .access(defaultAccess())
            .style(ObjectStyle())
    }
}
