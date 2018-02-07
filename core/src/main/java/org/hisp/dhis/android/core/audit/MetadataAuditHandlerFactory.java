package org.hisp.dhis.android.core.audit;

import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementFactory;
import org.hisp.dhis.android.core.dataelement.DataElementMetadataAuditHandler;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.option.OptionMetadataAuditHandler;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.option.OptionSetFactory;
import org.hisp.dhis.android.core.option.OptionSetMetadataAuditHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeMetadataAuditHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityFactory;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityMetadataAuditHandler;

public class MetadataAuditHandlerFactory {

    private final TrackedEntityFactory trackedEntityFactory;
    private final OptionSetFactory optionSetFactory;
    private final TrackedEntityAttributeFactory trackedEntityAttributeFactory;
    private final DataElementFactory dataElementFactory;

    public MetadataAuditHandlerFactory(
            TrackedEntityFactory trackedEntityFactory, OptionSetFactory optionSetFactory,
            DataElementFactory dataElementFactory,
            TrackedEntityAttributeFactory trackedEntityAttributeFactory) {
        this.trackedEntityFactory = trackedEntityFactory;
        this.optionSetFactory = optionSetFactory;
        this.dataElementFactory = dataElementFactory;
        this.trackedEntityAttributeFactory = trackedEntityAttributeFactory;
    }

    public MetadataAuditHandler getByClass(Class<?> klass) {
        if (klass == TrackedEntity.class) {
            return new TrackedEntityMetadataAuditHandler(trackedEntityFactory);
        } else if (klass == OptionSet.class) {
            return new OptionSetMetadataAuditHandler(optionSetFactory);
        } else if (klass == Option.class) {
            return new OptionMetadataAuditHandler(optionSetFactory);
        }  else if (klass == TrackedEntityAttribute.class) {
            return new TrackedEntityAttributeMetadataAuditHandler(
                    trackedEntityAttributeFactory);
        }  else if (klass == DataElement.class) {
            return new DataElementMetadataAuditHandler(dataElementFactory);
        } else {
            throw new IllegalArgumentException("No exists a metadata audit handler for: " + klass);
        }
    }

}
