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

package org.hisp.dhis.android.core.arch.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UtilsShould {


    @Test
    public void set_partition_returns_empty_array() {
        Set<String> list = new HashSet<>();

        List<Set<String>> partition = CollectionsHelper.setPartition(list, 10);
        assertThat(partition).isEmpty();
    }

    @Test
    public void set_partition_return_single_list() {
        Set<String> list = new HashSet<>();
        list.add("first");
        list.add("second");

        List<Set<String>> partition = CollectionsHelper.setPartition(list, 10);
        assertThat(partition.size()).isEqualTo(1);
        assertThat(partition.get(0).size()).isEqualTo(2);
        assertThat(containsElementsInList(partition, list)).isTrue();
    }

    @Test
    public void set_partition_splits_list() {
        Set<String> list = new HashSet<>();
        for(int i = 0; i < 20; i++) {
            list.add("element" + i);
        }

        List<Set<String>> partition = CollectionsHelper.setPartition(list, 10);
        assertThat(partition.size()).isEqualTo(2);
        assertThat(partition.get(0).size()).isEqualTo(10);
        assertThat(partition.get(1).size()).isEqualTo(10);
        assertThat(containsElementsInList(partition, list)).isTrue();
    }


    // Auxiliary methods

    private <T> Boolean containsElementsInList(List<Set<T>> partition, Set<T> list) {
        Set<T> flattenPartitions = new HashSet<>();
        for(Set<T> item : partition) {
            flattenPartitions.addAll(item);
        }

        return flattenPartitions.containsAll(list);
    }
}