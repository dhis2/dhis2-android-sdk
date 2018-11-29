package org.hisp.dhis.android.core.arch.db;

import org.assertj.core.util.Lists;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class WhereClauseBuilderShould {

    @Test
    public void build_where_statement_for_one_key_value_pair_with_string_value() {
        WhereClauseBuilder builder = new WhereClauseBuilder();
        String whereStatement = builder
                .appendKeyStringValue("COL", "VAL")
                .build();
        assertThat(whereStatement).isEqualTo("COL = 'VAL'");
    }

    @Test
    public void build_where_statement_for_one_key_value_pair_with_int_value() {
        WhereClauseBuilder builder = new WhereClauseBuilder();
        String whereStatement = builder
                .appendKeyNumberValue("COL", 2)
                .build();
        assertThat(whereStatement).isEqualTo("COL = 2");
    }

    @Test
    public void build_where_statement_for_one_key_value_pair_with_like_string_value() {
        WhereClauseBuilder builder = new WhereClauseBuilder();
        String whereStatement = builder
                .appendKeyLikeStringValue("COL", "VAL")
                .build();
        assertThat(whereStatement).isEqualTo("COL LIKE 'VAL'");
    }

    @Test
    public void build_where_statement_for_two_key_value_pairs() {
        WhereClauseBuilder builder = new WhereClauseBuilder();
        String whereStatement = builder
                .appendKeyStringValue("COL1", "VAL1")
                .appendKeyStringValue("COL2", "VAL2")
                .build();
        assertThat(whereStatement).isEqualTo("COL1 = 'VAL1' AND COL2 = 'VAL2'");
    }

    @Test
    public void build_where_statement_for_not_in_key_values() {
        WhereClauseBuilder builder = new WhereClauseBuilder();
        String whereStatement = builder
                .appendNotInKeyStringValues("COL1", Lists.newArrayList("VAL1", "VAL2"))
                .build();
        assertThat(whereStatement).isEqualTo("COL1 NOT IN ('VAL1', 'VAL2')");
    }

    @Test
    public void build_where_statement_for_in_key_values() {
        WhereClauseBuilder builder = new WhereClauseBuilder();
        String whereStatement = builder
                .appendInKeyStringValues("COL1", Lists.newArrayList("VAL1", "VAL2"))
                .build();
        assertThat(whereStatement).isEqualTo("COL1 IN ('VAL1', 'VAL2')");
    }

    @Test(expected = RuntimeException.class)
    public void throw_exception_for_no_pairs() {
        WhereClauseBuilder builder = new WhereClauseBuilder();
        builder.build();
    }
}