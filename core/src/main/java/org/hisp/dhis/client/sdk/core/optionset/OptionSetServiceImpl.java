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

package org.hisp.dhis.client.sdk.core.optionset;

import org.hisp.dhis.client.sdk.models.optionset.Option;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;

import java.util.List;

public class OptionSetServiceImpl implements OptionSetService {
    private final OptionSetStore optionSetStore;
    private final OptionStore optionStore;

    public OptionSetServiceImpl(OptionSetStore optionSetStore, OptionStore optionStore) {
        this.optionSetStore = optionSetStore;
        this.optionStore = optionStore;
    }

    @Override
    public OptionSet get(long id) {
        return optionSetStore.queryById(id);
    }

    @Override
    public OptionSet get(String uid) {
        return optionSetStore.queryByUid(uid);
    }

    @Override
    public List<OptionSet> list() {
        return optionSetStore.queryAll();
    }

    @Override
    public boolean remove(OptionSet object) {
        Preconditions.isNull(object, "Object must not be null");
        return optionSetStore.delete(object);
    }

    @Override
    public boolean save(OptionSet object) {
        Preconditions.isNull(object, "Object must not be null");
        return optionSetStore.save(object);
    }

    @Override
    public List<Option> list(OptionSet object) {
        return optionStore.query(object);
    }
}
