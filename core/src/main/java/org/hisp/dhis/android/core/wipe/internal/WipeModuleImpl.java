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

package org.hisp.dhis.android.core.wipe.internal;

import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.common.Unit;
import org.hisp.dhis.android.core.maintenance.D2Error;

import java.util.List;

final class WipeModuleImpl implements WipeModule {

    private final D2CallExecutor d2CallExecutor;
    private final List<ModuleWiper> moduleWipers;

    WipeModuleImpl(D2CallExecutor d2CallExecutor,
                   List<ModuleWiper> moduleWipers) {
        this.d2CallExecutor = d2CallExecutor;
        this.moduleWipers = moduleWipers;
    }

    @Override
    public Unit wipeEverything() throws D2Error {
        return d2CallExecutor.executeD2CallTransactionally(() -> {
            wipeMetadataInternal();
            wipeDataInternal();

            return new Unit();
        });
    }

    @Override
    public Unit wipeMetadata() throws D2Error {
        return d2CallExecutor.executeD2CallTransactionally(() -> {
            wipeMetadataInternal();

            return new Unit();
        });
    }

    @Override
    public Unit wipeData() throws D2Error {
        return d2CallExecutor.executeD2CallTransactionally(() -> {
            wipeDataInternal();

            return new Unit();
        });
    }

    private void wipeMetadataInternal() {
        for (ModuleWiper moduleWiper : moduleWipers) {
            moduleWiper.wipeMetadata();
        }
    }

    private void wipeDataInternal() {
        for (ModuleWiper moduleWiper : moduleWipers) {
            moduleWiper.wipeData();
        }
    }
}