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
package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class UidsHelper {

    private UidsHelper() {}

    public static <O extends IdentifiableObject> Set<String> getUids(Collection<O> objects) {
        return addUids(new HashSet<>(), objects);
    }

    public static String getUidOrNull(ObjectWithUidInterface object) {
        return object == null ? null : object.uid();
    }

    public static <O extends IdentifiableObject> Set<String> addUids(Set<String> uids, Collection<O> objects) {
        for (IdentifiableObject object: objects) {
            uids.add(object.uid());
        }
        return uids;
    }

    public static <O extends ObjectWithUidInterface> List<String> getUidsList(List<O> objects) {
        List<String> uids = new ArrayList<>(objects.size());
        int i = 0;
        for (O o: objects) {
            uids.add(o.uid());
        }
        return uids;
    }

    private static <O extends ObjectWithUidInterface> String[] uidsArray(Collection<O> objects) {
        String[] uids = new String[objects.size()];
        int i = 0;
        for (O o: objects) {
            uids[i++] = "'" + o.uid() + "'";
        }
        return uids;
    }

    public static <O extends ObjectWithUidInterface> Map<String, O> mapByUid(Collection<O> objects) {
        Map<String, O> map = new HashMap<>(objects.size());
        for (O o: objects) {
            map.put(o.uid(), o);
        }
        return map;
    }

    public static <O extends ObjectWithUidInterface> O findByUid(Collection<O> objects, String uid) {
        for (O o: objects) {
            if (uid.equals(o.uid())) {
                return o;
            }
        }
        return null;
    }

    public static <O extends ObjectWithUidInterface> String commaSeparatedUidsWithSingleQuotationMarks(
            Collection<O> objects) {
        return Utils.commaAndSpaceSeparatedArrayValues(uidsArray(objects));
    }
}
