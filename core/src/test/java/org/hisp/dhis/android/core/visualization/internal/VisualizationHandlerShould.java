/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.visualization.internal;

import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.visualization.CategoryDimension;
import org.hisp.dhis.android.core.visualization.DataDimensionItem;
import org.hisp.dhis.android.core.visualization.Visualization;
import org.hisp.dhis.android.core.visualization.VisualizationCategoryDimensionLink;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;

@RunWith(JUnit4.class)
public class VisualizationHandlerShould {

    @Mock
    private IdentifiableObjectStore<Visualization> visualizationStore;

    @Mock
    private CollectionCleaner<Visualization> visualizationCollectionCleaner;

    @Mock
    private LinkStore<DataDimensionItem> dataDimensionItemStore;

    @Mock
    private LinkStore<VisualizationCategoryDimensionLink> visualizationCategoryDimensionLinkStore;

    @Mock
    private LinkHandler<ObjectWithUid, VisualizationCategoryDimensionLink> visualizationCategoryDimensionLinkHandler;

    @Mock
    private LinkHandler<DataDimensionItem, DataDimensionItem> dataDimensionItemHandler;

    @Mock
    private DataDimensionItem dataDimensionItem;

    @Mock
    private CategoryDimension categoryDimension;

    @Mock
    private ObjectWithUid category;

    @Mock
    private Visualization visualization;

    @Mock
    private List<ObjectWithUid> categories;

    // object to test
    private VisualizationHandler visualizationHandler;

    public VisualizationHandlerShould() {
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        visualizationHandler = new VisualizationHandler(
                visualizationStore,
                visualizationCollectionCleaner,
                visualizationCategoryDimensionLinkStore,
                dataDimensionItemStore,
                visualizationCategoryDimensionLinkHandler,
                dataDimensionItemHandler);

        List<DataDimensionItem> dataDimensionItems = new ArrayList<>();
        dataDimensionItems.add(dataDimensionItem);
        List<CategoryDimension> categoryDimensions = new ArrayList<>();
        categories = new ArrayList<>();
        categories.add(category);
        categoryDimensions.add(categoryDimension);

        when(visualization.dataDimensionItems()).thenReturn(dataDimensionItems);
        when(visualizationStore.updateOrInsert(any())).thenReturn(HandleAction.Insert);
        when(visualization.uid()).thenReturn("visualization_uid");
        when(category.uid()).thenReturn("category_uid");
        when(categoryDimension.category()).thenReturn(category);
        when(visualization.categoryDimensions()).thenReturn(categoryDimensions);
    }

    @Test
    public void extend_identifiable_sync_handler_impl() {
        IdentifiableHandlerImpl<Visualization> genericHandler = new VisualizationHandler
                (visualizationStore, visualizationCollectionCleaner, visualizationCategoryDimensionLinkStore, dataDimensionItemStore,
                        visualizationCategoryDimensionLinkHandler, dataDimensionItemHandler);
    }

    @Test
    public void call_stores_to_delete_before_collection_handled() {
        visualizationHandler.handleMany(Collections.singletonList(visualization));
        verify(visualizationCategoryDimensionLinkStore).delete();
        verify(dataDimensionItemStore).delete();
    }

    @Test
    public void call_data_dimension_items_handler() {
        visualizationHandler.handleMany(Collections.singletonList(visualization));
        verify(dataDimensionItemHandler).handleMany(any(), any(), any());
    }

    @Test
    public void call_category_dimensions_link_handler() {
        visualizationHandler.handleMany(Collections.singletonList(visualization));
        verify(visualizationCategoryDimensionLinkHandler).handleMany(any(), any(), any());
    }

    @Test
    public void call_collection_cleaner() {
        visualizationHandler.handleMany(Collections.singletonList(visualization));
        verify(visualizationCollectionCleaner).deleteNotPresent(any());
    }
}
