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
package org.hisp.dhis.android.core.legendset.internal;

import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.legendset.Legend;
import org.hisp.dhis.android.core.legendset.LegendSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class LegendSetHandlerShould {

    @Mock
    private IdentifiableObjectStore<LegendSet> legendSetStore;

    @Mock
    private HandlerWithTransformer<Legend> legendHandler;

    @Mock
    private Legend legend;

    @Mock
    private List<Legend> legends;

    @Mock
    private LegendSet legendSet;

    @Mock
    private OrphanCleaner<LegendSet, Legend> legendCleaner;

    // object to test
    private LegendSetHandler legendSetHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        legendSetHandler = new LegendSetHandler(legendSetStore, legendHandler, legendCleaner);
        legends = new ArrayList<>();
        legends.add(legend);
        when(legendSet.legends()).thenReturn(legends);
    }

    @Test
    public void extend_identifiable_sync_handler_impl() {
        IdentifiableHandlerImpl<LegendSet> genericHandler = new LegendSetHandler
                (legendSetStore, null, null);
    }

    @Test
    public void call_style_handler() {
        legendSetHandler.handle(legendSet);
        verify(legendHandler).handleMany(eq(legendSet.legends()), any());
    }

    @Test
    public void clean_orphan_legends_after_update() {
        when(legendSetStore.updateOrInsert(any(LegendSet.class))).thenReturn(HandleAction.Update);
        legendSetHandler.handle(legendSet);
        verify(legendCleaner).deleteOrphan(legendSet, legends);
    }

    @Test
    public void not_clean_orphan_legends_after_insert() {
        when(legendSetStore.updateOrInsert(any(LegendSet.class))).thenReturn(HandleAction.Insert);
        legendSetHandler.handle(legendSet);
        verify(legendCleaner, never()).deleteOrphan(legendSet, legends);
    }
}