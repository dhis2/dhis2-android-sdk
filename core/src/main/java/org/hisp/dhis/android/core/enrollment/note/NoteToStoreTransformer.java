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

package org.hisp.dhis.android.core.enrollment.note;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.Transformer;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import java.text.ParseException;

public class NoteToStoreTransformer extends Transformer<Note> {

    private final Note.Builder builder;
    private final DHISVersionManager versionManager;

    public NoteToStoreTransformer(Enrollment enrollment, DHISVersionManager versionManager) {
        this.versionManager = versionManager;
        this.builder = Note.builder()
                .enrollment(enrollment.uid());
    }

    @Override
    public Note transform(Note note) {

        String storedDate;
        String uid = null;
        try {
            if (this.versionManager.is2_29()) {
                storedDate = BaseIdentifiableObject.dateToDateStr(
                        BaseIdentifiableObject.parseSpaceDate(note.storedDate()));
            } else {
                storedDate = BaseIdentifiableObject.dateToDateStr(
                        BaseIdentifiableObject.parseDate(note.storedDate()));
                uid = note.uid();
            }
        } catch (ParseException ignored) {
            storedDate = null;
        }

        return builder
                .uid(uid)
                .value(note.value())
                .storedBy(note.storedBy())
                .storedDate(storedDate)
                .build();
    }
}