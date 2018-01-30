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
package org.hisp.dhis.android.core.program;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(JUnit4.class)
public class ProgramRuleHandlerShould {
    @Mock
    private ProgramRuleStore programRuleStore;

    @Mock
    private ProgramRuleActionHandler programRuleActionHandler;

    @Mock
    private ProgramRule programRule;

    @Mock
    private Program program;

    @Mock
    private ProgramStage programStage;

    // object to test
    private ProgramRuleHandler programRuleHandler;

    // list of program rules
    private List<ProgramRule> programRules;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        programRuleHandler = new ProgramRuleHandler(programRuleStore, programRuleActionHandler);

        when(programRule.uid()).thenReturn("test_program_rule_uid");
        when(programRule.program()).thenReturn(program);
        when(programRule.programStage()).thenReturn(programStage);

        programRules = new ArrayList<>();
        programRules.add(programRule);

    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        programRuleHandler.handleProgramRules(null);

        // verify that store is never called
        verify(programRuleStore, never()).delete(anyString());

        verify(programRuleStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyInt(), anyString(), anyString(), anyString(),
                anyString());

        verify(programRuleStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyInt(), anyString(), anyString(), anyString());

        // verify that program rule action handler is never called
        verify(programRuleActionHandler, never()).handleProgramRuleActions(anyListOf(ProgramRuleAction.class));

    }

    @Test
    public void invoke_delete_when_handle_program_rule_set_as_deleted() throws Exception {
        when(programRule.deleted()).thenReturn(Boolean.TRUE);

        programRuleHandler.handleProgramRules(programRules);

        // verify that delete is called once
        verify(programRuleStore, times(1)).delete(anyString());

        // verify that update and insert is never called
        verify(programRuleStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyInt(), anyString(), anyString(), anyString(),
                anyString());

        verify(programRuleStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyInt(), anyString(), anyString(), anyString());

        // verify that program rule action handler is called
        verify(programRuleActionHandler, never()).handleProgramRuleActions(
                anyListOf(ProgramRuleAction.class));
    }

    @Test
    public void invoke_only_update_when_handle_program_rule_inserted() throws Exception {
        when(programRuleStore.update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyInt(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(1);

        programRuleHandler.handleProgramRules(programRules);

        // verify that update is called once
        verify(programRuleStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyInt(), anyString(), anyString(), anyString(),
                anyString());

        // verify that insert and delete is never called
        verify(programRuleStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyInt(), anyString(), anyString(), anyString());

        verify(programRuleStore, never()).delete(anyString());

        // verify that program rule action handler is called
        verify(programRuleActionHandler, times(1)).handleProgramRuleActions(anyListOf(ProgramRuleAction.class));
    }

    @Test
    public void invoke_update_and_insert_when_handle_program_rule_not_inserted() throws Exception {
        when(programRuleStore.update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyInt(), anyString(), anyString(), anyString(),
                anyString())).thenReturn(0);

        programRuleHandler.handleProgramRules(programRules);

        // verify that insert is called once
        verify(programRuleStore, times(1)).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyInt(), anyString(), anyString(), anyString());

        // verify that update is called once since we try to update before we insert
        verify(programRuleStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyInt(), anyString(), anyString(), anyString(), anyString());

        // verify that delete is never called
        verify(programRuleStore, never()).delete(anyString());

        // verify that program rule action handler is called
        verify(programRuleActionHandler, times(1)).handleProgramRuleActions(anyListOf(ProgramRuleAction.class));
    }
}
