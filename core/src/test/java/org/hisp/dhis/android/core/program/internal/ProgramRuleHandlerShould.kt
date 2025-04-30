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
package org.hisp.dhis.android.core.program.internal

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.same
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.ProgramRule
import org.hisp.dhis.android.core.program.ProgramRuleAction
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProgramRuleHandlerShould {
    private val programRuleStore: ProgramRuleStore = mock()
    private val programRuleActionHandler: ProgramRuleActionHandler = mock()
    private val programRule: ProgramRule = mock()
    private val programRuleCleaner: ProgramRuleSubCollectionCleaner = mock()
    private val programRuleActionCleaner: ProgramRuleActionOrphanCleaner = mock()
    private val programRuleActions: List<ProgramRuleAction> = mock()

    // object to test
    private lateinit var programRuleHandler: IdentifiableHandlerImpl<ProgramRule>

    @Before
    fun setUp() {
        programRuleHandler = ProgramRuleHandler(
            programRuleStore, programRuleActionHandler,
            programRuleCleaner, programRuleActionCleaner,
        )

        whenever(programRule.uid()).thenReturn("test_program_rule_uid")
        whenever(programRule.program()).thenReturn(ObjectWithUid.create("program"))
        whenever(programRule.programRuleActions()).thenReturn(programRuleActions)

        whenever(programRuleStore.updateOrInsert(programRule)).thenReturn(HandleAction.Insert)
    }

    @Test
    fun call_program_rule_action_handler() {
        programRuleHandler.handle(programRule)
        verify(programRuleActionHandler).handleMany(same(programRuleActions))
    }

    @Test
    fun call_program_rule_action_orphan_cleaner_on_update() {
        whenever(programRuleStore.updateOrInsert(programRule)).thenReturn(HandleAction.Update)

        programRuleHandler.handle(programRule)
        verify(programRuleActionCleaner).deleteOrphan(programRule, programRuleActions)
    }

    @Test
    fun not_call_program_rule_action_orphan_cleaner_on_insert() {
        whenever(programRuleStore.updateOrInsert(programRule)).thenReturn(HandleAction.Insert)

        programRuleHandler.handle(programRule)
        verify(programRuleActionCleaner, never()).deleteOrphan(programRule, programRuleActions)
    }

    @Test
    fun call_program_rule_orphan_cleaner() {
        val rules: Collection<ProgramRule> = listOf(programRule)
        programRuleHandler.handleMany(rules)
        verify(programRuleCleaner).deleteNotPresent(rules)
    }
}
