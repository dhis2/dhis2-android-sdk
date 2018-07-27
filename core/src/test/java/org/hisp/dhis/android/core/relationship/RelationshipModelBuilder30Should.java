package org.hisp.dhis.android.core.relationship;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class RelationshipModelBuilder30Should {
    private Relationship pojo;

    private RelationshipModel model;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        pojo = buildPojo();
        model = buildModel();
    }

    protected Relationship buildPojo() {
        return Relationship.create(
                null,
                null,
                "relationship",
                "relationship-type",
                null,
                null,
                RelationshipItem.create(RelationshipItemTrackedEntityInstance.create("tei_uid"), null, null),
                RelationshipItem.create(null, RelationshipItemEnrollment.create("enrollment"), null)
        );
    }

    protected RelationshipModel buildModel() {
        return new RelationshipModelBuilder().buildModel(pojo);
    }

    @Test
    public void copy_pojo_relationship_properties() {
        assertThat(model.uid()).isEqualTo(pojo.relationship());
        assertThat(model.relationshipType()).isEqualTo(pojo.relationshipType());
    }
}
