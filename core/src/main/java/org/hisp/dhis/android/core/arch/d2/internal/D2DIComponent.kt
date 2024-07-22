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
package org.hisp.dhis.android.core.arch.d2.internal

import android.content.Context
import androidx.annotation.VisibleForTesting
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.api.internal.HttpServiceClient
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.storage.internal.*
import org.hisp.dhis.android.core.category.internal.CategoryOptionStore
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManager
import org.hisp.dhis.android.core.configuration.internal.MultiUserDatabaseManagerForD2Manager
import org.hisp.dhis.android.core.dataelement.internal.DataElementEndpointCallFactory
import org.hisp.dhis.android.core.dataset.internal.DataSetEndpointCallFactory
import org.hisp.dhis.android.core.domain.aggregated.internal.AggregatedModuleImpl
import org.hisp.dhis.android.core.domain.metadata.internal.MetadataModuleImpl
import org.hisp.dhis.android.core.option.internal.OptionCall
import org.hisp.dhis.android.core.option.internal.OptionSetCall
import org.hisp.dhis.android.core.period.internal.PeriodHandler
import org.hisp.dhis.android.core.program.internal.ProgramCall
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeHandler
import org.hisp.dhis.android.core.trackedentity.internal.NewTrackerImporterTrackedEntityPostPayloadGenerator
import org.hisp.dhis.android.core.trackedentity.internal.OldTrackerImporterPayloadGenerator
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeHandler
import org.hisp.dhis.android.core.tracker.importer.internal.interpreters.InterpreterSelector
import org.hisp.dhis.android.core.wipe.internal.WipeModule
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("LongParameterList")
internal class D2DIComponent(
    val modules: D2Modules,
    val metadataModule: MetadataModuleImpl,
    val aggregatedModule: AggregatedModuleImpl,
    val wipeModule: WipeModule,
    val databaseAdapter: DatabaseAdapter,
    val userIdInMemoryStore: UserIdInMemoryStore,
    val multiUserDatabaseManagerForD2Manager: MultiUserDatabaseManagerForD2Manager,
    val credentialsSecureStore: CredentialsSecureStore,
    val appContext: Context,

    @get:VisibleForTesting
    val httpServiceClient: HttpServiceClient,

    @get:VisibleForTesting
    val coroutineApiCallExecutor: CoroutineAPICallExecutor,

    @get:VisibleForTesting
    val internalModules: D2InternalModules,

    @get:VisibleForTesting
    val programCall: ProgramCall,

    @get:VisibleForTesting
    val optionSetCall: OptionSetCall,

    @get:VisibleForTesting
    val optionCall: OptionCall,

    @get:VisibleForTesting
    val dataElementCallFactory: DataElementEndpointCallFactory,

    @get:VisibleForTesting
    val dataSetCallFactory: DataSetEndpointCallFactory,

    @get:VisibleForTesting
    val relationshipTypeHandler: RelationshipTypeHandler,

    @get:VisibleForTesting
    val trackedEntityTypeHandler: TrackedEntityTypeHandler,

    @get:VisibleForTesting
    internal val oldTrackerImporterPayloadGenerator: OldTrackerImporterPayloadGenerator,

    @get:VisibleForTesting
    internal val newTrackerImporterPayloadGenerator: NewTrackerImporterTrackedEntityPostPayloadGenerator,

    @get:VisibleForTesting
    val categoryOptionStore: CategoryOptionStore,

    @get:VisibleForTesting
    val periodHandler: PeriodHandler,

    @get:VisibleForTesting
    val interpreterSelector: InterpreterSelector,

    @get:VisibleForTesting
    val multiUserDatabaseManager: MultiUserDatabaseManager,
)
