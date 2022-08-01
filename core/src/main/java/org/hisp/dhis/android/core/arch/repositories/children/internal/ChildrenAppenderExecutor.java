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
package org.hisp.dhis.android.core.arch.repositories.children.internal;

import org.hisp.dhis.android.core.common.CoreObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ChildrenAppenderExecutor {

    private ChildrenAppenderExecutor() {
    }

    public static <M extends CoreObject> M appendInObject(
            M m, Map<String, ChildrenAppender<M>> childrenAppenders, ChildrenSelection childrenSelection) {

        if (m == null) {
            return null;
        } else {
            M mWithChildren = m;
            for (ChildrenAppender<M> appender : getSelectedChildrenAppenders(childrenAppenders, childrenSelection)) {
                appender.prepareChildren(Collections.singleton(mWithChildren));
                mWithChildren = appender.appendChildren(mWithChildren);
            }
            return mWithChildren;
        }
    }

    public static <M extends CoreObject> List<M> appendInObjectCollection(
            List<M> list, Map<String, ChildrenAppender<M>> childrenAppenders, ChildrenSelection childrenSelection) {


        Collection<ChildrenAppender<M>> selectedAppenders
                = getSelectedChildrenAppenders(childrenAppenders, childrenSelection);

        for (ChildrenAppender<M> appender : selectedAppenders) {
            appender.prepareChildren(list);
        }

        List<M> setWithChildren = new ArrayList<>(list.size());
        for (M m : list) {
            M mWithChildren = m;
            for (ChildrenAppender<M> appender : selectedAppenders) {
                mWithChildren = appender.appendChildren(mWithChildren);
            }
            setWithChildren.add(mWithChildren);
        }
        return setWithChildren;
    }

    private static <M extends CoreObject> Collection<ChildrenAppender<M>> getSelectedChildrenAppenders(
            Map<String, ChildrenAppender<M>> appendersMap, ChildrenSelection childrenSelection) {
        List<ChildrenAppender<M>> appendersList = new ArrayList<>(appendersMap.size());
        for (String key : childrenSelection.children) {
            appendersList.add(appendersMap.get(key));
        }
        return appendersList;
    }
}