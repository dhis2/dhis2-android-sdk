package org.hisp.dhis.android.core.sms.domain.converter;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hisp.dhis.android.core.sms.domain.converter.Utility.check;

public class EventConverter {

    public String format(Event event, String username, String categoryOptionCombo) {
        if (event == null) {
            throw new NullPointerException("Event is null");
        }
        check(categoryOptionCombo);

        StringBuilder result = new StringBuilder();
        result
                .append(check(username))
                .append(' ')

                .append(check(event.organisationUnit()))
                .append(' ')

                .append(SubmissionType.PROGRAM_EVENT_NO_REG)
                .append(' ')

                .append(check(event.program()))
                .append(' ')

                .append(check(event.attributeOptionCombo()))
                .append(' ')

                .append(check(event.uid()))
                .append(' ')

                .append(Utility.timestamp())
                .append(' ');

        List<TrackedEntityDataValue> dataValues = event.trackedEntityDataValues();
        if (dataValues == null) {
            throw new NullPointerException("Event's data values are null");
        }
        for (TrackedEntityDataValue dataValue : dataValues) {
            result
                    .append('|')
                    .append(check(dataValue.dataElement()))
                    .append('-')
                    .append(categoryOptionCombo)
                    .append('=')
                    .append(check(dataValue.value(), false));
        }
        result.append('|');

        try {
            // TODO compress using compression library
            return Utility.addCheckSum(result.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<String> getConfirmationRequiredTexts(Event event) {
        // TODO what is the confirmation sms text?
        return new ArrayList<>();
    }
}
