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

import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyIdentifiableCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyNameableCollectionRepository;

import javax.inject.Inject;

import dagger.Reusable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
@Reusable
public final class CategoryModule {

    public final ReadOnlyIdentifiableCollectionRepository<Category> categories;
    public final ReadOnlyNameableCollectionRepository<CategoryOption> categoryOptions;
    public final ReadOnlyIdentifiableCollectionRepository<CategoryOptionCombo> categoryOptionCombos;
    public final CategoryComboCollectionRepository categoryCombos;

    @Inject
    CategoryModule(
            ReadOnlyIdentifiableCollectionRepository<Category> categories,
            ReadOnlyNameableCollectionRepository<CategoryOption> categoryOptions,
            ReadOnlyIdentifiableCollectionRepository<CategoryOptionCombo> categoryOptionCombos,
            CategoryComboCollectionRepository categoryCombos) {
        this.categories = categories;
        this.categoryOptions = categoryOptions;
        this.categoryOptionCombos = categoryOptionCombos;
        this.categoryCombos = categoryCombos;
    }
}