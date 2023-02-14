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

package org.hisp.dhis.android.core.fileresource.internal

import org.hisp.dhis.android.core.category.internal.CategoryComboStore
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl
import org.hisp.dhis.android.core.event.internal.EventStoreImpl
import org.hisp.dhis.android.core.option.internal.OptionSetStore
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore
import org.hisp.dhis.android.core.program.internal.ProgramStageStore
import org.hisp.dhis.android.core.program.internal.ProgramStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeStore
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStoreImpl
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataDispatcher
import org.junit.After
import org.junit.Before

internal open class BaseFileResourceRoutineIntegrationShould : BaseMockIntegrationTestMetadataDispatcher() {

    // Data stores
    protected val eventStore = EventStoreImpl.create(databaseAdapter)
    protected val enrollmentStore = EnrollmentStoreImpl.create(databaseAdapter)
    protected val trackedEntityDataValueStore = TrackedEntityDataValueStoreImpl.create(databaseAdapter)
    protected val trackedEntityAttributeValueStore = TrackedEntityAttributeValueStoreImpl.create(databaseAdapter)
    protected val fileResourceStore = FileResourceStoreImpl.create(d2.databaseAdapter())
    private val optionSetStore = OptionSetStore.create(d2.databaseAdapter())

    // Metadata stores
    protected val categoryComboStore = CategoryComboStore.create(databaseAdapter)

    protected val dataElementStore = DataElementStore.create(databaseAdapter)
    protected val organisationUnitStore = OrganisationUnitStore.create(databaseAdapter)

    protected val trackedEntityTypeStore = TrackedEntityTypeStore.create(databaseAdapter)
    protected val trackedEntityAttributeStore = TrackedEntityAttributeStore.create(databaseAdapter)
    protected val trackedEntityInstanceStore = TrackedEntityInstanceStoreImpl.create(databaseAdapter)
    protected val programStore = ProgramStore.create(databaseAdapter)
    protected val programStageStore = ProgramStageStore.create(databaseAdapter)

    @Before
    fun setUp() {
        organisationUnitStore.insert(FileResourceRoutineSamples.orgUnit1)
        fileResourceStore.insert(FileResourceRoutineSamples.fileResource1)
        fileResourceStore.insert(FileResourceRoutineSamples.fileResource2)
        fileResourceStore.insert(FileResourceRoutineSamples.fileResource3)
        categoryComboStore.insert(FileResourceRoutineSamples.categoryCombo)
        dataElementStore.insert(FileResourceRoutineSamples.dataElement1)
        trackedEntityTypeStore.insert(FileResourceRoutineSamples.trackedEntityType)
        programStore.insert(FileResourceRoutineSamples.program)
        programStageStore.insert(FileResourceRoutineSamples.programStage1)
        eventStore.insert(FileResourceRoutineSamples.event1)
        trackedEntityDataValueStore.insert(FileResourceRoutineSamples.trackedEntityDataValue)
        optionSetStore.insert(FileResourceRoutineSamples.optionSet)
        trackedEntityAttributeStore.insert(FileResourceRoutineSamples.trackedEntityAttribute)
        trackedEntityInstanceStore.insert(FileResourceRoutineSamples.trackedEntityInstance)
        trackedEntityAttributeValueStore.insert(FileResourceRoutineSamples.trackedEntityAttributeValue)
    }

    @After
    fun tearDown() {
        organisationUnitStore.deleteById(FileResourceRoutineSamples.orgUnit1)
        fileResourceStore.deleteById(FileResourceRoutineSamples.fileResource1)
        fileResourceStore.deleteById(FileResourceRoutineSamples.fileResource2)
        fileResourceStore.deleteById(FileResourceRoutineSamples.fileResource3)
        categoryComboStore.deleteById(FileResourceRoutineSamples.categoryCombo)
        dataElementStore.deleteById(FileResourceRoutineSamples.dataElement1)
        trackedEntityTypeStore.deleteById(FileResourceRoutineSamples.trackedEntityType)
        programStore.deleteById(FileResourceRoutineSamples.program)
        programStageStore.deleteById(FileResourceRoutineSamples.programStage1)
        eventStore.deleteById(FileResourceRoutineSamples.event1)
        trackedEntityDataValueStore.deleteById(FileResourceRoutineSamples.trackedEntityDataValue)
        optionSetStore.deleteById(FileResourceRoutineSamples.optionSet)
        trackedEntityAttributeStore.deleteById(FileResourceRoutineSamples.trackedEntityAttribute)
        trackedEntityInstanceStore.deleteById(FileResourceRoutineSamples.trackedEntityInstance)
        trackedEntityAttributeValueStore.deleteById(FileResourceRoutineSamples.trackedEntityAttributeValue)
    }
}
