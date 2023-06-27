/*
 *  Copyright (c) 2004-2023, University of Oslo
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
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.arch.handlers.internal.OrderedLinkHandler
import org.hisp.dhis.android.core.category.*
import org.hisp.dhis.android.core.common.ObjectWithUid

@Reusable
internal class CategoryComboHandler @Inject constructor(
    store: IdentifiableObjectStore<CategoryCombo>,
    private val optionComboHandler: HandlerWithTransformer<CategoryOptionCombo>,
    private val categoryCategoryComboLinkHandler: OrderedLinkHandler<Category, CategoryCategoryComboLink>,
    private val categoryOptionCleaner: OrphanCleaner<CategoryCombo, CategoryOptionCombo>
) : IdentifiableHandlerImpl<CategoryCombo>(store) {

    override fun afterObjectHandled(o: CategoryCombo, action: HandleAction) {
        optionComboHandler.handleMany(
            CategoryComboInternalAccessor.accessCategoryOptionCombos(o)
        ) { optionCombo: CategoryOptionCombo ->
            optionCombo.toBuilder()
                .categoryCombo(ObjectWithUid.create(o.uid()))
                .build()
        }
        categoryCategoryComboLinkHandler.handleMany(
            o.uid(), o.categories()
        ) { category: Category, sortOrder: Int? ->
            CategoryCategoryComboLink.builder()
                .categoryCombo(o.uid())
                .category(category.uid())
                .sortOrder(sortOrder)
                .build()
        }
        if (action === HandleAction.Update) {
            categoryOptionCleaner.deleteOrphan(o, CategoryComboInternalAccessor.accessCategoryOptionCombos(o))
        }
    }
}
