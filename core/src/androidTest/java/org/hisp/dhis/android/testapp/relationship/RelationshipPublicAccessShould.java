package org.hisp.dhis.android.testapp.relationship;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class RelationshipPublicAccessShould extends BasePublicAccessShould<Relationship> {

    @Mock
    private Relationship object;

    @Override
    public Relationship object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        Relationship.create(null);
    }

    @Override
    public void has_public_builder_method() {
        Relationship.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}