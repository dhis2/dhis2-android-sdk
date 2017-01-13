package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;

import java.text.ParseException;
import java.util.Date;

public class CreateTrackedEntityDataValueUtils {

    private static String EVENT = "test_event";
    private static String DATA_ELEMENT = "test_dataElement";
    private static String STORED_BY = "test_storedBy";
    private static String VALUE = "test_value";
    private static Boolean PROVIDED_ELSEWHERE = false;

    // used for timestamps
    private static final String DATE = "2011-12-24T12:24:25.203";

    public static ContentValues create(long id) throws ParseException {

        Date date = BaseIdentifiableObject.DATE_FORMAT.parse(DATE);

        TrackedEntityDataValueModel trackedEntityDataValueModel = TrackedEntityDataValueModel.builder()
                .id(id)
                .event(EVENT)
                .dataElement(DATA_ELEMENT)
                .storedBy(STORED_BY)
                .value(VALUE)
                .created(date)
                .lastUpdated(date)
                .providedElsewhere(PROVIDED_ELSEWHERE)
                .build();
        return trackedEntityDataValueModel.toContentValues();
    }
}
