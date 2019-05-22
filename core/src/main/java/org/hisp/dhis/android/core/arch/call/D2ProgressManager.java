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

package org.hisp.dhis.android.core.arch.call;

import java.util.ArrayList;
import java.util.List;

public class D2ProgressManager {

    private D2Progress progress;

    public D2ProgressManager(Integer totalCalls) {
        this.progress = D2Progress.empty(totalCalls);
    }

    public D2Progress getProgress() {
        return progress;
    }

    public <T> D2Progress increaseProgress(Class<T> resourceClass, boolean isComplete) {
        List<String> doneCalls = new ArrayList<>(progress.doneCalls());
        doneCalls.add(resourceClass.getSimpleName());
        progress = progress.toBuilder()
                .doneCalls(doneCalls)
                .isComplete(isComplete)
                .build();
        return progress;

    }

    public <T> D2Progress increaseProgressAndCompleteWithCount(Class<T> resourceClass) {
        Integer totalCalls = progress.totalCalls();
        if (totalCalls == null) {
            throw new IllegalStateException("Can't determine progress, total calls is not set");
        } else {
            return increaseProgress(resourceClass, progress.doneCalls().size() +  1 == totalCalls);
        }
    }
}