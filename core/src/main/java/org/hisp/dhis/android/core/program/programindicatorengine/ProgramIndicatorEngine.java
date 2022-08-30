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

package org.hisp.dhis.android.core.program.programindicatorengine;

import androidx.annotation.NonNull;

public interface ProgramIndicatorEngine {

    /**
     * @deprecated
     * Use {@link #getEnrollmentProgramIndicatorValue(String, String)} or
     * {@link #getEventProgramIndicatorValue(String, String)} instead.
     *
     * @param enrollmentUid Enrollment uid to evaluate the program indicator
     * @param eventUid Single event to evaluate the program indicator
     * @param programIndicatorUid Program indicator to evaluate
     * @return Program indicator evaluation
     */
    @Deprecated
    String getProgramIndicatorValue(String enrollmentUid, String eventUid, String programIndicatorUid);

    /**
     * Evaluates a program indicator in the context of an enrollment. This is only intended to evaluate the
     * called "inline program indicator" (those indicators that appear at data entry) or line-list indicators.
     *
     * @param enrollmentUid Enrollment to evaluate
     * @param programIndicatorUid Program indicator to evaluate
     * @return Program indicator evaluation
     */
    String getEnrollmentProgramIndicatorValue(@NonNull String enrollmentUid, @NonNull String programIndicatorUid);

    /**
     * Evaluates a program indicator in the context of an event (single or tracker). This is only intended to
     * evaluate the called "inline program indicator" (those indicators that appear at data entry) or line-list
     * indicators.
     *
     * @param eventUid Event to evaluate
     * @param programIndicatorUid Program indicator to evaluate
     * @return Program indicator evaluation
     */
    String getEventProgramIndicatorValue(@NonNull String eventUid, @NonNull String programIndicatorUid);

}