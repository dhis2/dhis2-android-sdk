package org.hisp.dhis.android.core.audit;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionMetadataAuditHandler;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.option.OptionSetMetadataAuditHandler;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitFactory;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMetadataAuditHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
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
    private OptionSetFactory optionSetFactory;

    @Mock
    private OrganisationUnitFactory organisationUnitFactory;

    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    @Parameterized.Parameters(name = "{index} MetadataAuditHandlerFactory should return: {0} for "
            + "{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {TrackedEntityMetadataAuditHandler.class, TrackedEntity.class},
                {OptionSetMetadataAuditHandler.class, OptionSet.class},
                {OptionMetadataAuditHandler.class, Option.class},
                {OrganisationUnitMetadataAuditHandler.class, OrganisationUnit.class}
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
                new MetadataAuditHandlerFactory(trackedEntityFactory, optionSetFactory, organisationUnitFactory);
    }

    @Test
    public void return_tracked_entity_audit() {
        MetadataAuditHandler metadataAuditHandler =
                metadataAuditHandlerFactory.getByClass(dhisType);

        assertThat(metadataAuditHandler, instanceOf(metadataAuditHandlerExpected));
    }
}
