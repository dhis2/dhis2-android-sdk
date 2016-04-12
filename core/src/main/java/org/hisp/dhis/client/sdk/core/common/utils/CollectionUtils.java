/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.core.common.utils;

import org.hisp.dhis.client.sdk.utils.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CollectionUtils {

    private CollectionUtils() {
        // no instances
    }

    public static String join(List<String> strings, String delimiter) {
        Preconditions.isNull(delimiter, "String delimiter must not be null");

        StringBuilder buffer = new StringBuilder();

        if (strings != null) {
            Iterator<? extends String> iterator = strings.iterator();

            if (iterator.hasNext()) {
                buffer.append(iterator.next());

                while (iterator.hasNext()) {
                    buffer.append(delimiter).append(iterator.next());
                }
            }
        }

        return buffer.toString();
    }

    public static List<List<String>> slice(List<String> stringList, int subListSize) {
        List<List<String>> listOfSubLists = new ArrayList<>();

        if (stringList != null) {
            int leftBoundary = 0;
            int rightBoundary = subListSize < stringList.size() ? subListSize : stringList.size();

            do {
                listOfSubLists.add(stringList.subList(leftBoundary, rightBoundary));

                leftBoundary = rightBoundary;
                rightBoundary = rightBoundary + subListSize < stringList.size() ?
                        rightBoundary + subListSize : stringList.size();
            } while (leftBoundary != rightBoundary);

            return listOfSubLists;
        }

        return listOfSubLists;
    }

    public static <T> Collection<T> isEmpty(Collection<T> collection, String messgae) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(messgae);
        }

        return collection;
    }
}
