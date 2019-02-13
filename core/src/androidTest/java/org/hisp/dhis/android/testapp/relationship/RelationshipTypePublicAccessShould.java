package org.hisp.dhis.android.testapp.relationship;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class RelationshipTypePublicAccessShould extends BasePublicAccessShould<RelationshipType> {

    @Mock
    private RelationshipType object;

    @Override
    public RelationshipType object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        RelationshipType.create(null);
    }

    @Override
    public void has_public_builder_method() {
        RelationshipType.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}