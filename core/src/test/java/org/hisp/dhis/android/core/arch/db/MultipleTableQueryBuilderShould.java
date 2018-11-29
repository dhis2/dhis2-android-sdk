package org.hisp.dhis.android.core.arch.db;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class MultipleTableQueryBuilderShould {

    @Test
    public void build_query_with_one_table() {
        List<String> tableNames = Collections.singletonList("table1");
        String query = new MultipleTableQueryBuilder().generateQuery("columnName", tableNames).build();

        assertThat(query).isEqualTo("SELECT columnName FROM table1 WHERE columnName IS NOT NULL;");
    }

    @Test
    public void build_query_with_more_than_one_table() {
        List<String> tableNames = Arrays.asList("table1", "table2");

        String query = new MultipleTableQueryBuilder()
                .generateQuery("columnName", tableNames)
                .build();

        assertThat(query).isEqualTo("SELECT columnName FROM table1 WHERE columnName IS NOT NULL UNION " +
                "SELECT columnName FROM table2 WHERE columnName IS NOT NULL;");
    }

    @Test(expected = RuntimeException.class)
    public void throw_exception_for_no_pairs() {
        MultipleTableQueryBuilder builder = new MultipleTableQueryBuilder();
        builder.build();
    }
}