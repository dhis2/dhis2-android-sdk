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

import com.google.auto.value.AutoValue;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@AutoValue
public abstract class D2Progress {

    @NonNull
    public abstract Boolean isComplete();

    @Nullable
    public abstract Integer totalCalls();

    @NonNull
    public abstract List<String> doneCalls();

    @Nullable
    public String lastCall() {
        if (this.doneCalls().size() == 0) {
            return null;
        } else {
            return this.doneCalls().get(this.doneCalls().size() - 1);
        }
    }

    @Nullable
    public Double percentage() {
        Integer totalCalls = this.totalCalls();
        if (totalCalls == null) {
            return null;
        } else {
            return 100.0 * this.doneCalls().size() / totalCalls;
        }
    }

    public static Builder builder() {
        return new AutoValue_D2Progress.Builder();
    }

    public abstract Builder toBuilder();

    public static D2Progress empty(Integer totalCalls) {
        if (totalCalls != null && totalCalls < 0) {
            throw new IllegalArgumentException("Negative total calls");
        }
        return D2Progress.builder()
            .isComplete(totalCalls != null && totalCalls == 0)
            .totalCalls(totalCalls)
            .doneCalls(Collections.emptyList())
            .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder isComplete(Boolean isComplete);

        public abstract Builder totalCalls(Integer totalCalls);

        public abstract Builder doneCalls(List<String> doneCalls);

        public abstract D2Progress build();
    }
}