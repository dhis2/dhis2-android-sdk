package org.hisp.dhis.client.sdk.core.commons;

import android.content.ContentResolver;
import android.database.Cursor;

import org.hisp.dhis.client.sdk.core.commons.DbContract.StateColumn;
import org.hisp.dhis.client.sdk.models.common.DataModel;
import org.hisp.dhis.client.sdk.models.common.Model;
import org.hisp.dhis.client.sdk.models.common.State;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public abstract class AbsDataStore<ModelType extends DataModel & Model> extends AbsStore<ModelType> implements DataStore<ModelType> {

    public AbsDataStore(ContentResolver contentResolver, Mapper<ModelType> mapper) {
        super(contentResolver, mapper);
    }

    @Override
    public List<ModelType> query(State state) {
        return query(Collections.singletonList(state));
    }

    @Override
    public List<ModelType> query(List<State> states) {
        isNull(states, "State list must not be null");

        if (states.isEmpty()) {
            throw new IllegalArgumentException("States must not be empty");
        }

        String[] selectionArgs = new String[states.size()];
        for (int i = 0; i < states.size(); i++) {
            selectionArgs[i] = states.get(i).toString();
        }

        StringBuilder stringBuilder = new StringBuilder();

        Iterator iterator = states.iterator();
        do {
            stringBuilder.append(StateColumn.COLUMN_STATE);
            stringBuilder.append(" = ?");
            if (iterator.hasNext()) {
                stringBuilder.append(" OR ");
            }
        } while (iterator.hasNext());

        Cursor cursor = contentResolver.query(mapper.getContentUri(), mapper.getProjection(),
                stringBuilder.toString(), selectionArgs, null);

        return toModels(cursor);
    }
}