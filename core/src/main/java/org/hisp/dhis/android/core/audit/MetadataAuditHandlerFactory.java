package org.hisp.dhis.android.core.audit;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboFactory;
import org.hisp.dhis.android.core.category.CategoryComboMetadataAuditHandler;
import org.hisp.dhis.android.core.category.CategoryFactory;
import org.hisp.dhis.android.core.category.CategoryMetadataAuditHandler;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryOptionComboMetadataAuditHandler;
import org.hisp.dhis.android.core.category.CategoryOptionMetadataAuditHandler;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionMetadataAuditHandler;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.option.OptionSetMetadataAuditHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityMetadataAuditHandler;

public class MetadataAuditHandlerFactory {

    private final TrackedEntityFactory trackedEntityFactory;
    private final OptionSetFactory optionSetFactory;
    private final CategoryFactory categoryFactory;
    private final CategoryComboFactory categoryComboFactory;

    public MetadataAuditHandlerFactory(
            TrackedEntityFactory trackedEntityFactory, OptionSetFactory optionSetFactory,
            CategoryFactory categoryFactory, CategoryComboFactory categoryComboFactory) {
        this.trackedEntityFactory = trackedEntityFactory;
        this.optionSetFactory = optionSetFactory;
        this.categoryFactory = categoryFactory;
        this.categoryComboFactory = categoryComboFactory;
    }

    public MetadataAuditHandler getByClass(Class<?> klass) {
        if (klass == TrackedEntity.class) {
            return new TrackedEntityMetadataAuditHandler(trackedEntityFactory);
        } else if (klass == OptionSet.class) {
            return new OptionSetMetadataAuditHandler(optionSetFactory);
        } else if (klass == Option.class) {
            return new OptionMetadataAuditHandler(optionSetFactory);
        } else if(klass == Category.class) {
            return new CategoryMetadataAuditHandler(categoryFactory);
        } else if(klass == CategoryOption.class) {
            return new CategoryOptionMetadataAuditHandler(categoryFactory);
        } else if(klass == CategoryCombo.class) {
            return new CategoryComboMetadataAuditHandler(categoryComboFactory);
        } else if(klass == CategoryOptionCombo.class) {
            return new CategoryOptionComboMetadataAuditHandler(categoryComboFactory);
        } else {
            throw new IllegalArgumentException("No exists a metadata audit handler for: " + klass);
        }
    }

}
