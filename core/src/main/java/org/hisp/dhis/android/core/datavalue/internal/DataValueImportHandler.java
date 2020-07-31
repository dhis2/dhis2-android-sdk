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

package org.hisp.dhis.android.core.datavalue.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.internal.DataValueImportSummary;
import org.hisp.dhis.android.core.imports.internal.ImportConflict;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class DataValueImportHandler {

    private final DataValueStore dataValueStore;

    @Inject
    DataValueImportHandler(DataValueStore dataValueStore) {
        this.dataValueStore = dataValueStore;
    }

    void handleImportSummary(@NonNull DataValueSet dataValueSet,
                             @NonNull DataValueImportSummary dataValueImportSummary) {
        if (dataValueImportSummary == null || dataValueSet == null) {
            return;
        }

        State state = (dataValueImportSummary.importStatus() == ImportStatus.ERROR) ? State.ERROR :
                (dataValueImportSummary.importStatus() == ImportStatus.WARNING) ? State.WARNING : State.SYNCED;

        if (state == State.WARNING) {
            handleDataValueWarnings(dataValueSet, dataValueImportSummary);
        } else {
            setStateToDataValues(state, dataValueSet.dataValues);
        }
    }

    private void handleDataValueWarnings(DataValueSet dataValueSet, DataValueImportSummary dataValueImportSummary) {
        if (dataValueImportSummary.importConflicts() == null) {
            setStateToDataValues(State.WARNING, dataValueSet.dataValues);
        } else {
            Set<DataValue> dataValueConflicts = new HashSet<>();
            boolean setStateOnlyForConflicts = Boolean.TRUE;
            for (ImportConflict importConflict : dataValueImportSummary.importConflicts()) {
                List<DataValue> dataValues = getDataValues(importConflict, dataValueSet.dataValues);
                if (dataValues.isEmpty()) {
                    setStateOnlyForConflicts = Boolean.FALSE;
                }
                dataValueConflicts.addAll(dataValues);
            }
            setDataValueStates(dataValueSet, dataValueConflicts, setStateOnlyForConflicts);
        }
    }

    private void setDataValueStates(DataValueSet dataValueSet,
                                    Set<DataValue> dataValueConflicts,
                                    boolean setStateOnlyForConflicts) {
        if (setStateOnlyForConflicts) {
            Iterator<DataValue> i = dataValueSet.dataValues.iterator();
            while (i.hasNext()) {
                if (dataValueConflicts.contains(i.next())) {
                    i.remove();
                }
            }
            setStateToDataValues(State.WARNING, dataValueConflicts);
        }
        setStateToDataValues(State.SYNCED, dataValueSet.dataValues);
    }

    private List<DataValue> getDataValues(ImportConflict importConflict, Collection<DataValue> dataValues)
            throws IllegalArgumentException {
        String patternStr = "(?<=:\\s)[a-zA-Z0-9]{11}";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(importConflict.value());

        List<DataValue> foundDataValues = new ArrayList<>();

        if (matcher.find()) {
            String value = importConflict.object();
            String dataElementUid = matcher.group(0);
            for (DataValue dataValue : dataValues) {
                if (dataValue.value().equals(value) && dataValue.dataElement().equals(dataElementUid)) {
                    foundDataValues.add(dataValue);
                }
            }
        }

        return foundDataValues;
    }

    private void setStateToDataValues(State state, Collection<DataValue> dataValues) {
        for (DataValue dataValue : dataValues) {
            if (dataValueStore.isDataValueBeingUpload(dataValue)) {
                if (state == State.SYNCED && dataValueStore.isDeleted(dataValue)) {
                    dataValueStore.deleteWhere(dataValue);
                } else {
                    dataValueStore.setState(dataValue, state);
                }
            }
        }
    }
}
