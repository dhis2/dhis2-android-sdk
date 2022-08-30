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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramRuleHandlerShould {
    @Mock
    private IdentifiableObjectStore<ProgramRule> programRuleStore;

    @Mock
    private HandlerWithTransformer<ProgramRuleAction> programRuleActionHandler;

    @Mock
    private ProgramRule programRule;

    @Mock
    private SubCollectionCleaner<ProgramRule> programRuleCleaner;

    @Mock
    private OrphanCleaner<ProgramRule, ProgramRuleAction> programRuleActionCleaner;

    @Mock
    private List<ProgramRuleAction> programRuleActions;

    // object to test
    private IdentifiableHandlerImpl<ProgramRule> programRuleHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        programRuleHandler = new ProgramRuleHandler(programRuleStore, programRuleActionHandler,
                programRuleCleaner, programRuleActionCleaner);

        when(programRule.uid()).thenReturn("test_program_rule_uid");
        when(programRule.program()).thenReturn(ObjectWithUid.create("program"));
        when(programRule.programRuleActions()).thenReturn(programRuleActions);
    }

    @Test
    public void extend_identifiable_sync_handler_impl() {
        IdentifiableHandlerImpl<ProgramRule> genericHandler = new ProgramRuleHandler(programRuleStore, null, null, null);
    }

    @Test
    public void call_program_rule_action_handler() {
        programRuleHandler.handle(programRule);
        verify(programRuleActionHandler).handleMany(same(programRuleActions), any());
    }

    @Test
    public void call_program_rule_action_orphan_cleaner_on_update() {
        when(programRuleStore.updateOrInsert(programRule)).thenReturn(HandleAction.Update);
        programRuleHandler.handle(programRule);
        verify(programRuleActionCleaner).deleteOrphan(programRule, programRuleActions);
    }

    @Test
    public void not_call_program_rule_action_orphan_cleaner_on_insert() {
        when(programRuleStore.updateOrInsert(programRule)).thenReturn(HandleAction.Insert);
        programRuleHandler.handle(programRule);
        verify(programRuleActionCleaner, never()).deleteOrphan(programRule, programRuleActions);
    }

    @Test
    public void call_program_rule_orphan_cleaner() {
        Collection<ProgramRule> rules = Collections.singletonList(programRule);
        programRuleHandler.handleMany(rules);
        verify(programRuleCleaner).deleteNotPresent(rules);
    }
}