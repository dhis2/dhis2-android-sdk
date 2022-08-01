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
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.handlers.internal.OrderedLinkHandler
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLink
import org.hisp.dhis.android.core.category.CategoryOption

@Reusable
internal class CategoryCategoryOptionLinkPersistor @Inject constructor(
    private val categoryCategoryOptionLinkHandler: OrderedLinkHandler<CategoryOption, CategoryCategoryOptionLink>
) {

    fun handleMany(categories: List<Category>, categoryOptions: List<CategoryOption>) {
        val categoryOptionsMap = categoryOptions.map { it.uid() to it }.toMap()
        categories.forEach { handleCategory(it, categoryOptionsMap) }
    }

    private fun handleCategory(category: Category, categoryOptions: Map<String, CategoryOption>) {
        val categoryOptionsWithAccess = category.categoryOptions()!!.filter {
            categoryOptions.containsKey(it.uid())
        }

        categoryCategoryOptionLinkHandler.handleMany(
            category.uid(),
            categoryOptionsWithAccess
        ) { categoryOption: CategoryOption, sortOrder: Int ->
            CategoryCategoryOptionLink.builder()
                .category(category.uid())
                .categoryOption(categoryOption.uid())
                .sortOrder(sortOrder)
                .build()
        }
    }
}
