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

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
class ProgramDHISVersionManager {

    private final DHISVersionManager versionManager;

    @Inject
    ProgramDHISVersionManager(DHISVersionManager versionManager) {
        this.versionManager = versionManager;
    }

    Program addCaptureCoordinatesOrFeatureType(Program program) {
        if (versionManager.is2_29() || versionManager.is2_30()) {
            return addFeatureType(program);
        } else {
            return addCaptureCoordinates(program);
        }
    }

    private Program addFeatureType(Program program) {
        FeatureType featureType = program.captureCoordinates() ? FeatureType.POINT : FeatureType.NONE;
        return program.toBuilder().featureType(featureType).build();
    }

    private Program addCaptureCoordinates(Program program) {
        boolean captureCoordinates = program.featureType() != FeatureType.NONE;
        return program.toBuilder().captureCoordinates(captureCoordinates).build();
    }
}