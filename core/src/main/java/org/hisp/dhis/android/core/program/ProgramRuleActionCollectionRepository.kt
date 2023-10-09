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
package org.hisp.dhis.android.core.program

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.program.internal.ProgramRuleActionStore
import javax.inject.Inject

@Reusable
@Suppress("TooManyFunctions")
class ProgramRuleActionCollectionRepository @Inject internal constructor(
    store: ProgramRuleActionStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramRuleAction, ProgramRuleActionCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        ProgramRuleActionCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byData(): StringFilterConnector<ProgramRuleActionCollectionRepository> {
        return cf.string(ProgramRuleActionTableInfo.Columns.DATA)
    }

    fun byContent(): StringFilterConnector<ProgramRuleActionCollectionRepository> {
        return cf.string(ProgramRuleActionTableInfo.Columns.CONTENT)
    }

    fun byLocation(): StringFilterConnector<ProgramRuleActionCollectionRepository> {
        return cf.string(ProgramRuleActionTableInfo.Columns.LOCATION)
    }

    fun byTrackedEntityAttributeUid(): StringFilterConnector<ProgramRuleActionCollectionRepository> {
        return cf.string(ProgramRuleActionTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE)
    }

    fun byProgramIndicatorUid(): StringFilterConnector<ProgramRuleActionCollectionRepository> {
        return cf.string(ProgramRuleActionTableInfo.Columns.PROGRAM_INDICATOR)
    }

    fun byProgramStageSectionUid(): StringFilterConnector<ProgramRuleActionCollectionRepository> {
        return cf.string(ProgramRuleActionTableInfo.Columns.PROGRAM_STAGE_SECTION)
    }

    fun byProgramRuleActionType(): EnumFilterConnector<ProgramRuleActionCollectionRepository, ProgramRuleActionType> {
        return cf.enumC(ProgramRuleActionTableInfo.Columns.PROGRAM_RULE_ACTION_TYPE)
    }

    fun byProgramStageUid(): StringFilterConnector<ProgramRuleActionCollectionRepository> {
        return cf.string(ProgramRuleActionTableInfo.Columns.PROGRAM_STAGE)
    }

    fun byDataElementUid(): StringFilterConnector<ProgramRuleActionCollectionRepository> {
        return cf.string(ProgramRuleActionTableInfo.Columns.DATA_ELEMENT)
    }

    fun byProgramRuleUid(): StringFilterConnector<ProgramRuleActionCollectionRepository> {
        return cf.string(ProgramRuleActionTableInfo.Columns.PROGRAM_RULE)
    }

    fun byOptionUid(): StringFilterConnector<ProgramRuleActionCollectionRepository> {
        return cf.string(ProgramRuleActionTableInfo.Columns.OPTION)
    }

    fun byOptionGroupUid(): StringFilterConnector<ProgramRuleActionCollectionRepository> {
        return cf.string(ProgramRuleActionTableInfo.Columns.OPTION_GROUP)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<ProgramRuleAction> = mapOf()
    }
}
