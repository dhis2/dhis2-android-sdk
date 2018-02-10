package org.hisp.dhis.android.core.common;

import android.support.annotation.Nullable;
import java.util.Locale;
import java.util.Set;

public abstract class BaseQuery {
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final int DEFAULT_PAGE = 0;
    public static final boolean DEFAULT_IS_PAGING = false;
    public static final String DEFAULT_TRANSLATION_LOCALE = Locale.ENGLISH.toString();
    public static final boolean DEFAULT_IS_TRANSLATION_ON = false;

    @Nullable
    public abstract Set<String> uIds();

    public abstract int page();

    public abstract int pageSize();

    public abstract boolean isPaging();

    public abstract boolean isTranslationOn();

    public abstract String translationLocale();

    protected static abstract class Builder<T extends BaseQuery.Builder> {
        @Nullable
        public abstract T uIds(Set<String> uIds);

        public abstract T page(int page);

        public abstract T pageSize(int pageSize);

        public abstract T isPaging(boolean paging);

        public abstract T isTranslationOn(boolean isTranslationOn);

        public abstract T translationLocale(String translationLocale);
    }
}
