package org.hisp.dhis.android.core.data.database.migrations;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.reactivex.observers.TestObserver;

/**
 * Created by lally on 3/18/17.
 */
@RunWith(AndroidJUnit4.class)
public class SQLBriteMigrationLibraryExamplesShould {
    public static final String migrationDir = "migrations/library_examples";
    private Context context;
    String dbName= "testdb";

    @Before
    public void setup() {
        context = InstrumentationRegistry.getContext();
    }

    @Test
    public void be_able_to_load_and_parse_default_migrations_from_input_stream() throws IOException {
        SQLBriteTestOpenHelper briteOpenHelper = new SQLBriteTestOpenHelper(context, dbName, 1, true, migrationDir);
        Map parsed = briteOpenHelper.parse();

        assertThat(parsed.get("up"), is(not(equalTo(null))));
        assertThat(parsed.get("down"), is(not(equalTo(null))));
        assertThat(parsed.get("seeds"), is(not(equalTo(null))));
    }

    @Test
    public void be_able_to_load_and_parse_specific_migrations_from_input_stream() throws IOException {
        SQLBriteTestOpenHelper briteOpenHelper = new SQLBriteTestOpenHelper(context, dbName, 1, true, migrationDir);
        Map parsed = briteOpenHelper.parse(2);

        assertThat(parsed.get("up"), is(not(equalTo(null))));
        assertThat(parsed.get("down"), is(not(equalTo(null))));
        assertThat(parsed.get("seeds"), is(not(equalTo(null))));
    }

    @Test
    public void be_able_toLoadAndParseAllMigrationsFromInputStream() throws IOException {
        SQLBriteTestOpenHelper briteOpenHelper = new SQLBriteTestOpenHelper(context, dbName, 1, true, migrationDir);
        List<Map<String, List<String>>> parsed = briteOpenHelper.parse(1, 2, true);

        assertThat(parsed, is(not(equalTo(null))));
        assertThat(parsed.size(), is((equalTo(1))));
    }

    @Test
    public void be_able_to_load_and_parse_all_migrations_from_input_streamV2() throws IOException {
        SQLBriteTestOpenHelper briteOpenHelper = new SQLBriteTestOpenHelper(context, dbName, 1, true, migrationDir);
        List<Map<String, List<String>>> parsed = briteOpenHelper.parse(1, 3, true);

        assertThat(parsed, is(not(equalTo(null))));
        assertThat(parsed.size(), is((equalTo(2))));
    }

    @Test
    public void be_able_to_get_SQLBriteDatabase() {
        BriteDatabase database = SQLBriteTestOpenHelper.get(context, dbName, 1, true, migrationDir);
        assertThat(database, is(notNullValue()));
    }

    @Test
    public void be_able_to_create_and_seed_initial_database() {
        BriteDatabase database = SQLBriteTestOpenHelper.get(context, dbName, 1, true, migrationDir);
        TestObserver<Brite> observer = new TestObserver<>();

        database.createQuery("brites", " SELECT * FROM brites LIMIT 1")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(observer);

        //asserts
        observer.assertNoErrors();
        observer.assertValueCount(1);
    }

    @Test
    public void be_able_to_create_and_seed_initial_databaseV2() {
        BriteDatabase database = SQLBriteTestOpenHelper.get(context, dbName, 3, true, migrationDir);
        TestObserver<Brite> observer = new TestObserver<>();

        database.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 3.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(observer);

        //asserts
        observer.assertNoErrors();
        observer.assertValueCount(1);
        observer.assertValue(new Brite("Test Debug 3.2"));
    }

    @Test
    public void be_able_to_upgrade_and_seed_database() {
        BriteDatabase database1 = SQLBriteTestOpenHelper.get(context, dbName, 1, true, migrationDir);
        BriteDatabase database2 = SQLBriteTestOpenHelper.get(context, dbName, 2, true, migrationDir);

        TestObserver<Brite> observer1 = new TestObserver<>();

        database1.createQuery("brites", " SELECT * FROM brites LIMIT 1")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(observer1);

        //asserts
        observer1.assertNoErrors();
        observer1.assertValueCount(1);

        TestObserver<Brite> observer2 = new TestObserver<>();

        database2.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 2.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(observer2);

        //asserts
        observer2.assertNoErrors();
        observer2.assertValueCount(1);
    }

    @Test
    public void be_able_to_upgrade_and_seed_databaseV2() {
        BriteDatabase database1 = SQLBriteTestOpenHelper.get(context, dbName, 1, true, migrationDir);
        BriteDatabase database2 = SQLBriteTestOpenHelper.get(context, dbName, 3, true, migrationDir);

        TestObserver<Brite> observer1 = new TestObserver<>();

        database1.createQuery("brites", " SELECT * FROM brites LIMIT 1")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(observer1);

        //asserts
        observer1.assertNoErrors();
        observer1.assertValueCount(1);

        TestObserver<Brite> observer2 = new TestObserver<>();

        database2.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 3.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(observer2);

        //asserts
        observer2.assertNoErrors();
        observer2.assertValueCount(1);
    }

    @Test
    public void be_able_to_downgrade_database() {
        BriteDatabase database1 = SQLBriteTestOpenHelper.get(context, dbName, 1, true, migrationDir);
        BriteDatabase database2 = SQLBriteTestOpenHelper.get(context, dbName, 2, true, migrationDir);

        TestObserver<Brite> observer1 = new TestObserver<>();

        database1.createQuery("brites", " SELECT * FROM brites LIMIT 1")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(observer1);

        //asserts
        observer1.assertNoErrors();
        observer1.assertValueCount(1);

        TestObserver<Brite> observer2 = new TestObserver<>();

        database2.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 2.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(observer2);

        //asserts
        observer2.assertNoErrors();
        observer2.assertValueCount(1);

        //downgrade
        BriteDatabase database3 = SQLBriteTestOpenHelper.get(context, dbName, 1, true, migrationDir);

        TestObserver<Brite> observer3 = new TestObserver<>();

        database3.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 2.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(observer3);

        //asserts
        observer3.assertNoErrors();
        observer3.assertValueCount(0);

    }

    @Test
    public void be_able_to_downgrade_databaseV2() {
        BriteDatabase database1 = SQLBriteTestOpenHelper.get(context, dbName, 1, true, migrationDir);
        BriteDatabase database2 = SQLBriteTestOpenHelper.get(context, dbName, 3, true, migrationDir);

        TestObserver<Brite> observer1 = new TestObserver<>();

        database1.createQuery("brites", " SELECT * FROM brites LIMIT 1")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(observer1);

        //asserts
        observer1.assertNoErrors();
        observer1.assertValueCount(1);

        TestObserver<Brite> observer2 = new TestObserver<>();

        database2.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 2.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(observer2);

        //asserts
        observer2.assertNoErrors();
        observer2.assertValueCount(1);

        //downgrade
        BriteDatabase database3 = SQLBriteTestOpenHelper.get(context, dbName, 1, true,
                migrationDir);

        TestObserver<Brite> observer3 = new TestObserver<>();

        database3.createQuery("brites", " SELECT * FROM brites WHERE name = 'Test Debug 2.2'")
                .lift(SqlBrite.Query.mapToOne(Brite.MAPPER))
                .subscribe(observer3);

        //asserts
        observer3.assertNoErrors();
        observer3.assertValueCount(0);

    }
}
