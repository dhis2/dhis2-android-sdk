package org.hisp.dhis.android.core.sms.data.webapirepository;

import java.util.Date;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "UWF_UNWRITTEN_FIELD", justification = "Expected for json model classes")
class MetadataResponseModel {
    SystemInfo system;
    List<Id> categoryOptionCombos;
    List<Id> organisationUnits;
    List<Id> dataElements;
    List<Id> users;
    List<Id> trackedEntityTypes;
    List<Id> trackedEntityAttributes;
    List<Id> programs;

    static class SystemInfo {
        Date date;
    }

    static class Id {
        String id;
    }
}