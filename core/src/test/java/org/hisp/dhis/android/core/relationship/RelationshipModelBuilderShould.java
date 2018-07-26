package org.hisp.dhis.android.core.relationship;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Java6Assertions.assertThat;
import java.io.IOException;

public class RelationshipModelBuilderShould {
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
                "tracked-entity-instance-a",
                "tracked-entity-instance-b",
                "relationship",
                "relationship-type",
                null,
                null,
                null,
                null
        );
    }

    protected RelationshipModel buildModel() {
        return new RelationshipModelBuilder().buildModel(pojo);
    }

    @Test
    public void copy_pojo_relationship_properties() {
        assertThat(model.relationshipType()).isEqualTo(pojo.relationship());
    }
}
