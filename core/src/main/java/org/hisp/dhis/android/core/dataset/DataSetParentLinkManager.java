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

import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkModel;
import org.hisp.dhis.android.core.indicator.DataSetIndicatorLinkStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.user.User;

import java.util.List;

class DataSetParentLinkManager {
    private final ObjectWithoutUidStore<DataSetDataElementLinkModel> dataSetDataElementStore;
    private final ObjectWithoutUidStore<DataSetOrganisationUnitLinkModel> dataSetOrganisationUnitStore;
    private final ObjectWithoutUidStore<DataSetIndicatorLinkModel> dataSetIndicatorStore;

    DataSetParentLinkManager(
            ObjectWithoutUidStore<DataSetDataElementLinkModel> dataSetDataElementStore,
            ObjectWithoutUidStore<DataSetOrganisationUnitLinkModel> dataSetOrganisationUnitStore,
            ObjectWithoutUidStore<DataSetIndicatorLinkModel> dataSetIndicatorStore) {
        this.dataSetDataElementStore = dataSetDataElementStore;
        this.dataSetOrganisationUnitStore = dataSetOrganisationUnitStore;
        this.dataSetIndicatorStore = dataSetIndicatorStore;
    }

    static DataSetParentLinkManager create(DatabaseAdapter databaseAdapter) {
        return new DataSetParentLinkManager(
                DataSetDataElementLinkStore.create(databaseAdapter),
                DataSetOrganisationUnitLinkStore.create(databaseAdapter),
                DataSetIndicatorLinkStore.create(databaseAdapter));
    }

    void saveDataSetDataElementAndIndicatorLinks(List<DataSet> dataSets) {
        for (DataSet dataSet : dataSets) {
            saveDataSetDataElementLink(dataSet);
            saveDataSetIndicatorLink(dataSet);
        }
    }

    private void saveDataSetDataElementLink(DataSet dataSet) {
        List<DataElementUids> dataSetElements = dataSet.dataSetElements();
        assert dataSetElements != null;
        for (DataElementUids dataSetDataElement : dataSetElements) {
            this.dataSetDataElementStore.updateOrInsertWhere(
                    DataSetDataElementLinkModel.create(
                            dataSet.uid(),
                            dataSetDataElement.dataElement().uid()
                    ));
        }
    }

    private void saveDataSetIndicatorLink(DataSet dataSet) {
        for (ObjectWithUid indicator : dataSet.indicators()) {
            this.dataSetIndicatorStore.updateOrInsertWhere(
                    DataSetIndicatorLinkModel.create(
                            dataSet.uid(),
                            indicator.uid()
                    ));
        }
    }

    void saveDataSetOrganisationUnitLinks(User user) {
        List<OrganisationUnit> organisationUnits = user.organisationUnits();

        if (organisationUnits != null) {
            for (OrganisationUnit organisationUnit : organisationUnits) {
                saveDataSetOrganisationUnitLink(organisationUnit);
            }
        }
    }

    private void saveDataSetOrganisationUnitLink(OrganisationUnit organisationUnit) {
        List<DataSet> dataSets = organisationUnit.dataSets();
        assert dataSets != null;
        for (DataSet dataSet : dataSets) {
            this.dataSetOrganisationUnitStore.updateOrInsertWhere(
                    DataSetOrganisationUnitLinkModel.create(
                            dataSet.uid(),
                            organisationUnit.uid()
                    ));
        }
    }
}