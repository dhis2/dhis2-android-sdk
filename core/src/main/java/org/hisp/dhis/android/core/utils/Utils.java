/*
 * Copyright (c) 2017, University of Oslo
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

package org.hisp.dhis.android.core.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

import java.util.Collections;
import java.util.List;

/**
 * A collection of utility abstractions
 */
public final class Utils {

    private Utils() {
        // no instances
    }

    /**
     * A Null-safe safeUnmodifiableList.
     *
     * @param list
     * @return
     */
    @Nullable
    public static <T> List<T> safeUnmodifiableList(@Nullable List<T> list) {
        if (list != null) {
            return Collections.unmodifiableList(list);
        }

        return null;
    }

    public static <T extends BaseIdentifiableObject> boolean isDeleted(@NonNull T object) {
        return object.deleted() != null && object.deleted();
    }

    //----------------------------------------------------------------------------------------------------
    // DUPLICATION OF ISDELETED METHODS BECAUSE TRACKER MODELS DOESN'T INHERIT FROM BASEIDENTIFIABLEOBJECT
    //----------------------------------------------------------------------------------------------------

    public static boolean isDeleted(@NonNull Event object) {
        return object.deleted() != null && object.deleted();
    }

    public static boolean isDeleted(@NonNull Enrollment object) {
        return object.deleted() != null && object.deleted();
    }

    public static boolean isDeleted(@NonNull TrackedEntityInstance object) {
        return object.deleted() != null && object.deleted();
    }

    public static <T> void isNull(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Object must not be null");
        }
    }


}
