package org.hisp.dhis.android.core.audit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.hisp.dhis.android.core.option.Option;
import org.junit.Test;

public class SyncedMetadataMapperShould {
    @Test
    public void map_from_metadata_audit_to_synced_metadata() {
        MetadataAudit metadataAudit = givenAMetadataAudit();

        SyncedMetadata syncedMetadata = SyncedMetadataMapper.map(Option.class, metadataAudit);

        assertThat(syncedMetadata.klass(), is(Option.class.getName()));
        assertThat(syncedMetadata.type(), is(metadataAudit.getType()));
        assertThat(syncedMetadata.uid(), is(metadataAudit.getUid()));
    }

    private MetadataAudit givenAMetadataAudit() {
        MetadataAudit metadataAudit =
                new MetadataAudit(2323,
                        "user example",
                        "klass example",
                        "uid example",
                        "code example",
                        AuditType.CREATE, null);

        return metadataAudit;
    }
}
