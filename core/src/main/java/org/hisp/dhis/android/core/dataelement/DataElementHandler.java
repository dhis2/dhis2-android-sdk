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
package org.hisp.dhis.android.core.dataelement;

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.DictionaryTableHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleHandler;
import org.hisp.dhis.android.core.common.ObjectStyleModelBuilder;
import org.hisp.dhis.android.core.common.ValueTypeRendering;
import org.hisp.dhis.android.core.common.ValueTypeRenderingHandler;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public class DataElementHandler extends IdentifiableSyncHandlerImpl<DataElement> {
    private final SyncHandlerWithTransformer<ObjectStyle> styleHandler;
    private final DictionaryTableHandler<ValueTypeRendering> renderTypeHandler;

    DataElementHandler(IdentifiableObjectStore<DataElement> dataElementStore,
                       SyncHandlerWithTransformer<ObjectStyle> styleHandler,
                       DictionaryTableHandler<ValueTypeRendering> renderTypeHandler) {
        super(dataElementStore);
        this.styleHandler = styleHandler;
        this.renderTypeHandler = renderTypeHandler;
    }

    public static DataElementHandler create(DatabaseAdapter databaseAdapter) {
        return new DataElementHandler(
                DataElementStore.create(databaseAdapter),
                ObjectStyleHandler.create(databaseAdapter),
                ValueTypeRenderingHandler.create(databaseAdapter));
    }

    @Override
    protected void afterObjectHandled(DataElement dateElement, HandleAction action) {
        styleHandler.handle(dateElement.style(),
                new ObjectStyleModelBuilder(dateElement.uid(), DataElementTableInfo.TABLE_INFO.name()));
        renderTypeHandler.handle(dateElement.renderType(), dateElement.uid(), DataElementTableInfo.TABLE_INFO.name());
    }
}