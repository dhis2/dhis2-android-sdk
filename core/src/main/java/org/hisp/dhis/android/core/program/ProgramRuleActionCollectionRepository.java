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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.program.ProgramRuleActionTableInfo.Columns;

import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class ProgramRuleActionCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramRuleAction, ProgramRuleActionCollectionRepository> {

    @Inject
    ProgramRuleActionCollectionRepository(final IdentifiableObjectStore<ProgramRuleAction> store,
                                          final Map<String, ChildrenAppender<ProgramRuleAction>> childrenAppenders,
                                          final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new ProgramRuleActionCollectionRepository(store, childrenAppenders, s)));
    }

    public StringFilterConnector<ProgramRuleActionCollectionRepository> byData() {
        return cf.string(Columns.DATA);
    }

    public StringFilterConnector<ProgramRuleActionCollectionRepository> byContent() {
        return cf.string(Columns.CONTENT);
    }

    public StringFilterConnector<ProgramRuleActionCollectionRepository> byLocation() {
        return cf.string(Columns.LOCATION);
    }

    public StringFilterConnector<ProgramRuleActionCollectionRepository> byTrackedEntityAttributeUid() {
        return cf.string(Columns.TRACKED_ENTITY_ATTRIBUTE);
    }

    public StringFilterConnector<ProgramRuleActionCollectionRepository> byProgramIndicatorUid() {
        return cf.string(Columns.PROGRAM_INDICATOR);
    }

    public StringFilterConnector<ProgramRuleActionCollectionRepository> byProgramStageSectionUid() {
        return cf.string(Columns.PROGRAM_STAGE_SECTION);
    }

    public EnumFilterConnector<ProgramRuleActionCollectionRepository,
            ProgramRuleActionType> byProgramRuleActionType() {

        return cf.enumC(Columns.PROGRAM_RULE_ACTION_TYPE);
    }

    public StringFilterConnector<ProgramRuleActionCollectionRepository> byProgramStageUid() {
        return cf.string(Columns.PROGRAM_STAGE);
    }

    public StringFilterConnector<ProgramRuleActionCollectionRepository> byDataElementUid() {
        return cf.string(Columns.DATA_ELEMENT);
    }

    public StringFilterConnector<ProgramRuleActionCollectionRepository> byProgramRuleUid() {
        return cf.string(Columns.PROGRAM_RULE);
    }

    public StringFilterConnector<ProgramRuleActionCollectionRepository> byOptionUid() {
        return cf.string(Columns.OPTION);
    }

    public StringFilterConnector<ProgramRuleActionCollectionRepository> byOptionGroupUid() {
        return cf.string(Columns.OPTION_GROUP);
    }
}