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

package org.hisp.dhis.android.core.category.internal;

import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryModule;
import org.hisp.dhis.android.core.category.CategoryOption;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import retrofit2.Retrofit;

@Module(includes = {
        CategoryEntityDIModule.class,
        CategoryCategoryComboEntityDIModule.class,
        CategoryCategoryOptionEntityDIModule.class,
        CategoryComboEntityDIModule.class,
        CategoryOptionEntityDIModule.class,
        CategoryOptionComboEntityDIModule.class,
        CategoryOptionComboCategoryOptionEntityDIModule.class
})
public final class CategoryPackageDIModule {

    @Provides
    @Reusable
    CategoryService categoryService(Retrofit retrofit) {
        return retrofit.create(CategoryService.class);
    }

    @Provides
    @Reusable
    CategoryOptionService categoryOptionService(Retrofit retrofit) {
        return retrofit.create(CategoryOptionService.class);
    }

    @Provides
    @Reusable
    CategoryComboService categoryComboService(Retrofit retrofit) {
        return retrofit.create(CategoryComboService.class);
    }

    @Provides
    @Reusable
    UidsCall<Category> categoryCall(CategoryCall impl) {
        return impl;
    }

    @Provides
    @Reusable
    UidsCall<CategoryOption> categoryOptionCall(CategoryOptionCall impl) {
        return impl;
    }

    @Provides
    @Reusable
    UidsCall<CategoryCombo> categoryComboCall(CategoryComboCall impl) {
        return impl;
    }

    @Provides
    @Reusable
    CategoryModule module(CategoryModuleImpl impl) {
        return impl;
    }
}