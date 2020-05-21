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
package org.hisp.dhis.android.localanalytics

import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.D2DIComponentAccessor
import org.hisp.dhis.android.core.category.internal.CategoryComboStore
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStoreImpl
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore

object LocalAnalyticsDatabaseFiller {

    fun fillDatabase(d2: D2) {
        val da = d2.databaseAdapter()

        val generator = LocalAnalyticsDataGenerator(LocalAnalyticsParams.Default)

        OrganisationUnitStore.create(da).insert(generator.getOrganisationUnits())

        val categoryCombos = generator.getCategoryCombos()
        CategoryComboStore.create(da).insert(categoryCombos)

        CategoryOptionComboStoreImpl.create(da).insert(generator.getCategoryOptionCombos(categoryCombos))

        val defaultCategoryCombo = categoryCombos.first()
        DataElementStore.create(da).insert(generator.getDataElementsAggregated(categoryCombos) +
                generator.getDataElementsTracker(defaultCategoryCombo))

        val d2DIComponent = D2DIComponentAccessor.getD2DIComponent(d2)
        d2DIComponent.periodHandler().generateAndPersist()

        val programs = generator.getPrograms(defaultCategoryCombo)
        ProgramStore.create(da).insert(programs)

        ProgramStageStore.create(da).insert(generator.getProgramStages(programs))

        TrackedEntityAttributeStore.create(da).insert(generator.getTrackedEntityAttributes())
    }
}