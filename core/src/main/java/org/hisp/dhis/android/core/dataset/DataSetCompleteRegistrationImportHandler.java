/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.imports.DataValueImportSummary;
import org.hisp.dhis.android.core.imports.ImportConflict;
import org.hisp.dhis.android.core.imports.ImportCount;
import org.hisp.dhis.android.core.imports.ImportStatus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

final class DataSetCompleteRegistrationImportHandler {

    private final DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore;

    DataSetCompleteRegistrationImportHandler(
            DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore) {
        this.dataSetCompleteRegistrationStore = dataSetCompleteRegistrationStore;
    }

    DataValueImportSummary handleImportSummary(
            @NonNull DataSetCompleteRegistrationPayload dataSetCompleteRegistrationPayload,
            @NonNull DataValueImportSummary dataValueImportSummary,
            @NonNull List<DataSetCompleteRegistration> deletedDataSetCompleteRegistrations,
            @NonNull List<DataSetCompleteRegistration> withErrorDataSetCompleteRegistrations) {
        State newState =
                dataValueImportSummary.importStatus() == ImportStatus.ERROR ||
                        !withErrorDataSetCompleteRegistrations.isEmpty() ? State.ERROR : State.SYNCED;

        for (DataSetCompleteRegistration dataSetCompleteRegistration :
                dataSetCompleteRegistrationPayload.dataSetCompleteRegistrations) {
            dataSetCompleteRegistrationStore.setState(dataSetCompleteRegistration, newState);
        }

        List<ImportConflict> conflicts = new ArrayList<>();
        for (DataSetCompleteRegistration dataSetCompleteRegistration : withErrorDataSetCompleteRegistrations) {
            dataSetCompleteRegistrationStore.setState(dataSetCompleteRegistration, State.ERROR);
            conflicts.add(ImportConflict.create(
                    dataSetCompleteRegistration.toString(), "Error marking as incomplete"));
        }

        for (DataSetCompleteRegistration dataSetCompleteRegistration : deletedDataSetCompleteRegistrations) {
            dataSetCompleteRegistrationStore.deleteById(dataSetCompleteRegistration);
        }

        if (dataValueImportSummary.importConflicts() != null) {
            dataValueImportSummary.importConflicts().addAll(conflicts);
            conflicts = dataValueImportSummary.importConflicts();
        }

        return recreateDataValueImportSummary(dataValueImportSummary, conflicts,
                deletedDataSetCompleteRegistrations.size());
    }

    private DataValueImportSummary recreateDataValueImportSummary(DataValueImportSummary dataValueImportSummary,
                                                                  List<ImportConflict> conflicts,
                                                                  int deletedDataSetCompleteRegistrationsSize) {

        ImportCount ic = dataValueImportSummary.importCount();
        return DataValueImportSummary.create(ImportCount.create(ic.imported(), ic.updated(),
                ic.deleted() + deletedDataSetCompleteRegistrationsSize, ic.ignored()),
                dataValueImportSummary.importStatus(),
                dataValueImportSummary.responseType(),
                dataValueImportSummary.reference(),
                conflicts.isEmpty() ? null : conflicts);
    }
}