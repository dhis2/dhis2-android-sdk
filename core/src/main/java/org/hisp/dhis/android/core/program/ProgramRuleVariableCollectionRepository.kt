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
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.program.internal.ProgramRuleVariableStore
import javax.inject.Inject

@Reusable
class ProgramRuleVariableCollectionRepository @Inject internal constructor(
    store: ProgramRuleVariableStore,
    childrenAppenders: MutableMap<String, ChildrenAppender<ProgramRuleVariable>>,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramRuleVariable, ProgramRuleVariableCollectionRepository>(
    store,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        ProgramRuleVariableCollectionRepository(
            store,
            childrenAppenders,
            s,
        )
    },
) {
    fun byUseCodeForOptionSet(): BooleanFilterConnector<ProgramRuleVariableCollectionRepository> {
        return cf.bool(ProgramRuleVariableTableInfo.Columns.USE_CODE_FOR_OPTION_SET)
    }

    fun byProgramUid(): StringFilterConnector<ProgramRuleVariableCollectionRepository> {
        return cf.string(ProgramRuleVariableTableInfo.Columns.PROGRAM)
    }

    fun byProgramStageUid(): StringFilterConnector<ProgramRuleVariableCollectionRepository> {
        return cf.string(ProgramRuleVariableTableInfo.Columns.PROGRAM_STAGE)
    }

    fun byDataElementUid(): StringFilterConnector<ProgramRuleVariableCollectionRepository> {
        return cf.string(ProgramRuleVariableTableInfo.Columns.DATA_ELEMENT)
    }

    fun byTrackedEntityAttributeUid(): StringFilterConnector<ProgramRuleVariableCollectionRepository> {
        return cf.string(ProgramRuleVariableTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE)
    }

    fun byProgramRuleVariableSourceType(): EnumFilterConnector<
        ProgramRuleVariableCollectionRepository,
        ProgramRuleVariableSourceType,
        > {
        return cf.enumC(ProgramRuleVariableTableInfo.Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE)
    }
}