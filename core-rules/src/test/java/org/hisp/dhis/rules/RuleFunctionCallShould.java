package org.hisp.dhis.rules;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;

@RunWith(JUnit4.class)
public class RuleFunctionCallShould {

    @Test
    public void return_function_call_with_single_argument() {
        RuleFunctionCall ruleFunctionCall = RuleFunctionCall.from("d2:floor(19.4)");

        assertThat(ruleFunctionCall.functionCall()).isEqualTo("d2:floor(19.4)");
        assertThat(ruleFunctionCall.functionName()).isEqualTo("d2:floor");
        assertThat(ruleFunctionCall.arguments().size()).isEqualTo(1);
        assertThat(ruleFunctionCall.arguments().get(0)).isEqualTo("19.4");
    }

    @Test
    public void return_function_call_with_multiple_arguments() {
        RuleFunctionCall ruleFunctionCall = RuleFunctionCall.from(
                "d2:some('one', 'two', 'three')");

        assertThat(ruleFunctionCall.functionCall()).isEqualTo("d2:some('one', 'two', 'three')");
        assertThat(ruleFunctionCall.functionName()).isEqualTo("d2:some");
        assertThat(ruleFunctionCall.arguments().size()).isEqualTo(3);
        assertThat(ruleFunctionCall.arguments().get(0)).isEqualTo("'one'");
        assertThat(ruleFunctionCall.arguments().get(1)).isEqualTo("'two'");
        assertThat(ruleFunctionCall.arguments().get(2)).isEqualTo("'three'");
    }

    @Test
    public void return_function_call_with_no_arguments() {
        RuleFunctionCall ruleFunctionCall = RuleFunctionCall.from("d2:some()");

        assertThat(ruleFunctionCall.functionCall()).isEqualTo("d2:some()");
        assertThat(ruleFunctionCall.functionName()).isEqualTo("d2:some");
        assertThat(ruleFunctionCall.arguments().size()).isEqualTo(0);
    }

    @Test
    public void throw_null_pointer_exception_when_null_arguments() {
        try {
            RuleFunctionCall.from(null);
            fail("NullPointerException was expected, but nothing was thrown.");
        } catch (NullPointerException nullPointerException) {
            // noop
        }
    }

    @Test
    public void return_function_call_immutable_arguments() {
        RuleFunctionCall ruleFunctionCall = RuleFunctionCall.from("d2:some()");

        try {
            ruleFunctionCall.arguments().add("another_argument");
            fail("UnsupportedOperationException was expected, but nothing was thrown.");
        } catch (UnsupportedOperationException unsupportedOperationException) {
            // noop
        }
    }
}
