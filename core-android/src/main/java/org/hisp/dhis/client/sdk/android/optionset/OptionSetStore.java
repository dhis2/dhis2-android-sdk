/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.optionset;

import org.hisp.dhis.client.sdk.android.api.persistence.flow.ModelLinkFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OptionSetFlow;
import org.hisp.dhis.client.sdk.android.common.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.common.persistence.IDbOperation;
import org.hisp.dhis.client.sdk.core.common.persistence.ITransactionManager;
import org.hisp.dhis.client.sdk.core.optionset.IOptionSetStore;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;

import java.util.ArrayList;
import java.util.List;

public final class OptionSetStore extends AbsIdentifiableObjectStore<OptionSet, OptionSetFlow>
        implements IOptionSetStore {
//    private static final String OPTIONSET_TO_OPTIONS = "optionsetToOptions";
//    private final ITransactionManager transactionManager;

    public OptionSetStore() {
        super(OptionSetFlow.MAPPER);
//        this.transactionManager = transactionManager;
    }

//    @Override
//    public boolean insert(OptionSet optionSet) {
//        OptionSetFlow databaseEntity = getMapper().mapToDatabaseEntity(optionSet);
//        if (databaseEntity != null) {
//            databaseEntity.insert();
//
//            /* setting id which DbFlows' BaseModel generated after insertion */
//            optionSet.setId(databaseEntity.getId());
//
////            List<Option> options = optionSet.getOptions();
////            if(options != null) {
////                for (Option option : options) {
////                    if (!mOptionStore.insert(option)) {
////                        return false;
////                    }
////                }
////            }
////            return true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public boolean save(OptionSet optionSet) {
//        OptionSetFlow databaseEntity = getMapper().mapToDatabaseEntity(optionSet);
//        if (databaseEntity != null) {
//            databaseEntity.save();
//
//            /* setting id which DbFlows' BaseModel generated after insertion */
//            optionSet.setId(databaseEntity.getId());
//
////            List<Option> options = optionSet.getOptions();
////            if(options != null) {
////                for (Option option : options) {
////                    if (!mOptionStore.save(option)) {
////                        return false;
////                    }
////                }
////            }
////            return true;
//        }
//
//        return false;
//    }

//    private void updateOptionSetRelationships(OptionSet optionSet) {
//        List<IDbOperation> dbOperations = new ArrayList<>();
//        dbOperations.addAll(ModelLinkFlow.updateLinksToModel(optionSet,
//                optionSet.getOptions(), OPTIONSET_TO_OPTIONS));
//        transactionManager.transact(dbOperations);
//    }
}
