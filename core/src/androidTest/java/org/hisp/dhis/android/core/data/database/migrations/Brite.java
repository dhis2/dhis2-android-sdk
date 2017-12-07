package org.hisp.dhis.android.core.data.database.migrations;

import android.database.Cursor;
import io.reactivex.functions.Function;

public class Brite {

    private String name;

    public Brite(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Brite brite = (Brite) o;

        return name != null ? name.equals(brite.name) : brite.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    static final Function<Cursor, Brite> MAPPER = new Function<Cursor, Brite>() {
        @Override
        public Brite apply(Cursor cursor) {
            return new Brite(cursor.getString(cursor.getColumnIndexOrThrow("name"))
            );
        }
    };
}
