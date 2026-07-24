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

package org.hisp.dhis.android.core.trackedentity

import org.hisp.dhis.android.annotations.ModelBuilder
import org.hisp.dhis.android.core.arch.helpers.AccessHelper.defaultAccess
import org.hisp.dhis.android.core.arch.repositories.scope.internal.TrackerSearchOperator
import org.hisp.dhis.android.core.common.Access
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.BaseNameableObject
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.ObjectWithStyleKt
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValueType
import java.util.Date

@ModelBuilder
@Suppress("TooManyFunctions")
data class TrackedEntityAttribute(
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
    val pattern: String?,
    val sortOrderInListNoProgram: Int?,
    val optionSet: ObjectWithUid?,
    val valueType: ValueType?,
    val expression: String?,
    val aggregationType: AggregationType?,
    val programScope: Boolean?,
    val displayInListNoProgram: Boolean?,
    val generated: Boolean?,
    val displayOnVisitSchedule: Boolean?,
    val confidential: Boolean?,
    val orgUnitScope: Boolean?,
    val unique: Boolean?,
    val inherit: Boolean?,
    val fieldMask: String?,
    val legendSets: List<ObjectWithUid>?,
    val access: Access,
    val formName: String?,
    val displayFormName: String?,
    val preferredSearchOperator: TrackerSearchOperator?,
    val blockedSearchOperators: List<TrackerSearchOperator>?,
    val minCharactersToSearch: Int?,
    override val style: ObjectStyle,
) : BaseNameableObject, CoreObject, ObjectWithStyleKt {

    fun pattern(): String? = pattern
    fun sortOrderInListNoProgram(): Int? = sortOrderInListNoProgram
    fun optionSet(): ObjectWithUid? = optionSet
    fun valueType(): ValueType? = valueType
    fun expression(): String? = expression
    fun aggregationType(): AggregationType? = aggregationType
    fun programScope(): Boolean? = programScope
    fun displayInListNoProgram(): Boolean? = displayInListNoProgram
    fun generated(): Boolean? = generated
    fun displayOnVisitSchedule(): Boolean? = displayOnVisitSchedule
    fun confidential(): Boolean? = confidential
    fun orgUnitScope(): Boolean? = orgUnitScope
    fun unique(): Boolean? = unique
    fun inherit(): Boolean? = inherit
    fun fieldMask(): String? = fieldMask
    fun legendSets(): List<ObjectWithUid>? = legendSets
    fun access(): Access = access
    fun formName(): String? = formName
    fun displayFormName(): String? = displayFormName
    fun preferredSearchOperator(): TrackerSearchOperator? = preferredSearchOperator
    fun blockedSearchOperators(): List<TrackerSearchOperator>? = blockedSearchOperators
    fun minCharactersToSearch(): Int? = minCharactersToSearch

    fun toBuilder(): Builder = TrackedEntityAttributeBuilder.from(this)

    class Builder : TrackedEntityAttributeBuilder()

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
            .access(defaultAccess())
            .style(ObjectStyle())
    }
}
