package org.hisp.dhis.rules.android;

import android.support.test.runner.AndroidJUnit4;

import com.squareup.duktape.Duktape;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DuktapeEvaluatorTests {
    private Duktape duktape;
    private DuktapeEvaluator duktapeEvaluator;

    @Before
    public void setUp() throws Exception {
        duktape = Duktape.create();
        duktapeEvaluator = new DuktapeEvaluator(duktape);
    }

    @After
    public void tearDown() throws Exception {
        duktape.close();
    }

    @Test
    public void checkReturnValue() throws Exception {
        assertThat(duktapeEvaluator.evaluate("2 + 2")).isEqualTo("4.0");
    }
}
