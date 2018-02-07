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

import static org.hisp.dhis.android.core.utils.Utils.isDeleted;

import org.hisp.dhis.android.core.option.OptionSetHandler;

public class DataElementHandler {
    private final DataElementStore dataElementStore;
    private final OptionSetHandler optionSetHandler;

    public DataElementHandler(DataElementStore dataElementStore,
            OptionSetHandler optionSetHandler) {
        this.dataElementStore = dataElementStore;
        this.optionSetHandler = optionSetHandler;
    }

    public void handleDataElement(DataElement dataElement) {
        if (dataElement == null) {
            return;
        }
        deleteOrPersistDataElement(dataElement);
    }

    /**
     * Deletes or persists data elements and applies changes to database.
     * This method has a nested call to deleteOrPersistOptionSets
     */
    private void deleteOrPersistDataElement(DataElement dataElement) {
        if (isDeleted(dataElement)) {
            dataElementStore.delete(dataElement.uid());
        } else {
            String optionSetUid = null;

            if (dataElement.optionSet() != null) {
                optionSetUid = dataElement.optionSet().uid();
            }
            String categoryCombo = null;

            if (dataElement.categoryCombo() != null) {
                categoryCombo = dataElement.categoryCombo().uid();
            }

            int updatedRow = dataElementStore.update(dataElement.uid(), dataElement.code(),
                    dataElement.name(),
                    dataElement.displayName(), dataElement.created(), dataElement.lastUpdated(),
                    dataElement.shortName(), dataElement.displayShortName(),
                    dataElement.description(),
                    dataElement.displayDescription(), dataElement.valueType(),
                    dataElement.zeroIsSignificant(), dataElement.aggregationType(),
                    dataElement.formName(),
                    dataElement.numberType(), dataElement.domainType(), dataElement.dimension(),
                    dataElement.displayFormName(), optionSetUid, categoryCombo, dataElement.uid());

            if (updatedRow <= 0) {
                dataElementStore.insert(dataElement.uid(), dataElement.code(), dataElement.name(),
                        dataElement.displayName(), dataElement.created(), dataElement.lastUpdated(),
                        dataElement.shortName(), dataElement.displayShortName(),
                        dataElement.description(),
                        dataElement.displayDescription(), dataElement.valueType(),
                        dataElement.zeroIsSignificant(), dataElement.aggregationType(),
                        dataElement.formName(),
                        dataElement.numberType(), dataElement.domainType(), dataElement.dimension(),
                        dataElement.displayFormName(), optionSetUid, categoryCombo);
            }

            optionSetHandler.handleOptionSet(dataElement.optionSet());
        }
    }
}
