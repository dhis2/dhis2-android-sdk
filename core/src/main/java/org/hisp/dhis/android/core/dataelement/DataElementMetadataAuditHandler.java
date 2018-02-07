package org.hisp.dhis.android.core.dataelement;

import org.hisp.dhis.android.core.audit.AuditType;
import org.hisp.dhis.android.core.audit.MetadataAudit;
import org.hisp.dhis.android.core.audit.MetadataAuditHandler;

import java.util.HashSet;
import java.util.Set;

public class DataElementMetadataAuditHandler implements MetadataAuditHandler {

    private final DataElementFactory dataElementFactory;

    public DataElementMetadataAuditHandler(DataElementFactory dataElementFactory) {
        this.dataElementFactory = dataElementFactory;
    }

    public void handle(MetadataAudit metadataAudit) throws Exception {
        DataElement dataElement = (DataElement) metadataAudit.getValue();

        if (metadataAudit.getType() == AuditType.UPDATE) {
            //metadataAudit of UPDATE type does not return payload
            //It's necessary sync by metadata call

            Set<String> uIds = new HashSet<>();
            uIds.add(metadataAudit.getUid());
            DataElementQuery dataElementQuery = new DataElementQuery(uIds);
            dataElementFactory.newEndPointCall(dataElementQuery,
                    metadataAudit.getCreatedAt()).call();
        } else {
            if (metadataAudit.getType() == AuditType.DELETE) {
                dataElement = dataElement.toBuilder().deleted(true).build();
            }
            dataElementFactory.getDataElementHandler().handleDataElement(dataElement);
        }
    }
}
