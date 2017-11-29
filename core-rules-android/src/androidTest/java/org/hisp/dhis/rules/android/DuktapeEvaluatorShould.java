package org.hisp.dhis.rules.android;

import android.support.test.runner.AndroidJUnit4;

import com.squareup.duktape.Duktape;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class DuktapeEvaluatorShould {
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
    public void return_expected_values() throws Exception {
        assertThat(duktapeEvaluator.evaluate("2 + 2")).isEqualTo("4.0");
        assertThat(duktapeEvaluator.evaluate("true")).isEqualTo("true");
        assertThat(duktapeEvaluator.evaluate("\'test_string\'")).isEqualTo("test_string");
    }
}
