/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.testapp.option;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.option.Option;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class OptionCollectionRepositoryMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void find_all() {
        List<Option> options = d2.optionModule().options().blockingGet();
        assertThat(options.size()).isEqualTo(3);
    }

    @Test
    public void filter_by_sort_order() {
        List<Option> options = d2.optionModule().options()
                .bySortOrder().eq(2).blockingGet();
        assertThat(options.size()).isEqualTo(1);
        assertThat(options.get(0).uid()).isEqualTo("egT1YqFWsVk");
    }

    @Test
    public void filter_by_option_set_uid() {
        List<Option> options = d2.optionModule().options()
                .byOptionSetUid().eq("VQ2lai3OfVG").blockingGet();
        assertThat(options.size()).isEqualTo(2);
    }

    @Test
    public void filter_by_field_color() {
        List<Option> options = d2.optionModule().options()
                .byColor().eq("#13f2dd")
                .blockingGet();
        assertThat(options.size()).isEqualTo(1);
    }

    @Test
    public void filter_by_field_icon() {
        List<Option> options = d2.optionModule().options()
                .byIcon().eq("woman_negative")
                .blockingGet();
        assertThat(options.size()).isEqualTo(1);
    }

    @Test
    public void order_by_sort_order() {
        List<Option> options = d2.optionModule().options()
                .byOptionSetUid().eq("VQ2lai3OfVG")
                .orderBySortOrder(RepositoryScope.OrderByDirection.DESC)
                .blockingGet();
        assertThat(options.get(0).sortOrder()).isEqualTo(2);
        assertThat(options.get(1).sortOrder()).isEqualTo(1);
    }
}