/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.option.internal;

import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionGroup;
import org.hisp.dhis.android.core.option.OptionModule;
import org.hisp.dhis.android.core.option.OptionSet;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import retrofit2.Retrofit;

@Module(includes = {
        OptionEntityDIModule.class,
        OptionGroupEntityDIModule.class,
        OptionGroupOptionEntityDIModule.class,
        OptionSetEntityDIModule.class
})
public final class OptionPackageDIModule {

    @Provides
    @Reusable
    UidsCall<OptionSet> optionSetCall(OptionSetCall impl) {
        return impl;
    }

    @Provides
    @Reusable
    OptionSetService optionSetService(Retrofit retrofit) {
        return retrofit.create(OptionSetService.class);
    }

    @Provides
    @Reusable
    UidsCall<Option> optionCall(OptionCall impl) {
        return impl;
    }

    @Provides
    @Reusable
    OptionService optionService(Retrofit retrofit) {
        return retrofit.create(OptionService.class);
    }

    @Provides
    @Reusable
    UidsCall<OptionGroup> optionGroupCall(OptionGroupCall impl) {
        return impl;
    }

    @Provides
    @Reusable
    OptionGroupService optionGroupService(Retrofit retrofit) {
        return retrofit.create(OptionGroupService.class);
    }

    @Provides
    @Reusable
    OptionModule module(OptionModuleImpl impl) {
        return impl;
    }
}