package org.hisp.dhis.android.core.data.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.lykmapipo.sqlbrite.migrations.SQLBriteOpenHelper;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.schedulers.Schedulers;

/**
 * A helper class to manage database migrations and seeding using
 * an application's raw asset files.
 * <p>
 * This class provides developers with a simple way to ship their Android app
 * with migrations files which manage database creation and any upgrades required with subsequent
 * version releases.
 * <p>
 * <p>For examples see <a href="https://github.com/lykmapipo/sqlbrite-migrations">
 * https://github.com/lykmapipo/sqlbrite-migrations</a>
 * <p>
 * <p class="note"><strong>Note:</strong> this class assumes
 * monotonically increasing version numbers for upgrades.</p>
 */
public class CustomSQLBriteOpenHelper extends SQLBriteOpenHelper {

    private String migrationTestDir = "migrations";
    final private Context context;
    private final int version;
    //current database version to support seed up to requested database version
    private boolean isOnTestMode;
    private static BriteDatabase briteDatabase;

    //The @link and the @see are commented because are generating bugs with travis

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of
     */
//    {@link com.squareup.sqlbrite2.BriteDatabase#getWritableDatabase} or
//     * {@link com.squareup.sqlbrite2.BriteDatabase#getReadableDatabase} is called.
//     *
//     * @see com.github.lykmapipo.sqlbrite.migrations.SQLBriteOpenHelper
//     */
    public CustomSQLBriteOpenHelper(Context context, String name,
            SQLiteDatabase.CursorFactory factory, int version, String migrationTestDir) {
        super(context, name, factory, version);
        this.version = version;
        this.context = context;
        this.migrationTestDir = migrationTestDir;
    }


    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of
     */
//    {@link com.squareup.sqlbrite2.BriteDatabase#getWritableDatabase} or
//     * {@link com.squareup.sqlbrite2.BriteDatabase#getReadableDatabase} is called.
//     *
//     * @see com.github.lykmapipo.sqlbrite.migrations.SQLBriteOpenHelper
//     */
    public CustomSQLBriteOpenHelper(Context context, String name,
            SQLiteDatabase.CursorFactory factory,
            int version, String migrationTestDir, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        this.version = version;
        this.context = context;
        this.migrationTestDir = migrationTestDir;
    }


    /**
     * Create a helper object to create, open, and/or manage a testing database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of
     */
//     * {@link com.squareup.sqlbrite2.BriteDatabase#getWritableDatabase} or
//     * {@link com.squareup.sqlbrite2.BriteDatabase#getReadableDatabase} is called.
//     *
//     * @see com.github.lykmapipo.sqlbrite.migrations.SQLBriteOpenHelper

    public CustomSQLBriteOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        this.context = context;
        this.version = version;
        this.isOnTestMode = false;
    }

    /**
     * Create a helper object to create, open, and/or manage a testing database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of
     */
//    {@link com.squareup.sqlbrite2.BriteDatabase#getWritableDatabase} or
//     * {@link com.squareup.sqlbrite2.BriteDatabase#getReadableDatabase} is called.
//     *
//     * @see com.github.lykmapipo.sqlbrite.migrations.SQLBriteOpenHelper
//     */
    public CustomSQLBriteOpenHelper(Context context, String name, int version, boolean testing,
            String migrationTestDir) {
        super(context, name, null, version);
        this.context = context;
        this.version = version;
        this.isOnTestMode = testing;
        this.migrationTestDir = migrationTestDir;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            int version = db.getVersion();
            if(version == this.version || version == 0){
                return;
            }
            //run all migrations from begin to current requested database version
            version = version > this.version ? version
                    : this.version; //ensure we start at requested database version
            List<Map<String, List<String>>> parsed = parse(0, version, true);
            up(db, parsed);
        } catch (IOException e) {
            Log.e("Database Error:", e.getMessage());
        }
    }

    /**
     * Upgrade database to latest new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            //load all migrations from old version to current version
            List<Map<String, List<String>>> parsed = parse(oldVersion, newVersion, true);

            //upgrade database
            up(db, parsed);
        } catch (IOException e) {
            Log.e("Database Error:", e.getMessage());
        }
    }

    //{@link com.squareup.sqlbrite2.BriteDatabase}
    /**
     * Create a  instance.N
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of
     */
//    {@link com.squareup.sqlbrite2.BriteDatabase#getWritableDatabase} or
//     * {@link com.squareup.sqlbrite2.BriteDatabase#getReadableDatabase} is called.
//     *
//     * @see com.github.lykmapipo.sqlbrite.migrations.SQLBriteOpenHelper
//     * @see com.squareup.sqlbrite2.BriteDatabase
//     */

    @SuppressWarnings("PMD")
    public synchronized static BriteDatabase get(Context context, String name, int version) {
        if (briteDatabase == null) {
            SQLBriteOpenHelper sqlBriteOpenHelper = new SQLBriteOpenHelper(context, name, null, version);
            SqlBrite sqlBrite = new SqlBrite.Builder().build();
            briteDatabase = sqlBrite.wrapDatabaseHelper(sqlBriteOpenHelper, Schedulers.io());
        }
        return briteDatabase;
    }


    /**
     * Create a
     */
//    {@link com.squareup.sqlbrite2.BriteDatabase}
    /** instance.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of
     */
//    {@link com.squareup.sqlbrite2.BriteDatabase#getWritableDatabase} or
//     * {@link com.squareup.sqlbrite2.BriteDatabase#getReadableDatabase} is called.
//     *
//     * @see com.github.lykmapipo.sqlbrite.migrations.SQLBriteOpenHelper
//     * @see com.squareup.sqlbrite2.BriteDatabase
//     */

    @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
    public synchronized static BriteDatabase get(Context context, String name,
            SQLiteDatabase.CursorFactory factory, int version, String migrationDir) {
        if (briteDatabase == null) {
            CustomSQLBriteOpenHelper sqlBriteOpenHelper = new CustomSQLBriteOpenHelper(context, name, factory,
                    version, migrationDir);
            SqlBrite sqlBrite = new SqlBrite.Builder().build();
            briteDatabase = sqlBrite.wrapDatabaseHelper(sqlBriteOpenHelper, Schedulers.io());
        }
        return briteDatabase;
    }


    /**
     * Create a
     */
//    {@link com.squareup.sqlbrite2.BriteDatabase}
     /** instance.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of
     */
//     {@link com.squareup.sqlbrite2.BriteDatabase#getWritableDatabase} or
//     * {@link com.squareup.sqlbrite2.BriteDatabase#getReadableDatabase} is called.
//     *
//     * @see com.github.lykmapipo.sqlbrite.migrations.SQLBriteOpenHelper
//     * @see com.squareup.sqlbrite2.BriteDatabase
//     */

    @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
    public synchronized static BriteDatabase get(Context context, String name,
            SQLiteDatabase.CursorFactory factory, int version, String migrationDir,
            DatabaseErrorHandler errorHandler) {
        if (briteDatabase == null) {
            CustomSQLBriteOpenHelper sqlBriteOpenHelper = new CustomSQLBriteOpenHelper(context, name, factory,
                    version, migrationDir, errorHandler);
            SqlBrite sqlBrite = new SqlBrite.Builder().build();
            briteDatabase = sqlBrite.wrapDatabaseHelper(sqlBriteOpenHelper, Schedulers.io());
        }
        return briteDatabase;
    }

//    /**
//     * Create a {@link com.squareup.sqlbrite2.BriteDatabase} instance for testing.
     /** This method always returns very quickly.  The database is not actually
     * created or opened until one of
     */
//     * {@link com.squareup.sqlbrite2.BriteDatabase#getWritableDatabase} or
//     * {@link com.squareup.sqlbrite2.BriteDatabase#getReadableDatabase} is called.
//     *
//     * @see com.github.lykmapipo.sqlbrite.migrations.SQLBriteOpenHelper
//     * @see com.squareup.sqlbrite2.BriteDatabase
//     */

    @SuppressWarnings("PMD.AvoidSynchronizedAtMethodLevel")
    public synchronized static BriteDatabase get(Context context, String name, int version, boolean testing,
            String migrationTestDir) {
        //always return new instance on test mode
        CustomSQLBriteOpenHelper sqlBriteOpenHelper = new CustomSQLBriteOpenHelper(context, name, version,
                testing, migrationTestDir);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        briteDatabase = sqlBrite.wrapDatabaseHelper(sqlBriteOpenHelper, Schedulers.trampoline());
        return briteDatabase;
    }

    /**
     * Downgrade database to previous old version
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            //load all migrations from old version to current version
            List<Map<String, List<String>>> parsed = parse(oldVersion, newVersion, false);

            //upgrade database
            down(db, parsed);
        } catch (IOException e) {
            Log.e("Database Error:", e.getMessage());
        }
    }


    /**
     * Load and parse all required database migrations.
     */
    public List<Map<String, List<String>>> parse(int oldVersion, int newVersion, boolean up)
            throws IOException {
        synchronized (this) {
            //collect all require migrations
            List<Map<String, List<String>>> scripts = new ArrayList<Map<String, List<String>>>();

            //iterate over all required migrations
            if (up) {
                //parse up migrations
                int startVersion = oldVersion + 1;
                for (int i = startVersion; i <= newVersion; i++) {
                    Map<String, List<String>> script = this.parse(i);
                    scripts.add(script);
                }
            } else {
                //parse down migrations
                for (int i = oldVersion; i > newVersion; i--) {
                    Map<String, List<String>> script = this.parse(i);
                    scripts.add(script);
                }
            }

            return scripts;
        }
    }


    /**
     * Load and parse initial database migrations.
     * <p>
     * It used it creating initial database
     * </p>
     */
    public Map<String, List<String>> parse() throws IOException {
        synchronized (this) {
            return this.parse(1);
        }
    }


    /**
     * Parse migration file
     * <p>
     * It used to upgrade database to newer version
     * </p>
     *
     * @param newVersion newer database version
     */
    public Map<String, List<String>> parse(int newVersion) throws IOException {
        synchronized (this) {
            //obtain migration path
            InputStream inputStream = null;
            String migrationPath = migrationTestDir + "/" + newVersion + ".yaml";
            //handle test mode
            if (isOnTestMode) {
                inputStream = this.getClass().getClassLoader().getResourceAsStream(migrationPath);
            } else {
                inputStream = this.context.getAssets().open(migrationPath);
            }
            //parse migration content
            Yaml yaml = new Yaml();
            Map<String, List<String>> parsed = (Map) yaml.load(inputStream);

            return parsed;
        }
    }


    /**
     * Apply up migration scripts and seed database with provided seeds
     */
    private void up(SQLiteDatabase database, Map<String, List<String>> scripts) {
        synchronized (this) {
            database.beginTransaction();
            try {
                //obtain up migrations
                List<String> ups = scripts.get("up");

                //apply up migrations
                if (ups != null) {
                    for (String script : ups) {
                        database.execSQL(script);
                    }
                }

                //obtain seeds
                List<String> seeds = scripts.get("seeds");

                //apply seed migrations
                if (seeds != null) {
                    for (String script : seeds) {
                        database.execSQL(script);
                    }
                }

                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    }

    /**
     * Apply up migration scripts and seed database with provided seeds
     */
    private void up(SQLiteDatabase database, List<Map<String, List<String>>> scripts) {
        synchronized (this) {
            //ensure we apply or fail as whole
            database.beginTransaction();
            try {
                //start apply all migrations
                for (Map<String, List<String>> script : scripts) {
                    up(database, script);
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    }


    /**
     * Apply down migration scripts
     */
    private void down(SQLiteDatabase database, Map<String, List<String>> scripts) {
        synchronized (this) {
            database.beginTransaction();
            try {
                //obtain down migrations
                List<String> downs = scripts.get("down");

                //apply down migrations
                if (downs != null) {
                    for (String script : downs) {
                        database.execSQL(script);
                    }
                }

                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    }

    /**
     * Apply down migration scripts
     */
    private void down(SQLiteDatabase database, List<Map<String, List<String>>> scripts) {
        synchronized (this) {
            //ensure we apply or fail as whole
            database.beginTransaction();
            try {
                //start apply all migrations
                for (Map<String, List<String>> script : scripts) {
                    down(database, script);
                }
                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
            }
        }
    }
}
