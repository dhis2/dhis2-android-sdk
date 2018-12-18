/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

import org.hisp.dhis.android.core.calls.factories.UidsCallFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

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
    CategoryModule module(DatabaseAdapter databaseAdapter) {
        return new CategoryModule(
                CategoryCollectionRepository.create(databaseAdapter),
                CategoryOptionCollectionRepository.create(databaseAdapter),
                CategoryOptionComboCollectionRepository.create(databaseAdapter),
                CategoryComboCollectionRepository.create(databaseAdapter)
        );
    }

    @Provides
    @Reusable
    CategoryService categoryService(Retrofit retrofit) {
        return retrofit.create(CategoryService.class);
    }

    @Provides
    @Reusable
    CategoryComboService categoryComboService(Retrofit retrofit) {
        return retrofit.create(CategoryComboService.class);
    }

    @Provides
    @Reusable
    UidsCallFactory<Category> categoryCallFactory(CategoryEndpointCallFactory impl) {
        return impl;
    }

    @Provides
    @Reusable
    UidsCallFactory<CategoryCombo> categoryComboCallFactory(CategoryComboEndpointCallFactory impl) {
        return impl;
    }
}