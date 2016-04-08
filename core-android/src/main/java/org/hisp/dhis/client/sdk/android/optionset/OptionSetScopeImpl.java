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

import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.core.optionset.IOptionSetService;
import org.hisp.dhis.client.sdk.models.optionset.Option;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;

import java.util.List;

import rx.Observable;

public class OptionSetScopeImpl implements OptionSetScope {
    private final IOptionSetService optionSetService;

    public OptionSetScopeImpl(IOptionSetService optionSetService) {
        this.optionSetService = optionSetService;
    }

    @Override
    public Observable<OptionSet> get(final String uid) {
        return Observable.create(new DefaultOnSubscribe<OptionSet>() {
            @Override
            public OptionSet call() {
                return optionSetService.get(uid);
            }
        });
    }

    @Override
    public Observable<OptionSet> get(final long id) {
        return Observable.create(new DefaultOnSubscribe<OptionSet>() {
            @Override
            public OptionSet call() {
                return optionSetService.get(id);
            }
        });
    }

    @Override
    public Observable<List<OptionSet>> list() {
        return Observable.create(new DefaultOnSubscribe<List<OptionSet>>() {
            @Override
            public List<OptionSet> call() {
                return optionSetService.list();
            }
        });
    }

    @Override
    public Observable<List<Option>> list(final OptionSet optionSet) {
        return Observable.create(new DefaultOnSubscribe<List<Option>>() {
            @Override
            public List<Option> call() {
                return optionSetService.list(optionSet);
            }
        });
    }
}
