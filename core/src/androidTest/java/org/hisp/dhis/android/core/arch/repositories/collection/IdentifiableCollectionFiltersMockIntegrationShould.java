package org.hisp.dhis.android.core.arch.repositories.collection;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeItem;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class IdentifiableCollectionFiltersMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void append_scope_for_multiple_filters() {
        ReadOnlyIdentifiableCollectionRepository<CategoryCombo> repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byName().isEqualTo("Combi")
                .byCode().like("cody");
        List<RepositoryScopeItem> scope = repositoryWithUpdatedScope.getScope();

        assertThat(scope.get(0).equals(RepositoryScopeItem.builder().key("name").operator("eq").value("Combi").build()), is(true));
        assertThat(scope.get(1).equals(RepositoryScopeItem.builder().key("code").operator("like").value("cody").build()), is(true));
    }

    @Test
    public void find_objects_with_equal_name() {
        ReadOnlyIdentifiableCollectionRepository<CategoryCombo> repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byName().isEqualTo("Births");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is("m2jTvAj5kkm"));
    }

    @Test
    public void find_objects_with_equal_code() {
        ReadOnlyIdentifiableCollectionRepository<CategoryCombo> repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byCode().isEqualTo("BIRTHS");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is("m2jTvAj5kkm"));
    }

    @Test
    public void find_objects_with_equal_uid() {
        ReadOnlyIdentifiableCollectionRepository<CategoryCombo> repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byUid().isEqualTo("m2jTvAj5kkm");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is("m2jTvAj5kkm"));
    }

    @Test
    public void find_objects_with_equal_display_name() {
        ReadOnlyIdentifiableCollectionRepository<CategoryCombo> repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byDisplayName().isEqualTo("Births Display");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is("m2jTvAj5kkm"));
    }

    @Test
    public void do_not_find_objects_with_wrong_equal_name() {
        ReadOnlyIdentifiableCollectionRepository<CategoryCombo> repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byName().isEqualTo("Deaths");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.isEmpty(), is(true));
    }

    @Test
    public void do_not_find_objects_with_wrong_equal_code() {
        ReadOnlyIdentifiableCollectionRepository<CategoryCombo> repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byCode().isEqualTo("DEATHS");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.isEmpty(), is(true));
    }

    @Test
    public void find_objects_with_like_name() {
        ReadOnlyIdentifiableCollectionRepository<CategoryCombo> repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byName().like("%bi%");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is("m2jTvAj5kkm"));
    }

    @Test
    public void find_objects_with_like_code() {
        ReadOnlyIdentifiableCollectionRepository<CategoryCombo> repositoryWithUpdatedScope = d2.categoryModule().categoryCombos
                .byCode().like("%bi%");
        List<CategoryCombo> combos = repositoryWithUpdatedScope.get();
        assertThat(combos.size(), is(1));
        assertThat(combos.get(0).uid(), is("m2jTvAj5kkm"));
    }
}