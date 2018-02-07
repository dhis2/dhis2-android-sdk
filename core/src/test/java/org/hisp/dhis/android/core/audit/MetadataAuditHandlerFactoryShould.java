package org.hisp.dhis.android.core.audit;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementFactory;
import org.hisp.dhis.android.core.dataelement.DataElementMetadataAuditHandler;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionMetadataAuditHandler;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.option.OptionSetMetadataAuditHandler;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramFactory;
import org.hisp.dhis.android.core.program.ProgramIndicator;
import org.hisp.dhis.android.core.program.ProgramIndicatorMetadataAuditHandler;
import org.hisp.dhis.android.core.program.ProgramMetadataAuditHandler;
import org.hisp.dhis.android.core.program.ProgramRule;
import org.hisp.dhis.android.core.program.ProgramRuleAction;
import org.hisp.dhis.android.core.program.ProgramRuleActionMetadataAuditHandler;
import org.hisp.dhis.android.core.program.ProgramRuleMetadataAuditHandler;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramRuleVariableMetadataAuditHandler;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageMetadataAuditHandler;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeFactory;
import org.hisp.dhis.android.core.relationship.RelationshipTypeMetadataAuditHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeMetadataAuditHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityMetadataAuditHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MetadataAuditHandlerFactoryShould {

    @Mock
    private TrackedEntityFactory trackedEntityFactory;

    @Mock
    private TrackedEntityAttributeFactory trackedEntityAttributeFactory;

    @Mock
    private OptionSetFactory optionSetFactory;

    @Mock
    private DataElementFactory dataElementFactory;

    @Mock
    private ProgramFactory programFactory;

    @Mock
    private RelationshipTypeFactory relationshipTypeFactory;

    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    @Parameterized.Parameters(name = "{index} MetadataAuditHandlerFactory should return: {0} for "
            + "{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {TrackedEntityMetadataAuditHandler.class, TrackedEntity.class},
                {OptionSetMetadataAuditHandler.class, OptionSet.class},
                {OptionMetadataAuditHandler.class, Option.class},
                {DataElementMetadataAuditHandler.class, DataElement.class},
                {TrackedEntityAttributeMetadataAuditHandler.class, TrackedEntityAttribute.class},
                {OptionMetadataAuditHandler.class, Option.class},
                {ProgramMetadataAuditHandler.class, Program.class},
                {ProgramStageMetadataAuditHandler.class, ProgramStage.class},
                {ProgramIndicatorMetadataAuditHandler.class, ProgramIndicator.class},
                {ProgramRuleMetadataAuditHandler.class, ProgramRule.class},
                {ProgramRuleActionMetadataAuditHandler.class, ProgramRuleAction.class},
                {ProgramRuleVariableMetadataAuditHandler.class, ProgramRuleVariable.class},
                {RelationshipTypeMetadataAuditHandler.class, RelationshipType.class}
        });
    }

    private final Class<?> metadataAuditHandlerExpected;
    private final Class<?> dhisType;

    public MetadataAuditHandlerFactoryShould(
            Class<?> metadataAuditHandlerExpected, Class<?> dhisType) {
        this.metadataAuditHandlerExpected = metadataAuditHandlerExpected;
        this.dhisType = dhisType;
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        metadataAuditHandlerFactory =
                new MetadataAuditHandlerFactory(trackedEntityFactory, optionSetFactory,
                        dataElementFactory, trackedEntityAttributeFactory, programFactory,
                        relationshipTypeFactory);
    }

    @Test
    public void return_metadata_audit_handler() {
        MetadataAuditHandler metadataAuditHandler =
                metadataAuditHandlerFactory.getByClass(dhisType);

        assertThat(metadataAuditHandler, instanceOf(metadataAuditHandlerExpected));
    }
}
