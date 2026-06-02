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

package org.hisp.dhis.android.core.common

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode

interface FilterQueryCriteria {

    val followUp: Boolean?
    val organisationUnit: String?
    val ouMode: OrganisationUnitMode?
    val assignedUserMode: AssignedUserMode?
    val order: String?
    val displayColumnOrder: List<String>?
    val eventDate: DateFilterPeriod?
    val lastUpdatedDate: DateFilterPeriod?

    fun followUp(): Boolean? = followUp
    fun organisationUnit(): String? = organisationUnit
    fun ouMode(): OrganisationUnitMode? = ouMode
    fun assignedUserMode(): AssignedUserMode? = assignedUserMode
    fun order(): String? = order
    fun displayColumnOrder(): List<String>? = displayColumnOrder
    fun eventDate(): DateFilterPeriod? = eventDate
    fun lastUpdatedDate(): DateFilterPeriod? = lastUpdatedDate

    interface Builder<T : Builder<T>> {

        fun followUp(followUp: Boolean?): T

        fun organisationUnit(organisationUnit: String?): T

        fun ouMode(ouMode: OrganisationUnitMode?): T

        fun assignedUserMode(assignedUserMode: AssignedUserMode?): T

        fun order(order: String?): T

        fun displayColumnOrder(displayColumnOrder: List<String>?): T

        fun eventDate(eventDate: DateFilterPeriod?): T

        fun lastUpdatedDate(lastUpdatedDate: DateFilterPeriod?): T
    }
}
