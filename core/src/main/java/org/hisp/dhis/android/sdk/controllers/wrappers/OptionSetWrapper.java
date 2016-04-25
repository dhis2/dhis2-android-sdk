/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.controllers.wrappers;

import android.util.Log;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.models.Option;
import org.hisp.dhis.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis.android.sdk.persistence.models.meta.DbOperation;
import org.hisp.dhis.android.sdk.utils.DbUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simen Skogly Russnes on 24.08.15.
 */
public class OptionSetWrapper {

    public static List<DbOperation> getOperations(List<OptionSet> optionSets) {
        List<DbOperation> operations = new ArrayList<>();
        // Building option to optionset relationship.
        List<OptionSet> persistedOptionSets = MetaDataController.getOptionSets();
        if (optionSets != null && !optionSets.isEmpty()) {
            for (OptionSet optionSet: optionSets) {
                if (optionSet == null || optionSet.getOptions() == null) {
                    continue;
                }
                OptionSet persistedOptionSet = MetaDataController.getOptionSet(optionSet.getUid());
                List<Option> persistedOptions = null;
                if(persistedOptionSet != null) {
                    persistedOptions = persistedOptionSet.getOptions();
                }

                int sortIndex = 0;
                for (Option option : optionSet.getOptions()) {
                    option.setUid(optionSet.getUid() + option.getCode());//options don't have uid, but uid is used in createOperations
                    option.setLastUpdated(new DateTime().toString());//same with these dates
                    option.setCreated(new DateTime().toString());
                    option.setOptionSet(optionSet.getUid());
                    option.setSortIndex(sortIndex);
                    sortIndex++;
                }
                operations.addAll(DbUtils.createOperations(persistedOptions, optionSet.getOptions(), false));
            }
        }
        operations.addAll(DbUtils.createOperations(persistedOptionSets, optionSets, true));
        return operations;
    }

}
