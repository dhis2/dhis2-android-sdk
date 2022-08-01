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
package org.hisp.dhis.android.core.category.internal

import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall
import org.hisp.dhis.android.core.arch.modules.internal.UntypedModuleDownloader
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.category.CategoryOption

@Reusable
class CategoryModuleDownloader @Inject internal constructor(
    private val categoryCall: UidsCall<Category>,
    private val categoryComboCall: UidsCall<CategoryCombo>,
    private val categoryOptionCall: UidsCall<CategoryOption>,
    private val categoryOptionOrganisationUnitsCall: CategoryOptionOrganisationUnitsCall,
    private val categoryComboUidsSeeker: CategoryComboUidsSeeker,
    private val categoryCategoryOptionLinkPersistor: CategoryCategoryOptionLinkPersistor
) : UntypedModuleDownloader {

    override fun downloadMetadata(): Completable {
        return Single.fromCallable { categoryComboUidsSeeker.seekUids() }
            .flatMap { categoryComboCall.download(it) }
            .flatMapCompletable { comboUids ->
                val categoryUids = CategoryParentUidsHelper.getCategoryUids(comboUids)
                categoryCall.download(categoryUids).flatMap { categories ->
                    categoryOptionCall.download(categoryUids)
                        .flatMap { categoryOptions ->
                            categoryCategoryOptionLinkPersistor.handleMany(categories, categoryOptions)
                            categoryOptionOrganisationUnitsCall.download(categoryOptions.map { it.uid() }.toSet())
                        }
                }.ignoreElement()
            }
    }
}
