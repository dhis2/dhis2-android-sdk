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
package org.hisp.dhis.android.core.dataset;

import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleHandler;
import org.hisp.dhis.android.core.common.ObjectStyleModel;
import org.hisp.dhis.android.core.common.ObjectStyleModelBuilder;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.common.OrphanCleanerImpl;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

public class DataSetHandler extends IdentifiableHandlerImpl<DataSet, DataSetModel> {

    private final GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler;
    private final GenericHandler<Section, SectionModel> sectionHandler;
    private final OrphanCleaner<DataSet, Section> sectionOrphanCleaner;

    DataSetHandler(IdentifiableObjectStore<DataSetModel> dataSetStore,
                   GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler,
                   GenericHandler<Section, SectionModel> sectionHandler,
                   OrphanCleaner<DataSet, Section> sectionOrphanCleaner) {

        super(dataSetStore);
        this.styleHandler = styleHandler;
        this.sectionHandler = sectionHandler;
        this.sectionOrphanCleaner = sectionOrphanCleaner;
    }

    public static DataSetHandler create(DatabaseAdapter databaseAdapter) {

        return new DataSetHandler(
                DataSetStore.create(databaseAdapter),
                ObjectStyleHandler.create(databaseAdapter), SectionHandler.create(databaseAdapter),
                new OrphanCleanerImpl<DataSet, Section>(SectionModel.TABLE,
                        SectionModel.Columns.DATA_SET,
                        databaseAdapter));
    }

    @Override
    protected void afterObjectHandled(DataSet dataSet, HandleAction action) {

        styleHandler.handle(dataSet.style(),
                new ObjectStyleModelBuilder(dataSet.uid(), DataSetModel.TABLE));

        sectionHandler.handleMany(dataSet.sections(), new SectionModelBuilder());

        if (action == HandleAction.Update) {
            sectionOrphanCleaner.deleteOrphan(dataSet, dataSet.sections());
        }
    }
}
