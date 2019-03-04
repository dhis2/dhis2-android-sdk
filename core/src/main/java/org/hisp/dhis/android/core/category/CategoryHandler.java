/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.category;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.OrderedLinkSyncHandler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class CategoryHandler extends IdentifiableSyncHandlerImpl<Category> {

    private final SyncHandler<CategoryOption> categoryOptionHandler;
    private final OrderedLinkSyncHandler<CategoryOption, CategoryCategoryOptionLink> categoryCategoryOptionLinkHandler;

    @Inject
    CategoryHandler(
            @NonNull IdentifiableObjectStore<Category> categoryStore,
            @NonNull SyncHandler<CategoryOption> categoryOptionHandler,
            @NonNull OrderedLinkSyncHandler<CategoryOption, CategoryCategoryOptionLink>
                    categoryCategoryOptionLinkHandler) {
        super(categoryStore);
        this.categoryOptionHandler = categoryOptionHandler;
        this.categoryCategoryOptionLinkHandler = categoryCategoryOptionLinkHandler;
    }

    @Override
    protected void afterObjectHandled(Category category, HandleAction handleAction) {
        List<CategoryOption> categoryOptions = category.categoryOptions();
        if (categoryOptions != null) {
            List<CategoryOption> categoryOptionsWithAccess = new ArrayList<>();
            for (CategoryOption categoryOption : categoryOptions) {
                if (categoryOption.access().data().read()) {
                    categoryOptionsWithAccess.add(categoryOption);
                }
            }

            categoryOptionHandler.handleMany(categoryOptionsWithAccess);
            categoryCategoryOptionLinkHandler.handleMany(category.uid(),
                    categoryOptionsWithAccess,
                    (categoryOption, sortOrder) -> CategoryCategoryOptionLink.builder()
                            .category(category.uid())
                            .categoryOption(categoryOption.uid())
                            .sortOrder(sortOrder)
                            .build());
        }
    }
}