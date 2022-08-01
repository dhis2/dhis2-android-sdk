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
package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.SubCollectionCleaner;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;

import java.util.Collection;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class ProgramRuleHandler extends IdentifiableHandlerImpl<ProgramRule> {
    private final HandlerWithTransformer<ProgramRuleAction> programRuleActionHandler;
    private final SubCollectionCleaner<ProgramRule> programRuleCleaner;
    private final OrphanCleaner<ProgramRule, ProgramRuleAction> programRuleActionCleaner;

    @Inject
    ProgramRuleHandler(IdentifiableObjectStore<ProgramRule> programRuleStore,
                       HandlerWithTransformer<ProgramRuleAction> programRuleActionHandler,
                       SubCollectionCleaner<ProgramRule> programRuleCleaner,
                       OrphanCleaner<ProgramRule, ProgramRuleAction> programRuleActionCleaner) {
        super(programRuleStore);
        this.programRuleActionHandler = programRuleActionHandler;
        this.programRuleCleaner = programRuleCleaner;
        this.programRuleActionCleaner = programRuleActionCleaner;
    }

    @Override
    protected void afterObjectHandled(ProgramRule programRule, HandleAction handleAction) {
        programRuleActionHandler.handleMany(programRule.programRuleActions(),
                pra -> pra.toBuilder().programRule(ObjectWithUid.create(programRule.uid())).build());
        if (handleAction == HandleAction.Update) {
            programRuleActionCleaner.deleteOrphan(programRule, programRule.programRuleActions());
        }
    }

    @Override
    protected void afterCollectionHandled(Collection<ProgramRule> programRules) {
        this.programRuleCleaner.deleteNotPresent(programRules);
    }
}
