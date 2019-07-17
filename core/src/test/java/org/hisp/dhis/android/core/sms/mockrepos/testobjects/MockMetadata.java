package org.hisp.dhis.android.core.sms.mockrepos.testobjects;

import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.smscompression.models.SMSMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockMetadata extends SMSMetadata {

    public MockMetadata() {
        users = Collections.singletonList(new ID(MockObjects.user));
        trackedEntityTypes = Collections.singletonList(new ID(MockObjects.teiUid));
        trackedEntityAttributes = getTrackedEntityAttributes();
        programs = Collections.singletonList(new ID(MockObjects.program));
        organisationUnits = Collections.singletonList(new ID(MockObjects.orgUnit));
        categoryOptionCombos = Collections.singletonList(new ID(MockObjects.categoryOptionCombo));
        dataElements = getDataElements();
    }

    private List<ID> getTrackedEntityAttributes() {
        ArrayList<ID> attrs = new ArrayList<>();
        for (TrackedEntityAttributeValue item : MockObjects.getTestAttributeValues()) {
            attrs.add(new ID(item.trackedEntityAttribute()));
        }
        return attrs;
    }

    private List<ID> getDataElements() {
        ArrayList<ID> attrs = new ArrayList<>();
        for (DataValue item : MockObjects.getDataValues()) {
            attrs.add(new ID(item.dataElement()));
        }
        return attrs;
    }
}