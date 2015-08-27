package org.hisp.dhis.android.sdk.persistence.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.joda.time.DateTime;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

public final class DateTimeManager {
    private static final String PREFERENCES = "preferences:lastUpdated";
    private static final String METADATA_UPDATE_DATETIME = "key:metaDataUpdateDateTime";

    private static DateTimeManager mPreferences;
    private final SharedPreferences mPrefs;

    private DateTimeManager(Context context) {
        isNull(context, "Context object must not be null");
        mPrefs = context.getSharedPreferences(PREFERENCES,
                Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        mPreferences = new DateTimeManager(context);
    }

    public static DateTimeManager getInstance() {
        if (mPreferences == null) {
            throw new IllegalArgumentException("You have to call init() method first");
        }

        return mPreferences;
    }

    public void setLastUpdated(ResourceType type, DateTime dateTime) {
        setLastUpdated(type, null, dateTime);
    }

    public void setLastUpdated(ResourceType type, String salt, DateTime dateTime) {
        isNull(type, "ResourceType object must not be null");
        isNull(dateTime, "DateTime object must not be null");

        String identifier = METADATA_UPDATE_DATETIME + type.toString();
        if( salt != null ) {
            identifier += salt;
        }
        putString(identifier, dateTime.toString());
    }



    public DateTime getLastUpdated(ResourceType type) {
        return getLastUpdated(type, null);
    }

    /**
     *
     * @param type
     * @param salt add some extra salt for a specific resource. For example an UID
     * @return
     */
    public DateTime getLastUpdated(ResourceType type, String salt) {
        String identifier = METADATA_UPDATE_DATETIME + type.toString();
        if( salt != null ) {
            identifier += salt;
        }
        String dateTimeString = getString(identifier);
        if (dateTimeString != null) {
            return DateTime.parse(dateTimeString);
        }
        return null;
    }

    /**
     * Removes all key-value pairs.
     */
    public void delete() {
        mPrefs.edit().clear().commit();
    }

    public void deleteLastUpdated(ResourceType type) {
        deleteLastUpdated(type, null);
    }

    public void deleteLastUpdated(ResourceType type, String salt) {
        String identifier = METADATA_UPDATE_DATETIME + type.toString();
        if( salt != null ) {
            identifier += salt;
        }
        deleteString(identifier);
    }

    public boolean isLastUpdatedSet(ResourceType type) {
        return getLastUpdated(type) != null;
    }

    private void putString(String key, String value) {
        mPrefs.edit().putString(key, value).commit();
    }

    private String getString(String key) {
        return mPrefs.getString(key, null);
    }

    private void deleteString(String key) {
        mPrefs.edit().remove(key).commit();
    }

}