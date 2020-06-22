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

package org.hisp.dhis.android.core.common.internal;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventTableInfo;
import org.hisp.dhis.android.core.event.internal.EventStore;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class DataStatePropagatorImpl implements DataStatePropagator {

    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final EnrollmentStore enrollmentStore;
    private final EventStore eventStore;

    @Inject
    DataStatePropagatorImpl(TrackedEntityInstanceStore trackedEntityInstanceStore,
                            EnrollmentStore enrollmentStore,
                            EventStore eventStore) {
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.enrollmentStore = enrollmentStore;
        this.eventStore = eventStore;
    }

    @Override
    public void propagateEnrollmentUpdate(Enrollment enrollment) {
        setTeiStateForUpdate(enrollment.trackedEntityInstance());
    }

    @Override
    public void propagateEventUpdate(Event event) {
        if (event.enrollment() != null) {
            Enrollment enrollment = enrollmentStore.selectByUid(event.enrollment());
            setEnrollmentStateForUpdate(enrollment.uid());
            propagateEnrollmentUpdate(enrollment);
        }
    }

    @Override
    public void propagateTrackedEntityDataValueUpdate(TrackedEntityDataValue dataValue) {
        Event event = eventStore.selectByUid(dataValue.event());
        setEventStateForUpdate(event.uid());
        propagateEventUpdate(event);
    }

    @Override
    public void propagateTrackedEntityAttributeUpdate(TrackedEntityAttributeValue trackedEntityAttributeValue) {
        setTeiStateForUpdate(trackedEntityAttributeValue.trackedEntityInstance());
    }

    @Override
    public void propagateNoteCreation(Note note) {
        if (note.noteType() == Note.NoteType.ENROLLMENT_NOTE) {
            Enrollment enrollment = enrollmentStore.selectByUid(note.enrollment());
            setEnrollmentStateForUpdate(enrollment.uid());
            propagateEnrollmentUpdate(enrollment);
        } else if (note.noteType() == Note.NoteType.EVENT_NOTE) {
            Event event = eventStore.selectByUid(note.event());
            setEventStateForUpdate(event.uid());
            propagateEventUpdate(event);
        }
    }

    private void setTeiStateForUpdate(String trackedEntityInstanceUid) {
        TrackedEntityInstance instance = trackedEntityInstanceStore.selectByUid(trackedEntityInstanceUid);
        if (instance != null) {
            Date now = new Date();
            trackedEntityInstanceStore.update(instance.toBuilder()
                    .state(getStateForUpdate(instance.state()))
                    .lastUpdated(getMaxDate(instance.lastUpdated(), now))
                    .lastUpdatedAtClient(getMaxDate(instance.lastUpdatedAtClient(), now))
                    .build());
        }
    }

    private void setEventStateForUpdate(String eventUid) {
        Event event = eventStore.selectByUid(eventUid);
        if (event != null) {
            Date now = new Date();
            eventStore.update(event.toBuilder()
                    .state(getStateForUpdate(event.state()))
                    .lastUpdated(getMaxDate(event.lastUpdated(), now))
                    .lastUpdatedAtClient(getMaxDate(event.lastUpdatedAtClient(), now))
                    .build());
        }
    }

    private void setEnrollmentStateForUpdate(String enrollmentUid) {
        Enrollment enrollment = enrollmentStore.selectByUid(enrollmentUid);
        if (enrollment != null) {
            Date now = new Date();
            enrollmentStore.update(enrollment.toBuilder()
                    .state(getStateForUpdate(enrollment.state()))
                    .lastUpdated(getMaxDate(enrollment.lastUpdated(), now))
                    .lastUpdatedAtClient(getMaxDate(enrollment.lastUpdatedAtClient(), now))
                    .build());
        }
    }

    public void resetUploadingEnrollmentAndEventStates(String trackedEntityInstanceUid) {
        if (trackedEntityInstanceUid == null) {
            return;
        }

        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE, trackedEntityInstanceUid)
                .build();
        List<Enrollment> enrollments = enrollmentStore.selectWhere(whereClause);

        for (Enrollment enrollment : enrollments) {
            if (State.UPLOADING.equals(enrollment.state())) {
                enrollmentStore.setState(enrollment.uid(), State.TO_UPDATE);
                resetUploadingEventStates(enrollment.uid());
            }
        }
    }

    public void resetUploadingEventStates(String enrollmentUid) {
        if (enrollmentUid == null) {
            return;
        }

        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(EventTableInfo.Columns.ENROLLMENT, enrollmentUid)
                .build();
        List<Event> events = eventStore.selectWhere(whereClause);

        for (Event event : events) {
            if (State.UPLOADING.equals(event.state())) {
                eventStore.setState(event.uid(), State.TO_UPDATE);
            }
        }
    }

    private Date getMaxDate(Date existing, Date today) {
        if (existing == null) {
            return today;
        } else if (today == null || existing.after(today)) {
            return existing;
        } else {
            return today;
        }
    }

    private State getStateForUpdate(State existingState) {
        if (State.TO_POST.equals(existingState) || State.RELATIONSHIP.equals(existingState)) {
            return existingState;
        } else {
            return State.TO_UPDATE;
        }
    }
}