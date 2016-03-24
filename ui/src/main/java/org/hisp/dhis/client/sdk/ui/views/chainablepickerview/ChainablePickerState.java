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

package org.hisp.dhis.client.sdk.ui.views.chainablepickerview;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Parcelable implementation that can be used to store the state of {@link Picker} objects in a
 * {@link android.support.v7.widget.RecyclerView}
 */
public class ChainablePickerState implements Parcelable {

    public static final Creator<ChainablePickerState> CREATOR = new Creator<ChainablePickerState>
            () {
        @Override
        public ChainablePickerState createFromParcel(Parcel in) {
            return new ChainablePickerState(in);
        }

        @Override
        public ChainablePickerState[] newArray(int size) {
            return new ChainablePickerState[size];
        }
    };
    List<List<Picker>> pickerTree;

    /**
     * Creates a ChainablePickerState by converting a List of linked pickers into a list of lists
     *
     * @param rootNodes
     */
    public ChainablePickerState(List<Picker> rootNodes) {
        List<List<Picker>> rootNodesLists = new ArrayList<>();
        for (Picker rootNode : rootNodes) {
            List<Picker> rootNodeList = new ArrayList<>();
            Picker current = rootNode;
            rootNodeList.add(current);
            while (current.getNextLinkedSibling() != null) {
                current = current.getNextLinkedSibling();
                rootNodeList.add(current);
            }
            rootNodesLists.add(rootNodeList);
        }
        pickerTree = rootNodesLists;
    }

    protected ChainablePickerState(Parcel in) {
        in.readList(pickerTree, Picker.class.getClassLoader());
    }

    /**
     * Converts a List of List of pickers into a List of chained pickers
     *
     * @return
     */
    public List<Picker> getRootNodes() {
        List<Picker> rootNodes = new ArrayList<>();
        for (List<Picker> rootNodeList : pickerTree) {
            if (rootNodeList.isEmpty()) {
                continue;
            }
            Picker rootNode = rootNodeList.get(0);
            Picker currentNode = rootNode;
            for (int i = 0; i < rootNodeList.size() - 1; i++) {
                currentNode.setNextLinkedSibling(rootNodeList.get(i + 1));
                currentNode = rootNodeList.get(i + 1);
            }
            rootNodes.add(rootNode);
        }
        return rootNodes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(pickerTree);
    }
}
