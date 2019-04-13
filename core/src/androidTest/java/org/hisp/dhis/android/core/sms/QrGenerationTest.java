package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.configuration.Configuration;
import org.hisp.dhis.android.core.data.api.BasicAuthenticatorFactory;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.DbOpenHelper;
import org.hisp.dhis.android.core.data.database.SqLiteDatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.sms.domain.interactor.QrCodeCase;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static org.junit.Assert.fail;

//@RunWith(AndroidJUnit4.class)
public class QrGenerationTest {
    private static String URL = "";
    private static String LOGIN = "";
    private static String PASS = "";
    private static String TEI_ID = "";
    private static String ENROLLMENT_ID = "";
    private static String ORG_UNIT = "";
    private static String PROGRAM = "";
    private static String TRACKED_ENTITY_TYPE = "";
    private static String GATEWAY_NUMBER = "";
    private static String[][] VALUES = {
            {"", ""},
            {"", ""}
    };

    // uncomment to be able to run
    //@Test
    public void convertEnrollmentTest() {
        CountDownLatch latch = new CountDownLatch(1);
        CompositeDisposable disposables = new CompositeDisposable();
        D2 d2 = initD2();
        QrCodeCase qrCase = d2.smsModule().qrCodeCase();
        disposables.add(Completable.fromAction(() ->
                d2.userModule().logIn(LOGIN, PASS).call()
        ).doOnError(error ->
                System.out.println("Login failed")
        ).onErrorComplete((error) -> {
            // when authenticated before, just continue
            return error instanceof D2Error && ((D2Error) error).errorCode() == D2ErrorCode.ALREADY_AUTHENTICATED;
        }).doOnComplete(() ->
                System.out.println("Logged in")
        ).andThen(
                d2.smsModule().initCase().initSMSModule(GATEWAY_NUMBER, getMetadataConfig())
        ).doOnComplete(() ->
                System.out.println("Initiated")
        ).andThen(
                qrCase.generateTextCode(getTestEnrollment(), TRACKED_ENTITY_TYPE, getTestValues())
        ).subscribeOn(Schedulers.newThread()
        ).observeOn(AndroidSchedulers.mainThread()
        ).subscribeWith(new DisposableSingleObserver<String>() {
            @Override
            public void onSuccess(String result) {
                System.out.println("Result: " + result);
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                latch.countDown();
                e.printStackTrace();
                fail();
            }
        }));

        try {
            latch.await(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        disposables.dispose();
    }

    private D2 initD2() {
        String dbname = "qrtestdb";
        InstrumentationRegistry.getContext().deleteDatabase(dbname);
        DbOpenHelper dbOpenHelper = new DbOpenHelper(InstrumentationRegistry.getContext(), dbname);
        DatabaseAdapter databaseAdapter = new SqLiteDatabaseAdapter(dbOpenHelper);

        Configuration config = Configuration.builder()
                .serverUrl(HttpUrl.parse(URL))
                .build();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new D2.Builder()
                .configuration(config)
                .databaseAdapter(databaseAdapter)
                .okHttpClient(
                        new OkHttpClient.Builder()
                                .addInterceptor(BasicAuthenticatorFactory.create(databaseAdapter))
                                .addInterceptor(loggingInterceptor)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .build()
                )
                .context(InstrumentationRegistry.getContext())
                .build();
    }


    private static Enrollment getTestEnrollment() {
        return Enrollment.builder()
                .uid(ENROLLMENT_ID)
                .created(new Date())
                .lastUpdated(new Date())
                .organisationUnit(ORG_UNIT)
                .program(PROGRAM)
                .enrollmentDate(new Date())
                .trackedEntityInstance(TEI_ID)
                .id(341L).build();
    }


    private static ArrayList<TrackedEntityAttributeValue> getTestValues() {
        ArrayList<TrackedEntityAttributeValue> list = new ArrayList<>();
        for (String[] pair : VALUES) {
            list.add(getTestValue(pair[0], pair[1]));
        }
        return list;
    }

    private static TrackedEntityAttributeValue getTestValue(String attr, String value) {
        return TrackedEntityAttributeValue.builder()
                .value(value)
                .created(new Date())
                .lastUpdated(new Date())
                .trackedEntityAttribute(attr)
                .trackedEntityInstance(TEI_ID)
                .build();
    }

    private WebApiRepository.GetMetadataIdsConfig getMetadataConfig() {
        WebApiRepository.GetMetadataIdsConfig config = new WebApiRepository.GetMetadataIdsConfig();
        config.categoryOptionCombos = true;
        config.dataElements = true;
        config.organisationUnits = true;
        config.programs = true;
        config.trackedEntityAttributes = true;
        config.trackedEntityTypes = true;
        config.users = true;
        return config;
    }
}
