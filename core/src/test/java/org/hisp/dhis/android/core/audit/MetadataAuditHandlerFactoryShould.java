package org.hisp.dhis.android.core.audit;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboFactory;
import org.hisp.dhis.android.core.category.CategoryComboMetadataAuditHandler;
import org.hisp.dhis.android.core.category.CategoryFactory;
import org.hisp.dhis.android.core.category.CategoryMetadataAuditHandler;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionMetadataAuditHandler;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionMetadataAuditHandler;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.option.OptionSetMetadataAuditHandler;
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
    private CategoryFactory categoryFactory;

    @Mock
    private CategoryComboFactory categoryComboFactory;

    private MetadataAuditHandlerFactory metadataAuditHandlerFactory;

    @Parameterized.Parameters(name = "{index} MetadataAuditHandlerFactory should return: {0} for "
            + "{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {TrackedEntityMetadataAuditHandler.class, TrackedEntity.class},
                {OptionSetMetadataAuditHandler.class, OptionSet.class},
                {OptionMetadataAuditHandler.class, Option.class},
                {CategoryMetadataAuditHandler.class, Category.class},
                {CategoryOptionMetadataAuditHandler.class, CategoryOption.class},
                {CategoryComboMetadataAuditHandler.class, CategoryCombo.class}
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
                new MetadataAuditHandlerFactory(trackedEntityFactory, optionSetFactory, categoryFactory, categoryComboFactory);
    }

    @Test
    public void return_tracked_entity_audit() {
        MetadataAuditHandler metadataAuditHandler =
                metadataAuditHandlerFactory.getByClass(dhisType);

        assertThat(metadataAuditHandler, instanceOf(metadataAuditHandlerExpected));
    }
}
