/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.core.models.optionset;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.models.common.base.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.common.meta.DbOperation;
import org.hisp.dhis.android.sdk.models.common.meta.IDbOperation;
import org.hisp.dhis.android.sdk.models.optionset.IOptionStore;
import org.hisp.dhis.android.sdk.models.optionset.Option;
import org.hisp.dhis.android.sdk.models.optionset.OptionSet;
import org.hisp.dhis.android.sdk.core.models.flow.Option$Flow;
import org.hisp.dhis.android.sdk.core.models.flow.OptionSet$Flow;
import org.hisp.dhis.android.sdk.core.models.flow.OptionSet$Flow$Table;
import org.hisp.dhis.android.sdk.core.utils.DbUtils;

import java.util.ArrayList;
import java.util.List;

public final class OptionSetStore implements IIdentifiableObjectStore<OptionSet> {

    private final IOptionStore optionStore;

    public OptionSetStore(IOptionStore optionStore) {
        this.optionStore = optionStore;
    }

    @Override
    public boolean insert(OptionSet object) {
        OptionSet$Flow optionSetFlow = OptionSet$Flow.fromModel(object);
        optionSetFlow.insert();

        object.setId(optionSetFlow.getId());
        return true;
    }

    @Override
    public boolean update(OptionSet object) {
        OptionSet$Flow.fromModel(object).update();
        return true;
    }

    @Override
    public boolean save(OptionSet object) {
        OptionSet$Flow optionSetFlow =
                OptionSet$Flow.fromModel(object);
        optionSetFlow.save();

        object.setId(optionSetFlow.getId());
        return true;
    }

    @Override
    public boolean delete(OptionSet object) {
        List<Option> options = object.getOptions();
        List<IDbOperation> operations;
        if (options != null) {
            operations = new ArrayList<>();
            for (Option option : options) {
                operations.add(DbOperation.with(optionStore).delete(option));
            }
            DbUtils.applyBatch(operations);
        }
        OptionSet$Flow.fromModel(object).delete();
        return true;
    }

    @Override
    public List<OptionSet> queryAll() {
        List<OptionSet$Flow> optionSetFlows = new Select()
                .from(OptionSet$Flow.class)
                .queryList();
        for (OptionSet$Flow optionSetFlow : optionSetFlows) {
            optionSetFlow.setOptions(Option$Flow.fromModels(optionStore.query(OptionSet$Flow.toModel(optionSetFlow))));
        }
        return OptionSet$Flow.toModels(optionSetFlows);
    }

    @Override
    public OptionSet queryById(long id) {
        OptionSet$Flow optionSetFlow = new Select()
                .from(OptionSet$Flow.class)
                .where(Condition.column(OptionSet$Flow$Table.ID).is(id))
                .querySingle();
        optionSetFlow.setOptions(Option$Flow.fromModels(optionStore.query(OptionSet$Flow.toModel(optionSetFlow))));
        return OptionSet$Flow.toModel(optionSetFlow);
    }

    @Override
    public OptionSet queryByUid(String uid) {
        OptionSet$Flow optionSetFlow = new Select()
                .from(OptionSet$Flow.class)
                .where(Condition.column(OptionSet$Flow$Table.UID).is(uid))
                .querySingle();
        optionSetFlow.setOptions(Option$Flow.fromModels(optionStore.query(OptionSet$Flow.toModel(optionSetFlow))));
        return OptionSet$Flow.toModel(optionSetFlow);
    }
}
