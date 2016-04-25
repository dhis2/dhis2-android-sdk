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

package org.hisp.dhis.client.sdk.ui.models.picker;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class PickerChain {
    private PickerChainItem rootNode;

    public PickerChain() {
        // explicit empty constructor
    }

    // if selection changed in the middle of chain,
    // this method call should chop off the tail of
    // list which comes after given selectedPicker
    public boolean put(Picker picker) {
        isNull(picker, "picker must not be null");

        // if we don't have any items in chain,
        // just create root node and exist
        if (rootNode == null) {
            rootNode = new PickerChainItem(picker);
            return true;
        }

        // if we already have picker in chain, return false
        if (contains(picker)) {
            return false;
        }

        // if we already have item with given parent, replace it
        PickerChainItem parentNode = findNode(picker.getParent());

        // chain last node with new one
        PickerChainItem childNode = new PickerChainItem(picker);

        // set relationship to previous item
        childNode.setPrevious(parentNode);

        if (parentNode != null) {
            parentNode.setNext(childNode);
        }

        return true;
    }

    public boolean remove(Picker picker) {
        // check if we have any items
        if (rootNode == null) {
            return false;
        }

        // if there is no picker in chain, return false
        if (!contains(picker)) {
            return false;
        }

        PickerChainItem node = findNode(picker);
        if (node == null) {
            return false;
        }

        PickerChainItem previousNode = node.getPrevious();
        PickerChainItem nextNode = node.getNext();

        if (previousNode != null) {
            previousNode.setNext(null);
        }

        if (nextNode != null) {
            nextNode.setPrevious(null);
        }

        node.setPrevious(null);
        node.setNext(null);

        return true;
    }

    public boolean contains(Picker picker) {
        return findNode(picker) != null;
    }

    public Picker get(int position) {
        if (position < 0) {
            throw new IllegalArgumentException();
        }

        if (rootNode == null) {
            return null;
        }

        int index = 0;

        // go through chain and increment index
        PickerChainItem node = rootNode;
        do {
            if (index == position) {
                return node.getPicker();
            }

            index = index + 1;
        } while ((node = node.getNext()) != null);

        return null;
    }

    public int size() {
        if (rootNode == null) {
            return 0;
        }

        // total count of PickerChainItems
        int count = 0;

        // walk down the chain
        PickerChainItem node = rootNode;
        do {
            count = count + 1;
        } while ((node = node.getNext()) != null);

        return count;
    }

    public void clear() {
        rootNode = null;
    }

    private PickerChainItem findNode(Picker picker) {
        PickerChainItem node = rootNode;

        if (node != null) {
            do {
                if (node.getPicker().equals(picker)) {
                    return node;
                }
            } while ((node = node.getNext()) != null);
        }

        return null;
    }

    public static class PickerChainItem {
        private final Picker picker;
        private PickerChainItem previous;
        private PickerChainItem next;

        public PickerChainItem(Picker picker) {
            this.picker = picker;
        }

        public Picker getPicker() {
            return picker;
        }

        public PickerChainItem getPrevious() {
            return previous;
        }

        public void setPrevious(PickerChainItem previous) {
            this.previous = previous;
        }

        public PickerChainItem getNext() {
            return next;
        }

        public void setNext(PickerChainItem next) {
            this.next = next;
        }
    }
}
