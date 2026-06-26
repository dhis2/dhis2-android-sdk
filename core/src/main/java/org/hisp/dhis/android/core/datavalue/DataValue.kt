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

package org.hisp.dhis.android.core.datavalue

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.DataObjectKt
import org.hisp.dhis.android.core.common.DeletableDataObjectKt
import org.hisp.dhis.android.core.common.State
import java.util.Date

@ModelBuilder
@Suppress("TooManyFunctions")
data class DataValue(
    override val syncState: State?,
    override val deleted: Boolean?,
    val dataElement: String,
    val period: String,
    val organisationUnit: String,
    val categoryOptionCombo: String,
    val attributeOptionCombo: String,
    internal val sourceDataSet: String?,
    val value: String?,
    val storedBy: String?,
    val created: Date?,
    val lastUpdated: Date?,
    val comment: String?,
    val followUp: Boolean?,
) : DataObjectKt, DeletableDataObjectKt {

    fun dataElement(): String = dataElement
    fun period(): String = period
    fun organisationUnit(): String = organisationUnit
    fun categoryOptionCombo(): String = categoryOptionCombo
    fun attributeOptionCombo(): String = attributeOptionCombo
    internal fun sourceDataSet(): String? = sourceDataSet
    fun value(): String? = value
    fun storedBy(): String? = storedBy
    fun created(): Date? = created
    fun lastUpdated(): Date? = lastUpdated
    fun comment(): String? = comment
    fun followUp(): Boolean? = followUp

    @Deprecated("Use syncState() instead")
    override fun state(): State? = syncState()

    fun toBuilder(): Builder = DataValueBuilder.from(this)

    class Builder : DataValueBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
            .syncState(State.SYNCED)
            .deleted(false)
    }
}
