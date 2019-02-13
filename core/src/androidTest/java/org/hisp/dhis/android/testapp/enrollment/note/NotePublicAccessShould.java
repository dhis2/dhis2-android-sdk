package org.hisp.dhis.android.testapp.enrollment.note;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class NotePublicAccessShould extends BasePublicAccessShould<Note> {

    @Mock
    private Note object;

    @Override
    public Note object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        Note.create(null);
    }

    @Override
    public void has_public_builder_method() {
        Note.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}