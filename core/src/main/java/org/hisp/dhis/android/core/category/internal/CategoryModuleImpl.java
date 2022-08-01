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
package org.hisp.dhis.android.core.category.internal;

import org.hisp.dhis.android.core.category.CategoryCollectionRepository;
import org.hisp.dhis.android.core.category.CategoryComboCollectionRepository;
import org.hisp.dhis.android.core.category.CategoryModule;
import org.hisp.dhis.android.core.category.CategoryOptionCollectionRepository;
import org.hisp.dhis.android.core.category.CategoryOptionComboCollectionRepository;
import org.hisp.dhis.android.core.category.CategoryOptionComboService;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class CategoryModuleImpl implements CategoryModule {

    private final CategoryCollectionRepository categories;
    private final CategoryOptionCollectionRepository categoryOptions;
    private final CategoryOptionComboCollectionRepository categoryOptionCombos;
    private final CategoryComboCollectionRepository categoryCombos;

    private final CategoryOptionComboService categoryOptionComboService;

    @Inject
    CategoryModuleImpl(
            CategoryCollectionRepository categories,
            CategoryOptionCollectionRepository categoryOptions,
            CategoryOptionComboCollectionRepository categoryOptionCombos,
            CategoryComboCollectionRepository categoryCombos,
            CategoryOptionComboService categoryOptionComboService) {
        this.categories = categories;
        this.categoryOptions = categoryOptions;
        this.categoryOptionCombos = categoryOptionCombos;
        this.categoryCombos = categoryCombos;
        this.categoryOptionComboService = categoryOptionComboService;
    }

    @Override
    public CategoryCollectionRepository categories() {
        return categories;
    }

    @Override
    public CategoryOptionCollectionRepository categoryOptions() {
        return categoryOptions;
    }

    @Override
    public CategoryOptionComboCollectionRepository categoryOptionCombos() {
        return categoryOptionCombos;
    }

    @Override
    public CategoryComboCollectionRepository categoryCombos() {
        return categoryCombos;
    }

    @Override
    public CategoryOptionComboService categoryOptionComboService() {
        return categoryOptionComboService;
    }
}