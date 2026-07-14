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

package org.hisp.dhis.android.core.program

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.common.BaseIdentifiableObject
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValueTypeRendering
import org.hisp.dhis.android.core.dataelement.DataElement
import java.util.Date

@ModelBuilder
data class ProgramStageDataElement(
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: Date?,
    override val lastUpdated: Date?,
    override val deleted: Boolean?,
    val displayInReports: Boolean?,
    val compulsory: Boolean?,
    val allowProvidedElsewhere: Boolean?,
    val sortOrder: Int?,
    val allowFutureDate: Boolean?,
    internal val fullDataElement: DataElement?,
    val dataElement: ObjectWithUid?,
    val programStage: ObjectWithUid?,
    val renderType: ValueTypeRendering?,
) : BaseIdentifiableObject, CoreObject {

    fun displayInReports(): Boolean? = displayInReports
    fun compulsory(): Boolean? = compulsory
    fun allowProvidedElsewhere(): Boolean? = allowProvidedElsewhere
    fun sortOrder(): Int? = sortOrder
    fun allowFutureDate(): Boolean? = allowFutureDate
    internal fun fullDataElement(): DataElement? = fullDataElement
    fun dataElement(): ObjectWithUid? = dataElement
    fun programStage(): ObjectWithUid? = programStage
    fun renderType(): ValueTypeRendering? = renderType

    fun toBuilder(): Builder = ProgramStageDataElementBuilder.from(this)

    class Builder : ProgramStageDataElementBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
