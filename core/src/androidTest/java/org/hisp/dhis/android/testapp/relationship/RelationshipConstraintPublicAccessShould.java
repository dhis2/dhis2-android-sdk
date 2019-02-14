package org.hisp.dhis.android.testapp.relationship;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.relationship.RelationshipConstraint;
import org.hisp.dhis.android.testapp.arch.BasePublicAccessShould;
import org.junit.runner.RunWith;
import org.mockito.Mock;

@RunWith(AndroidJUnit4.class)
public class RelationshipConstraintPublicAccessShould extends BasePublicAccessShould<RelationshipConstraint> {

    @Mock
    private RelationshipConstraint object;

    @Override
    public RelationshipConstraint object() {
        return object;
    }

    @Override
    public void has_public_create_method() {
        RelationshipConstraint.create(null);
    }

    @Override
    public void has_public_builder_method() {
        RelationshipConstraint.builder();
    }

    @Override
    public void has_public_to_builder_method() {
        object().toBuilder();
    }
}