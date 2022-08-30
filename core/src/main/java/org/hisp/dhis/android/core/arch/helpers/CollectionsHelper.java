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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.common.ObjectWithDeleteInterface;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A collection of utility abstractions
 */
public final class CollectionsHelper {

    private CollectionsHelper() {
        // no instances
    }

    /**
     * A Null-safe safeUnmodifiableList.
     *
     * @param list
     * @return
     */
    @Nullable
    public static <T> List<T> safeUnmodifiableList(@Nullable List<T> list) {
        if (list != null) {
            return Collections.unmodifiableList(list);
        }

        return null;
    }

    /**
     * A Null-safe deleted method.
     *
     * @param object object with deleted() method.
     * @return A boolean with the response of the deleted() method.
     */
    public static boolean isDeleted(@NonNull ObjectWithDeleteInterface object) {
        return object.deleted() != null && object.deleted();
    }

    /**
     * Throws an illegal argument exception if an object is null.
     *
     * @param object Object to validate.
     */
    public static <T> void isNull(T object) {
        if (object == null) {
            throw new IllegalArgumentException("Object must not be null");
        }
    }

    /**
     * Appends objects to an array in a new array.
     *
     * @param first Object array.
     * @param rest Objects to append.
     * @return An new array with the all the objects.
     */
    @SafeVarargs
    public static <T> T[] appendInNewArray(T[] first, T... rest) {
        int totalLength = first.length + rest.length;

        T[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        System.arraycopy(rest, 0, result, offset, rest.length);
        return result;
    }

    /**
     * Build a {@link String} with the values separated by commas and spaces.
     *
     * @param values Array with the values to concatenate.
     * @return A {@link String} with the concatenated values.
     */
    @SuppressWarnings("PMD.UseVarargs")
    public static String commaAndSpaceSeparatedArrayValues(String[] values) {
        String withBrackets = Arrays.toString(values);
        return withBrackets.substring(1, withBrackets.length() - 1);
    }

    /**
     * Build a {@link String} with the values separated by commas and spaces.
     *
     * @param values Collection with the values to concatenate.
     * @return A {@link String} with the concatenated values.
     */
    @SuppressWarnings("PMD.OptimizableToArrayCall")
    public static String commaAndSpaceSeparatedCollectionValues(Collection<String> values) {
        return commaAndSpaceSeparatedArrayValues(values.toArray(new String[0]));
    }

    /**
     * Put in single quotes the string values inside a collection.
     *
     * @param objects Collection with the values to put in single quotes.
     * @return A {@link String[]} with the single quoted values.
     */
    public static String[] withSingleQuotationMarksArray(Collection<String> objects) {
        String[] withSingleQuotationMarksArray = new String[objects.size()];
        int i = 0;
        for (String o: objects) {
            withSingleQuotationMarksArray[i++] = "'" + o + "'";
        }
        return withSingleQuotationMarksArray;
    }

    /**
     * Put in single quotes a value.
     *
     * @param value Value to put in single quotes.
     * @return The single quote {@link String} value.
     */
    public static String withSingleQuotationMarks(String value) {
        return "'" + value + "'";
    }

    /**
     * Build a {@link String} with the values separated by commas.
     *
     * @param values Array with the values to concatenate.
     * @return A {@link String} with the concatenated values.
     */
    private static String commaSeparatedArrayValues(String... values) {
        return commaAndSpaceSeparatedArrayValues(values).replace(", ", ",");
    }

    /**
     * Build a {@link String} with the values separated by commas.
     *
     * @param values Collection with the values to concatenate.
     * @return A {@link String} with the concatenated values.
     */
    public static String commaSeparatedCollectionValues(Collection<String> values) {
        return commaSeparatedArrayValues(values.toArray(new String[values.size()]));
    }

    /**
     * Build a {@link String} with the uids of the objects separated by commas.
     *
     * @param objectsWithUid Collection with the objects to concatenate.
     * @return A {@link String} with the concatenated uids.
     */
    public static <O extends ObjectWithUidInterface> String commaSeparatedUids(Collection<O> objectsWithUid) {
        return commaSeparatedCollectionValues(UidsHelper.getUids(objectsWithUid));
    }

    /**
     * Build a {@link String} with the values separated by semicolons.
     *
     * @param values Collection with the values to concatenate.
     * @return A {@link String} with the concatenated values.
     */
    public static String semicolonSeparatedCollectionValues(Collection<String> values) {
        return joinCollectionWithSeparator(values, ";");
    }

    /**
     * Build a {@link String} with the values separated by custom separators.
     *
     * @param values    Collection with the values to concatenate.
     * @param separator Customizable separator.
     * @return A {@link String} with the concatenated values.
     */
    public static String joinCollectionWithSeparator(Collection<String> values, String separator) {
        return commaSeparatedCollectionValues(values).replace(",", separator);
    }

    /**
     * Divide a {@link Collection} in a {@link List} of sets with a maximum size.
     *
     * @param originalSet The collection to divide.
     * @param size The maximum size of the partitions.
     * @return A {@link List} with the partitions of the given collection.
     */
    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    public static <T> List<Set<T>> setPartition(Collection<T> originalSet, int size) {
        int setCount = (int) Math.ceil((double) originalSet.size() / size);
        List<Set<T>> sets = new ArrayList<>(setCount);
        for (int i = 0; i < setCount; i++) {
            Set<T> setI = new HashSet<>(size);
            sets.add(setI);
        }

        int index = 0;
        for (T object : originalSet) {
            sets.get(index++ % setCount).add(object);
        }

        return sets;
    }
}