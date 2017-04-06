package org.hisp.dhis.android.rules.models;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public enum ValueType {
    // boolean
    BOOLEAN(Boolean.class),
    TRUE_ONLY(Boolean.class),

    // dates
    DATE(Date.class),
    DATETIME(Date.class),

    // numeric values
    NUMBER(Double.class),
    UNIT_INTERVAL(Double.class),
    PERCENTAGE(Double.class),
    INTEGER(Integer.class),
    INTEGER_POSITIVE(Integer.class),
    INTEGER_NEGATIVE(Integer.class),
    INTEGER_ZERO_OR_POSITIVE(Integer.class),

    // text values
    TIME(String.class),
    TEXT(String.class),
    EMAIL(String.class),
    LETTER(String.class),
    USERNAME(String.class),
    LONG_TEXT(String.class),
    COORDINATE(String.class),
    PHONE_NUMBER(String.class),

    // files
    FILE_RESOURCE(String.class);

    private static final Set<ValueType> INTEGER_TYPES = new HashSet<>(Arrays.asList(INTEGER,
            INTEGER_POSITIVE, INTEGER_NEGATIVE, INTEGER_ZERO_OR_POSITIVE));

    private static final Set<ValueType> NUMERIC_TYPES = new HashSet<>(Arrays.asList(INTEGER, NUMBER,
            INTEGER_POSITIVE, INTEGER_NEGATIVE, INTEGER_ZERO_OR_POSITIVE, UNIT_INTERVAL, PERCENTAGE));

    private static final Set<ValueType> BOOLEAN_TYPES = new HashSet<>(Arrays.asList(BOOLEAN,
            TRUE_ONLY));

    private static final Set<ValueType> TEXT_TYPES = new HashSet<>(Arrays.asList(TEXT, LONG_TEXT,
            LETTER, COORDINATE, TIME, EMAIL, PHONE_NUMBER, USERNAME));

    private static final Set<ValueType> DATE_TYPES = new HashSet<>(Arrays.asList(DATE, DATETIME));

    private final Class<?> javaClass;

    ValueType() {
        this.javaClass = null;
    }

    ValueType(Class<?> javaClass) {
        this.javaClass = javaClass;
    }

    public Class<?> getJavaClass() {
        return javaClass;
    }

    public boolean isInteger() {
        return INTEGER_TYPES.contains(this);
    }

    public boolean isNumeric() {
        return NUMERIC_TYPES.contains(this);
    }

    public boolean isBoolean() {
        return BOOLEAN_TYPES.contains(this);
    }

    public boolean isText() {
        return TEXT_TYPES.contains(this);
    }

    public boolean isDate() {
        return DATE_TYPES.contains(this);
    }

    public boolean isFile() {
        return this == FILE_RESOURCE;
    }

    public boolean isCoordinate() {
        return this == COORDINATE;
    }
}