/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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
package org.hisp.dhis.android.core.arch.helpers;

import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class UidsHelper {

    private UidsHelper() {}

    /**
     * Returns a {@link Set} of uids of the given objects.
     *
     * @param objects A collection of objects with uid.
     * @return A {@link Set} with the uids of the given objects.
     */
    public static <O extends ObjectWithUidInterface> Set<String> getUids(Collection<O> objects) {
        return addUids(new HashSet<>(), objects);
    }

    /**
     * Return the uid of the object if the object is not null. If it is null, return null.
     *
     * @param object A object with uid.
     * @return The uid of the object.
     */
    public static String getUidOrNull(ObjectWithUidInterface object) {
        return object == null ? null : object.uid();
    }

    /**
     * Add the uids of a {@link Collection} of objects with uid to the given {@link Set} of uids.
     *
     * @param uids A {@link Set} of uids.
     * @param objects A {@link Collection} of objects with uid.
     * @return The {@link Set} with the existing uids plus the uids of the objects from the given collection.
     */
    public static <O extends ObjectWithUidInterface> Set<String> addUids(Set<String> uids, Collection<O> objects) {
        for (ObjectWithUidInterface object: objects) {
            uids.add(object.uid());
        }
        return uids;
    }

    /**
     * Returns a {@link List} of uids of the given objects.
     *
     * @param objects A collection of objects with uid.
     * @return A {@link List} with the uids of the given objects.
     */
    public static <O extends ObjectWithUidInterface> List<String> getUidsList(Collection<O> objects) {
        List<String> uids = new ArrayList<>(objects.size());
        for (O o: objects) {
            uids.add(o.uid());
        }
        return uids;
    }

    /**
     * Remove from the given {@link Collection} the objects which uid is contained in the given {@link List} of uids.
     *
     * @param objects A collection of objects with uid.
     * @param uids A list of uids.
     * @return A {@link List} of the objects which uid is not contained in the given {@link List} of uids.
     */
    public static <O extends ObjectWithUidInterface> List<O> excludeUids(Collection<O> objects, List<String> uids) {
        List<O> list = new ArrayList<>();
        for (O o: objects) {
            if (!uids.contains(o.uid())) {
                list.add(o);
            }
        }
        return list;
    }

    private static <O extends ObjectWithUidInterface> String[] uidsArray(Collection<O> objects) {
        String[] uids = new String[objects.size()];
        int i = 0;
        for (O o: objects) {
            uids[i++] = "'" + o.uid() + "'";
        }
        return uids;
    }

    /**
     * Map a {@link Collection} by uid.
     *
     * @param objects A collection of objects with uid.
     * @return A {@link Map} with the uid as key of the objects from the collection.
     */
    public static <O extends ObjectWithUidInterface> Map<String, O> mapByUid(Collection<O> objects) {
        Map<String, O> map = new HashMap<>(objects.size());
        for (O o: objects) {
            map.put(o.uid(), o);
        }
        return map;
    }

    /**
     * Map a {@link Collection} with the a custom key extracted from the parentExtractor.
     *
     * @param objects           A collection of objects with uid.
     * @param parentExtractor   A {@code Transformer} that extracts the parent property of the object.
     * @return A {@link Map} with a custom parent as key of the objects from the collection.
     */
    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    public static <O extends ObjectWithUidInterface> Map<String, List<O>> mapByParentUid(
            Collection<O> objects, Transformer<O, String> parentExtractor) {

        Map<String, List<O>> map = new HashMap<>();
        for (O o : objects) {
            String parentUid = parentExtractor.transform(o);
            List<O> list = map.get(parentUid);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(o);
            map.put(parentUid, list);
        }
        return map;
    }

    /**
     * Find an object with uid from a collection and returns it. Returns null if the object can`t be find.
     *
     * @param objects   A collection of objects with uid.
     * @param uid       The uid of the object to extract.
     * @return The objects from the collection with the given uid.
     */
    public static <O extends ObjectWithUidInterface> O findByUid(Collection<O> objects, String uid) {
        for (O o: objects) {
            if (uid.equals(o.uid())) {
                return o;
            }
        }
        return null;
    }

    /**
     * Build a {@link String} with the single quoted uids separated by commas and spaces.
     *
     * @param objects Array with the objects with uid to concatenate.
     * @return A {@link String} with the concatenated uids.
     */
    public static <O extends ObjectWithUidInterface> String commaSeparatedUidsWithSingleQuotationMarks(
            Collection<O> objects) {
        return CollectionsHelper.commaAndSpaceSeparatedArrayValues(uidsArray(objects));
    }
}