package org.hisp.dhis.android.core.commons;

import android.database.Cursor;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.google.common.truth.Truth.assertThat;

class RecordingObserver implements Observer<Cursor> {
    private static final Object COMPLETED = "<completed>";
    private final BlockingDeque<Object> events;
    private Disposable disposable;

    public RecordingObserver() {
        this.events = new LinkedBlockingDeque<>();
    }

    @Override
    public void onNext(Cursor cursor) {
        events.add(cursor);
    }

    @Override
    public void onError(Throwable throwable) {
        events.add(throwable);
    }

    @Override
    public void onComplete() {
        events.add(COMPLETED);
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        this.disposable = disposable;
    }

    private Object takeEvent() {
        Object item = events.removeFirst();
        if (item == null) {
            throw new AssertionError("No items.");
        }
        return item;
    }

    final CursorAssert assertCursor() {
        Object event = takeEvent();
        assertThat(event).isInstanceOf(Cursor.class);
        return CursorAssert.assertThatCursor((Cursor) event);
    }

    final void assertErrorContains(String expected) {
        Object event = takeEvent();
        assertThat(event).isInstanceOf(Throwable.class);
        assertThat(((Throwable) event).getMessage()).contains(expected);
    }

    final void assertIsCompleted() {
        Object event = takeEvent();
        assertThat(event).isEqualTo(COMPLETED);
    }

    void assertNoMoreEvents() {
        assertThat(events).isEmpty();
    }

    void dispose() {
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
