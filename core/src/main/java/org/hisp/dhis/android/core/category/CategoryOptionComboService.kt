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
package org.hisp.dhis.android.core.category

import dagger.Reusable
import io.reactivex.Single
import java.util.Date
import javax.inject.Inject

@Reusable
class CategoryOptionComboService @Inject constructor(
    private val categoryOptionRepository: CategoryOptionCollectionRepository
) {

    fun blockingHasAccess(categoryOptionComboUid: String, date: Date?, orgUnitUid: String? = null): Boolean {
        val categoryOptions = categoryOptionRepository
            .byCategoryOptionComboUid(categoryOptionComboUid)
            .blockingGet()

        return blockingIsAssignedToOrgUnit(categoryOptionComboUid, orgUnitUid) &&
            blockingHasWriteAccess(categoryOptions) &&
            isInOptionRange(categoryOptions, date)
    }

    fun blockingHasWriteAccess(
        categoryOptions: List<CategoryOption>,
    ): Boolean {
        return categoryOptions.none { it.access().data().write() == false }
    }

    fun blockingIsAssignedToOrgUnit(
        categoryOptionComboUid: String,
        orgUnitUid: String?
    ): Boolean {
        return orgUnitUid?.let {
            val categoryOptions = categoryOptionRepository
                .byCategoryOptionComboUid(categoryOptionComboUid)
                .withOrganisationUnits()
                .blockingGet()

            categoryOptions.all { categoryOption ->
                categoryOption.organisationUnits()?.any {
                    it.uid() == orgUnitUid
                } ?: true
            }
        } ?: true
    }

    fun hasAccess(categoryOptionComboUid: String, date: Date?): Single<Boolean> {
        return Single.fromCallable { blockingHasAccess(categoryOptionComboUid, date) }
    }

    fun isInOptionRange(options: List<CategoryOption>, date: Date?): Boolean {
        return date?.let {
            options.all { option ->
                option.startDate()?.before(date) ?: true && option.endDate()?.after(date) ?: true
            }
        } ?: true
    }
}
